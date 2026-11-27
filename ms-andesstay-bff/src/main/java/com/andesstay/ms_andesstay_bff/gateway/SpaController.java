package com.andesstay.ms_andesstay_bff.gateway;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Sirve el router de Angular cuando se recarga una ruta del frontend o se vuelve
 * del login de Azure. Las APIs siguen pasando por GatewayController.
 */
@Controller
public class SpaController {

    @GetMapping({
            "/", "/login",
            "/dashboard", "/reservations", "/reservations/**",
            "/catalog", "/reports", "/audit"
    })
    public String index() {
        return "forward:/index.html";
    }
}
