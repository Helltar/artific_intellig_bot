package com.helltar.aibot.commands.user.images

import com.annimon.tgbotsmodule.api.methods.Methods
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.Method
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
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

class DalleVariations(ctx: MessageContext) : BotCommand(ctx) {

    private val log = LoggerFactory.getLogger(javaClass)

    private companion object {
        const val DALLE_IMAGE_SIZE = "256x256" // 512x512 1024x1024
    }

    override fun run() {
        val photo = ctx.message().replyToMessage.photo
        val photoSize = photo[photo.lastIndex]

        if (photoSize.fileSize > (1024 * 1000)) {
            replyToMessage(String.format(Strings.image_must_be_less_than, "1MB"))
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
            ImageIO.write(resizeImage(ImageIO.read(inputStream)), "png", squarePngImage)
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
                    .getString("url"),
                messageId = ctx.message().replyToMessage.messageId
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
                replyToMessage(Strings.bad_request)
            }
        }
    }

    private fun resizeImage(image: BufferedImage): BufferedImage {
        val resized = BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB)

        resized.createGraphics().apply {
            drawImage(image.getScaledInstance(512, 512, Image.SCALE_FAST), 0, 0, null)
            dispose()
        }

        return resized
    }

    private fun uploadImage(byteArrayStream: ByteArrayOutputStream): String {
        val parameters = listOf("n" to "1", "size" to DALLE_IMAGE_SIZE)

        // todo: tempFile
        val file = File.createTempFile("tmp", ".png").apply { writeBytes(byteArrayStream.toByteArray()) }

        return Fuel.upload("https://api.openai.com/v1/images/variations", Method.POST, parameters)
            .add(FileDataPart(file, "image"))
            .header("Authorization", "Bearer $openaiKey")
            .timeout(FUEL_TIMEOUT)
            .timeoutRead(FUEL_TIMEOUT)
            .response().second.data.decodeToString()
    }
}