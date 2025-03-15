package edu.uclm.esi.circuits.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private double credito;
    
    public JwtResponse(String token, Long id, String email, String nombre, String apellido, double credito) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.credito = credito;
    }
}
