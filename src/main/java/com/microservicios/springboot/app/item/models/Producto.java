package com.microservicios.springboot.app.item.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class Producto {
    private Long Id;
    private String Nombre;
    private Double Precio;
    private Date CreateAt;

}