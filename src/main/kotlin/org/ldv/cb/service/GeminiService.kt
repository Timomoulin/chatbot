package org.ldv.cb.service

import org.ldv.cb.dto.gemini.GeminiChatRequest
import org.ldv.cb.dto.gemini.GeminiChatResponse
import org.ldv.cb.dto.gemini.GeminiContent
import org.ldv.cb.dto.gemini.GeminiPart
import org.ldv.cb.model.entity.ModeleIA
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class GeminiService(
    private val restClientBuilder: RestClient.Builder,

    @Value("\${gemini.api.key}")
    private val apiKey: String
) : IAService {

    override fun getFournisseur(): String = "Gemini"

    override fun envoyerPrompt(
        prompt: String,
        modele: ModeleIA
    ): String {

        // Construction du corps de la requête Gemini
        val request = GeminiChatRequest(
            contents = listOf(
                GeminiContent(
                    role = "user",
                    parts = listOf(
                        GeminiPart(text = prompt)
                    )
                )
            )
        )

        // Appel à l'API Gemini
        val response = restClientBuilder.build()
            .post()
            .uri(modele.urlAPI)
            .header("x-goog-api-key", apiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(GeminiChatResponse::class.java)

        // Extraction du texte généré
        return response
            ?.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.joinToString("\n") { it.text }
            ?: "Aucune réponse reçue de Gemini."
    }
}