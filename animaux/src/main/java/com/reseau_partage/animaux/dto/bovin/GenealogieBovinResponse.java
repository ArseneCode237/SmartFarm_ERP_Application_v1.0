package com.reseau_partage.animaux.dto.bovin;

import java.util.List;

public record GenealogieBovinResponse(
        Long animalId,
        AnimalBovinLieResponse mere,
        AnimalBovinLieResponse pere,
        List<AnimalBovinLieResponse> descendants) {
}
