package spring.api.news.model;


import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;


//Κλάση που αντιπροσωπεύει την οντότητα των νέων
@Entity
@Table(name = "News")
public class News {
    //Χαρακτήριστικά της κλάσης "News"
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(unique = true)
    private String title;

    private String context;

    private LocalDate creationDate;
    private LocalDate publish_date;
    private String notApprovedMessage;

    private State state;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @ManyToMany(mappedBy = "news")
    private List<Topic> topics;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;


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
        this.creationDate = creation_date;
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

    public void setUser(User user) {
        this.user = user;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public  void addTopic(Topic topic){topics.add(topic);}


    //Getters
    public String getTitle() {
        return title;
    }

    public String getNotApprovedMessage() {
        return notApprovedMessage;
    }

    public LocalDate getCreation_date() {
        return creationDate;
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

    public List<Comment> getComments() {
        return comments;
    }

    @Override
    public String toString() {

        return "{" +
                "\"id\":" + id +
                ", \"title\":\"" + title + '\"' +
                ", \"context\":\"" + context + '\"' +
                ", \"creation_date\":" + creationDate +
                ", \"publish_date\":" + publish_date +
                ", \"notApprovedMessage\":\"" + notApprovedMessage + '\"' +
                ", \"state\":" + state +
                "}";
    }
}
