package com.tuempresa.fact_app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    private Integer id;
    private String nombre;
    private boolean activo;

    @Override
    public String toString() {
        return nombre; // Vital para que el ComboBox muestre el texto de la categoría
    }
}