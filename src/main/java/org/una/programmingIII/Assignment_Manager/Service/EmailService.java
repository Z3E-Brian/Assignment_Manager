// EmailService.java
package org.una.programmingIII.Assignment_Manager.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    private final int AN_HOUR = 3600000;
    private final int FIVE_MINUTES = 300000;
    public void sendActivationEmail(String toEmail, String subject, long id) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String activationLink = "http://localhost:8080/activation.html?id=" + id;

            String htmlBody = "<html>"
                    + "<head>"
                    + "<style>"
                    + "body { font-family: Arial, sans-serif; background-color: #f2f2f2; padding: 20px; }"
                    + "h1 { color: #000359; text-align: center; font-size: 28px; margin-bottom: 10px; }"
                    + "p { color: #666; font-size: 16px; line-height: 1.6; }"
                    + ".container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 25px; border-radius: 12px; box-shadow: 0px 0px 20px rgba(0, 0, 0, 0.1); }"
                    + ".button { display: inline-block; padding: 10px 20px; font-size: 16px; color: #6ae3b0 !important; "
                    + "background: linear-gradient(135deg, purple, black); text-align: center; text-decoration: none; "
                    + "border: none; border-radius: 5px; cursor: pointer; font-weight: bold; "
                    + "transition: background 0.3s ease; }"
                    + ".button:hover { background: linear-gradient(135deg, black, purple); }"
                    + ".footer { font-size: 12px; color: #999; margin-top: 20px; text-align: center; line-height: 1.4; }"
                    + ".divider { border: none; border-top: 1px solid #ddd; margin: 20px 0; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class='container'>"
                    + "<h1>Welcome to ApiClient!</h1>"
                    + "<p>Hello,</p>"
                    + "<p>Thank you for signing up for <strong>Assignment Manager</strong>. We're excited to have you with us. To start using all our features, please activate your account by clicking the button below:</p>"
                    + "<a href='" + activationLink + "' class='button'>Activate Account</a>"
                    + "<hr class='divider'>"
                    + "<p class='footer'>This email was automatically generated. Please do not reply to this message.</p>"
                    + "<p class='footer'>Need help? Visit our <a href='#' style='color: #4CAF50; text-decoration: none;'>help center</a> or contact our <a href='#' style='color: #4CAF50; text-decoration: none;'>support team</a>.</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            helper.setText(htmlBody, true);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setFrom("wikiPetsUNA@gmail.com");

            mailSender.send(mimeMessage);

        } catch (MailException | MessagingException e) {
            throw new RuntimeException("Error sending activation email", e);
        }
    }

    public void sendActivationEmailThread(String toEmail, String subject, long id) {
        Thread thread = new Thread(() -> {
            boolean sent = false;
            long startTime = System.currentTimeMillis();
            long elapsedTime = 0;
            while (!sent && elapsedTime < AN_HOUR) {
                try {
                    elapsedTime = System.currentTimeMillis() - startTime;
                    sendActivationEmail(toEmail, subject, id);
                    sent = true;
                } catch (Exception e) {
                    System.out.println("Error sending activation email. Retrying in 5 minutes...");
                    try {
                        Thread.sleep(FIVE_MINUTES);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            if (sent) {
                System.out.println("Activation email sent to " + toEmail);
            } else {
                System.out.println("Activation email could not be sent to " + toEmail);
            }
        });
        thread.start();
    }


    public void sendEmailToStudent(String toEmail, String subject, String body) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        String htmlBody = "<html>"
                + "<head>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; background-color: #f2f2f2; padding: 20px; }"
                + "h1 { color: #000359; text-align: center; }"
                + "p { color: #666; font-size: 16px; }"
                + ".footer { font-size: 12px; color: #999; margin-top: 20px; text-align: center; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 10px; box-shadow: 0px 0px 15px rgba(0, 0, 0, 0.1);'>"
                + "<h1>¡Assignment Manager!</h1>"
                + "<p>" + body + "</p>"
                + "<hr style='border: none; border-top: 1px solid #ddd;'>"
                + "<p class='footer'>This email was generated automatically. Please do not reply to this message.</p>"
                + "</div>"
                + "</body>"
                + "</html>";

        helper.setText(htmlBody, true);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setFrom("wikiPetsUNA@gmail.com");
        mailSender.send(mimeMessage);
    }
}
