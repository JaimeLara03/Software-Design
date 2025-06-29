package edu.uclm.esi.iso2.circuits;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"edu.uclm.esi.iso2.circuits", "edu.uclm.esi.iso2.users.domain"})
@EnableJpaRepositories("edu.uclm.esi.iso2.circuits.domain.repository")
public class CircuitsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CircuitsServiceApplication.class, args);
	}

}
