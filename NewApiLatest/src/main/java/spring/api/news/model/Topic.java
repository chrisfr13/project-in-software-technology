package spring.api.news.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

//Κλάση που αντιπροσωπεύει την οντότητα των θεμάτων
@Entity
@Table(name = "Topics")
public class Topic {

    //Χαρακτήριστικά της κλάσης "Topic"
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate creationDate;

    @ManyToOne
    @JoinColumn(name = "parent_topic_id")
    private Topic parentTopic;

    @OneToMany
    private List<Topic> childTopics;


    public enum State {CREATED, APPROVED}

    private State state;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "news_topic",
            joinColumns = @JoinColumn(name = "news_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id",
                    referencedColumnName = "id"))
    private List<News> news;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;



    //Setters
    public void setParentTopic(Topic parentTopic) {
        this.parentTopic = parentTopic;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setChildTopics(List<Topic> childTopics) {
        this.childTopics = childTopics;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }

    //Getters
    public Long getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public List<Topic> getChildTopics() {
        return childTopics;
    }

    public Topic getParentTopic() {
        return parentTopic;
    }

    public List<News> getNews() {
        return news;
    }


    public void addArticle(News article){
        news.add(article);
    }

    @Override
    public String toString() {
        String s="";
        if(parentTopic!=null)
            s=", parentTopic=" + parentTopic.title;
        return "Topic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", creationDate=" + creationDate +
                 s+

                ", state=" + state +
                ", news=" + news.toString() +
                '}';
    }

}
