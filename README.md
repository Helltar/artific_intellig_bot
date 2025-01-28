# AI Bot for Telegram

This bot is focused on Group Chats.

## Installation

### Docker Compose

```bash
mkdir aibot && cd aibot && \
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

First, get the following API key:

- [OpenAI API Key](https://platform.openai.com/api-keys)

Add it using the command in the bot:

- `/updatekey sk-qwerty...`

If you want to use DeepSeek for `/chat`, also add the key ([DeepSeek API Key](https://platform.deepseek.com/api_keys)) with the command:

- `/updatekey deepseek.com sk-qwerty...`

### Commands

- `/chat` - ChatGPT (gpt-4o, deepseek-chat)
- `/vision` - Describes images with GPT-4 Vision (gpt-4o)
- `/dalle` - Creates an image given a prompt (dall-e-3)
- `/dallevar` - Creates a variation of a given image (dall-e-2)
- `/asr` - Transcribes audio (whisper-1)
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

#### DeepSeek

- `/deepseekon`
- `/deepseekoff`

For example, if you enable it with the command `/deepseekon`, DeepSeek (deepseek-chat) will be used for **text** requests in the `/chat` command.
When you turn it off with `/deepseekoff`, OpenAI (gpt-4o) will be used.
By default, gpt-4o is used.

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
