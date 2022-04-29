package me.sonam.catalog;

import io.r2dbc.spi.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@org.springframework.boot.autoconfigure.SpringBootApplication
public class SpringBootApplication {
    private static final Logger LOG = LoggerFactory.getLogger(SpringBootApplication.class);

    @Value("${trustOrigin}")
    private String trustOrigin;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApplication.class, args);
    }

    @Bean()
    ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        // This will create our database table and schema
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));

        // This will drop our table after we are done so we can have a fresh start next run
        //initializer.setDatabaseCleaner(new ResourceDatabasePopulator(new ClassPathResource("cleanup.sql")));
        return initializer;
    }


    @Configuration
    @EnableWebFlux
    public class CorsGlobalConfiguration implements WebFluxConfigurer {

        @Override
        public void addCorsMappings(CorsRegistry corsRegistry) {
            corsRegistry.addMapping("/**")
                    .allowedOrigins(trustOrigin)
                    .allowedMethods("GET", "POST", "PUT")
                    .maxAge(3600);
        }
    }
}
