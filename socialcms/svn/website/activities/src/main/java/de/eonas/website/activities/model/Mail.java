package de.eonas.website.activities.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import java.util.Date;

@Entity
public class Mail {
    @javax.persistence.Id
    @GeneratedValue
    long Id;
    String MailFrom;
    String MailTo;
    String MailCC;
    String MailBCC;
    private String MailSubject;
    String MailContent;
    String MailContentType;
    private int messageNumber;
    boolean Mailread;
    private String host;


    private Date rectDate;

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

    public String getMailContentType() {
        return MailContentType;
    }

    public void setMailContentType(String mailContentType) {
        MailContentType = mailContentType;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
    public Date getRectDate() {
        return rectDate;
    }

    public void setRectDate(Date rectDate) {
        this.rectDate = rectDate;
    }



}

