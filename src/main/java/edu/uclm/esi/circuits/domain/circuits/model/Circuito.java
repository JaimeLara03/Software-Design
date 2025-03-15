package edu.uclm.esi.circuits.domain.circuits.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "circuitos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Circuito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
    
    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;
    
    @Min(value = 0, message = "El coste debe ser mayor o igual a 0")
    private double coste;
    
    private boolean activo;
}
