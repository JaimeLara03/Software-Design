package edu.uclm.esi.circuits.domain.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.uclm.esi.circuits.domain.users.model.Usuario;
import edu.uclm.esi.circuits.domain.users.repository.UsuarioRepository;
import edu.uclm.esi.circuits.exception.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;

@Service
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
        usuario.setVerificado(false);
        usuario.setTokenVerificacion(UUID.randomUUID().toString());
        
        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        emailService.enviarEmailVerificacion(nuevoUsuario.getEmail(), nuevoUsuario.getTokenVerificacion());
        
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
        Usuario usuario = usuarioRepository.findByTokenVerificacion(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token de verificación no válido"));
        
        usuario.setVerificado(true);
        usuario.setTokenVerificacion(null);
        usuarioRepository.save(usuario);
        
        emailService.enviarEmailConfirmacionCreacionCuenta(usuario.getEmail());
        
        return true;
    }
    
    @Transactional
    public void solicitarRecuperacionContrasena(String email) {
        Usuario usuario = getUsuarioByEmail(email);
        usuario.setTokenVerificacion(UUID.randomUUID().toString());
        usuarioRepository.save(usuario);
        
        emailService.enviarEmailRecuperacionContrasena(usuario.getEmail(), usuario.getTokenVerificacion());
    }
    
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        Usuario usuario = usuarioRepository.findByTokenVerificacion(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token de recuperación no válido"));
        
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuario.setTokenVerificacion(null);
        usuarioRepository.save(usuario);
        
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
