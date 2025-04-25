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

### Commands

- `/chat` - ChatGPT (gpt-4.1)
- `/vision` - Describes images with Vision (gpt-4.1)
- `/dalle` - Creates an image given a prompt (dall-e-3)
- `/dallevar` - Creates a variation of a given image (dall-e-2)

### Additional Chat Commands

- `/chatctx` - View dialogue history
- `/chatrm` - Clear history

### Admin Commands

#### Change Command State

- `/enable commandName` (Example: `/enable chat`)
- `/disable commandName` (Example: `/disable dalle`)

> **NOTE:** Run `/enable` or `/disable` with no arguments to view supported commands.

#### Ban User

- `/ban` (Use as reply to a user message, Example: `/ban reason`)
- `/unban` (Use as reply to user message or by user ID)
- `/banlist`

#### Slowmode

- `/slowmode` (Default: 10 requests per hour per user)

#### Manage Admins

- `/addadmin` (Add admin by ID, Example: `/addadmin 123456789 username`)
- `/rmadmin` (Remove admin by ID)
- `/sudoers` (View an admin list)

#### Manage Chats

- `/addchat` (Add chat to allowlist, Use in chat or by ID)
- `/rmchat` (Remove chat from allowlist, Use in chat or by ID)
- `/chats` (View a chat list)

<br>
<a href="https://jb.gg/OpenSourceSupport"><img src="https://resources.jetbrains.com/storage/products/company/brand/logos/IntelliJ_IDEA.png" alt="IntelliJ IDEA logo." width="32%"></a>
