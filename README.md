artific_intellig_bot
--------------------

Demo: https://t.me/+siikRmY3uyE5YTBi

<br>

./_config/_

- **bot_token**.txt : [BotFather](https://t.me/BotFather)
- **sudoers**.txt : List of superuser IDs (ID per line)
- **chats_white_list**.txt : Chat IDs where and only where commands are available, if empty - commands are available to everyone (ID per line)

./_config/api_keys.ini_

- **openai_key** : [OpenAI API Key](https://beta.openai.com/account/api-keys)
- **stable_diffusion_key** : [Stable Diffusion API Key](https://beta.dreamstudio.ai/membership?tab=apiKeys)
- **google_cloud_key** : [Google Cloud API Key](https://console.cloud.google.com/apis/credentials)

<br>

```
./gradlew run
```

### Commands

- /**chat** - ChatGPT
- /**dalle** - DALL·E 2
- /**sdif** - Stable Diffusion

Admin commands:

- /**enable** _commandName_
- /**disable** _commandName_

- /**chat_as_text** (ChatGPT answers as text)
- /**chat_as_voice** (ChatGPT answers as voice)

- /**ban_user** (use as reply to user message)
- /**unban_user** (use as reply to user message)
- /**ban_list** (view banned users list)

### Docs

./_config/json/_

- **ChatGPT**.json : [OpenAI Chat](https://beta.openai.com/playground/p/default-chat?model=text-davinci-003)
- **DallE2**.json : [OpenAI Image generation](https://beta.openai.com/docs/guides/images/usage?lang=curl)
- **StableDiffusion**.json : [Stable Diffusion text-to-image](https://api.stability.ai/docs#tag/v1alphageneration/operation/v1alpha/generation#textToImage)
- **TextToSpeech**.json : [Cloud Text-to-Speech](https://cloud.google.com/text-to-speech/docs/reference/rest/v1/text/synthesize)
