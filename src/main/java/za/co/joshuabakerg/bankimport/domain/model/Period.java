package za.co.joshuabakerg.bankimport.domain.model;

import java.time.LocalDate;

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
public class Period {

    private String name;

    private LocalDate start;

    private LocalDate end;

}
