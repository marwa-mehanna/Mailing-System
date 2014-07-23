package de.eonas.website.activities;

import de.eonas.website.activities.model.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.net.URL;
import java.util.*;

@Component
public class Dao {
    public static final int MAX_CHATLOG_DISPLAY = 15;
    @PersistenceContext(unitName = "Activities")
    EntityManager em;

    public List<Feed> getFeedsByPoi(Poi poi) {
        TypedQuery<Feed> query = em.createQuery("select a from Feed a where poi = :poi order by a.name", Feed.class);
        query.setParameter("poi", poi);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Feed> getFollowedFeeds(Poi poi) {

        TypedQuery<Feed> query = em.createQuery("select a from Feed a, Poi b where a.poi member of b.following and b = :poi", Feed.class);
        query.setParameter("poi", poi);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

   public int getNumberOfFollowedComments(Poi poi, Date first) {
       //Query query = em.createQuery("select count(c) from Comment c, Poi p where c.author member of p.following and p = :poi and c.date > :first");
       //Query query = em.createQuery("select count(c) from Comment c where c.parent = any (select distinct c.parent from Comment c, Poi p where c.author member of p.following and p = :poi) and c.date > :first");
       Query query = em.createQuery("select count(distinct c2) from Poi p, Comment c1, Comment c2 where c1.author member of p.following and c1.parent = c2.parent and p = :poi and c2.date > :first");
       query.setParameter("poi", poi);
       query.setParameter("first", first);

       try {
           return query.getFirstResult();
       } catch (NoResultException e) {
           return 0;
       }
    }

    public int getNumberOfOwnComments(Poi poi, Date first) {
        Query query = em.createQuery("select count(c) from Comment c where c.author = :poi and c.date > :first");
        query.setParameter("poi", poi);
        query.setParameter("first", first);

        try {
            return query.getFirstResult();
        } catch (NoResultException e) {
            return 0;
        }
    }

    public int getNumberOfComments(Poi poi, Date first) {
        Query query = em.createQuery("select count(c) from Poi p, Comment c where (c.parent.feed.poi member of p.following or c.parent.feed.poi = p) and p = :poi and c.date > :first");
        query.setParameter("poi", poi);
        query.setParameter("first", first);

        try {
            return query.getFirstResult();
        } catch (NoResultException e) {
            return 0;
        }
    }

    public List<FeedEntry> getFeedEntriesByFollowedComments(Poi poi) {
        TypedQuery<FeedEntry> query = em.createQuery("SELECT DISTINCT c.parent FROM Comment c, Poi p LEFT JOIN FETCH c.parent.comments LEFT JOIN FETCH c.parent.helpful WHERE c.author MEMBER OF p.following AND p = :poi", FeedEntry.class);
        query.setParameter("poi", poi);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<FeedEntry> getFeedEntries(Feed feed) {
        Query query = em.createNamedQuery("FeedEntry.findByFeed.joinFetch");
        query.setParameter("feed", feed);
        try {
            return query.getResultList();
        } catch ( NoResultException ex ) {
            return new ArrayList<FeedEntry>();
        }
    }
    public List<Mailer>getAllAccounts(){
        TypedQuery<Mailer> query = em.createQuery("select a from Mailer a", Mailer.class);
        return query.getResultList();
    }

    public Feed saveFeed(Feed feed) {
        return em.merge(feed);
    }
    public Mail saveMail(Mail mail) {
        return em.merge(mail);

    }
    public boolean checkMailUnExistence(int MessageNumber,Date recDate,String host){
        TypedQuery<Mail> query = em.createQuery("select a from Mail a where a.messageNumber = :MessageNumber and a.rectDate = :recDate and a.host =:host", Mail.class);
        query.setParameter("MessageNumber", MessageNumber);
        query.setParameter("recDate", recDate);
        query.setParameter("host", host);



            if( query.getResultList().size()==0){
                return true;
            }
            else {
                return false;
            }

    }
    public boolean checkMailUnExistenceByUrl(URL url){
        TypedQuery<Mail> query = em.createQuery("select a from Mail a where a.url= :url", Mail.class);
        query.setParameter("url", url);



        if( query.getResultList().size()==0){
            return true;
        }
        else {
            return false;
        }

    }


    public void saveFeedEntry(FeedEntry feedEntry) {
        em.merge(feedEntry);
    }

    public List<Feed> getAllFeeds() {
        TypedQuery<Feed> query = em.createQuery("select a from Feed a", Feed.class);
        return query.getResultList();
    }

    public List<FeedEntry> getFeedEntries(Feed feed, Date first) {
        TypedQuery<FeedEntry> query = em.createQuery("select a from FeedEntry a where feed = :feed and date >= :first order by date", FeedEntry.class);
        query.setParameter("feed", feed);
        query.setParameter("first", first);
        return query.getResultList();
    }

    public PortletSettings fetchSettings(String windowId) {
        TypedQuery<PortletSettings> query = em.createQuery("select a from PortletSettings a where a.portletId = :portletId", PortletSettings.class);
        query.setParameter("portletId", windowId);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    public PortletSettings getDefaultValues(String windowId) {
        PortletSettings portletSettings = new PortletSettings();
        portletSettings.setPortletId(windowId);

        return portletSettings;
    }
    public List<Person> getAllPersons() {
        TypedQuery<Person> query = em.createQuery("select a from Person a order by name ", Person.class);
        return query.getResultList();
    }

    public List<Mail> getAllGMails() {
        TypedQuery<Mail> query = em.createQuery("select a from Mail a where a.host='gmail.com' order by current_date ", Mail.class);
        return query.getResultList();
    }
    public List<Mail> getAllHMails() {
        TypedQuery<Mail> query = em.createQuery("select a from Mail a where a.host='live.com' order by current_date ", Mail.class);
        return query.getResultList();
    }
    public List<Mail> getAllYMails() {
        TypedQuery<Mail> query = em.createQuery("select a from Mail a where a.host='mail.yahoo.com' order by current_date ", Mail.class);
        return query.getResultList();
    }

    public List<Topic> getAllTopics() {
        TypedQuery<Topic> query = em.createQuery("select a from Topic a order by name", Topic.class);
        return query.getResultList();
    }

    public Person getPersonById(long id) {
        TypedQuery<Person> query = em.createQuery("select a from Person a where a.id = :id", Person.class);
        query.setParameter("id", id);

        return query.getSingleResult();
    }

    public Topic getTopicById(long id) {
        TypedQuery<Topic> query = em.createQuery("select a from Topic a where a.id = :id", Topic.class);
        query.setParameter("id", id);

        return query.getSingleResult();
    }

    public Poi getPoiById(long id) {
        TypedQuery<Poi> query = em.createQuery("select a from Poi a where a.id = :id", Poi.class);
        query.setParameter("id", id);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void savePortletSettings(PortletSettings settings) {
        em.merge(settings);
    }

    public void savePoi(Poi poi) {
        em.merge(poi);
    }

    public void removeFeed(Feed feed) {
        em.remove(em.merge(feed));
    }

    public List<ICal> getAllICals() {
        TypedQuery<ICal> query = em.createQuery("select a from ICal a", ICal.class);
        return query.getResultList();
    }

    public List<ICalEntry> getICalEntries(ICal iCal) {
        TypedQuery<ICalEntry> query = em.createQuery("select a from ICalEntry a where iCal = :iCal", ICalEntry.class);
        query.setParameter("iCal", iCal);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;

        }
    }

    public ICal saveICal(ICal iCal) {
        return em.merge(iCal);
    }
    public Mailer saveAccount(Mailer account){return em.merge(account);}

    public void saveICalEntry(ICalEntry iCalEntry) {
        em.merge(iCalEntry);
    }

    public List<ICal> getICalsByPoi(Poi poi) {
        TypedQuery<ICal> query = em.createQuery("select a from ICal a where poi = :poi", ICal.class);
        query.setParameter("poi", poi);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }
    public List<Mailer> getAccountsByPoi(Poi poi){
        TypedQuery<Mailer> query=em.createQuery("select a from Mailer a where poi= :poi",Mailer.class);
        query.setParameter("poi",poi);
        try{
            return query.getResultList();
        }catch (NoResultException e){
            return null;
        }
    }
    public Mail getSelectedMail(int ms){
        TypedQuery<Mail> query=em.createQuery("select a from Mail a where a.messageNumber = :ms ",Mail.class);
        query.setParameter("ms",ms);
        return query.getSingleResult();
    }
    public Mail getSMail(int ms,Date date){
        TypedQuery<Mail> query=em.createQuery("select a from Mail a where a.messageNumber = :ms and a.rectDate =:date",Mail.class);
        query.setParameter("ms",ms);
        query.setParameter("date",date);
        return query.getSingleResult();
    }
    public Mailer getAccountDetails(String account){
        TypedQuery<Mailer> query=em.createQuery("select a from Mailer a where a.username = :account",Mailer.class);
        query.setParameter("account",account);
        try{
            return query.getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    public void removeICal(ICal ical) {
        em.remove(em.merge(ical));
    }

    public List<ICalEntry> getICalEntries(ICal iCal, Date first) {
        TypedQuery<ICalEntry> query = em.createQuery("select a from ICalEntry a where iCal= :iCal and startDate >= :first order by startDate", ICalEntry.class);
        query.setParameter("iCal", iCal);
        query.setParameter("first", first);
        return query.getResultList();

    }

    public boolean isFeedEntryHelpfulForPerson(FeedEntry entry, Person person) {
        return entry.getHelpful().contains(person);
        /*TypedQuery<Integer> query = em.createQuery("select count(*) from FeedEntry a where a = :entry and :person member of a.helpful", Integer.class);
        query.setParameter("person", person);
        query.setParameter("entry", entry);
        int res = query.getSingleResult();
        return res > 0;*/
    }


    public void saveComment(Comment comment) {
        FeedEntry parent = comment.getParent();
        Set<Comment> comments = parent.getComments();
        if ( comments == null ) comments = new HashSet<Comment>();
        comments.add(comment);
        Poi poi = comment.getAuthor();
        poi = em.merge(poi);
        poi.getComments().add(comment);

        em.merge(poi);
        em.merge(parent);
        em.merge(comment);
    }

    public Poi getPoiWithFollowing(Poi poi) {
        TypedQuery<Poi> query = em.createQuery("select a from Poi a left join fetch a.following where a = :poi", Poi.class);
        query.setParameter("poi", poi);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void savePoi(Set<Poi> following) {
        em.merge(following);
    }

    public Feed getStatusFeed(Poi poi) {
        TypedQuery<Feed> query = em.createQuery("select a from Feed a left join fetch a.feedEntries where a.poi = :poi and a.url = ''", Feed.class);
        query.setParameter("poi", poi);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            Feed f = new Feed("", "", "", "", poi);
            f.setFeedEntries(new HashSet<FeedEntry>());
            f = saveFeed(f);
            return f;
        }
    }

    public Person getPersonByName(String name) {
        TypedQuery<Person> query = em.createQuery("select a from Person a where a.name = :name", Person.class);
        query.setParameter("name", name);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
