package pl.woelke.gus_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GUS API") // Tytuł API
                        .version("1.0.0") // Wersja API
                        .description("API do integracji z polskim serwisem GUS") // Opis API
                        .contact(new Contact()
                                .name("Twoje Imię i Nazwisko")
                                .url("http://twoja-strona.pl")
                                .email("twojemail@example.com") // Kontakt
                        )
                );
    }


}
