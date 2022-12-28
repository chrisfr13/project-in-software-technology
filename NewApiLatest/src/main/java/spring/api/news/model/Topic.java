package spring.api.news.model;

import com.sun.istack.NotNull;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Topics")
public class Topic {
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

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", creationDate=" + creationDate +
                ", parentTopic=" + parentTopic +
                ", childTopics=" + childTopics +
                ", state=" + state +
                ", news=" + news +
                '}';
    }


    /*
    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"title\":\"" + title + '\"' +
                ", \"creation_date\":" + creationDate +
                ", \"publish_date\":" + publish_date +
                ", \"notApprovedMessage\":\"" + notApprovedMessage + '\"' +
                ", \"state\":" + state +
                ", \"comments\":" + comments +
                ", \"topics\":" + topics +
                "}";
    }*/
}
