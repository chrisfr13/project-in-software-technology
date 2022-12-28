package spring.api.news.model;


import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "News")
public class News {
    @Id
    @GeneratedValue
    private long id;
    @Column(unique = true)
    private String title;

    //@Column(unique = true)
    private String context;

    //@Column(nullable = false)
    private LocalDate creation_date;
    private LocalDate publish_date;
    private String notApprovedMessage;

    public enum State {CREATED, SUBMITTED, APPROVED, PUBLISHED}
    private State state;

    @OneToMany(mappedBy = "news")
    private List<Comment> comments;

    @ManyToMany(mappedBy = "news")
    private List<Topic> topics;

    public News() {
    }

    //Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setCreation_date(LocalDate creation_date) {
        this.creation_date = creation_date;
    }

    public void setPublish_date(LocalDate publish_date) {
        this.publish_date = publish_date;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public void setNotApprovedMessage(String notApprovedMessage) {
        this.notApprovedMessage = notApprovedMessage;
    }

    //Getters
    public String getTitle() {
        return title;
    }

    public String getNotApprovedMessage() {
        return notApprovedMessage;
    }

    public LocalDate getCreation_date() {
        return creation_date;
    }

    public LocalDate getPublish_date() {
        return publish_date;
    }

    public Long getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public String getContext() {
        return context;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"title\":\"" + title + '\"' +
                ", \"context\":\"" + context + '\"' +
                ", \"creation_date\":" + creation_date +
                ", \"publish_date\":" + publish_date +
                ", \"notApprovedMessage\":\"" + notApprovedMessage + '\"' +
                ", \"state\":" + state +
                ", \"comments\":" + comments +
                ", \"topics\":" + topics +
                "}";
    }
}
