package za.co.joshuabakerg.bankimport.controllers.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Joshua Baker on 18/12/2019
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtRequest {

    private String username;
    private String password;

}
