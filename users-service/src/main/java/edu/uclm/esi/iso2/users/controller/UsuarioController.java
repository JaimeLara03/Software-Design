package edu.uclm.esi.iso2.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.uclm.esi.iso2.users.domain.users.model.Usuario;
import edu.uclm.esi.iso2.users.domain.users.service.UsuarioService;
import edu.uclm.esi.iso2.users.payload.request.LoginRequest;
import edu.uclm.esi.iso2.users.payload.request.PasswordResetRequest;
import edu.uclm.esi.iso2.users.payload.response.JwtResponse;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioService.getUsuarioById(id);
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    @PostMapping("/registro")
    public ResponseEntity<Usuario> createUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.createUsuario(usuario);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        Usuario updatedUsuario = usuarioService.updateUsuario(id, usuario);
        return new ResponseEntity<>(updatedUsuario, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/verificar")
    public ResponseEntity<String> verificarCuenta(@RequestParam String token) {
        boolean verificado = usuarioService.verificarCuenta(token);
        if (verificado) {
            return new ResponseEntity<>("Cuenta verificada con éxito", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Error al verificar la cuenta", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<String> solicitarRecuperacionContrasena(@RequestParam String email) {
        usuarioService.solicitarRecuperacionContrasena(email);
        return new ResponseEntity<>("Se ha enviado un email con las instrucciones para recuperar la contraseña", HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        boolean reseteado = usuarioService.resetPassword(request.getToken(), request.getNewPassword());
        if (reseteado) {
            return new ResponseEntity<>("Contraseña cambiada con éxito", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Error al cambiar la contraseña", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/comprobar-credito/{id}/{cantidad}")
    public ResponseEntity<Boolean> comprobarCredito(@PathVariable Long id, @PathVariable double cantidad) {
        boolean creditoSuficiente = usuarioService.comprobarCredito(id, cantidad);
        return new ResponseEntity<>(creditoSuficiente, HttpStatus.OK);
    }

    @PutMapping("/actualizar-credito/{id}/{nuevoCredito}")
    public ResponseEntity<Usuario> actualizarCredito(@PathVariable Long id, @PathVariable double nuevoCredito) {
        Usuario usuario = usuarioService.actualizarCredito(id, nuevoCredito);
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }
}
