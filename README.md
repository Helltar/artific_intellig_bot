AIBot for Telegram
--------------------

Demo: https://t.me/+siikRmY3uyE5YTBi

Installation
------------

- CREATOR_ID: your Telegram user-ID
- BOT_TOKEN: [BotFather](https://t.me/BotFather)
- BOT_USERNAME: [BotFather](https://t.me/BotFather) (example: artific_intellig_bot)

NOTE: to get your Telegram ID, you can use [@artific_intellig_bot](https://t.me/artific_intellig_bot) by entering the command /**myid**

### Docker

```bash
docker run --rm -d \
  --name aibot \
  -e CREATOR_ID=12345 \
  -e BOT_TOKEN=123:xxx \
  -e BOT_USERNAME=name_bot \
  -v aibot_data:/app/data \
  ghcr.io/helltar/aibot:latest
```

### Or run without Docker

- [artific_intellig_bot-0.9.8.zip](https://github.com/Helltar/artific_intellig_bot/releases/download/0.9.8/artific_intellig_bot-0.9.8.zip)

```bash
unzip artific_intellig_bot-0.9.8.zip && cd artific_intellig_bot-0.9.8/bin
```

- Update the environment variables in the **.env** file
- Install https://ffmpeg.org on your system (for **/asr** command)

```bash
./artific_intellig_bot # artific_intellig_bot.bat for windows
```

Usage
-----

First obtain API keys:

- [OpenAI API Key](https://platform.openai.com/api-keys)
- [Stable Diffusion API Key](https://platform.stability.ai/account/keys)

and add them using the command in the bot:

- /**_updatekey_** _openai.com sk-qwerty_
- /**_updatekey_** _stability.ai sk-qwerty_

### Commands

- /**chat** - ChatGPT
- /**vision** - GPT-4 Vision
- /**dalle** - DALL·E 2
- /**sdif** - Stable Diffusion
- /**dallevar** - DALL·E 2 Variations
- /**asr** - Automatic Speech Recognition (openai-Whisper)
- /**about**

Additional chat commands:

- /**chatctx** - view dialogue history
- /**chatrm** - clear history

For audio responses (TTS), add the **#voice** tag to your message, for example:

`Hello, how are you? #voice`

### Admin commands

- /**enable** _commandName_ (example: _/enable chat_)
- /**disable** _commandName_ (example: _/disable dalle_)

NOTE: run _/enable_ or _/disable_ with no arguments for view supported commands
<br>
<br>

- /**ban** (use as reply to user message, example: _/ban reason_)
- /**unban** (use as reply to user message or by user ID)
- /**banlist**
<br>

- /**slowmode** (use as reply to user message, example: _/slowmode 5_) (requests per. hour)
- /**slowmodeoff** (use as reply to user message or by user ID)
- /**slowmodelist**
<br>

- /**addadmin** (add admin by ID, example: _/addadmin 123456789 username_) (creator-only command)
- /**rmadmin** (remove admin by ID)
- /**sudoers** (view admin list, run it only in private chat)
<br>

- /**addchat** (add chat to white list, use in chat or by ID) (creator-only command)
- /**rmchat** (remove chat from white list, use in chat or by ID)
- /**chats** (view chats list, run it only in private chat)

<br>
<a href="https://jb.gg/OpenSourceSupport"><img src="https://resources.jetbrains.com/storage/products/company/brand/logos/IntelliJ_IDEA.png" alt="IntelliJ IDEA logo." width="32%"></a>
