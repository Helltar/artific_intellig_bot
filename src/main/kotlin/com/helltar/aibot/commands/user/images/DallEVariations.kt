package com.helltar.aibot.commands.user.images

import com.annimon.tgbotsmodule.api.methods.Methods
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.FileDataPart
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.OpenAICommand
import com.helltar.aibot.commands.user.images.models.Dalle
import com.helltar.aibot.commands.user.images.models.Dalle.VARIATIONS_API_URL
import com.helltar.aibot.commands.user.images.models.Dalle.VARIATIONS_FILEDATAPART_NAME
import com.helltar.aibot.commands.user.images.models.Dalle.dalleVariationsParams
import com.helltar.aibot.utils.Network.uploadWithFile
import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.awt.Image
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO

open class DallEVariations(ctx: MessageContext) : OpenAICommand(ctx) {

    private companion object {
        const val IMAGE_FORMAT = "png"
        val log = KotlinLogging.logger {}
    }

    override suspend fun run() {
        if (isNotReply) {
            replyToMessage(Strings.DALLE_VARIATIONS_USE_AS_REPLY)
            return
        }

        if (replyMessage?.hasPhoto() == false) {
            replyToMessage(Strings.NO_PHOTO_IN_MESSAGE)
            return
        }

        val photoFile = downloadPhotoOrReplyIfInvalid() ?: return

        try {
            val originalImage = ImageIO.read(photoFile)
            val resizedImage = resizeImage(originalImage)

            ByteArrayOutputStream().use { outputStream ->
                ImageIO.write(resizedImage, IMAGE_FORMAT, outputStream)

                val responseJson = uploadImage(outputStream).also { log.debug { it } }
                val imageUrl = json.decodeFromString<Dalle.ResponseData>(responseJson).data.first().url

                replyToMessageWithPhoto(imageUrl, messageId = replyMessage?.messageId)
            }
        } catch (e: Exception) {
            log.error { e.message }
            replyToMessage(Strings.BAD_REQUEST)
        }
    }

    override fun getCommandName() =
        Commands.CMD_DALLE_VARIATIONS

    protected fun downloadPhotoOrReplyIfInvalid(): File? {
        val photo = replyMessage?.photo?.maxByOrNull { it.fileSize }

        return photo?.let {
            if (it.fileSize <= 1024 * 1024) {
                try {
                    ctx.sender.downloadFile(Methods.getFile(it.fileId).call(ctx.sender))
                } catch (e: TelegramApiException) {
                    log.error { e.message }
                    null
                }
            } else {
                replyToMessage(Strings.IMAGE_MUST_BE_LESS_THAN.format("1.MB"))
                null
            }
        }
    }

    private fun resizeImage(image: BufferedImage): BufferedImage {
        val targetWidth = 512
        val targetHeight = 512

        val scaledImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB)

        scaledImage.createGraphics().apply {
            setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
            setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
            setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            drawImage(image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH), 0, 0, targetWidth, targetHeight, null)
            dispose()
        }

        return scaledImage
    }

    private suspend fun uploadImage(imageData: ByteArrayOutputStream): String {
        val url = VARIATIONS_API_URL
        val parameters = dalleVariationsParams
        val file = File.createTempFile("tmp", ".$IMAGE_FORMAT").apply { writeBytes(imageData.toByteArray()) } // todo: temp file
        return uploadWithFile(url, createAuthHeader(), parameters, FileDataPart(file, VARIATIONS_FILEDATAPART_NAME))
    }
}
