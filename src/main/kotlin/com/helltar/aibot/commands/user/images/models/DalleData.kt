package com.helltar.aibot.commands.user.images.models

object DalleData {

    /* https://beta.openai.com/docs/guides/images/usage?lang=curl */

    const val DALLE_REQUEST_IMAGE_SIZE = "256x256" // 512x512 1024x1024

    data class RequestData(
        val prompt: String,
        val n: Int,
        val size: String = DALLE_REQUEST_IMAGE_SIZE
    )
}