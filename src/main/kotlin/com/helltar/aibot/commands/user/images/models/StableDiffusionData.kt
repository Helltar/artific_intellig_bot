package com.helltar.aibot.commands.user.images.models

object StableDiffusionData {

    /* https://platform.stability.ai/docs/api-reference#tag/v1generation/operation/textToImage */

    const val ENGINE_ID = "stable-diffusion-xl-1024-v1-0"

    data class TextPromptData(
        val text: String,
        val weight: Int = 1
    )

    data class RequestData(
        val steps: Int = 30, // number of diffusion steps to run, 10 - 150
        val width: Int = 1024, // SDXL v1.0 valid dimensions are 1024x1024, 1152x896, 1216x832, 1344x768, 1536x640, 640x1536, 768x1344, 832x1216, or 896x1152
        val height: Int = 1024,
        val seed: Int = 0,
        val cfg_scale: Int = 5, // how strictly the diffusion process adheres to the prompt text (higher values keep your image closer to your prompt), 0 - 35
        val samples: Int = 1,
        val style_preset: String,
        val text_prompts: List<TextPromptData>
    )

    /* todo: enum, etc */

    object StylePresets {
        const val ANALOG_FILM = "analog-film"
        const val ANIME = "anime"
        const val CINEMATIC = "cinematic"
        const val COMIC_BOOK = "comic-book"
        const val DIGITAL_ART = "digital-art"
        const val ENHANCE = "enhance"
        const val FANTASY_ART = "fantasy-art"
        const val ISOMETRIC = "isometric"
        const val LINE_ART = "line-art"
        const val LOW_POLY = "low-poly"
        const val MODELING_COMPOUND = "modeling-compound"
        const val MODEL_3D = "3d-model"
        const val NEON_PUNK = "neon-punk"
        const val ORIGAMI = "origami"
        const val PHOTOGRAPHIC = "photographic"
        const val PIXEL_ART = "pixel-art"
        const val TILE_TEXTURE = "tile-texture"
    }
}