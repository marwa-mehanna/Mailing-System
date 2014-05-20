package de.eonas.website.activities.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

@Entity
public class Mail {
    @javax.persistence.Id
    @GeneratedValue
    long Id;
    String MailFrom;
    String MailTo;
    String MailCC;
    String MailBCC;
    String MailSubject;
    String MailContent;
    String MailContentType;
    boolean Mailsent;
    boolean Mailread;

    public String getMailCC() {
        return MailCC;
    }

    public void setMailCC(String mailCC) {
        MailCC = mailCC;
    }

    public String getMailFrom() {
        return MailFrom;
    }

    public void setMailFrom(String mailFrom) {
        MailFrom = mailFrom;
    }

    public String getMailTo() {
        return MailTo;
    }

    public void setMailTo(String mailTo) {
        MailTo = mailTo;
    }

    public String getMailBCC() {
        return MailBCC;
    }

    public void setMailBCC(String mailBCC) {
        MailBCC = mailBCC;
    }

    public String getMailSubject() {
        return MailSubject;
    }

    public void setMailSubject(String mailSubject) {
        MailSubject = mailSubject;
    }

    public String getMailContent() {
        return MailContent;
    }

    public void setMailContent(String mailContent) {
        MailContent = mailContent;
    }

    public boolean isMailread() {
        return Mailread;
    }

    public void setMailread(boolean mailread) {
        Mailread = mailread;
    }

    public boolean isMailsent() {
        return Mailsent;
    }

    public void setMailsent(boolean mailsent) {
        Mailsent = mailsent;
    }

    public String getMailContentType() {
        return MailContentType;
    }

    public void setMailContentType(String mailContentType) {
        MailContentType = mailContentType;
    }


}
