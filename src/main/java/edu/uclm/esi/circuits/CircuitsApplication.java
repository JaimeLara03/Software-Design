package edu.uclm.esi.circuits;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ComponentScan(basePackages = {"edu.uclm.esi.circuits"})
@EntityScan("edu.uclm.esi.circuits.domain")
@EnableJpaRepositories("edu.uclm.esi.circuits.domain")
public class CircuitsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CircuitsApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
					.allowedOrigins("http://localhost:4200")
					.allowedMethods("GET", "POST", "PUT", "DELETE")
					.allowedHeaders("*");
			}
		};
	}
	
	@RestController
	public static class TestController {
		
		@GetMapping("/api/test")
		public String test() {
			return "¡El backend está funcionando correctamente!";
		}
	}
}
