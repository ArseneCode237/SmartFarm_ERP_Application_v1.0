package com.reseau_partage.core.util;

import com.reseau_partage.core.entities.Batiment;
import com.reseau_partage.core.entities.Enclos;
import com.reseau_partage.core.entities.Porcherie;
import com.reseau_partage.core.entities.Poulailler;
import com.reseau_partage.core.entities.Structure;

/**
 * Centralise la lecture de la capacité maximale d'accueil (nombre d'animaux)
 * d'une structure. Retourne {@code null} lorsque le type de structure ne définit
 * pas de capacité d'accueil exprimée en nombre d'animaux (Étang, Entrepôt,
 * Parcelle...).
 */
public final class CapaciteStructure {

    private CapaciteStructure() {
    }

    public static Integer capaciteMaxAnimaux(Structure structure) {
        if (structure instanceof Batiment b) {
            return b.getCapaciteMaxAnimaux();
        }
        if (structure instanceof Enclos e) {
            return e.getCapaciteMaxAnimaux();
        }
        if (structure instanceof Poulailler p) {
            return p.getCapaciteMaxAnimaux();
        }
        if (structure instanceof Porcherie pc) {
            return pc.getCapaciteMaxAnimaux();
        }
        return null;
    }
}
