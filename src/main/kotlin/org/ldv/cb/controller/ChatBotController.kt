package org.ldv.cb.controller

import org.ldv.cb.config.IAServiceFactory
import org.ldv.cb.model.dao.ConversationDAO
import org.ldv.cb.model.dao.MessageDAO
import org.ldv.cb.model.dao.ModeleIADAO
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
    private val iaServiceFactory: IAServiceFactory,
    private val conversationDAO: ConversationDAO,
    private val messageDAO: MessageDAO,
    private val modeleIADAO: ModeleIADAO
) {

    // Page d'accueil : liste des conversations
    @GetMapping("/")
    fun accueil(model: Model): String {

        model.addAttribute(
            "conversations",
            conversationDAO.findAllByOrderByDateDerniereModificationDesc()
        )

        model.addAttribute("modeles", modeleIADAO.findAll())
        model.addAttribute("conversation", null)
        model.addAttribute("messages", emptyList<Message>())

        return "chat"
    }

    // Création d'une nouvelle conversation
    @PostMapping("/conversations")
    fun creerConversation(
        @RequestParam prompt: String,
        @RequestParam idModele: Long
    ): String {

        // Récupération du modèle sélectionné
        val modele = modeleIADAO.findById(idModele)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Modèle IA introuvable"
                )
            }

        // Création de la conversation
        val conversation = Conversation(
            titre = prompt.take(50),
            dateCreation = LocalDateTime.now(),
            modeleIA = modele
        )

        val conversationEnregistree =
            conversationDAO.save(conversation)

        // Enregistrement du premier message
        val messageUtilisateur = Message(
            contenu = prompt,
            dateCreation = LocalDateTime.now(),
            typeMessage = TypeMessage.UTILISATEUR,
            conversation = conversationEnregistree
        )

        messageDAO.save(messageUtilisateur)

        try {
            // Sélection automatique du service
            val iaService = iaServiceFactory.getService(modele)

// Appel du fournisseur correspondant
            val reponse = iaService.envoyerPrompt(prompt, modele)
            // Utilisation du modèle sélectionné


            val messageAssistant = Message(
                contenu = reponse,
                dateCreation = LocalDateTime.now(),
                typeMessage = TypeMessage.ASSISTANT,
                conversation = conversationEnregistree
            )

            messageDAO.save(messageAssistant)

        } catch (e: Exception) {
            println("Erreur Mistral : ${e.message}")
        }

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
        @RequestParam prompt: String,

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
var modele = conversation.modeleIA!!
            // Sélection automatique du service
            val iaService = iaServiceFactory.getService(modele)

// Appel du fournisseur correspondant
            val reponse = iaService.envoyerPrompt(prompt, modele)



            val messageAssistant = Message(
                contenu = reponse,
                dateCreation = LocalDateTime.now(),
                typeMessage = TypeMessage.ASSISTANT,
                conversation = conversation
            )

            messageDAO.save(messageAssistant)

        } catch (e: Exception) {
            println("Erreur lors de l'appel à ${conversation.modeleIA!!.nom} : ${e.message}")
        }

        conversation.dateDerniereModification = LocalDateTime.now()
        conversationDAO.save(conversation)

        return "redirect:/conversations/$id"
    }
}