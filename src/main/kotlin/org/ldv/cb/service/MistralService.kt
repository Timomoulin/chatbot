package org.ldv.cb.service

import org.ldv.cb.dto.MistralChatRequest
import org.ldv.cb.dto.MistralChatResponse
import org.ldv.cb.dto.MistralMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient


@Service
class MistralService(

    private val restClientBuilder: RestClient.Builder,

    @Value("\${mistral.api.url}")
    private val apiUrl: String,

    @Value("\${mistral.api.key}")
    private val apiKey: String,

    @Value("\${mistral.api.model}")
    private val model: String
) {

    fun envoyerPrompt(prompt: String): String {

        // Création de la requête
        val request = MistralChatRequest(
            model = model,
            messages = listOf(
                MistralMessage(
                    role = "user",
                    content = prompt
                )
            )
        )

        // Création du client HTTP
        val restClient = restClientBuilder.build()

        // Envoi de la requête à Mistral
        val response = restClient.post()
            .uri(apiUrl)
            .header("Authorization", "Bearer $apiKey")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(MistralChatResponse::class.java)

        // Récupération de la réponse
        return response
            ?.choices
            ?.firstOrNull()
            ?.message
            ?.content
            ?: "Aucune réponse reçue de Mistral."
    }
}