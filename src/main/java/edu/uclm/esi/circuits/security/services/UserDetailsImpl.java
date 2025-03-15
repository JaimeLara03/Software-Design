package edu.uclm.esi.circuits.security.services;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uclm.esi.circuits.domain.users.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Getter
    private Long id;
    
    private String email;
    
    @Getter
    private String nombre;
    
    @Getter
    private String apellido;

    @JsonIgnore
    private String password;
    
    @Getter
    private double credito;
    
    @Getter
    private boolean verificado;

    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImpl build(Usuario usuario) {
        // Por simplicidad, no se están considerando roles en esta implementación
        return new UserDetailsImpl(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getPassword(),
                usuario.getCredito(),
                usuario.isVerificado(),
                Collections.emptyList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
