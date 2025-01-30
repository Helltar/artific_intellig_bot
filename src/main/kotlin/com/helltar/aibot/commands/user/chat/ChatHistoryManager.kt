package com.helltar.aibot.commands.user.chat

import com.helltar.aibot.openai.api.models.common.MessageData
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class ChatHistoryManager(private val userId: Long) {

    private companion object {
        val userChatContextMap = ConcurrentHashMap<Long, CopyOnWriteArrayList<MessageData>>() // todo: mutex, sync., etc.
    }

    val userChatDialogHistory: List<MessageData>
        get() = getUserChatHistory()

    fun addMessage(messageData: MessageData) =
        getUserChatHistory().add(messageData)

    fun removeSecondMessage() {
        if (getUserChatHistory().size > 1) getUserChatHistory().removeAt(1)
    }

    fun getMessagesLengthSum() =
        getUserChatHistory().sumOf { it.content.length }

    fun clear() =
        getUserChatHistory().clear()

    private fun getUserChatHistory() =
        userChatContextMap.getOrPut(userId) { CopyOnWriteArrayList<MessageData>() }
}
