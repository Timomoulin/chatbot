package org.ldv.cb.model.dao

import org.ldv.cb.model.entity.Message
import org.springframework.data.jpa.repository.JpaRepository

interface MessageDAO : JpaRepository<Message, Long> {
}