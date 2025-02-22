package pl.woelke.gus_api.config;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

/**
 * @author Created by Krzysztof Woelke on 16.02.2025
 * Configuration class for creating and customizing a WebClient bean.
 * This class configures a WebClient instance to interact with an external GUS SOAP API.
 * It sets up a custom Reactor Netty HttpClient that uses SSL with a trust manager
 * allowing insecure certificates (intended for testing purposes only).
 * The WebClient is configured with a base URL and some default HTTP headers
 * for appropriate communication with the external service. (GUS API doc requiments)
 */
@Configuration
public class WebClientConfig {

    /**
     * @return a configured {@link WebClient} instance that can communicate with the specified external SOAP API endpoint.
     * Configuration Reactor NEtty HttpClient with SSL certification
     */
    @Bean
    public WebClient webClient(@Value("${gus.soap.url}") String soapUrl) {

        HttpClient httpClient = HttpClient.create()
                .secure(spec -> {
                    try {
                        spec.sslContext(SslContextBuilder.forClient()
                                // Akceptowanie "niezaufanych" certyfikatów. Dla testów!
                                .trustManager(InsecureTrustManagerFactory.INSTANCE).build()
                        );
                    } catch (SSLException e) {
                        throw new RuntimeException(e);
                    }
                });

        return WebClient.builder()
                .baseUrl(soapUrl) // URL usługi BIR1
                .defaultHeader("Content-Type", "application/soap+xml; charset=utf-8")
                .defaultHeader("Accept", "application/soap+xml")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
