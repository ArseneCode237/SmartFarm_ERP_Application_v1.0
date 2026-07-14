package com.reseau_partage.auth.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Accepte un tableau JSON ou une chaine de valeurs separees par des virgules. */
public class CommaSeparatedListDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        List<String> values = new ArrayList<>();

        if (node.isArray()) {
            for (JsonNode item : node) {
                if (!item.isTextual()) {
                    throw context.weirdStringException(item.toString(), String.class,
                            "Chaque valeur doit etre une chaine de caracteres.");
                }
                addValues(values, item.asText());
            }
            return values;
        }

        if (node.isTextual()) {
            addValues(values, node.asText());
            return values;
        }

        if (node.isNull()) {
            return List.of();
        }

        throw context.weirdStringException(node.toString(), List.class,
                "Le champ doit etre un tableau ou une chaine separee par des virgules.");
    }

    private void addValues(List<String> values, String input) {
        for (String value : input.split(",")) {
            values.add(value.trim());
        }
    }
}
