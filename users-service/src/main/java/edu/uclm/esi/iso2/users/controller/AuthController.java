package edu.uclm.esi.iso2.users.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.uclm.esi.iso2.users.domain.model.Usuario;
import edu.uclm.esi.iso2.users.domain.service.UsuarioService;
import edu.uclm.esi.iso2.users.payload.request.LoginRequest;
import edu.uclm.esi.iso2.users.payload.response.MessageResponse;
import edu.uclm.esi.iso2.users.payload.request.PasswordResetRequest;
import edu.uclm.esi.iso2.users.payload.response.JwtResponse;
import edu.uclm.esi.iso2.users.security.jwt.JwtUtils;
import edu.uclm.esi.iso2.users.security.services.UserDetailsImpl;

import java.util.Map;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt,
                                                 userDetails.getId(),
                                                 userDetails.getUsername(),
                                                 userDetails.getNombre(),
                                                 userDetails.getApellido(),
                                                 userDetails.getCredito()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioService.createUsuario(usuario);
            return ResponseEntity.ok(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            usuarioService.solicitarRecuperacionContrasena(email);
            // Se envía una respuesta genérica para no revelar si el email existe en la base de datos
            return ResponseEntity.ok(java.util.Map.of("message", "Si existe una cuenta con el correo proporcionado, se enviará un enlace de recuperación."));
        } catch (Exception e) {
            // Incluso en caso de error, se devuelve una respuesta genérica
            return ResponseEntity.ok(java.util.Map.of("message", "Si existe una cuenta con el correo proporcionado, se enviará un enlace de recuperación."));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestParam("token") String token) {
        try {
            boolean isVerified = usuarioService.verificarCuenta(token);
            if (isVerified) {
                return ResponseEntity.ok("Cuenta verificada correctamente.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token de verificación no válido o expirado.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al verificar la cuenta: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        try {
            boolean result = usuarioService.resetPassword(request.getToken(), request.getNewPassword());
            if (result) {
                return ResponseEntity.ok(new MessageResponse("Contraseña actualizada correctamente."));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Token no válido o expirado."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
}
