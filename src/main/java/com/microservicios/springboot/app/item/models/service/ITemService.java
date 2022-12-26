package com.microservicios.springboot.app.item.models.service;

import com.microservicios.springboot.app.item.models.Item;

import java.util.List;

public interface ITemService {
    public List<Item> findAll();
    public Item findById(Long id, Integer cantidad);
}
