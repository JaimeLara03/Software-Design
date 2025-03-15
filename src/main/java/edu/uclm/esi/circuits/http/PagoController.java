package edu.uclm.esi.circuits.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.uclm.esi.circuits.domain.payments.model.Pago;
import edu.uclm.esi.circuits.domain.payments.service.PagoService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @GetMapping
    public ResponseEntity<List<Pago>> getAllPagos() {
        List<Pago> pagos = pagoService.getAllPagos();
        return new ResponseEntity<>(pagos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pago> getPagoById(@PathVariable Long id) {
        Pago pago = pagoService.getPagoById(id);
        return new ResponseEntity<>(pago, HttpStatus.OK);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Pago>> getPagosByUsuarioId(@PathVariable Long usuarioId) {
        List<Pago> pagos = pagoService.getPagosByUsuarioId(usuarioId);
        return new ResponseEntity<>(pagos, HttpStatus.OK);
    }

    @GetMapping("/circuito/{circuitoId}")
    public ResponseEntity<List<Pago>> getPagosByCircuitoId(@PathVariable Long circuitoId) {
        List<Pago> pagos = pagoService.getPagosByCircuitoId(circuitoId);
        return new ResponseEntity<>(pagos, HttpStatus.OK);
    }

    @PostMapping("/procesar")
    public ResponseEntity<Map<String, Object>> procesarPago(
            @RequestParam Long usuarioId,
            @RequestParam Long circuitoId,
            @RequestParam String metodoPago) {
        Map<String, Object> resultado = pagoService.procesarPago(usuarioId, circuitoId, metodoPago);
        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }

    @GetMapping("/verificar/{referenciaPago}")
    public ResponseEntity<Map<String, Object>> verificarEstadoPago(@PathVariable String referenciaPago) {
        Map<String, Object> estado = pagoService.verificarEstadoPago(referenciaPago);
        return new ResponseEntity<>(estado, HttpStatus.OK);
    }
}
