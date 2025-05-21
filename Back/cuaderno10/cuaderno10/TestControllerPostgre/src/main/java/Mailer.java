import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class Mailer {
    private final Session session;
    private final String from;
    private final String host;
    private final int    port;
    private final String user;
    private final String pass;

    public Mailer(String host, int port, final String user, final String pass, String from) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.from = from;

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtps");          // usar smtps
        props.put("mail.smtps.host", host);
        props.put("mail.smtps.port", String.valueOf(port));
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.ssl.enable", "true");
        props.put("mail.smtps.ssl.trust", host);
        // Timeouts
        props.put("mail.smtps.connectiontimeout", "10000");
        props.put("mail.smtps.timeout", "10000");

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });
        session.setDebug(true);  // ahora veremos toda la conversación SMTP en consola
    }

    public void send(String to, String subject, String body) throws MessagingException {
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        msg.setText(body);

        // En lugar de Transport.send(...), usamos explícitamente smtps
        Transport transport = session.getTransport("smtps");
        try {
            transport.connect(host, port, user, pass);
            transport.sendMessage(msg, msg.getAllRecipients());
            System.out.println("✅ Correo enviado a: " + to);
        } finally {
            transport.close();
        }
    }
}