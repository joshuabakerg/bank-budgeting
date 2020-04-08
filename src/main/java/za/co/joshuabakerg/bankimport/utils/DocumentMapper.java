package za.co.joshuabakerg.bankimport.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.bson.Document;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Joshua Baker on 08/04/2020
 */
public final class DocumentMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static List<Document> map(final Collection items) {
        return OBJECT_MAPPER.convertValue(items, new TypeReference<List<Document>>() {
        }).stream()
                .map(DocumentMapper::mapId).collect(Collectors.toList());
    }

    private static Document mapId(Document document) {
        final Object id = document.get("id");
        document.put("_id", id);
        document.remove("id");
        return document;
    }

    private DocumentMapper() {
    }

}
