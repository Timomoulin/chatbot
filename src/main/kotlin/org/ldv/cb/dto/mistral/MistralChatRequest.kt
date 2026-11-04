package org.ldv.cb.dto.mistral

data class MistralChatRequest(
    val model: String,
    val messages: List<MistralMessage>
)