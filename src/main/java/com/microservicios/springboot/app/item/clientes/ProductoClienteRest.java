package com.microservicios.springboot.app.item.clientes;

import com.microservicios.springboot.app.item.models.Producto;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
// configuration = FeignAutoConfiguration.class
@FeignClient(name = "servicio-productos", url = "http://localhost:8080")
public interface ProductoClienteRest {
    @RequestMapping(method = RequestMethod.GET, value = "/mostrar")
    public List<Producto> Mostrar();

    //@ResponseStatus(value = HttpStatus.OK)
    //@GetMapping("/buscar/{Id}")
    @RequestMapping(method = RequestMethod.GET, value = "/buscar/{Id}")
    public Producto detalle(@PathVariable long Id);
}
