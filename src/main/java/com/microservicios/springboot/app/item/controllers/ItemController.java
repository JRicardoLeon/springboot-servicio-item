package com.microservicios.springboot.app.item.controllers;

import com.microservicios.springboot.app.item.models.Item;
import com.microservicios.springboot.app.item.models.Producto;
import com.microservicios.springboot.app.item.models.service.ITemService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ItemController {
    @Autowired
    @Qualifier("ItemServiceFeign")
    private ITemService iTemService;

    @RequestMapping(value = "/mostrar", method = RequestMethod.GET)
    // @GetMapping("/mostrar")
    public List<Item> Listar() {
        return iTemService.findAll();
    }

    @HystrixCommand(fallbackMethod = "MetodoAlternativo")
    @GetMapping("/buscar/{id}/cantidad/{cantidad}")
    public Item Detalle(@PathVariable Long id, @PathVariable Integer cantidad) {
        return iTemService.findById(id, cantidad);
    }

    public Item MetodoAlternativo(Long id, Integer cantidad) {
        Item item = new Item();
        Producto producto = new Producto();
        item.setCantidad(cantidad);
        producto.setId(id);
        producto.setNombre("Producto de emergencia...");
        producto.setPrecio(0.0);
        item.setProducto(producto);
        return item;
    }
}
