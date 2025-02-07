package com.helltar.aibot.commands.user.chat

import com.helltar.aibot.openai.api.models.common.MessageData
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class ChatHistoryManager(private val userId: Long) {

    data class ChatMessage(
        val datetime: LocalDateTime,
        val message: MessageData,
    )

    private companion object {
        val userChatContextMap = ConcurrentHashMap<Long, CopyOnWriteArrayList<ChatMessage>>()
    }

    val userChatDialogHistory: List<ChatMessage>
        get() = getOrCreateChatHistory()

    fun addMessage(messageData: MessageData) {
        val history = getOrCreateChatHistory()
        history.add(ChatMessage(LocalDateTime.now(), messageData))
    }

    fun removeSecondMessage() {
        val history = getOrCreateChatHistory()
        if (history.size > 1) history.removeAt(1)
    }

    fun getMessagesLengthSum() =
        getOrCreateChatHistory().sumOf { it.message.content.length }

    fun clear() =
        getOrCreateChatHistory().clear()

    private fun getOrCreateChatHistory() =
        userChatContextMap.getOrPut(userId) { CopyOnWriteArrayList<ChatMessage>() }
}
