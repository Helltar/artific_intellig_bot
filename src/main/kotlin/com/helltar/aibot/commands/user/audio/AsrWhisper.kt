package com.helltar.aibot.commands.user.audio

import com.annimon.tgbotsmodule.api.methods.Methods
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.FileDataPart
import com.helltar.aibot.BotConfig.PROVIDER_OPENAI_COM
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.utils.NetworkUtils.httpUpload
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.Video
import org.telegram.telegrambots.meta.api.objects.VideoNote
import org.telegram.telegrambots.meta.api.objects.Voice
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class AsrWhisper(ctx: MessageContext) : BotCommand(ctx) {

    private val tempDir = System.getProperty("java.io.tmpdir")
    private val log = LoggerFactory.getLogger(javaClass)

    private companion object {
        const val MAX_AUDIO_FILE_SIZE = 25600 * 1000
    }

    override fun run() {
        if (isNotReply) {
            replyToMessage(Strings.ASR_WHISPER_USE_AS_REPLY)
            return
        }

        val replyMessage = this.replyMessage ?: return

        if (!replyMessage.hasVoice() && !replyMessage.hasVideo() && !replyMessage.hasVideoNote()) {
            replyToMessage(Strings.VIDEO_OR_AUDIO_NOT_FOUND)
            return
        }

        var isVideo = false

        val fileId =
            if (replyMessage.hasVoice())
                checkVoiceAndGetFileId(replyMessage.voice)
            else {
                isVideo = true

                if (replyMessage.hasVideo())
                    checkVideoAndGetFileId(replyMessage.video)
                else
                    if (replyMessage.hasVideoNote())
                        checkVideoNoteAndGetFileId(replyMessage.videoNote)
                    else
                        null
            }

        fileId ?: return

        val fileExt = if (isVideo) "mp4" else "wav"
        var outFile = File("$tempDir/${UUID.randomUUID()}.$fileExt")

        try {
            ctx.sender.downloadFile(Methods.getFile(fileId).call(ctx.sender), outFile)
        } catch (e: TelegramApiException) {
            log.error(e.message)
            return
        }

        if (isVideo)
            outFile =
                extractAudioFromVideo(outFile)
                    ?: run {
                        replyToMessage(Strings.ERROR_RETRIEVING_AUDIO_FROM_VIDEO)
                        return
                    }

        if (outFile.length() > MAX_AUDIO_FILE_SIZE) {
            replyToMessage(Strings.AUDIO_MUST_BE_LESS_THAN.format("${MAX_AUDIO_FILE_SIZE / 1024000} mb."))
            return
        }

        val responseJson = uploadAudio(outFile)

        try {
            val messageId = replyMessage.messageId

            val text =
                JSONObject(responseJson).getString("text").ifEmpty {
                    replyToMessage(Strings.COULDNT_RECOGNIZE_VOICE, messageId)
                    return
                }

            text.chunked(4096).forEach { replyToMessage(it, messageId) } // todo: 20 msg per min. (?)

        } catch (e: JSONException) {
            log.error(e.message)
            replyToMessage(Strings.CHAT_EXCEPTION)
        }
    }

    override fun getCommandName() =
        Commands.CMD_ASR

    private fun checkVoiceAndGetFileId(voice: Voice): String? {
        val maxVoiceSize = if (!isCreator()) 1024 * 1000 else MAX_AUDIO_FILE_SIZE

        return if (voice.fileSize <= maxVoiceSize)
            voice.fileId
        else {
            replyToMessage(Strings.AUDIO_MUST_BE_LESS_THAN.format("${maxVoiceSize / 1000} kb."))
            null
        }
    }

    private fun checkVideoAndGetFileId(video: Video): String? {
        val maxVideoDuration = 60

        return if (!isCreator() && video.duration > maxVideoDuration) {
            replyToMessage(Strings.BAD_VIDEO_DURATION.format(maxVideoDuration))
            null
        } else
            video.fileId
    }

    private fun checkVideoNoteAndGetFileId(videoNote: VideoNote): String? {
        val maxVideoNoteDuration = 90

        return if (!isCreator() && videoNote.duration > maxVideoNoteDuration) {
            replyToMessage(Strings.BAD_VIDEO_NOTE_DURATION.format(maxVideoNoteDuration))
            null
        } else
            videoNote.fileId
    }

    private fun extractAudioFromVideo(videoFile: File): File? {
        val outputFilename = "$tempDir/audio_${UUID.randomUUID()}.wav"
        val ffmpegCommand = "ffmpeg -i ${videoFile.absolutePath} -acodec pcm_s16le -ac 1 -ar 16000 $outputFilename"

        return try {
            Runtime.getRuntime().exec(ffmpegCommand).waitFor(2, TimeUnit.MINUTES)
            File(outputFilename).run { if (exists()) this else null }
        } catch (e: Exception) {
            log.error(e.message)
            null
        }
    }

    private fun uploadAudio(file: File): String {
        val url = "https://api.openai.com/v1/audio/transcriptions"
        val headers = mapOf("Authorization" to "Bearer ${getApiKey(PROVIDER_OPENAI_COM)}")
        val parameters = listOf("model" to "whisper-1")
        val dataPart = FileDataPart(file, "file")
        return httpUpload(url, parameters, headers, dataPart).data.decodeToString()
    }
}