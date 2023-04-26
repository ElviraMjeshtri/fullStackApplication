package com.elacode;

import com.github.javafaker.Faker;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

@Testcontainers
public abstract class AbstractTestcontainers {

    @BeforeAll
    static void beforeAll() {
        Flyway flyway = Flyway
                .configure()
                .dataSource(
                        postgresSQLContainer.getJdbcUrl(),
                        postgresSQLContainer.getUsername(),
                        postgresSQLContainer.getPassword()
                ).load();
        flyway.migrate();
    }

    @Container
    protected static final PostgreSQLContainer<?> postgresSQLContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("elacode-dao-unit-test")
                    .withUsername("elacode")
                    .withPassword("elacodepassword");


    @DynamicPropertySource
    protected static void registerDataSourceProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add(
                "spring.datasource.url",
                postgresSQLContainer::getJdbcUrl
        );
        dynamicPropertyRegistry.add(
                "spring.datasource.username",
                postgresSQLContainer::getUsername
        );
        dynamicPropertyRegistry.add(
                "spring.datasource.password",
                postgresSQLContainer::getPassword
        );
    }

    private static DataSource getDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(postgresSQLContainer.getDriverClassName())
                .url(postgresSQLContainer.getJdbcUrl())
                .username(postgresSQLContainer.getUsername())
                .password(postgresSQLContainer.getPassword())
                .build();
    }

    protected static JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }

    protected static final Faker FAKER = new Faker();
}
