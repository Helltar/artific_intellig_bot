artific_intellig_bot
--------------------

Demo: https://t.me/+siikRmY3uyE5YTBi


Download
--------

- [artific_intellig_bot-0.9.5.zip](https://github.com/Helltar/artific_intellig_bot/releases/download/0.9.5/artific_intellig_bot-0.9.5.zip) (71.4 MB)

Installation
------------

```bash
unzip artific_intellig_bot-0.9.5.zip && cd artific_intellig_bot-0.9.5/bin
```

./_config/_

**bot_config.yaml**:

- token: 123:xxx ([BotFather](https://t.me/BotFather))
- username: bot_username ([BotFather](https://t.me/BotFather))
- creatorId: 1234567890 (your Telegram user-ID)

NOTE: to get your telegram id, open [@artific_intellig_bot](https://t.me/artific_intellig_bot) and run the command /**myid**

Install and configure **Google Cloud CLI** for text-to-speech audio responses:

- https://cloud.google.com/sdk/docs/install

```bash
gcloud init
```
```bash
gcloud auth application-default login
```

Check:

```bash
gcloud auth application-default print-access-token
```

Install **ffmpeg** for **/asr** command support:

- https://ffmpeg.org/

Done, run:

```bash
./artific_intellig_bot # or artific_intellig_bot.bat for windows
```

Just got to get the api keys:

- [OpenAI API Key](https://platform.openai.com/account/api-keys)
- [Stable Diffusion API Key](https://platform.stability.ai/account/keys)

and add them with the /**updatekey** command:

- /**_updatekey_** _openai.com sk-qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxc_
- /**_updatekey_** _stability.ai sk-qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxc_

Commands
--------

- /**chat** - ChatGPT
- /**dalle** - DALL·E 2
- /**sdif** - Stable Diffusion
- /**dallevariations** - DALL·E 2 Variations
- /**asr** - Automatic Speech Recognition (openai-Whisper)
- /**uptime**
- /**about**

NOTE: /**dallevariations** - is not a typical command in use, run it as a reply to an image with text: "**@**"

Additional chat commands:

- /**chatctx** - view dialogue history
- /**chatrm** - clear history

For audio responses, add the **#voice** tag to your message, for example:

`Hello, how are you? #voice`

Admin commands
--------------

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
