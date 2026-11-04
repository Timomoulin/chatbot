package org.ldv.cb.dto.gemini

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiChatResponse(
    val candidates: List<GeminiCandidate> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiCandidate(
    val content: GeminiContent? = null
)