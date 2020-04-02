/*
 * Copyright Notice
 * ================
 * This file contains proprietary information of Discovery Health.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 2018
 */
package za.co.joshuabakerg.bankimport.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.joshuabakerg.bankimport.domain.model.Category;
import za.co.joshuabakerg.bankimport.domain.model.CategoryMatcher;
import za.co.joshuabakerg.bankimport.domain.model.Group;

/**
 * Configuration properties specific to this application are placed here for application consumption and reference.
 */
@Component
@ConfigurationProperties("app")
@Validated
@Getter
@Setter
@NoArgsConstructor
public class ApplicationProperties {

    private Collection<CategoryMatcher> categoryMatchers;

    private Collection<Group> groups;

    private Collection<Category> categories;


}
