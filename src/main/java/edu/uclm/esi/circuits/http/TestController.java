package edu.uclm.esi.circuits.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.circuits.domain.users.model.Usuario;
import edu.uclm.esi.circuits.domain.users.repository.UsuarioRepository;
import edu.uclm.esi.circuits.payload.request.LoginRequest;
import edu.uclm.esi.circuits.payload.response.JwtResponse;
import edu.uclm.esi.circuits.security.jwt.JwtUtils;
import edu.uclm.esi.circuits.security.services.UserDetailsImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/public")
public class TestController {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @GetMapping("/test")
    public String testEndpoint() {
        return "El backend está funcionando correctamente!";
    }
    
    @GetMapping("/status")
    public String status() {
        return "Servidor iniciado y funcionando";
    }
    
    @PostMapping("/test-registro")
    public ResponseEntity<?> testRegistro(@RequestBody Usuario usuario) {
        try {
            // Comprobar si el email ya existe
            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                return ResponseEntity
                    .badRequest()
                    .body("Error: El email ya está en uso!");
            }
            
            // Establecer valores por defecto
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuario.setActivo(true);
            usuario.setVerificado(true); // Para pruebas, lo establecemos como verificado
            usuario.setTokenVerificacion(UUID.randomUUID().toString());
            
            // Guardar usuario
            Usuario savedUsuario = usuarioRepository.save(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario registrado con éxito");
            response.put("id", savedUsuario.getId());
            response.put("email", savedUsuario.getEmail());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al registrar usuario: " + e.getMessage());
        }
    }
    
    @PostMapping("/test-login")
    public ResponseEntity<?> testLogin(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), 
                    loginRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            JwtResponse response = new JwtResponse(
                jwt, 
                userDetails.getId(), 
                userDetails.getUsername(), 
                userDetails.getNombre(),
                userDetails.getApellido(),
                userDetails.getCredito()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Error al iniciar sesión: " + e.getMessage());
        }
    }
}
