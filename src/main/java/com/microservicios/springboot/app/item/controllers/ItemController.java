package com.microservicios.springboot.app.item.controllers;

import com.microservicios.springboot.app.item.models.Item;
import com.microservicios.springboot.app.item.models.Producto;
import com.microservicios.springboot.app.item.models.service.ITemService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RefreshScope
@RestController
public class ItemController {
    /*
    **
    Usando el patron CircuitBreaker, es el encargado de evitar la comunicacion con el servicio cuando falla.
    Cuando un servicio realiza una peticion a otro y la conexion es lenta o falla el sistema puede desencadenar un error en cascada,
    porque llegan nuevas peticiones y se acomulan, ya que tambíen esperan una respuesta, ahi es cuando es fundamental implemantar CircuitBreaker
    cuando detecta este fallo el aisla este sistema para no malgastar recursos con peticiones que tendrian una alta prioridad a fallos.
    El cual tiene 3 estados. CERRADO, ABIERTDO, SEMI-ABIERTO
    CERRADO: las peticiones se ejecutan de manera normal y si falla alguina peticion, estos fallos se cuentasn.
    ABIERTO: Cuando este contador de fallos supera el lumbral que se tiene establecido, no se pérmiten más peticiones haciendo fallar
    cualquier peticion que llegue, sin llegar a relaizarce esta petición y si se tiene un fallbackMethod este se ejecuta.
    SEMI-ABIERTO: En este estado se vuelve a intentar la peticion, si esta llega a ser exitosa el circuito se CIERRA nuevamente volviendo a la normalidad.
    Sí vuelve a fallar este circuito se mantine ABIERTO.
     */
    private final Logger logger = LoggerFactory.getLogger(ItemController.class);
    @Autowired
    private Environment environment;
    @Autowired
    private CircuitBreakerFactory cBreakerFactory;
    @Autowired
    @Qualifier("ItemServiceFeign")
    private ITemService iTemService;

    @Value("${configuracion.texto}")
    private String texto;

    @RequestMapping(value = "/mostrar", method = RequestMethod.GET)
    // @GetMapping("/mostrar")
    public List<Item> Listar(@RequestParam(name = "nombre", required = false) String nombre, @RequestHeader(name = "token-request", required = false) String token) {
        return iTemService.findAll();
    }

    @GetMapping("/buscar/{id}/cantidad/{cantidad}")
    public Item Detalle(@PathVariable Long id, @PathVariable Integer cantidad) {
        return cBreakerFactory.create("items").run(() -> iTemService.findById(id, cantidad), e -> MetodoAlternativo(id, cantidad, e));
        //return iTemService.findById(id,cantidad);
    }

    @CircuitBreaker(name = "items", fallbackMethod = "MetodoAlternativo")
    @GetMapping("/buscar2/{id}/cantidad/{cantidad}")
    public Item Detalle2(@PathVariable Long id, @PathVariable Integer cantidad) {
        return iTemService.findById(id, cantidad);
        //return iTemService.findById(id,cantidad);
    }

    @TimeLimiter(name = "items", fallbackMethod = "MetodoAlternativo2")
    @GetMapping("/buscar3/{id}/cantidad/{cantidad}")
    public CompletableFuture<Item> Detalle3(@PathVariable Long id, @PathVariable Integer cantidad) {
        return CompletableFuture.supplyAsync(() -> iTemService.findById(id, cantidad));
        //return iTemService.findById(id,cantidad);
    }

    public CompletableFuture<Item> MetodoAlternativo2(Long id, Integer cantidad, Throwable e) {
        logger.info(e.getMessage());
        Item item = new Item();
        Producto producto = new Producto();
        item.setCantidad(cantidad);
        producto.setId(id);
        producto.setNombre("Producto de emergencia...");
        producto.setPrecio(0.0);
        item.setProducto(producto);
        return CompletableFuture.supplyAsync(() -> item);
    }

    public Item MetodoAlternativo(Long id, Integer cantidad, Throwable e) {
        logger.info(e.getMessage());
        Item item = new Item();
        Producto producto = new Producto();
        item.setCantidad(cantidad);
        producto.setId(id);
        producto.setNombre("Producto de emergencia...");
        producto.setPrecio(0.0);
        item.setProducto(producto);
        return item;
    }

    @GetMapping("/mostrar-confi")
    public ResponseEntity<?> config() {
        Map<String, String> json = new HashMap<>();
        json.put("texto", texto);
        if (environment.getActiveProfiles().length > 0 && environment.getActiveProfiles()[0].equals("dev")) {
            json.put("autor.nombre", environment.getProperty("configuracion.autor.nombre"));
            json.put("autor.email", environment.getProperty("configuracion.autor.email"));
        }
        return new ResponseEntity<Map<String, String>>(json, HttpStatus.OK);
    }
}
