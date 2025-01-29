package com.helltar.aibot.utils

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost

object Network {

    private const val TIMEOUT = 180000

    fun postAsString(url: String, headers: Map<String, String>, jsonBody: String) =
        postAsByteArray(url, headers, jsonBody).decodeToString()

    fun postAsByteArray(url: String, headers: Map<String, String>, jsonBody: String) =
        post(url, headers, jsonBody).data

    fun uploadWithFile(url: String, headers: Map<String, String>, parameters: Parameters, dataPart: FileDataPart): String {
        val response =
            Fuel.upload(url, Method.POST, parameters)
                .add(dataPart)
                .applyCommonSettings(headers)
                .response().second

        if (!response.isSuccessful)
            throw Exception("[upload] request failed $url $headers $parameters $response")

        return response.data.decodeToString()
    }

    private fun post(url: String, headers: Map<String, String>, jsonBody: String): Response {
        val response =
            url.httpPost()
                .applyCommonSettings(headers)
                .jsonBody(jsonBody)
                .response().second

        if (!response.isSuccessful)
            throw Exception("[post] request failed $url $headers $jsonBody $response")

        return response
    }

    private fun Request.applyCommonSettings(headers: Map<String, String>) =
        this.header(headers)
            .timeout(TIMEOUT)
            .timeoutRead(TIMEOUT)
}
