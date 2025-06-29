package edu.uclm.esi.iso2.circuits.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import java.util.ArrayList;
import jakarta.persistence.Convert;
import java.util.List;
import jakarta.persistence.Column;
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

    private String ubicacion = "";

    @Min(value = 1, message = "El número de qubits debe ser al menos 1")
    private int qubits;

    @Min(value = 0, message = "El coste debe ser mayor o igual a 0")
    private double coste;

    private boolean activo;

    // Almacena la tabla de verdad como JSON (lista de salidas 0/1)
    @Convert(converter = StringListConverter.class)
    @Lob
    @Column(columnDefinition = "TEXT")
    private List<String> truthTable;

    @jakarta.persistence.Transient
        private boolean needsCredit;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "circuito_id")
    private List<Puerta> puertas = new ArrayList<>();

    public List<Puerta> getPuertas() {
        return puertas;
    }

    public void setPuertas(List<Puerta> puertas) {
        if (this.puertas != null) {
            this.puertas.clear();
        } else {
            this.puertas = new ArrayList<>();
        }
        if (puertas != null) {
            for (Puerta puerta : puertas) {
                puerta.setCircuito(this);
                this.puertas.add(puerta);
            }
        }
    }
}
