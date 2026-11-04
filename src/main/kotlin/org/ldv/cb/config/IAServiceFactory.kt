package org.ldv.cb.config

import org.ldv.cb.model.entity.ModeleIA
import org.ldv.cb.service.IAService
import org.springframework.stereotype.Component

@Component
class IAServiceFactory(
    private val services: List<IAService>
) {

    fun getService(modele: ModeleIA): IAService {

        val fournisseur = modele.fourniseurIA?.nom
            ?: throw IllegalArgumentException(
                "Aucun fournisseur associé au modèle"
            )

        return services.firstOrNull {
            it.getFournisseur().equals(
                fournisseur,
                ignoreCase = true
            )
        } ?: throw IllegalArgumentException(
            "Aucun service disponible pour $fournisseur"
        )
    }
}