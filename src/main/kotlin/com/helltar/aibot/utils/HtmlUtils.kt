package com.helltar.aibot.utils

object HtmlUtils {

    fun buildStyledHtmlPage(title: String, body: String): String =
        """
        <!DOCTYPE html>
        <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">

                <title>$title</title>

                <style>
                    :root {
                        font-family: system-ui, -apple-system, sans-serif;
                        line-height: 1.6;
                        color-scheme: light dark;
                    }

                    body {
                        max-width: 750px;
                        margin: 0 auto;
                        padding: 20px;
                        color: #333;
                        background-color: #fff;
                        white-space: pre-wrap;
                        word-wrap: break-word;
                    }

                    h1, h2, h3, p, ul, ol, pre, blockquote {
                        margin-top: 0;
                        margin-bottom: 0;
                    }

                    hr {
                        border: 0;
                        border-top: 1px solid #ccc;
                    }

                    pre {
                        background-color: #f4f4f4;
                        padding: 10px;
                        border-radius: 5px;
                        border: 1px solid #ddd;
                        white-space: pre;
                        overflow-x: auto;
                    }

                    code {
                        font-family: monospace;
                        background-color: #f4f4f4;
                        padding: 2px 4px;
                        border-radius: 3px;
                    }

                    a {
                        color: #007bff;
                    }

                    @media (prefers-color-scheme: dark) {
                        body { background-color: #1a1a1a; color: #ddd; }
                        pre, code { background-color: #2b2b2b; border-color: #444; }
                        hr { border-top-color: #444; }
                        a { color: #66b0ff; }
                    }
                </style>
            </head>
            <body><div>$body</div></body>
        </html>
       """.trimIndent()
}
