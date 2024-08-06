package com.helltar.aibot.commands.user.chat

import com.helltar.aibot.commands.user.chat.models.Chat
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class ChatHistoryManager(private val userId: Long) {

    private companion object {
        val userChatContextMap = ConcurrentHashMap<Long, CopyOnWriteArrayList<Chat.MessageData>>() // todo: mutex, sync., etc.
    }

    val userChatDialogHistory: List<Chat.MessageData>
        get() = getUserChatHistory()

    fun addMessage(messageData: Chat.MessageData) =
        getUserChatHistory().add(messageData)

    fun removeSecondMessage() {
        if (getUserChatHistory().size > 1) getUserChatHistory().removeAt(1)
    }

    fun getMessagesLengthSum() =
        getUserChatHistory().sumOf { it.content.length }

    fun clear() =
        getUserChatHistory().clear()

    private fun getUserChatHistory() =
        userChatContextMap.getOrPut(userId) { CopyOnWriteArrayList<Chat.MessageData>() }
}