package za.co.joshuabakerg.bankimport.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.bson.Document;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

/**
 * @author Joshua Baker on 08/04/2020
 */
@Component
@AllArgsConstructor
public final class DocumentMapper {

    private final ObjectMapper objectMapper;

    public List<Document> map(final Collection items) {
        return objectMapper.convertValue(items, new TypeReference<List<Document>>() {
        }).stream()
                .map(this::mapId).collect(Collectors.toList());
    }

    private Document mapId(Document document) {
        final Object id = document.get("id");
        document.put("_id", id);
        document.remove("id");
        return document;
    }

}
