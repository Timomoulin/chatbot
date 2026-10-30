package org.ldv.cb.controller

import org.ldv.cb.model.dao.ConversationDAO
import org.ldv.cb.model.dao.MessageDAO
import org.ldv.cb.model.entity.Conversation
import org.ldv.cb.model.entity.Message
import org.ldv.cb.model.entity.TypeMessage
import org.ldv.cb.service.MistralService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Controller
class ChatController(
    private val mistralService: MistralService,
    private val conversationDAO: ConversationDAO,
    private val messageDAO: MessageDAO
) {

    // Page d'accueil : liste des conversations
    @GetMapping("/")
    fun accueil(model: Model): String {

        val conversations =
            conversationDAO.findAllByOrderByDateDerniereModificationDesc()

        model.addAttribute("conversations", conversations)

        return "chat"
    }

    // Création d'une nouvelle conversation
    @PostMapping("/conversations")
    fun creerConversation(
        @RequestParam prompt: String
    ): String {

        // Création de la conversation
        val conversation = Conversation(
            titre = prompt.take(50),
            dateCreation = LocalDateTime.now()
        )

        val conversationEnregistree =
            conversationDAO.save(conversation)

        // Enregistrement du message utilisateur
        val messageUtilisateur = Message(
            contenu = prompt,
            dateCreation = LocalDateTime.now(),
            typeMessage = TypeMessage.UTILISATEUR,
            conversation = conversationEnregistree
        )

        messageDAO.save(messageUtilisateur)

        // Envoi du prompt à Mistral
        try {

            val reponse = mistralService.envoyerPrompt(prompt)

            // Enregistrement de la réponse
            val messageAssistant = Message(
                contenu = reponse,
                dateCreation = LocalDateTime.now(),
                typeMessage = TypeMessage.ASSISTANT,
                conversation = conversationEnregistree
            )

            messageDAO.save(messageAssistant)

        } catch (e: Exception) {
            // Pour ce premier test, on se contente de journaliser l'erreur.
            println("Erreur lors de l'appel à Mistral : ${e.message}")
        }

        // Mise à jour de la date de modification
        conversationEnregistree.dateDerniereModification =
            LocalDateTime.now()

        conversationDAO.save(conversationEnregistree)

        return "redirect:/conversations/${conversationEnregistree.id}"
    }

    // Affichage d'une conversation
    @GetMapping("/conversations/{id}")
    fun afficherConversation(
        @PathVariable id: Long,
        model: Model
    ): String {

        val conversation = conversationDAO.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

        model.addAttribute(
            "conversations",
            conversationDAO.findAllByOrderByDateDerniereModificationDesc()
        )

        model.addAttribute("conversation", conversation)
        model.addAttribute(
            "messages",
            conversation.messages.sortedBy { it.dateCreation }
        )

        return "chat"
    }

    // Envoi d'un nouveau message dans une conversation existante
    @PostMapping("/conversations/{id}/messages")
    fun envoyerMessage(
        @PathVariable id: Long,
        @RequestParam prompt: String
    ): String {

        val conversation = conversationDAO.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

        // Enregistrement du message utilisateur
        val messageUtilisateur = Message(
            contenu = prompt,
            dateCreation = LocalDateTime.now(),
            typeMessage = TypeMessage.UTILISATEUR,
            conversation = conversation
        )

        messageDAO.save(messageUtilisateur)

        try {

            val reponse = mistralService.envoyerPrompt(prompt)

            val messageAssistant = Message(
                contenu = reponse,
                dateCreation = LocalDateTime.now(),
                typeMessage = TypeMessage.ASSISTANT,
                conversation = conversation
            )

            messageDAO.save(messageAssistant)

        } catch (e: Exception) {
            println("Erreur lors de l'appel à Mistral : ${e.message}")
        }

        conversation.dateDerniereModification = LocalDateTime.now()
        conversationDAO.save(conversation)

        return "redirect:/conversations/$id"
    }
}