package de.eonas.website.activities;
import de.eonas.website.activities.model.Mail;
import de.eonas.website.activities.model.Mailer;

import javax.mail.*;
import java.util.Properties;

public class ReceiveMail {

    public static Mail receiveEmail() {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        try {
            Mail mail = new Mail();
            Mailer account=new Mailer();
            String accountconc=account.getUsername()+"@"+account.getHost();
            String accountpassword=account.getPassword();
            Session session = Session.getInstance(props, null);
            Store store = session.getStore();
            store.connect("imap.gmail.com", accountconc,accountpassword);
            System.out.println("Established Connection to Server!");
            Folder inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_ONLY);
            Message msg = inbox.getMessage(inbox.getMessageCount());
            System.out.println("Found specified Folder, retrieving the latest message...");
            Address[] in = msg.getFrom();

            for (Address address : in) {

                //System.out.println("FROM:" + address.toString());
                mail.setMailFrom(address.toString());
            }
            Multipart mp = (Multipart) msg.getContent();
            BodyPart bp = mp.getBodyPart(0);

                mail.setMailContent(bp.getContent().toString());
                mail.setMailContentType(bp.getContentType().toString());
                mail.setMailSubject(msg.getSubject());

            System.out.println("SENT DATE:" + msg.getSentDate());
            System.out.println("SUBJECT:" + msg.getSubject());
            System.out.println("CONTENT:" + bp.getContent());
            return mail;
        } catch (Exception mex) {
            mex.printStackTrace();
        }
        return null;
    }
       /* try {
            //1) get the session object
            properties properties = new properties();
            properties.put("mail.store.protocol", "imaps");
            //session emailsession = session.getdefaultinstance(properties);
            session emailsession = session.getdefaultinstance(properties,
                    new javax.mail.authenticator() {
                        protected passwordauthentication getpasswordauthentication() {
                            return new passwordauthentication(user, password);
                        }
                    }
            );

            //2) create the pop3 store object and connect with the pop server
            store emailstore = emailsession.getstore("imaps");
            emailstore.connect("imap.gmail.com", "marwa.mehana@gmail.com", password);


            //3) create the folder object and open it
            folder emailfolder = emailstore.getfolder("inbox");
            emailfolder.open(folder.read_only);

            //4) retrieve the messages from the folder in an array and print it
            message[] messages = emailfolder.getmessages();
            for (int i = 0; i < messages.length; i++) {
                message message = messages[i];
                system.out.println("---------------------------------");
                system.out.println("email number " + (i + 1));
                system.out.println("subject: " + message.getsubject());
                system.out.println("from: " + message.getfrom()[0]);
                system.out.println("text: " + message.getcontent().tostring());
            }
            //5) close the store and folder objects
            emailfolder.close(false);
            emailstore.close();

        } catch (nosuchproviderexception e) {e.printstacktrace();}
        catch (messagingexception e) {e.printstacktrace();}
        catch (ioexception e) {e.printstacktrace();}*/

    }

    //public static void main(String[] args) {

        //String host = "smtp.gmail.com";//change accordingly
        //String mailStoreType = "pop3";
        //final String username= "marwa.mehana@gmail.com";
        //final String password= "";//change accordingly

        //receiveEmail(host, mailStoreType, username, password);

    //}

