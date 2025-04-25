package com.helltar.aibot.exceptions

class ImageTooLargeException(limitBytes: Int) : Exception("image size limit: $limitBytes bytes")
