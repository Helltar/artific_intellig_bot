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
import org.telegram.telegrambots.meta.api.objects.*
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class AsrWhisper(ctx: MessageContext) : BotCommand(ctx) {

    private companion object {
        const val WHISPER_MAX_AUDIO_FILE_SIZE = 25600000
    }

    private val isCreator = isCreator()
    private val tempDir = System.getProperty("java.io.tmpdir")

    private val maxAudioSize = if (!isCreator) 1024000 else WHISPER_MAX_AUDIO_FILE_SIZE
    private val maxVoiceSize = maxAudioSize
    private val maxVideoDuration = if (!isCreator) 60 else 600
    private val maxVideoNoteDuration = if (!isCreator) 90 else 900

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run() {
        if (isNotReply) {
            replyToMessage(Strings.ASR_WHISPER_USE_AS_REPLY)
            return
        }

        val fileData =
            checkMediaTypeAndGetFileData(replyMessage!!)
                ?: run {
                    replyToMessage(Strings.VIDEO_OR_AUDIO_NOT_FOUND)
                    return
                }

        val fileId = fileData.first?.first ?: return
        val fileName = fileData.first?.second ?: return
        var outFile = File("$tempDir/${UUID.randomUUID()}_${fileName}")

        try {
            ctx.sender.downloadFile(Methods.getFile(fileId).call(ctx.sender), outFile)
        } catch (e: TelegramApiException) {
            log.error(e.message)
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

        if (outFile.length() > WHISPER_MAX_AUDIO_FILE_SIZE) {
            replyToMessage(Strings.VOICE_MUST_BE_LESS_THAN.format("${WHISPER_MAX_AUDIO_FILE_SIZE / 1024000} mb."))
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
            log.error("${e.message}: $responseJson")
            replyToMessage(Strings.CHAT_EXCEPTION)
        }
    }

    override fun getCommandName() =
        Commands.CMD_ASR

    private fun checkMediaTypeAndGetFileData(message: Message): Pair<Pair<String, String>?, Boolean>? { // fileId, fileName, isVideo
        if (message.hasVoice())
            return Pair(checkVoiceAndGetFileData(message.voice), false)

        if (message.hasAudio())
            return Pair(checkAudioAndGetFileData(message.audio), false)

        if (message.hasVideo())
            return Pair(checkVideoAndGetFileData(message.video), true)

        if (message.hasVideoNote())
            return Pair(checkVideoNoteAndGetFileData(message.videoNote), true)

        return null
    }

    private fun checkAudioAndGetFileData(audio: Audio) =
        if (audio.fileSize <= maxAudioSize)
            Pair(audio.fileId, audio.fileName)
        else {
            replyToMessage(Strings.AUDIO_MUST_BE_LESS_THAN.format("${maxAudioSize / 1000} kb."))
            null
        }

    private fun checkVoiceAndGetFileData(voice: Voice) =
        if (voice.fileSize <= maxVoiceSize)
            Pair(voice.fileId, "voice.ogg")
        else {
            replyToMessage(Strings.VOICE_MUST_BE_LESS_THAN.format("${maxVoiceSize / 1000} kb."))
            null
        }

    private fun checkVideoAndGetFileData(video: Video) =
        if (video.duration <= maxVideoDuration)
            Pair(video.fileId, video.fileName)
        else {
            replyToMessage(Strings.BAD_VIDEO_DURATION.format(maxVideoDuration))
            null
        }

    private fun checkVideoNoteAndGetFileData(videoNote: VideoNote) =
        if (videoNote.duration <= maxVideoNoteDuration)
            Pair(videoNote.fileId, "video_note.mp4")
        else {
            replyToMessage(Strings.BAD_VIDEO_NOTE_DURATION.format(maxVideoNoteDuration))
            null
        }

    private fun extractAudioFromVideo(file: File): File? {
        val outputFilename = "$tempDir/audio_${UUID.randomUUID()}.wav"
        val ffmpegCommand = "ffmpeg -i ${file.absolutePath} -acodec pcm_s16le -ac 1 -ar 16000 $outputFilename"

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