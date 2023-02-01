package com.microservicios.springboot.app.item.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private Producto producto;
    private Integer Cantidad;

    public Double getTotal(){
        return producto.getPrecio() * Cantidad;
    }
}