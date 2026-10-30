package org.ldv.cb.dto

data class MistralChatRequest(
    val model: String,
    val messages: List<MistralMessage>
)