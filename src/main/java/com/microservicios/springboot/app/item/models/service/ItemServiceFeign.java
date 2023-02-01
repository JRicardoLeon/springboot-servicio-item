package com.microservicios.springboot.app.item.models.service;

import com.microservicios.springboot.app.item.clientes.ProductoClienteRest;
import com.microservicios.springboot.app.item.models.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("ItemServiceFeign")
public class ItemServiceFeign implements ITemService {
    @Autowired
    private ProductoClienteRest productoClienteRest;

    public List<Item> findAll() {
        return productoClienteRest.Mostrar().stream().map(p-> new Item(p, 1)).toList();
    }

    @Override
    public Item findById(Long id, Integer cantidad) {
        return new Item(productoClienteRest.detalle(id),cantidad);
    }

}
