package edu.uclm.esi.iso2.circuits.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.FetchType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "puertas")
public class Puerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private int qubitObjetivo;

    private Integer qubitControl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "circuito_id")
    @JsonIgnore
    private Circuito circuito;

    public Puerta() {
    }

    public Puerta(String nombre, int qubitObjetivo, Integer qubitControl) {
        this.nombre = nombre;
        this.qubitObjetivo = qubitObjetivo;
        this.qubitControl = qubitControl;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getQubitObjetivo() {
        return qubitObjetivo;
    }

    public void setQubitObjetivo(int qubitObjetivo) {
        this.qubitObjetivo = qubitObjetivo;
    }

    public Integer getQubitControl() {
        return qubitControl;
    }

    public void setCircuito(Circuito circuito) {
        this.circuito = circuito;
    }

    public void setQubitControl(Integer qubitControl) {
        this.qubitControl = qubitControl;
    }
}
