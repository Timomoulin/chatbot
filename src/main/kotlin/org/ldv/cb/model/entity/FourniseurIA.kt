package org.ldv.cb.model.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class FourniseurIA (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null,
    var nom: String,
    var cheminLogo:String,
    var description: String,
    @OneToMany(mappedBy = "fourniseurIA", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    var modeleIAS: MutableList<ModeleIA> = mutableListOf(),
    ) {
}