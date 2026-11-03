package org.ldv.cb.model.dao

import org.ldv.cb.model.entity.ModeleIA
import org.springframework.data.jpa.repository.JpaRepository

interface ModeleIADAO : JpaRepository<ModeleIA, Long> {
}