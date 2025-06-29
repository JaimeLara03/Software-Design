package edu.uclm.esi.iso2.users.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void enviarEmailVerificacion(String emailDestinatario, String token) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(fromEmail);
        mensaje.setTo(emailDestinatario);
        mensaje.setSubject("Verificación de cuenta");
        mensaje.setText("Para verificar tu cuenta, haz clic en el siguiente enlace: "
                + "http://localhost:8080/api/usuarios/verificar?token=" + token);

        mailSender.send(mensaje);
    }

    public void enviarEmailConfirmacionCreacionCuenta(String emailDestinatario) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(fromEmail);
        mensaje.setTo(emailDestinatario);
        mensaje.setSubject("Cuenta creada con éxito");
        mensaje.setText("¡Tu cuenta ha sido creada con éxito! Ya puedes empezar a utilizar nuestros servicios.");

        mailSender.send(mensaje);
    }

    public void enviarEmailRecuperacionContrasena(String emailDestinatario, String token) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(fromEmail);
        mensaje.setTo(emailDestinatario);
        mensaje.setSubject("Recuperación de contraseña");
        mensaje.setText("Para recuperar tu contraseña, haz clic en el siguiente enlace: "
                + "http://localhost:4200/reset-password/" + token);

        mailSender.send(mensaje);
    }
}

