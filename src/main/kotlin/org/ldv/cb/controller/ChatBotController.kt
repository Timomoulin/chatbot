package org.ldv.cb.controller

import org.ldv.cb.service.MistralService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ChatController(
    private val mistralService: MistralService
) {

    @GetMapping("/")
    fun accueil(): String {
        return "chat"
    }

    @PostMapping("/chat")
    fun envoyerPrompt(
        @RequestParam prompt: String,
        model: Model
    ): String {

        model.addAttribute("prompt", prompt)

        try {
            val reponse = mistralService.envoyerPrompt(prompt)
            model.addAttribute("reponse", reponse)

        } catch (e: Exception) {
            model.addAttribute(
                "erreur",
                "Une erreur est survenue lors de la communication avec Mistral."
            )
        }

        return "chat"
    }
}