package org.ldv.cb.model.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity
class ModeleIA (
    @OneToMany(mappedBy = "modeleIA", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    var conversations: MutableList<Conversation> = mutableListOf(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null,
    var nom:String,
    var urlAPI:String,
    var description:String,
    var cheminLogo:String,

    @ManyToOne
    @JoinColumn(name = "fourniseur_ia_id")
    var fourniseurIA: FourniseurIA? = null,

    ) {
}