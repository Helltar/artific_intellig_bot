artific_intellig_bot
--------------------

Demo: https://t.me/+siikRmY3uyE5YTBi


Download
--------

- [artific_intellig_bot-0.5.0.zip](https://github.com/Helltar/artific_intellig_bot/releases/download/0.5.0/artific_intellig_bot-0.5.0.zip) (35.3 MB)

Installation
------------

./_config/_

**bot_config.yaml**:

- token: 123:xxx ([BotFather](https://t.me/BotFather))
- username: bot_username ([BotFather](https://t.me/BotFather))
- creatorId: 1234567890 (your Telegram user-ID)

**api_keys.ini**:

- openai_key [OpenAI API Key](https://beta.openai.com/account/api-keys)
- stable_diffusion_key [Stable Diffusion API Key](https://beta.dreamstudio.ai/membership?tab=apiKeys)
- google_cloud_key [Google Cloud API Key](https://console.cloud.google.com/apis/credentials)

<br>

```
unzip artific_intellig_bot-0.5.0.zip && cd artific_intellig_bot-0.5.0
java -jar artific_intellig_bot-0.5.0-all.jar
```

Commands
--------

- /**chat** - ChatGPT
- /**dalle** - DALL·E 2
- /**sdif** - Stable Diffusion
- /**dallevariations** - DALL·E 2 Variations (this is not a typical command in use, use as a reply to an image with text: "@")
- /**uptime**
- /**about**

Admin commands
--------------

- /**enable** _commandName_ (example: _/enable /chat_)
- /**disable** _commandName_ (example: _/disable /dalle_)
<br>

- /**chatastext** (ChatGPT answers as text, default)
- /**chatasvoice** (ChatGPT answers as voice, Google Text-to-Speech)
<br>

- /**ban** (use as reply to user message, example: _/ban reason_)
- /**unban** (use as reply to user message or by user ID)
- /**banlist** (view banned users list)
<br>

- /**addadmin** (add admin by ID, example: _/addadmin 123456789 username_) (creator-only command)
- /**rmadmin** (remove admin by ID)
- /**sudoers** (view admin list)
<br>

- /**addchat** (add chat to white list, use in chat or by ID) (creator-only command)
- /**rmchat** (remove chat from white list, use in chat or by ID)
- /**chats** (view chats list)

Docs
----

./_config/json/_

- **ChatGPT**.json : [OpenAI Chat](https://platform.openai.com/docs/api-reference/chat)
- **DallE2**.json : [OpenAI Image generation](https://beta.openai.com/docs/guides/images/usage?lang=curl)
- **StableDiffusion**.json : [Stable Diffusion text-to-image](https://api.stability.ai/docs#tag/v1alphageneration/operation/v1alpha/generation#textToImage)
- **TextToSpeech**.json : [Cloud Text-to-Speech](https://cloud.google.com/text-to-speech/docs/reference/rest/v1/text/synthesize)
