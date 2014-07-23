package de.eonas.website.activities.service;

import com.sun.mail.gimap.GmailFolder;
import com.sun.mail.gimap.GmailMessage;
import com.sun.mail.gimap.GmailSSLStore;
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
import java.net.URL;
import java.util.*;

@org.springframework.stereotype.Component
public class BackgroundService {
    Logger LOG = Logger.getLogger(BackgroundService.class);
    int oldMessagecount;



    int x;
    int y;
    int z;



    @Autowired
    Dao dao;
    List<Integer>updatedMessageNo=new ArrayList<Integer>(4);
    List<Integer>updatedMessageNohotmail=new ArrayList<Integer>(4);
    List<Integer>updatedMessageZ=new ArrayList<Integer>(4);

    public BackgroundService() {
        x=0;
        updatedMessageZ.add(z,Integer.valueOf(0));
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
    public void updateGMail(){
        List<Mailer> accounts=dao.getAllAccounts();
        y=0;
        for(int i=0;i<accounts.size();i++) {

            System.out.println(accounts.get(i).getHost());
            if(accounts.get(i).getHost().equals("gmail.com")){
            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "gimaps");
            try {

                updatedMessageNo.add(y,Integer.valueOf(0));

                String account = accounts.get(i).getUsername();
                String accountpassword = accounts.get(i).getPassword();
                String host ="imap."+""+accounts.get(i).getHost();
                GmailFolder folder = null;
                GmailSSLStore store = null;


                Session session = Session.getInstance(props, null);
                store =(GmailSSLStore) session.getStore();
                store.connect(account, accountpassword);
                System.out.println("Established Connection to Server! gmail");
                folder = (GmailFolder)store.getFolder("Inbox");
                folder.open(Folder.READ_ONLY);
                Message[] ms=folder.getMessages();
                FetchProfile fp = new FetchProfile();
                fp.add(GmailFolder.FetchProfileItem.MSGID);
                fp.add(GmailFolder.FetchProfileItem.THRID);
                fp.add(GmailFolder.FetchProfileItem.LABELS);

                folder.fetch(ms, fp);
                updatedMessageNo.add(x+1,Integer.valueOf(folder.getMessageCount()));
                Message message[] = folder.getMessages();
                GmailMessage gm;
                System.out.println("Found specified Folder, retrieving the latest message...");
                Mail mail = new Mail();
                for (Message m : ms) {
                    gm = (GmailMessage) m;
                    //System.out.println(gm.getMsgId());
                    // Hex version - useful for linking to Gmail
                    System.out.println(Long.toHexString(gm.getMsgId()));
                    String id= Long.toHexString(gm.getMsgId())+"";
                    String x ="https://mail.google.com/mail/#inbox/"+id;
                    URL url=new URL(x);
                    boolean f = dao.checkMailUnExistenceByUrl(url);
                    System.out.print(f);
                    if (f) {
                    //System.out.println(url);
                    Object content = gm.getContent();
                    if (content instanceof String) {
                        String body = (String) content;
                        mail.setMailContent(body);
                        mail.setMailSubject(gm.getSubject());
                        mail.setMailFrom(gm.getFrom().toString());
                        mail.setMessageNumber(gm.getMessageNumber());
                        mail.setHost(accounts.get(i).getHost());
                        mail.setRectDate(gm.getReceivedDate());
                        mail.setUrl(url);
                        mail.setMailFrom(gm.getSender().toString());
                        dao.saveMail(mail);
                        //return mail;

                    } else if (content instanceof Multipart) {
                        Multipart mp = (Multipart) content;
                        BodyPart bp = mp.getBodyPart(0);
                        mail.setMailContent(bp.getContent().toString());
                        mail.setMailContentType(bp.getContentType().toString());
                        mail.setMailSubject(gm.getSubject());
                        mail.setMessageNumber(gm.getMessageNumber());
                        mail.setHost(accounts.get(i).getHost());
                        mail.setRectDate(gm.getReceivedDate());
                        mail.setUrl(url);
                        mail.setMailFrom(gm.getSender().toString());

                        System.out.println("CONTENT:" + bp.getContent());
                        //return mail;
                        dao.saveMail(mail);
                    }
                }

            }


            } catch (Exception mex) {
                mex.printStackTrace();
            }}
        }
    }


@Scheduled(fixedDelay = 300000)
public void updateMail() {
    List<Mailer> accounts = dao.getAllAccounts();
    x = 0;
    for (int i = 0; i < accounts.size(); i++) {

        if (accounts.get(i).getHost().equals("live.com")) {
            Properties pop3Props = new Properties();
            pop3Props.setProperty("mail.pop3s.port",  "995");

                try{
                    updatedMessageNohotmail.add(x, Integer.valueOf(0));

                    String account = accounts.get(i).getUsername();
                    String accountpassword = accounts.get(i).getPassword();
                    String host = "pop3.live.com";
                    Session session = Session.getInstance(pop3Props, null);
                    Store store = session.getStore("pop3s");
                    store.connect(host, 995, account, accountpassword);

                    System.out.println("Established Connection to Server!");
                    Folder inbox = store.getFolder("Inbox");
                    inbox.open(Folder.READ_ONLY);
                    System.out.println(inbox.getMessageCount());
                    System.out.print(Integer.valueOf(inbox.getMessageCount()));
                    updatedMessageNohotmail.add(x + 1, Integer.valueOf(inbox.getMessageCount()));
                    Message message[] = inbox.getMessages();
                    if (updatedMessageNohotmail.get(x).intValue() != updatedMessageNohotmail.get(x + 1).intValue()) {
                        for (int j = updatedMessageNohotmail.get(x).intValue(); j < updatedMessageNohotmail.get(x + 1).intValue(); j++) {
                            Message msg = message[j];
                            boolean f = dao.checkMailUnExistence(msg.getMessageNumber(), msg.getReceivedDate(), accounts.get(i).getHost());
                            System.out.print(f);
                            if (f)
                            {
                                    System.out.println("Found specified Folder, retrieving the latest message...");


                                    Mail mail = new Mail();
                                    Address[] in = msg.getFrom();

                                    for (Address address : in) {
                                    mail.setMailFrom(address.toString());
                                    }
                                    Object content = msg.getContent();
                                    if (content instanceof String) {
                                        String body = (String) content;
                                        mail.setMailSubject(msg.getSubject());
                                        mail.setMailFrom(msg.getFrom().toString());
                                        mail.setMessageNumber(msg.getMessageNumber());
                                        mail.setHost(accounts.get(i).getHost());
                                        mail.setRectDate(msg.getReceivedDate());
                                        dao.saveMail(mail);
                                    } else if (content instanceof Multipart) {
                                        Multipart mp = (Multipart) content;
                                        int count3 = mp.getCount();
                                        for (int l = 0; l < count3; l++) {
                                            BodyPart bp = mp.getBodyPart(l);
                                            Object o2 = bp.getContent();
                                            if (o2 instanceof String) {
                                                String body = (String) o2;
                                                //mail.setMailContent(body);
                                                mail.setMailSubject(msg.getSubject());
                                                mail.setMailFrom(msg.getFrom().toString());
                                                mail.setMessageNumber(msg.getMessageNumber());
                                                mail.setHost(accounts.get(i).getHost());
                                                mail.setRectDate(msg.getReceivedDate());
                                                dao.saveMail(mail);
                                            }
                                            else if (o2 instanceof Multipart) {
                                                System.out.print(
                                                    "**This BodyPart is a nested Multipart.  ");
                                                Multipart mp2 = (Multipart)o2;
                                                int count2 = mp2.getCount();
                                                mp=mp2;
                                                count3=count2;
                                            }
                                        }

                                    }
                                }
                        }
                        updatedMessageNohotmail.add(x, Integer.valueOf(inbox.getMessageCount()));
                        System.out.print(oldMessagecount);
                        x = x + 2;

                    }
            } catch (Exception mex) {
                mex.printStackTrace();
            }
        }else if(accounts.get(i).getHost().equals("mail.yahoo.com")){
            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "imaps");
            try {
                //Flags seen = new Flags(Flags.Flag.SEEN);
                // FlagTerm unseenFlagTerm = new FlagTerm(seen, false);

                updatedMessageNo.add(z,Integer.valueOf(0));

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
                updatedMessageZ.add(z+1,Integer.valueOf(inbox.getMessageCount()));
                //int newMessagecount = inbox.getMessageCount();
                //Message msg = inbox.getMessage(inbox.getMessageCount());
                //message = inbox.getMessages(1,inbox.getMessageCount());
                //for (int i = 3; i < inbox.getMessageCount(); i++) {

                // message[i].equals(inbox.getMessage(i));
                //}
                Message message[] = inbox.getMessages();
                if (updatedMessageZ.get(z).intValue() != updatedMessageZ.get(z+1).intValue()) {
                    for (int j = updatedMessageZ.get(z).intValue(); j < updatedMessageZ.get(z+1).intValue(); j++) {
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
                    updatedMessageZ.add(z,Integer.valueOf(inbox.getMessageCount()));
                    System.out.print(oldMessagecount);
                    z=z+2;
                    //System.out.print(newMessagecount);
                }

            } catch (Exception mex) {
                mex.printStackTrace();
            }

        }

    }
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