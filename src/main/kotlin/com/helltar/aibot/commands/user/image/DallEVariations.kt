package com.helltar.aibot.commands.user.image

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.AiCommand
import com.helltar.aibot.config.Strings
import com.helltar.aibot.exceptions.ImageTooLargeException
import com.helltar.aibot.openai.ApiClient
import com.helltar.aibot.openai.service.DalleService
import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.Image
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class DallEVariations(ctx: MessageContext) : AiCommand(ctx) {

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

        val photo =
            try {
                downloadPhoto() ?: return
            } catch (_: ImageTooLargeException) {
                replyToMessage(Strings.IMAGE_MUST_BE_LESS_THAN.format("1.MB"))
                return
            }

        try {
            val originalImage = ImageIO.read(photo)
            val resizedImage = resizeImage(originalImage)

            ByteArrayOutputStream().use { outputStream ->
                ImageIO.write(resizedImage, IMAGE_FORMAT, outputStream)
                val imageUrl = DalleService(ApiClient(openaiKey())).generateImageVariations(outputStream, IMAGE_FORMAT)
                replyToMessageWithPhoto(imageUrl, messageId = replyMessage?.messageId)
            }
        } catch (e: Exception) {
            log.error { e.message }
            replyToMessage(Strings.BAD_REQUEST)
        }
    }

    override fun commandName() =
        Commands.User.CMD_DALLE_VARIATIONS

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
