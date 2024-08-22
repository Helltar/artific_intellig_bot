package com.helltar.aibot.utils

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost

object NetworkUtils {

    private const val TIMEOUT = 180000

    fun postJson(url: String, headers: Map<String, String>, jsonBody: String) =
        url.httpPost()
            .applyCommonSettings(headers)
            .jsonBody(jsonBody)
            .response().second

    fun post(url: String, headers: Map<String, String>, body: String) =
        url.httpPost()
            .applyCommonSettings(headers)
            .body(body)
            .response().second

    fun upload(url: String, headers: Map<String, String>, parameters: Parameters) =
        Fuel.upload(url, Method.POST, parameters)
            .applyCommonSettings(headers)
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