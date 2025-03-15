package edu.uclm.esi.circuits.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("test") // Esta configuración solo se activará con el perfil "test"
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Desactivar completamente la seguridad para pruebas
        http
            .csrf().disable()
            .authorizeHttpRequests()
            .anyRequest().permitAll();
            
        return http.build();
    }
}
