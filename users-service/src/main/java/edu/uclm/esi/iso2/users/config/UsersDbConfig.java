package edu.uclm.esi.iso2.users.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = {"edu.uclm.esi.iso2.users.domain.repository"}
)
public class UsersDbConfig {
    // Configuración simplificada para usar una única fuente de datos
}
