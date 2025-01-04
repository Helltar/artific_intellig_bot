package com.helltar.aibot.commands.user.images

import com.annimon.tgbotsmodule.api.methods.Methods
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.FileDataPart
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.OpenAICommand
import com.helltar.aibot.commands.user.images.models.Dalle
import com.helltar.aibot.commands.user.images.models.Dalle.DALLE_IMAGE_SIZE
import com.helltar.aibot.utils.NetworkUtils.uploadWithFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.awt.Image
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO

open class DallEVariations(ctx: MessageContext) : OpenAICommand(ctx) {

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

        val photo = downloadPhotoIfValid() ?: return
        val squarePngImage = ByteArrayOutputStream()

        try {
            withContext(Dispatchers.IO) {
                val originalImage = ImageIO.read(photo)
                val resizedImage = resizeImage(originalImage)
                ImageIO.write(resizedImage, "png", squarePngImage)
            }

            val responseJson = uploadImage(squarePngImage)

            log.debug(responseJson)

            val url = json.decodeFromString<Dalle.ResponseData>(responseJson).data.first().url

            replyToMessageWithPhoto(url, messageId = replyMessage.messageId)
        } catch (e: Exception) {
            log.error(e.message)
            replyToMessage(Strings.BAD_REQUEST)
        }
    }

    override fun getCommandName() =
        Commands.CMD_DALLE_VARIATIONS

    protected fun downloadPhotoIfValid(): File? {
        val photo = replyMessage!!.photo.last()

        if (photo.fileSize > 1024 * 1024) {
            replyToMessage(Strings.IMAGE_MUST_BE_LESS_THAN.format("1MB"))
            return null
        }

        return try {
            ctx.sender.downloadFile(Methods.getFile(photo.fileId).call(ctx.sender))
        } catch (e: TelegramApiException) {
            log.error(e.message)
            null
        }
    }

    private fun resizeImage(image: BufferedImage): BufferedImage {
        val targetWidth = 512
        val targetHeight = 512

        val resizedImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB)

        resizedImage.createGraphics().apply {
            setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
            setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
            setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            drawImage(image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH), 0, 0, targetWidth, targetHeight, null)

            dispose()
        }

        return resizedImage
    }

    private suspend fun uploadImage(byteArrayStream: ByteArrayOutputStream): String {
        val url = "https://api.openai.com/v1/images/variations"
        val parameters = listOf("n" to "1", "size" to DALLE_IMAGE_SIZE)

        val file = // todo: tempFile
            withContext(Dispatchers.IO) { File.createTempFile("tmp", ".png") }.apply {
                writeBytes(byteArrayStream.toByteArray())
            }

        return uploadWithFile(url, createAuthHeader(), parameters, FileDataPart(file, "image")).data.decodeToString()
    }
}
