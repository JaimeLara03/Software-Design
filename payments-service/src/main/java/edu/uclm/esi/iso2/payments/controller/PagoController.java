package edu.uclm.esi.iso2.payments.controller;

import edu.uclm.esi.iso2.payments.domain.payments.service.PagoService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/procesar")
    public Map<String, Object> procesarPago(@RequestParam Long usuarioId,
                                            @RequestParam Long circuitoId,
                                            @RequestParam String metodoPago) {
        return pagoService.procesarPago(usuarioId, circuitoId, metodoPago);
    }

    @GetMapping("/verificar/{referencia}")
    public Map<String, Object> verificarEstadoPago(@PathVariable String referencia) {
        return pagoService.verificarEstadoPago(referencia);
    }
}

