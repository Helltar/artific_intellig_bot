package com.helltar.aibot.openai.api

import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.Parameters
import com.helltar.aibot.utils.Network

class OpenAiClient(apiKey: String?) {

    private val authorizationHeader = mapOf("Authorization" to "Bearer $apiKey")
    private val headers = mapOf("Content-Type" to "application/json") + authorizationHeader

    fun postAsString(url: String, body: String) =
        Network.postAsString(url, headers, body)

    fun postAsByteArray(url: String, body: String) =
        Network.postAsByteArray(url, headers, body)

    fun uploadWithFile(url: String, parameters: Parameters, dataPart: FileDataPart) =
        Network.uploadWithFile(url, authorizationHeader, parameters, dataPart)
}
