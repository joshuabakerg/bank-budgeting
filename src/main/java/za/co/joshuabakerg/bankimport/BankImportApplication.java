package za.co.joshuabakerg.bankimport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import lombok.AllArgsConstructor;

@EnableWebSecurity
@SpringBootApplication
@AllArgsConstructor
public class BankImportApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankImportApplication.class, args);
    }

}
