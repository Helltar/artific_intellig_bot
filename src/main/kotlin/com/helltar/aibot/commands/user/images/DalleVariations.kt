package com.helltar.aibot.commands.user.images

import com.annimon.tgbotsmodule.api.methods.Methods
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.FileDataPart
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.user.chat.ChatGPT
import com.helltar.aibot.commands.user.images.models.DalleData.DALLE_REQUEST_IMAGE_SIZE
import com.helltar.aibot.utils.NetworkUtils.httpUpload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

class DalleVariations(ctx: MessageContext) : ChatGPT(ctx) {

    private companion object {
        const val MAX_PHOTO_FILE_SIZE = 1024000
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun run() {
        if (isNotReply) {
            replyToMessage(Strings.DALLE_VARIATIONS_USE_AS_REPLY)
            return
        }

        if (!replyMessage!!.hasPhoto()) {
            replyToMessage(Strings.NO_PHOTO_IN_MESSAGE)
            return
        }

        val photo = replyMessage.photo
        val photoSize = photo.last()

        if (photoSize.fileSize > MAX_PHOTO_FILE_SIZE) {
            replyToMessage(Strings.IMAGE_MUST_BE_LESS_THAN.format("1MB"))
            return
        }

        val inputStream = try {
            ctx.sender.downloadFileAsStream(Methods.getFile(photoSize.fileId).call(ctx.sender))
        } catch (e: TelegramApiException) {
            log.error(e.message)
            return
        }

        val squarePngImage = ByteArrayOutputStream()

        try {
            withContext(Dispatchers.IO) { ImageIO.write(resizeImage(ImageIO.read(inputStream)), "png", squarePngImage) }
        } catch (e: IOException) {
            log.error(e.message)
            return
        }

        val responseJson = uploadImage(squarePngImage)

        try {
            replyToMessageWithPhoto(
                JSONObject(responseJson)
                    .getJSONArray("data")
                    .getJSONObject(0)
                    .getString("url"), messageId = replyMessage.messageId
            )
        } catch (e: JSONException) {
            try {
                replyToMessage(
                    JSONObject(responseJson)
                        .getJSONObject("error")
                        .getString("message")
                )
            } catch (e: JSONException) {
                log.error(e.message, e)
                replyToMessage(Strings.BAD_REQUEST)
            }
        }
    }

    override fun getCommandName() =
        Commands.CMD_DALLE_VARIATIONS

    private fun resizeImage(image: BufferedImage): BufferedImage {
        val resized = BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB)

        resized.createGraphics().apply {
            drawImage(image.getScaledInstance(512, 512, Image.SCALE_FAST), 0, 0, null)
            dispose()
        }

        return resized
    }

    private suspend fun uploadImage(byteArrayStream: ByteArrayOutputStream): String {
        val url = "https://api.openai.com/v1/images/variations"
        val parameters = listOf("n" to "1", "size" to DALLE_REQUEST_IMAGE_SIZE)
        val file = withContext(Dispatchers.IO) { File.createTempFile("tmp", ".png") }.apply { writeBytes(byteArrayStream.toByteArray()) } // todo: tempFile
        val dataPart = FileDataPart(file, "image")
        return httpUpload(url, parameters, getOpenAIAuthorizationHeader(), dataPart).data.decodeToString()
    }
}