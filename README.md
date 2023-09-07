artific_intellig_bot
--------------------

Demo: https://t.me/+siikRmY3uyE5YTBi


Download
--------

- [artific_intellig_bot-0.9.0.zip](https://github.com/Helltar/artific_intellig_bot/releases/download/0.9.0/artific_intellig_bot-0.9.0.zip) (35.7 MB)

Installation
------------

```
unzip artific_intellig_bot-0.9.0.zip && cd artific_intellig_bot-0.9.0
```

./_config/_

**bot_config.yaml**:

- token: 123:xxx ([BotFather](https://t.me/BotFather))
- username: bot_username ([BotFather](https://t.me/BotFather))
- creatorId: 1234567890 (your Telegram user-ID)

NOTE: to get your telegram id, open [@artific_intellig_bot](https://t.me/artific_intellig_bot) and run the command /**myid**

**api_keys.ini**:

- openai_key [OpenAI API Key](https://platform.openai.com/account/api-keys)
- stable_diffusion_key [Stable Diffusion API Key](https://beta.dreamstudio.ai/account) (for /**sdif**)
- google_cloud_key [Google Cloud API Key](https://console.cloud.google.com/apis/credentials) (for Text-to-Speech, audio replies)

<br>

```
java -jar artific_intellig_bot-0.9.0-all.jar
```

Commands
--------

- /**chat** - ChatGPT
- /**dalle** - DALL·E 2
- /**sdif** - Stable Diffusion
- /**dallevariations** - DALL·E 2 Variations
- /**uptime**
- /**about**

NOTE: /**dallevariations** - is not a typical command in use, run it as a reply to an image with text: "**@**"

Additional chat commands:

- /**chatctx** - view dialogue history
- /**chatrm** - clear history

For audio responses, add the **#voice** tag to your message, for example:

`Hello, how are you? #voice`

...or use the /**chatasvoice** command to globally set bot replies as voice

Admin commands
--------------

- /**enable** _commandName_ (example: _/enable chat_)
- /**disable** _commandName_ (example: _/disable dalle_)
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

<br>
<a href="https://jb.gg/OpenSourceSupport"><img src="https://resources.jetbrains.com/storage/products/company/brand/logos/IntelliJ_IDEA.png" alt="IntelliJ IDEA logo." width="32%"></a>
