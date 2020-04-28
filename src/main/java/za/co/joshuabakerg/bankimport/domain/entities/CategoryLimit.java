package za.co.joshuabakerg.bankimport.domain.entities;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Joshua Baker on 08/04/2020
 */
@Document(collection = "category-limit")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryLimit {

    @Id
    private ObjectId id;

    private String userId;

    private String categoryId;

    private BigDecimal limit;

}
