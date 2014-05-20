package de.eonas.website.activities;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendMail {
    private String from;
    private String to;
    private String subject;
    private String text;
    Transport transport;

    public SendMail(String from, String to, String subject, String text) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.text = text;
    }

    /*public void Send(){
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
       // props.put("mail.smtp.host", "localhost");
        //props.put("mail.smtp.auth", "false");// set to false for no username
        //props.put("mail.debug", "false");
        //props.put("mail.smtp.port", "25");
       props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        Session mailSession = Session.getInstance(props);
        Message simpleMessage = new MimeMessage(mailSession);
        InternetAddress fromAddress = null;
        InternetAddress toAddress = null;
        try {
        fromAddress = new InternetAddress(from);
        toAddress = new InternetAddress(to);
         } catch (AddressException e) {

         e.printStackTrace();
         }

         try {
         simpleMessage.setFrom(fromAddress);
         simpleMessage.setRecipient(RecipientType.TO, toAddress);
         simpleMessage.setSubject(subject);
         simpleMessage.setText(text);
         transport.send(simpleMessage);
             System.out.println("MARWA");

             System.out.println("MARWA");

         } catch (MessagingException e) {

           e.printStackTrace();
             //try {
               //  transport.close();
             //} catch (MessagingException e1) {
               //  e1.printStackTrace();
             //}
         }
    }*/
    public void send() throws MessagingException {

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.gmail.comt");
        props.put("mail.smtp.auth", "false");// set to false for no username
        props.put("mail.debug", "false");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props);

        InternetAddress fromAddress = null;
        InternetAddress toAddress = null;
        Transport transport = session.getTransport("smtp");
        transport.connect();
        try {
            Message simpleMessage = new MimeMessage(session);
            fromAddress = new InternetAddress(from);
            toAddress = new InternetAddress(to);
            simpleMessage.setFrom(fromAddress);
            simpleMessage.setRecipient(RecipientType.TO, toAddress);
            simpleMessage.setSubject(subject);
            simpleMessage.setText(text);
            transport.sendMessage(simpleMessage,
                    simpleMessage.getAllRecipients());
        } catch (MessagingException e) {
            e.printStackTrace();
        } finally {
            transport.close();
        }
    }
}





