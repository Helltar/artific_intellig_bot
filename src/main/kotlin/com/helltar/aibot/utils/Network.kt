package com.helltar.aibot.utils

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost

object Network {

    private const val TIMEOUT = 180000

    fun postJson(url: String, headers: Map<String, String>, jsonBody: String) =
        url.httpPost()
            .applyCommonSettings(headers)
            .jsonBody(jsonBody)
            .response().second

    fun uploadWithFile(url: String, headers: Map<String, String>, parameters: Parameters, dataPart: FileDataPart) =
        Fuel.upload(url, Method.POST, parameters)
            .add(dataPart)
            .applyCommonSettings(headers)
            .response().second

    private fun Request.applyCommonSettings(headers: Map<String, String>) =
        this.header(headers)
            .timeout(TIMEOUT)
            .timeoutRead(TIMEOUT)
}
