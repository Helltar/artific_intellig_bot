package com.helltar.aibot.commands.user.images

import com.annimon.tgbotsmodule.api.methods.Methods
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.isSuccessful
import com.google.gson.Gson
import com.helltar.aibot.BotConfig.PROVIDER_OPENAI_COM
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.user.images.models.GPT4VisionData
import com.helltar.aibot.utils.NetworkUtils.httpPost
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.io.File
import java.util.*

class GPT4Vision(ctx: MessageContext) : BotCommand(ctx) {

    private companion object {
        const val MAX_PHOTO_FILE_SIZE = 1024000
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run() {
        if (isNotReply) {
            replyToMessage(Strings.GPT_VISION_USE_AS_REPLY)
            return
        }

        if (!replyMessage!!.hasPhoto()) {
            replyToMessage(Strings.GPT_VISION_NO_PHOTO_IN_MESSAGE)
            return
        }

        val lastPhoto = replyMessage.photo.last()

        if (lastPhoto.fileSize > MAX_PHOTO_FILE_SIZE) {
            replyToMessage(Strings.IMAGE_MUST_BE_LESS_THAN.format("${MAX_PHOTO_FILE_SIZE / 1000} kb."))
            return
        }

        val imageFile = try {
            ctx.sender.downloadFile(Methods.getFile(lastPhoto.fileId).call(ctx.sender))
        } catch (e: TelegramApiException) {
            log.error(e.message)
            return
        }

        val response = sendPrompt(argsText, imageFile)
        val responseJson = response.data.decodeToString()

        if (response.isSuccessful) {
            val answer =
                try {
                    Gson().fromJson(responseJson, GPT4VisionData.ResponseData::class.java).choices.first().message.content
                } catch (e: Exception) {
                    replyToMessage(Strings.CHAT_EXCEPTION)
                    log.error(responseJson)
                    log.error(e.message)
                    return
                }

            try {
                replyToMessage(answer, markdown = true)
            } catch (e: Exception) { // todo: TelegramApiRequestException
                replyToMessageWithDocument(
                    File.createTempFile("answer", ".txt").apply { writeText(answer) },
                    Strings.TELEGRAM_API_EXCEPTION_RESPONSE_SAVED_TO_FILE
                )

                log.error(e.message)
            }
        } else {
            try {
                replyToMessage(JSONObject(responseJson).getJSONObject("error").getString("message")) // todo: errors model
            } catch (e: JSONException) {
                replyToMessage(Strings.CHAT_EXCEPTION)
                log.error(e.message)
            }

            log.error(responseJson)
        }
    }

    override fun getCommandName() =
        Commands.CMD_GPT_VISION

    private fun sendPrompt(text: String, image: File): Response {
        val url = "https://api.openai.com/v1/chat/completions"
        val headers = mapOf("Content-Type" to "application/json", "Authorization" to "Bearer ${getApiKey(PROVIDER_OPENAI_COM)}")

        val requestData =
            GPT4VisionData.RequestData(
                messages = listOf(
                    GPT4VisionData.MessageData(
                        content = listOf(
                            GPT4VisionData.ContentData(
                                GPT4VisionData.MESSAGE_CONTENT_TYPE_TEXT,
                                text
                            ),
                            GPT4VisionData.ContentData(
                                GPT4VisionData.MESSAGE_CONTENT_TYPE_IMAGE,
                                image_url =
                                GPT4VisionData.ImageData(
                                    "data:image/jpeg;base64,${Base64.getEncoder().encodeToString(image.readBytes())}"
                                )
                            )
                        )
                    )
                )
            )

        return httpPost(url, headers, Gson().toJson(requestData))
    }
}