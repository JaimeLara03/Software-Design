package edu.uclm.esi.circuits.security.jwt;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import edu.uclm.esi.circuits.security.services.UserDetailsServiceImpl;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Debug - Log the path being requested
        logger.info("Procesando solicitud para la ruta: {}", request.getRequestURI());
        
        try {
            // Lista de rutas públicas que no necesitan autenticación
            String[] publicPaths = {
                "/public/", 
                "/public/test", 
                "/public/status",
                "/api/auth/",
                "/api/auth/login",
                "/api/usuarios/registro",
                "/api/usuarios/verificar",
                "/api/test",
                "/h2-console"
            };
            
            // Comprobar si la ruta actual es pública
            String path = request.getRequestURI();
            boolean isPublicPath = false;
            
            for (String publicPath : publicPaths) {
                if (path.startsWith(publicPath)) {
                    isPublicPath = true;
                    logger.info("Ruta pública detectada: {}", path);
                    break;
                }
            }
            
            // Si es una ruta pública, permitir el acceso sin token
            if (isPublicPath) {
                logger.info("Permitiendo acceso a ruta pública sin autenticación: {}", path);
                filterChain.doFilter(request, response);
                return;
            }
            
            // Para rutas protegidas, verificar el token JWT
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("No se pudo configurar la autenticación de usuario: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
