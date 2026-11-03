package org.ldv.cb.model.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import java.time.LocalDateTime

@Entity
class Conversation (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null,
    var titre:String,
    var dateCreation: LocalDateTime,
    var dateDerniereModification: LocalDateTime? = null,

    @OneToMany(mappedBy = "conversation", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    var messages: MutableList<Message> = mutableListOf(),

    @ManyToOne
    @JoinColumn(name = "modele_ia_id")
    var modeleIA: ModeleIA? = null,


    ) {
}