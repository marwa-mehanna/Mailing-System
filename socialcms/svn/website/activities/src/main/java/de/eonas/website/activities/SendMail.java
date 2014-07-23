package de.eonas.website.activities;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


public class SendMail {
    private String from;
    private String to;
    private String subject;
    private String text;
    private String username;
    private String password;
    private String host;
    //Transport transport;


    public SendMail(String from, String to, String subject, String text,String username,String password,String host) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.text = text;
        this.username=username;
        this.password=password;
        this.host =host;
    }


    public void send() throws MessagingException {
        if(host.equals("gmail.com")) {
            String hostsmtp = "smtp." + host;
            //String username = username;
            //final String password = "123456marwa";
            Properties props = new Properties();
            // set any needed mail.smtps.* properties here
            props.put("mail.transport.protocol", "smtp");
            // props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", hostsmtp);
            props.put("mail.smtp.user", username);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");
            props.put("mail.debug", "true");
            props.put("mail.smtp.debug", "true");
            props.put("mail.smtp.ssl.enable", "true");
            // props.put("mail.smtp.starttls.enable","true");


            //props.put("mail.mime.charset", "ISO-8859-1");
             props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.smtp.socketFactory.class",
                 "javax.net.ssl.SSLSocketFactory");
            Authenticator auth = new GMailAuthenticator();
            Session session = Session.getInstance(props,auth);

            session.setDebug(true);

            MimeMessage msg = new MimeMessage(session);

            // set the message content here
            InternetAddress fromAddress = null;
            InternetAddress toAddress = null;
            fromAddress = new InternetAddress(from);
            toAddress = new InternetAddress(to);
            msg.setFrom(fromAddress);
            msg.setRecipient(Message.RecipientType.TO, toAddress);
            msg.setSubject(subject);
            msg.setText(text);
            try {
                Transport.send(msg);
            }  finally {

            }
        }  else if(host.equals("live.com")){
            String hostsmtp = "smtp." + host;
            //String username = username;
            //final String password = "123456marwa";
            Properties props = new Properties();
            // set any needed mail.smtps.* properties here
            props.put("mail.transport.protocol", "smtp");
            // props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", hostsmtp);
            props.put("mail.smtp.user", username);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "25");
            props.put("mail.debug", "true");
            props.put("mail.smtp.debug", "true");
           // props.put("mail.smtp.ssl.enable", "true");
             props.put("mail.smtp.starttls.enable","true");
            Authenticator auth = new GMailAuthenticator();
            Session session = Session.getInstance(props,auth);

            session.setDebug(true);

            MimeMessage msg = new MimeMessage(session);

            // set the message content here
            InternetAddress fromAddress = null;
            InternetAddress toAddress = null;
            fromAddress = new InternetAddress(from);
            toAddress = new InternetAddress(to);
            msg.setFrom(fromAddress);
            msg.setRecipient(Message.RecipientType.TO, toAddress);
            msg.setSubject(subject);
            msg.setText(text);
            try {
                Transport.send(msg);
            }  finally {

            }
        }else if(host.equals("mail.yahoo.com")){
        String hostsmtp = "smtp." + host;
        //String username = username;
        //final String password = "123456marwa";
        Properties props = new Properties();
        // set any needed mail.smtps.* properties here
        props.put("mail.transport.protocol", "smtp");
        // props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", hostsmtp);
        props.put("mail.smtp.user", username);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.debug", "true");
        props.put("mail.smtp.debug", "true");
        // props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.starttls.enable","true");
        Authenticator auth = new GMailAuthenticator();
        Session session = Session.getInstance(props,auth);

        session.setDebug(true);

        MimeMessage msg = new MimeMessage(session);

        // set the message content here
        InternetAddress fromAddress = null;
        InternetAddress toAddress = null;
        fromAddress = new InternetAddress(from);
        toAddress = new InternetAddress(to);
        msg.setFrom(fromAddress);
        msg.setRecipient(Message.RecipientType.TO, toAddress);
        msg.setSubject(subject);
        msg.setText(text);
        try {
            Transport.send(msg);
        }  finally {

        }
    }

    }
    private class GMailAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            String user = username;
            String pwd = password;

            return new PasswordAuthentication(user, pwd);
        }
    }
}





