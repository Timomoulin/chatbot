package org.ldv.cb.model.dao

import org.ldv.cb.model.entity.Conversation
import org.springframework.data.jpa.repository.JpaRepository

interface ConversationDAO : JpaRepository<Conversation, Long> {
}