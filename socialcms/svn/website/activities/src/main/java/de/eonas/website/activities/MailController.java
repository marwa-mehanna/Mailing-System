package de.eonas.website.activities;

import de.eonas.website.activities.model.Mailer;
import de.eonas.website.activities.model.Person;
import de.eonas.website.activities.model.Poi;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

@ManagedBean
@SessionScoped
public class MailController extends AbstractController{



    private String name;
    private String username;
    private String password;
    private String host;

    private String from;
    private String to;
    private String subject;
    private String content;




    private List<Person> allPersons;


    public List<Mailer> getAllAccounts() {
        return allAccounts;
    }

    public void setAllAccounts(List<Mailer> allAccounts) {
        this.allAccounts = allAccounts;
    }

    private List<Mailer>allAccounts;


   public MailController() throws IOException{
       init();
       allPersons = dao.getAllPersons();
       updateIMapList();

   }
    public void addAccount(){
        Poi poi = getPoi();

        Mailer account=new Mailer();
        account.setUsername(username);
        account.setPassword(password);
        account.setHost(host);
        account.setPoi(poi);
        dao.getAccountsByPoi(poi).add(account);
        dao.saveAccount(account);
        dao.savePoi(poi);
        username="";
        password="";
        host="";

    }
    private void updateIMapList() {

        Poi poi = getPoi();
        allAccounts = dao.getAccountsByPoi(poi);
    }


    public void setNewPoi(long id) {
        System.out.print(id);
        Poi poi = dao.getPoiById(id);
        settings.setPrimaryPoi(poi);
        dao.savePortletSettings(settings);
    }
    public void send() throws MessagingException {
        SendMail sent=new SendMail(from,to,subject,content);
        try {
            sent.send();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }


    public Poi getPoi() {
        return settings.getPrimaryPoi();
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public List<Person> getAllPersons() {
        return allPersons;
    }

    public void setAllPersons(List<Person> allPersons) {
        this.allPersons = allPersons;
    }
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



}
