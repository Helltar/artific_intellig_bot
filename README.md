artific_intellig_bot
--------------------

./_config_:

- **bot_token**.txt : [BotFather](https://t.me/BotFather)
- **bot_username**.txt : [BotFather](https://t.me/BotFather)
- **openai_token**.txt : [OpenAI](https://beta.openai.com/account/api-keys)
- **stable_diffusion_token**.txt : [Stable Diffusion](https://beta.dreamstudio.ai/membership?tab=apiKeys)
- **text_to_speech_token.txt** : [Text-to-Speech](https://console.cloud.google.com/apis/credentials) (**API Key**)
- **sudoers**.txt : Superuser Ids List (ID per line)

`./gradlew run`

### Commands

- /**chat** - ChatGPT
- /**dalle** - DALL·E 2
- /**sdif** - Stable Diffusion

Admin commands:

- /**enable** _commandName_
- /**disable** _commandName_
- /**chat_as_text** (ChatGPT answers as text)
- /**chat_as_voice** (ChatGPT answers as voice)

### Docs

./_config/json_:

- **ChatGPT**.json : [OpenAI](https://beta.openai.com/playground/p/default-chat?model=text-davinci-003)
- **DallE2**.json : [OpenAI](https://beta.openai.com/docs/guides/images/usage?lang=curl)
- **StableDiffusion**.json : [Stable Diffusion](https://api.stability.ai/docs#tag/v1alphageneration/operation/v1alpha/generation#textToImage)
