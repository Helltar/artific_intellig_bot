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
import java.util.concurrent.TimeUnit

/* todo: refact. */

class Transcription(ctx: MessageContext) : BotCommand(ctx) {

    private data class MediaData(
        val fileId: String,
        val fileName: String,
        val isVideo: Boolean = false
    )

    private companion object {
        const val ONE_MB = 1024 * 1024
        const val MAX_AUDIO_SIZE_BYTES = ONE_MB * 2
        const val MAX_VOICE_SIZE_BYTES = MAX_AUDIO_SIZE_BYTES
        const val MAX_VIDEO_DURATION = 60
        const val MAX_VIDEO_NOTE_DURATION = 90
        val log = KotlinLogging.logger {}
    }

    override suspend fun run() {
        if (isNotReply) {
            replyToMessage(Strings.ASR_WHISPER_USE_AS_REPLY)
            return
        }

        val media = try {
            checkAndGetFileInfo(replyMessage!!)
        } catch (_: IllegalStateException) {
            replyToMessage(Strings.VIDEO_OR_AUDIO_NOT_FOUND)
            return
        }

        media ?: return

        var outFile = File.createTempFile("tmp", media.fileName)

        try {
            try {
                ctx.sender.downloadFile(Methods.getFile(media.fileId).call(ctx.sender)).renameTo(outFile)
            } catch (e: TelegramApiException) {
                log.error { e.message }
                return
            }

            if (media.isVideo) {
                extractAudioFromVideo(outFile)?.let {
                    outFile.delete()
                    outFile = it
                } ?: run {
                    replyToMessage(Strings.ERROR_RETRIEVING_AUDIO_FROM_VIDEO)
                    return
                }

                if (outFile.length() > MAX_AUDIO_SIZE_BYTES) {
                    replyToMessage(Strings.AUDIO_MUST_BE_LESS_THAN.format("${MAX_AUDIO_SIZE_BYTES / ONE_MB} MB."))
                    return
                }
            }

            try {
                val text = TranscriptionService(OpenAiClient(openaiKey())).transcribeAudio(outFile)

                if (text.isNotBlank())
                    replyToMessage(text, replyMessage.messageId)
                else
                    replyToMessage(Strings.COULDNT_RECOGNIZE_VOICE, replyMessage.messageId)
            } catch (e: Exception) {
                log.error { e.message }
                replyToMessage(Strings.CHAT_EXCEPTION)
            }
        } finally {
            outFile.delete()
        }
    }

    override fun getCommandName() =
        Commands.User.CMD_ASR

    private fun checkAndGetFileInfo(message: Message): MediaData? =
        when {
            message.hasVoice() -> checkVoiceAndGetFileData(message.voice)
            message.hasAudio() -> checkAudioAndGetFileData(message.audio)
            message.hasVideo() -> checkVideoAndGetFileData(message.video)
            message.hasVideoNote() -> checkVideoNoteAndGetFileData(message.videoNote)
            else -> throw IllegalStateException("supported media type not found in the message")
        }

    private fun checkAudioAndGetFileData(audio: Audio) =
        if (audio.fileSize <= MAX_AUDIO_SIZE_BYTES)
            MediaData(audio.fileId, audio.fileName)
        else {
            replyToMessage(Strings.AUDIO_MUST_BE_LESS_THAN.format("${MAX_AUDIO_SIZE_BYTES / ONE_MB} MB."))
            null
        }

    private fun checkVoiceAndGetFileData(voice: Voice) =
        if (voice.fileSize <= MAX_VOICE_SIZE_BYTES)
            MediaData(voice.fileId, "_voice.ogg")
        else {
            replyToMessage(Strings.VOICE_MUST_BE_LESS_THAN.format("${MAX_VOICE_SIZE_BYTES / ONE_MB} MB."))
            null
        }

    private fun checkVideoAndGetFileData(video: Video) =
        if (video.duration <= MAX_VIDEO_DURATION)
            MediaData(video.fileId, "_video.mp4", true)
        else {
            replyToMessage(Strings.BAD_VIDEO_DURATION.format(MAX_VIDEO_DURATION))
            null
        }

    private fun checkVideoNoteAndGetFileData(videoNote: VideoNote) =
        if (videoNote.duration <= MAX_VIDEO_NOTE_DURATION)
            MediaData(videoNote.fileId, "_video-note.mp4", true)
        else {
            replyToMessage(Strings.BAD_VIDEO_NOTE_DURATION.format(MAX_VIDEO_NOTE_DURATION))
            null
        }

    private fun extractAudioFromVideo(video: File): File? {
        val outFile = File.createTempFile("tmp", ".wav")
        val command = listOf("ffmpeg", "-y", "-i", video.absolutePath, "-acodec", "pcm_s16le", "-ac", "1", "-ar", "16000", outFile.absolutePath)

        val process = try {
            ProcessBuilder(command).start()
        } catch (e: Exception) {
            log.error { "failed to start process $command: ${e.message}" }
            return null
        }

        return try {
            if (!process.waitFor(2, TimeUnit.MINUTES))
                process.destroy()

            outFile.takeIf { it.length() > 0 }
        } catch (e: Exception) {
            log.error { e.message }
            null
        } finally {
            if (process.isAlive)
                process.destroyForcibly()
        }
    }
}
