package de.hsrm.orchestrationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@PropertySources({
        @PropertySource("classpath:application.properties"),
        @PropertySource("classpath:/config/orchestrator.properties")
})
public class OrchestrationsystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrchestrationsystemApplication.class, args);
    }

}
