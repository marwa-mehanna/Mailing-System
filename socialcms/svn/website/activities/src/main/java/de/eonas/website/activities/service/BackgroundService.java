package de.eonas.website.activities.service;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import de.eonas.website.activities.Dao;
import de.eonas.website.activities.model.*;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VJournal;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.scheduling.annotation.Scheduled;
import sun.misc.BASE64Encoder;

import javax.mail.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@org.springframework.stereotype.Component
public class BackgroundService {
    Logger LOG = Logger.getLogger(BackgroundService.class);
    int oldMessagecount;


    int x;



    @Autowired
    Dao dao;
    List<Integer>updatedMessageNo=new ArrayList<Integer>(4);

    public BackgroundService() {
        x=0;
        updatedMessageNo.add(x,Integer.valueOf(0));
    }

    public Feed addAndCheckFeed ( Feed feed ) throws IOException, FeedException {
        getFeed(feed); /* check feed availability */
        return dao.saveFeed(feed);
    }

    public FeedEntry sanitizedFeed(SyndEntry entry) {
        FeedEntry f = new FeedEntry();

        String subject = entry.getTitle().trim();

        String description = "";
        final SyndContent entryDescription = entry.getDescription();
        if (entryDescription != null && entryDescription.getValue() != null) {
            description = entryDescription.getValue().replaceAll("(<[^>]*>)", "").trim();
        }

        String link = entry.getLink().trim();
        Date date = entry.getPublishedDate();

        f.setSubject(subject);
        f.setLink(link);
        f.setDate(date);
        f.setDescription(description);
        return f;
    }
    @Scheduled(fixedDelay = 200000)
    public void updateMail(){
        //ReceiveMail m= new ReceiveMail();
       List<Mailer> accounts=dao.getAllAccounts();
        x=0;
        //m.receiveEmail(accounts);
        for(int i=0;i<accounts.size();i++) {
            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "imaps");
            try {
                //Flags seen = new Flags(Flags.Flag.SEEN);
                // FlagTerm unseenFlagTerm = new FlagTerm(seen, false);

                updatedMessageNo.add(x,Integer.valueOf(0));

                String account = accounts.get(i).getUsername();
                String accountpassword = accounts.get(i).getPassword();
                String host ="imap."+""+accounts.get(i).getHost();

                Session session = Session.getInstance(props, null);
                Store store = session.getStore();
                store.connect(host, account, accountpassword);
                System.out.println("Established Connection to Server!");
                Folder inbox = store.getFolder("Inbox");
                inbox.open(Folder.READ_ONLY);
                System.out.println(inbox.getMessageCount());
                System.out.print(Integer.valueOf(inbox.getMessageCount()));
                updatedMessageNo.add(x+1,Integer.valueOf(inbox.getMessageCount()));
                //int newMessagecount = inbox.getMessageCount();
                //Message msg = inbox.getMessage(inbox.getMessageCount());
                //message = inbox.getMessages(1,inbox.getMessageCount());
                //for (int i = 3; i < inbox.getMessageCount(); i++) {

                // message[i].equals(inbox.getMessage(i));
                //}
                Message message[] = inbox.getMessages();
                if (updatedMessageNo.get(x).intValue() != updatedMessageNo.get(x+1).intValue()) {
                    for (int j = updatedMessageNo.get(x).intValue(); j < updatedMessageNo.get(x+1).intValue(); j++) {
                        Message msg = message[j];
                        System.out.print(j);
                        //Flags flags = msg.getFlags();

                        boolean f = dao.checkMailUnExistence(msg.getMessageNumber(),msg.getReceivedDate(),accounts.get(i).getHost());
                        System.out.print(f);
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
                                mail.setMessageNumber(msg.getMessageNumber());
                                mail.setHost(accounts.get(i).getHost());
                                mail.setRectDate(msg.getReceivedDate());


                                dao.saveMail(mail);
                                //return mail;

                            } else if (content instanceof Multipart) {
                                Multipart mp = (Multipart) content;
                                BodyPart bp = mp.getBodyPart(0);
                                mail.setMailContent(bp.getContent().toString());
                                mail.setMailContentType(bp.getContentType().toString());
                                mail.setMailSubject(msg.getSubject());
                                mail.setMessageNumber(msg.getMessageNumber());
                                mail.setHost(accounts.get(i).getHost());
                                mail.setRectDate(msg.getReceivedDate());

                                System.out.println("CONTENT:" + bp.getContent());
                                //return mail;
                                dao.saveMail(mail);
                            }


                        }
                    }
                    updatedMessageNo.add(x,Integer.valueOf(inbox.getMessageCount()));
                    System.out.print(oldMessagecount);
                    x=x+2;
                    //System.out.print(newMessagecount);
                }

            } catch (Exception mex) {
                mex.printStackTrace();
            }
        }



        /*Message[] temp = m.getMessage();
        for (int i = 3; i < temp.length; i++) {
            Message msg = temp[i];
            boolean f=dao.checkMailUnExistence(msg.getMessageNumber());
            if (f){
                Mail mail = new Mail();
                msg.getMessageNumber();
                //Flags flags = msg.getFlags();

                System.out.println("Found specified Folder, retrieving the latest message...");

                try{

                    Address[] in = msg.getFrom();

                    for (Address address : in) {

                        //System.out.println("FROM:" + address.toString());
                        mail.setMailFrom(address.toString());
                    }
                    Object content = msg.getContent();
                    if (content instanceof String)
                    {
                        String body = (String)content;
                        mail.setMailContent(body);
                        mail.setMailSubject(msg.getSubject());
                        mail.setMailFrom(msg.getFrom().toString());
                        mail.setMessageNumber(msg.getMessageNumber());
                        dao.saveMail(mail);


                    }
                    else if (content instanceof Multipart)
                    {
                        Multipart mp = (Multipart)content;
                        BodyPart bp = mp.getBodyPart(0);
                        mail.setMailContent(bp.getContent().toString());
                        mail.setMailContentType(bp.getContentType().toString());
                        mail.setMailSubject(msg.getSubject());
                        mail.setMessageNumber(msg.getMessageNumber());
                        dao.saveMail(mail);
                        System.out.println("CONTENT:" + bp.getContent());

                    }
                }catch (Exception mex) {
                    mex.printStackTrace();
                }

            }

        }*/





    }

    @Scheduled(fixedDelay = 120000)
    public void updateRss() {
        List<Feed> feeds = dao.getAllFeeds();
        LOG.info("Scanning " + feeds.size() + " feeds.");
        for (Feed feed : feeds) {
            String url = feed.getUrl();
            if (url != null && !url.trim().equals("")) {
                Date now = new Date();
                boolean entriesHaveBeenUpdated = false;

                try {
                    SyndFeed webFeed = getFeed(feed);
                    feed.setLastSuccessfulUpdate(now);
                    // feed als ok markieren

                    Date first = null;
                    Date last = null;
                    final List<com.sun.syndication.feed.synd.SyndEntry> entries = webFeed.getEntries();
                    ArrayList<FeedEntry> sanitizedList = new ArrayList<FeedEntry>();
                    for (SyndEntry entry : entries) {
                        FeedEntry webEntry = sanitizedFeed(entry);
                        webEntry.setFeed(feed);
                        sanitizedList.add(webEntry);
                        Date date = webEntry.getDate();
                        if (date != null) {
                            if (first == null || date.before(first)) {
                                first = date;
                            }
                            if (last == null || date.after(last)) {
                                last = date;
                            }
                        } else {
                            LOG.warn("Skipping feed entry " + entry.getTitle() + ", date is null.");
                        }
                    }
                    LOG.info("First " + first + " / Last " + last);
                    List<FeedEntry> dbEntries = dao.getFeedEntries(feed, first);
                    LOG.info("Fetched " + dbEntries.size());
                    for (FeedEntry feedEntry : sanitizedList) {
                        if (!dbEntries.contains(feedEntry)) {
                            entriesHaveBeenUpdated = true;
                            try {
                                dao.saveFeedEntry(feedEntry);
                                LOG.info("Saved " + feedEntry.getSubject());
                            } catch ( JpaSystemException ex ) {
                                LOG.warn("Failed to save entry " + feedEntry.getSubject(), ex);
                            }
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Feed " + feed.getUrl() + " failed.", ex);
                    // feed als fehlgeschlagen markieren
                }
                finally {
                    if (entriesHaveBeenUpdated) {
                        feed.setLastUpdated(now);
                    }

                    dao.saveFeed(feed); // update lastUpdated+lastSuccessfulUpdate
                }
            }
        }

    }

    private InputStream getInputStream(String url, String authUser, String authPass) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("User-Agent", "Java");

       if (authUser != null && (authUser.length() > 0) && authPass != null && (authPass.length() > 0)) {
            //httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY, new org.apache.http.auth.UsernamePasswordCredentials(authUser, authPass));
            String loginPassword = authUser + ":" + authPass;
            String encoded = new BASE64Encoder().encode(loginPassword.getBytes());
            httpGet.addHeader("Authorization", "Basic " + encoded);
        }

        org.apache.http.HttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity responseEntity = httpResponse.getEntity();
        return responseEntity.getContent();
    }

    public SyndFeed getFeed(Feed feed) throws IOException, FeedException {
        InputStream content = getInputStream(feed.getUrl(), feed.getAuthUser(), feed.getAuthPass());

        XmlReader reader = new XmlReader(content);
        SyndFeedInput syndFeedInput = new SyndFeedInput();
        syndFeedInput.setXmlHealerOn(true);

        return syndFeedInput.build(reader);
    }

    /*********************************************************************************************************
     *                                        iCalendar
     ********************************************************************************************************/

    private Calendar getCalendar(ICal iCal) throws IOException, ParserException {
        CalendarBuilder calendarBuilder = new CalendarBuilder();
        InputStream inputStream = getInputStream(iCal.getUrl(), iCal.getAuthUser(), iCal.getAuthPass());
        return calendarBuilder.build(inputStream);
    }

    private ICalEntry getICalEntry(Component component) {
        ICalEntry iCalEntry = null;

        if (component instanceof VEvent || component instanceof VToDo || component instanceof VJournal) {
            iCalEntry = new ICalEntry();

            Uid uid = (Uid) component.getProperty(Property.UID);
            if (uid != null && uid.getValue() != null) {
                iCalEntry.setUid(uid.getValue());
            } else {
                return null;
            }

            Description description = (Description) component.getProperty(Property.DESCRIPTION);
            if (description != null) {
                iCalEntry.setDescription(description.getValue());
            }

            Summary summary = (Summary) component.getProperty(Property.SUMMARY);
            if (summary != null) {
                iCalEntry.setSummary(summary.getValue());
            }

            DtStart dtStart = (DtStart) component.getProperty(Property.DTSTART);
            if (dtStart != null) {
                iCalEntry.setStartDate(dtStart.getDate());
            }

            DtEnd dtEnd = (DtEnd) component.getProperty(Property.DTEND);
            if (dtEnd != null) {
                iCalEntry.setEndDate(dtEnd.getDate());
            }
        }

        return iCalEntry;
    }

    private List<ICalEntry> getICalEntries(ICal iCal) throws IOException, ParserException {
        List<ICalEntry> iCalEntries = new ArrayList<ICalEntry>();
        Calendar calendar = getCalendar(iCal);
        for(Iterator iterator = calendar.getComponents().iterator(); iterator.hasNext();) {
            Component component = (Component) iterator.next();
            ICalEntry iCalEntry = getICalEntry(component);
            if(iCalEntry != null) {
                iCalEntry.setICal(iCal);
                iCalEntries.add(iCalEntry);
            }
        }
        return iCalEntries;
    }

    private void updateDbICalEntry(ICalEntry dbEntry, ICalEntry iCalEntry) {
        if (!this.equals(iCalEntry)) return;

        dbEntry.setSummary(iCalEntry.getSummary());
        dbEntry.setDescription(iCalEntry.getDescription());
        dbEntry.setStartDate(iCalEntry.getStartDate());
        dbEntry.setEndDate(iCalEntry.getEndDate());
    }

    @Scheduled(fixedDelay = 120000)
    public void updateICal() {
        List<ICal> iCals = dao.getAllICals();
        for(ICal iCal : iCals) {
            String url = iCal.getUrl();
            if (url != null && !url.trim().equals("")) {
                LOG.info("Scanning iCal url " + url);
                Date now = new Date();
                boolean entriesHaveBeenUpdated = false;

                try {
                    List<ICalEntry> iCalEntries = getICalEntries(iCal);
                    List<ICalEntry> dbEntries = dao.getICalEntries(iCal);

                    iCal.setLastSuccessfulUpdate(now);

                    for(ICalEntry iCalEntry : iCalEntries) {
                        entriesHaveBeenUpdated = true;
                        int idx = dbEntries.indexOf(iCalEntry);
                        if(idx >= 0) {
                            /*ICalEntry dbEntry = dbEntries.get(idx);
                            updateDbICalEntry(dbEntry, iCalEntry);
                            dao.saveICalEntry(dbEntry);*/
                        } else {
                            dao.saveICalEntry(iCalEntry);
                        }
                    }
                } catch (Exception e) {
                    LOG.error("iCalendar " + iCal.getUrl() + " failed.", e);
                } finally {
                    if (entriesHaveBeenUpdated) {
                        iCal.setLastUpdated(now);
                    }

                    dao.saveICal(iCal);
                }

            }
        }
    }
}
