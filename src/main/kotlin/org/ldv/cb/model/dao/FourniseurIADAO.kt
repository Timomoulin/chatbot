package org.ldv.cb.model.dao

import org.ldv.cb.model.entity.FourniseurIA
import org.springframework.data.jpa.repository.JpaRepository

interface FourniseurIADAO : JpaRepository<FourniseurIA, Long> {
}