package WD.works.V2.usuario.auth.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarRecuperacaoSenha(
            String email,
            String nome,
            String link
    ) {

        SimpleMailMessage mensagem = new SimpleMailMessage();

        mensagem.setTo(email);
        mensagem.setSubject("Recuperação de senha - WD WORKS");

        mensagem.setText(
                "Olá, " + nome + "!\n\n" +

                        "Recebemos um pedido para redefinir a sua senha.\n\n" +

                        "Clique no link abaixo para criar uma nova senha:\n\n" +

                        link + "\n\n" +

                        "Este link é válido por 15 minutos " +
                        "e só pode ser utilizado uma vez.\n\n" +

                        "Se não solicitou esta alteração, " +
                        "pode ignorar este email.\n\n" +

                        "Atenciosamente,\n" +
                        "WD WORKS"
        );

        mailSender.send(mensagem);
    }
}