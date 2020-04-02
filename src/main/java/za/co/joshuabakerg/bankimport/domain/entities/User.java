package za.co.joshuabakerg.bankimport.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Joshua Baker on 17/12/2019
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String id;

    private String email;

    private String name;

    private String surname;

    private String password;

    private String roles;


}
