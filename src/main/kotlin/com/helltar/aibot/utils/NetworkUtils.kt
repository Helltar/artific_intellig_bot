package com.helltar.aibot.utils

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost

object NetworkUtils {

    private const val TIMEOUT = 180000

    fun httpPost(url: String, headers: Map<String, String>, jsonBody: String) =
        url.httpPost()
            .header(headers)
            .timeout(TIMEOUT)
            .timeoutRead(TIMEOUT)
            .jsonBody(jsonBody)
            .response().second

    fun httpUpload(url: String, headers: Map<String, String>, parameters: Parameters) =
        Fuel.upload(url, Method.POST, parameters)
            .header(headers)
            .timeout(TIMEOUT)
            .timeoutRead(TIMEOUT)
            .response().second

    fun httpUpload(url: String, parameters: Parameters, headers: Map<String, String>, dataPart: FileDataPart) =
        Fuel.upload(url, Method.POST, parameters)
            .add(dataPart)
            .header(headers)
            .timeout(TIMEOUT)
            .timeoutRead(TIMEOUT)
            .response().second
}