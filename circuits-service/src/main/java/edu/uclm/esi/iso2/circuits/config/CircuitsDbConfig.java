package edu.uclm.esi.iso2.circuits.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = {"edu.uclm.esi.iso2.circuits.domain.circuits.repository", "edu.uclm.esi.iso2.users.domain.users.repository"}
)
@Primary
public class CircuitsDbConfig {
    // Configuración simplificada para usar una única fuente de datos
}
