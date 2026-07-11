package com.example.roombooking.acceptance;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

/**
 * Delad Spring-kontext för alla Cucumber-scenarier. Persistensscenarierna
 * (persistens.feature) behöver en riktig Postgres - inte mockad, se README -
 * så vi startar en Testcontainers-container och pekar datasourcen mot den.
 */
@CucumberContextConfiguration
@SpringBootTest
@ContextConfiguration(initializers = CucumberSpringConfiguration.Initializer.class)
@Import(CucumberSpringConfiguration.FastKlockaFörTester.class)
public class CucumberSpringConfiguration {

    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16");

    static {
        POSTGRES.start();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + POSTGRES.getJdbcUrl(),
                    "spring.datasource.username=" + POSTGRES.getUsername(),
                    "spring.datasource.password=" + POSTGRES.getPassword()
            ).applyTo(context.getEnvironment());
        }
    }

    /**
     * Ersätter produktionens systemklocka med en fast söndagsklocka, så att
     * persistensscenariernas måndags-/tisdagsbokningar inte råkar avslås av
     * "bokning bakåt i tiden"-kontrollen beroende på när testerna körs.
     */
    @TestConfiguration
    static class FastKlockaFörTester {
        @Bean
        @Primary
        Clock klocka() {
            return Clock.fixed(Instant.parse("2024-01-07T00:00:00Z"), ZoneOffset.UTC);
        }
    }
}