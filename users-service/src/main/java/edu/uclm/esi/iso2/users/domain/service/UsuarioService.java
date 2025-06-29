package edu.uclm.esi.iso2.users.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.uclm.esi.iso2.users.domain.model.Usuario;
import edu.uclm.esi.iso2.users.domain.service.EmailService;
import edu.uclm.esi.iso2.users.domain.repository.UsuarioRepository;
import edu.uclm.esi.iso2.users.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario getUsuarioById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    public Usuario getUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    @Transactional
    public Usuario createUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setActivo(true);
        usuario.setVerificado(true); // Cambiar a true para que los usuarios puedan iniciar sesión sin verificar email
        usuario.setTokenVerificacion(UUID.randomUUID().toString());

    // Asignar rol por defecto
    usuario.getRoles().add(edu.uclm.esi.iso2.users.domain.model.Role.ROLE_USER);

        Usuario nuevoUsuario = usuarioRepository.saveAndFlush(usuario);
        try {
            emailService.enviarEmailVerificacion(nuevoUsuario.getEmail(), nuevoUsuario.getTokenVerificacion());
        } catch (Exception e) {
            log.error("Error al enviar el email de verificación para {}. La cuenta se ha creado igualmente.", nuevoUsuario.getEmail(), e);
        }

        return nuevoUsuario;
    }

    @Transactional
    public Usuario updateUsuario(Long id, Usuario usuarioDetails) {
        Usuario usuario = getUsuarioById(id);

        usuario.setNombre(usuarioDetails.getNombre());
        usuario.setApellido(usuarioDetails.getApellido());

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void deleteUsuario(Long id) {
        Usuario usuario = getUsuarioById(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public boolean verificarCuenta(String token) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByTokenVerificacion(token);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setVerificado(true);
            usuario.setTokenVerificacion(null);
            usuarioRepository.save(usuario);
            emailService.enviarEmailConfirmacionCreacionCuenta(usuario.getEmail());
            return true;
        }
        return false;
    }

    @Transactional
    public void solicitarRecuperacionContrasena(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String token = UUID.randomUUID().toString();
            usuario.setResetPasswordToken(token);
            usuario.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1)); // El token expira en 1 hora
            usuarioRepository.save(usuario);

            try {
                log.info("GENERATED RESET TOKEN for {}: {}", email, token);
                emailService.enviarEmailRecuperacionContrasena(usuario.getEmail(), token);
            } catch (Exception e) {
                log.error("Error al enviar el email de recuperación de contraseña para {}. El token se ha generado igualmente.", email, e);
            }
        }
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByResetPasswordToken(token);
        if (usuarioOpt.isEmpty()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            return false; // El token ha expirado
        }

        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuario.setResetPasswordToken(null);
        usuario.setResetPasswordTokenExpiry(null);
        usuarioRepository.saveAndFlush(usuario);

        return true;
    }

    @Transactional
    public boolean comprobarCredito(Long usuarioId, double cantidad) {
        Usuario usuario = getUsuarioById(usuarioId);
        return usuario.getCredito() >= cantidad;
    }

    @Transactional
    public Usuario actualizarCredito(Long usuarioId, double nuevoCredito) {
        Usuario usuario = getUsuarioById(usuarioId);
        usuario.setCredito(nuevoCredito);
        return usuarioRepository.save(usuario);
    }


}
