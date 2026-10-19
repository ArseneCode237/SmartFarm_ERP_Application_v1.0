package com.reseau_partage.animaux.dto.bovin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BovinMotifRequest(@NotBlank @Size(max = 200) String motif) {
}
