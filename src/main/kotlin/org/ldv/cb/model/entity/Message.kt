package org.ldv.cb.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
class Message (
    @ManyToOne
    @JoinColumn(name = "conversation_id")
    var conversation: Conversation? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null,
    @Column(columnDefinition = "TEXT", nullable = false)
    var contenu: String,
    var dateCreation: LocalDateTime,
    var typeMessage: TypeMessage,
){
}