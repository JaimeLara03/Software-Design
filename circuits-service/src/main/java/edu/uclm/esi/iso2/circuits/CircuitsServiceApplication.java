package edu.uclm.esi.iso2.circuits;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"edu.uclm.esi.iso2.circuits", "edu.uclm.esi.iso2.users.domain"})
public class CircuitsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CircuitsServiceApplication.class, args);
	}

}
