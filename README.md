# AI Bot for Telegram

- This bot is focused on Group Chats.
- [Demo](https://t.me/+siikRmY3uyE5YTBi)

## Installation

### Docker Compose

```bash
mkdir aibot && cd aibot
wget https://raw.githubusercontent.com/Helltar/artific_intellig_bot/master/{.env,compose.yaml}
```

Edit the **.env** file with the following:

- `CREATOR_ID`: Your Telegram user-ID (You can use [@artific_intellig_bot](https://t.me/artific_intellig_bot) by entering the command `/myid`)
- `BOT_TOKEN`: Obtain from [BotFather](https://t.me/BotFather)
- `BOT_USERNAME`: Obtain from [BotFather](https://t.me/BotFather) (Example: artific_intellig_bot)

Also include PostgreSQL connection data.

```bash
docker compose up -d
```

## Usage

### Obtain API Keys

First, obtain the following API keys:

- [OpenAI API Key](https://platform.openai.com/api-keys)
- [Stable Diffusion API Key](https://platform.stability.ai/account/keys)

Add them using the command in the bot:

- `/updatekey openai.com sk-qwerty`
- `/updatekey stability.ai sk-qwerty`

### Commands

- `/chat` - ChatGPT
- `/vision` - GPT-4 Vision
- `/dalle` - DALL·E 2
- `/sdif` - Stable Diffusion
- `/dallevar` - DALL·E 2 Variations
- `/asr` - Automatic Speech Recognition (openai-Whisper)
- `/privacy`
- `/about`

### Additional Chat Commands

- `/chatctx` - View dialogue history
- `/chatrm` - Clear history

For audio responses (TTS), add the **#voice** tag to your message, for example:

```text
Hello, how are you? #voice
```

### Admin Commands

#### Change Command State

- `/enable commandName` (Example: `/enable chat`)
- `/disable commandName` (Example: `/disable dalle`)

> **NOTE:** Run `/enable` or `/disable` with no arguments to view supported commands.

#### Ban User

- `/ban` (Use as reply to user message, Example: `/ban reason`)
- `/unban` (Use as reply to user message or by user ID)
- `/banlist`

#### Slowmode for User

- `/slowmode` (Use as reply to user message, Example: `/slowmode 5`) (Requests per hour)
- `/slowmodeoff` (Use as reply to user message or by user ID)
- `/slowmodelist`

#### Global Slowmode

(Default: 10 requests per hour per user)

- `/globalslowmode`

#### Manage Admins

- `/addadmin` (Add admin by ID, Example: `/addadmin 123456789 username`)
- `/rmadmin` (Remove admin by ID)
- `/sudoers` (View admin list)

#### Manage Chats

- `/addchat` (Add chat to whitelist, Use in chat or by ID)
- `/rmchat` (Remove chat from whitelist, Use in chat or by ID)
- `/chats` (View chats list)

#### Other

- `/updateprivacy` (Update bot privacy policy `/privacy`)

<br>
<a href="https://jb.gg/OpenSourceSupport"><img src="https://resources.jetbrains.com/storage/products/company/brand/logos/IntelliJ_IDEA.png" alt="IntelliJ IDEA logo." width="32%"></a>
