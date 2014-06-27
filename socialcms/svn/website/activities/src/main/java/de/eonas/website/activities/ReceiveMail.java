package de.eonas.website.activities;

import de.eonas.website.activities.model.Mail;
import de.eonas.website.activities.model.Mailer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.util.List;
import java.util.Properties;

@org.springframework.stereotype.Component
public class ReceiveMail {


    // Message[] message;
    @Autowired
    Dao dao;
    boolean f=true;

    public void receiveEmail(List<Mailer> accounts) {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        try {
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen, false);


            String accountconc = accounts.get(0).getUsername() + "@" + accounts.get(0).getHost();
            String accountpassword = accounts.get(0).getPassword();
            Session session = Session.getInstance(props, null);
            Store store = session.getStore();
            store.connect("imap.gmail.com", accountconc, accountpassword);
            System.out.println("Established Connection to Server!");
            Folder inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_ONLY);
            System.out.println(inbox.getMessageCount());
            //Message msg = inbox.getMessage(inbox.getMessageCount());
            //message = inbox.getMessages(1,inbox.getMessageCount());
            //for (int i = 3; i < inbox.getMessageCount(); i++) {

            // message[i].equals(inbox.getMessage(i));
            //}
            Message message[] = inbox.getMessages();
            for (int i = 3; i < message.length; i++) {
                Message msg = message[i];
                //Flags flags = msg.getFlags();

                //f =dao.checkMailUnExistence(msg.getMessageNumber());

                if (f) {
                    System.out.println("Found specified Folder, retrieving the latest message...");


                    Mail mail = new Mail();
                    Address[] in = msg.getFrom();

                    for (Address address : in) {

                        //System.out.println("FROM:" + address.toString());
                        mail.setMailFrom(address.toString());
                    }
                    Object content = msg.getContent();
                    if (content instanceof String) {
                        String body = (String) content;
                        mail.setMailContent(body);
                        mail.setMailSubject(msg.getSubject());
                        mail.setMailFrom(msg.getFrom().toString());
                        dao.saveMail(mail);
                        //return mail;

                    } else if (content instanceof Multipart) {
                        Multipart mp = (Multipart) content;
                        BodyPart bp = mp.getBodyPart(0);
                        mail.setMailContent(bp.getContent().toString());
                        mail.setMailContentType(bp.getContentType().toString());
                        mail.setMailSubject(msg.getSubject());
                        System.out.println("CONTENT:" + bp.getContent());
                        //return mail;
                        dao.saveMail(mail);
                    }



            }
            }}catch(Exception mex){
                mex.printStackTrace();
            }


    }


   // public Message[] getMessage() {
        //return message;
    //}

    //public void setMessage(Message[] message) {
        //this.message = message;
    //}

}