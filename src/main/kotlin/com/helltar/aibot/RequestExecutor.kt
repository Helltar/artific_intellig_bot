package com.helltar.aibot

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object RequestExecutor {

    private val requestsList = hashMapOf<String, Job>()

    fun addRequest(requestKey: String, block: () -> Unit): Boolean {
        if (requestsList.containsKey(requestKey))
            if (requestsList[requestKey]?.isCompleted == false)
                return false

        requestsList[requestKey] = CoroutineScope(Dispatchers.IO).launch { block() }

        return true
    }
}