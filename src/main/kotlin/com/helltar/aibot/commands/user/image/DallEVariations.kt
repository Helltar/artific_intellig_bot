package com.helltar.aibot.commands.user.image

import com.annimon.tgbotsmodule.api.methods.Methods
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Strings
import com.helltar.aibot.openai.api.OpenAiClient
import com.helltar.aibot.openai.api.service.DalleService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.awt.Image
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO

open class DallEVariations(ctx: MessageContext) : BotCommand(ctx) {

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
                val imageUrl = DalleService(OpenAiClient(openaiKey())).generateImageVariations(outputStream, IMAGE_FORMAT)
                replyToMessageWithPhoto(imageUrl, messageId = replyMessage?.messageId)
            }
        } catch (e: Exception) {
            log.error { e.message }
            replyToMessage(Strings.BAD_REQUEST)
        }
    }

    override fun getCommandName() =
        Commands.User.CMD_DALLE_VARIATIONS

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
}
