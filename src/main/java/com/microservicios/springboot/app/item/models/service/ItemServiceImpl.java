package com.microservicios.springboot.app.item.models.service;

import com.microservicios.springboot.app.item.models.Item;
import com.microservicios.springboot.app.item.models.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service("ItemServiceImpl")
public class ItemServiceImpl implements ITemService{
    @Autowired
    private RestTemplate restTemplate;
    @Override
    public List<Item> findAll() {
        List<Producto> productos = Arrays.asList(Objects.requireNonNull(restTemplate.getForObject("http://localhost:8080/mostrar", Producto[].class)));
        //List<Producto> productos = Arrays.asList(restTemplate.getForObject("localhost:8080/mostrar",Producto[].class));
        return productos.stream().map(p -> new Item(p,1)).toList();
    }

    @Override
    public Item findById(Long id, Integer cantidad) {
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("id", String.valueOf(id));
        Producto producto = restTemplate.getForObject("http://localhost:8080/buscar/{id}",Producto.class,pathVariables);
        return new Item(producto, cantidad);
    }
}
