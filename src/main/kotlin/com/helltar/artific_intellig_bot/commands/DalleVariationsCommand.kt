package com.helltar.artific_intellig_bot.commands

import com.annimon.tgbotsmodule.api.methods.Methods
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.DIR_OUT_DALLE2_VARIATIONS
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.Utils
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

class DalleVariationsCommand(ctx: MessageContext) : BotCommand(ctx) {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run() {
        val photo = ctx.message().replyToMessage.photo
        val photoSize = photo[photo.lastIndex]

        if (photoSize.fileSize > (1024 * 1000)) {
            replyToMessage("Image must be less than 1MB \uD83D\uDE25") // 😥
            return
        }

        var filename = DIR_OUT_DALLE2_VARIATIONS + "${userId}_${Utils.randomUUID()}.jpg"

        try {
            ctx.sender.downloadFile(Methods.getFile(photoSize.fileId).call(ctx.sender), File(filename))
        } catch (e: Exception) {
            log.error(e.message)
            return
        }

        ImageIO.write(resize(ImageIO.read(File(filename))), "png", File("$filename.png"))
        File(filename).delete() // jpg

        filename += ".png"

        postImage(filename)?.let { json ->
            try {
                replyToMessageWithPhoto(
                    JSONObject(json)
                        .getJSONArray("data")
                        .getJSONObject(0)
                        .getString("url")
                )
            } catch (e: JSONException) {
                try {
                    replyToMessage(
                        JSONObject(json)
                            .getJSONObject("error")
                            .getString("message")
                    )
                } catch (e: JSONException) {
                    log.error(e.message, e)
                    replyToMessage(Strings.bad_request)
                }
            }
        }

        File(filename).delete() // png
    }

    private fun resize(image: BufferedImage): BufferedImage {
        val resized = BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB)

        resized.createGraphics().run {
            drawImage(image.getScaledInstance(512, 512, Image.SCALE_FAST), 0, 0, null)
            dispose()
        }

        return resized
    }

    // todo: repl. okhttp
    private fun postImage(filename: String): String? {
        val requestBody =
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "image", filename,
                    RequestBody.create(MediaType.parse("image/png"), File(filename))
                )
                .addFormDataPart("n", "1")
                .addFormDataPart("size", "256x256")
                .build()

        val request =
            Request.Builder()
                .url("https://api.openai.com/v1/images/variations")
                .header("Authorization", "Bearer $openaiKey")
                .header("Content-Type", "multipart/form-data")
                .post(requestBody).build()

        return try {
            OkHttpClient().newBuilder()
                .connectTimeout(25, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)
                .build()
                .newCall(request)
                .execute()
                .body()?.string()
        } catch (e: IOException) {
            log.error(e.message)
            null
        }
    }
}
