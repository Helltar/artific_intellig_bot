package com.helltar.aibot

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object RequestExecutor {

    private val requestsMap = hashMapOf<String, Job>()

    fun addRequest(key: String, block: () -> Unit): Boolean {
        if (requestsMap.containsKey(key))
            if (requestsMap[key]?.isCompleted == false)
                return false

        requestsMap[key] = CoroutineScope(Dispatchers.IO).launch { block() }

        return true
    }
}