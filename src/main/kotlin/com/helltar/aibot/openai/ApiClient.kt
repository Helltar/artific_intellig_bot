package com.helltar.aibot.openai

import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.Parameters
import com.helltar.aibot.utils.Network

class ApiClient(apiKey: String?, private val baseUrl: String = ApiConfig.OpenAi.BASE_URL) {

    private val authorizationHeader = mapOf("Authorization" to "Bearer $apiKey")
    private val headers = mapOf("Content-Type" to "application/json") + authorizationHeader

    fun postAsString(endpoint: String, body: String) =
        Network.postAsString(baseUrl + endpoint, headers, body)

    fun postAsByteArray(endpoint: String, body: String) =
        Network.postAsByteArray(baseUrl + endpoint, headers, body)

    fun uploadWithFile(endpoint: String, parameters: Parameters, dataPart: FileDataPart) =
        Network.uploadWithFile(baseUrl + endpoint, authorizationHeader, parameters, dataPart)
}
