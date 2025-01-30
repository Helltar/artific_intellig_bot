package com.helltar.aibot.commands.user.audio

import com.annimon.tgbotsmodule.api.methods.Methods
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Strings
import com.helltar.aibot.openai.api.OpenAiClient
import com.helltar.aibot.openai.api.service.TranscriptionService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.meta.api.objects.Audio
import org.telegram.telegrambots.meta.api.objects.Video
import org.telegram.telegrambots.meta.api.objects.VideoNote
import org.telegram.telegrambots.meta.api.objects.Voice
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/* todo: refact. */

class Transcription(ctx: MessageContext) : BotCommand(ctx) {

    private companion object {
        const val MAX_AUDIO_SIZE = 1024000
        const val MAX_VOICE_SIZE = MAX_AUDIO_SIZE
        const val MAX_VIDEO_DURATION = 60
        const val MAX_VIDEO_NOTE_DURATION = 90
        val log = KotlinLogging.logger {}
    }

    private val tempDir = System.getProperty("java.io.tmpdir")

    override suspend fun run() {
        if (isNotReply) {
            replyToMessage(Strings.ASR_WHISPER_USE_AS_REPLY)
            return
        }

        val fileData =
            getFileDataBasedOnMediaType(replyMessage!!)
                ?: run {
                    replyToMessage(Strings.VIDEO_OR_AUDIO_NOT_FOUND)
                    return
                }

        val fileId = fileData.first?.first ?: return
        val fileName = fileData.first?.second ?: return
        var outFile = File.createTempFile("file", fileName)

        try {
            ctx.sender.downloadFile(Methods.getFile(fileId).call(ctx.sender)).renameTo(outFile)
        } catch (e: TelegramApiException) {
            log.error { e.message }
            return
        }

        val isVideo = fileData.second

        if (isVideo)
            outFile =
                extractAudioFromVideo(outFile)
                    ?: run {
                        replyToMessage(Strings.ERROR_RETRIEVING_AUDIO_FROM_VIDEO)
                        return
                    }

        if (outFile.length() > MAX_AUDIO_SIZE) {
            replyToMessage(Strings.VOICE_MUST_BE_LESS_THAN.format("${MAX_AUDIO_SIZE / 1024000} mb."))
            return
        }

        try {
            val text = TranscriptionService(OpenAiClient(openaiKey())).transcribeAudio(outFile)

            if (text.isNotBlank()) {
                text.chunked(4000).forEach {
                    replyToMessage(it, replyMessage.messageId)
                }
            } else
                replyToMessage(Strings.COULDNT_RECOGNIZE_VOICE, replyMessage.messageId)
        } catch (e: Exception) {
            log.error { e.message }
            replyToMessage(Strings.CHAT_EXCEPTION)
        }
    }

    override fun getCommandName() =
        Commands.User.CMD_ASR

    private fun getFileDataBasedOnMediaType(message: Message): Pair<Pair<String, String>?, Boolean>? = // fileId, fileName, isVideo
        when {
            message.hasVoice() -> Pair(checkVoiceAndGetFileData(message.voice), false)
            message.hasAudio() -> Pair(checkAudioAndGetFileData(message.audio), false)
            message.hasVideo() -> Pair(checkVideoAndGetFileData(message.video), true)
            message.hasVideoNote() -> Pair(checkVideoNoteAndGetFileData(message.videoNote), true)
            else -> null
        }

    private fun checkAudioAndGetFileData(audio: Audio) =
        if (audio.fileSize <= MAX_AUDIO_SIZE)
            Pair(audio.fileId, audio.fileName)
        else {
            replyToMessage(Strings.AUDIO_MUST_BE_LESS_THAN.format("${MAX_AUDIO_SIZE / 1000} kb."))
            null
        }

    private fun checkVoiceAndGetFileData(voice: Voice) =
        if (voice.fileSize <= MAX_VOICE_SIZE)
            Pair(voice.fileId, "voice.ogg")
        else {
            replyToMessage(Strings.VOICE_MUST_BE_LESS_THAN.format("${MAX_VOICE_SIZE / 1000} kb."))
            null
        }

    private fun checkVideoAndGetFileData(video: Video) =
        if (video.duration <= MAX_VIDEO_DURATION)
            Pair(video.fileId, "video.mp4") // todo: temp fix. (todo check mime_type or som. else)
        else {
            replyToMessage(Strings.BAD_VIDEO_DURATION.format(MAX_VIDEO_DURATION))
            null
        }

    private fun checkVideoNoteAndGetFileData(videoNote: VideoNote) =
        if (videoNote.duration <= MAX_VIDEO_NOTE_DURATION)
            Pair(videoNote.fileId, "video_note.mp4")
        else {
            replyToMessage(Strings.BAD_VIDEO_NOTE_DURATION.format(MAX_VIDEO_NOTE_DURATION))
            null
        }

    private fun extractAudioFromVideo(file: File): File? {
        val filename = "$tempDir/audio_${UUID.randomUUID()}.wav"
        val command = listOf("ffmpeg", "-i", file.absolutePath, "-acodec", "pcm_s16le", "-ac", "1", "-ar", "16000", filename)

        return startProcess(command)?.let { process ->
            try {
                if (!process.waitFor(2, TimeUnit.MINUTES))
                    process.destroy()

                File(filename).takeIf { it.exists() }
            } catch (e: Exception) {
                log.error { e.message }
                null
            } finally {
                if (process.isAlive)
                    process.destroyForcibly()
            }
        }
    }

    private fun startProcess(command: List<String>): Process? =
        try {
            ProcessBuilder(command)
                .directory(File(tempDir))
                .start()
        } catch (e: Exception) {
            log.error { e.message }
            null
        }
}
