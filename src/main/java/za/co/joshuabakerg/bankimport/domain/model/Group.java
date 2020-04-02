package za.co.joshuabakerg.bankimport.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Joshua Baker on 05/03/2020
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Group {

    private String id;

    private String name;

}
