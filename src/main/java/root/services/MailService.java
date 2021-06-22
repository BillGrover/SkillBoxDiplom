package root.services;

import com.sun.mail.smtp.SMTPTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Service
public class MailService {

    @Value("${email.siteAddress}")
    private String siteAddress;
    @Value("${email.host}")
    private String host;
    @Value("${email.domain}")
    private String domain;
    @Value("${email.senderName}")
    private String senderName;
    @Value("${email.mailgunUsername}")
    private String username;
    @Value("${email.mailgunPassword}")
    private String password;

    public void sendRecoveryPasswordEmail(String to, String code) {
        String bodyText = "Для смены пароля перейдите по ссылке\n" +
                siteAddress + "/login/change-password/" + code;

        Properties props = System.getProperties();
        props.put("mail.smtps.host", host);
        props.put("mail.smtps.auth", "true");

        Session session = Session.getInstance(props, null);
        Message msg = new MimeMessage(session);

        try {
            msg.setFrom(new InternetAddress(senderName + "@" + domain));
            InternetAddress[] addrs = InternetAddress.parse(to, false);
            msg.setRecipients(Message.RecipientType.TO, addrs);

            msg.setSubject("DevPub password recovery");
            msg.setText(bodyText);
            msg.setSentDate(new Date());

            SMTPTransport t = (SMTPTransport) session.getTransport("smtps");
            t.connect(host, username + "@" + domain, password);
            t.sendMessage(msg, msg.getAllRecipients());

            System.out.println("Response: " + t.getLastServerResponse());

            t.close();

        } catch (MessagingException e) {
            System.out.println("Sending email error:");
            e.printStackTrace();
        }
    }
}
