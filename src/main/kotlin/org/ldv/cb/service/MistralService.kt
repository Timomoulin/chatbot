package org.ldv.cb.service

import org.ldv.cb.dto.MistralChatRequest
import org.ldv.cb.dto.MistralChatResponse
import org.ldv.cb.dto.MistralMessage
import org.ldv.cb.model.entity.ModeleIA
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class MistralService(
    private val restClientBuilder: RestClient.Builder,

    @Value("\${mistral.api.key}")
    private val apiKey: String
) {

    fun envoyerPrompt(prompt: String, modele: ModeleIA): String {

        val request = MistralChatRequest(
            model = modele.nom,
            messages = listOf(
                MistralMessage(
                    role = "user",
                    content = prompt
                )
            )
        )

        val response = restClientBuilder.build()
            .post()
            .uri(modele.urlAPI)
            .header("Authorization", "Bearer $apiKey")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(MistralChatResponse::class.java)

        return response
            ?.choices
            ?.firstOrNull()
            ?.message
            ?.content
            ?: "Aucune réponse reçue de Mistral."
    }
}