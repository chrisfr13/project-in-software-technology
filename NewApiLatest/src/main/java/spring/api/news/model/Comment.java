package spring.api.news.model;

import javax.persistence.*;
import java.time.LocalDate;

//Κλάση που αντιπροσωπεύει την οντότητα των σχολίων
@Entity
@Table(name = "Comments")
public class Comment {

    //Χαρακτήριστικά της κλάσης "Comment"
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private String context;
    private LocalDate creationDate;

    public enum State {CREATED, APPROVED}

    private State state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id")
    private News news;


    private String fullName;


    public Comment() {
    }

    //Setters
    public void setComment_id(Long comment_id) {
        this.id = comment_id;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setCreation_date(LocalDate creation_date) {
        this.creationDate = creation_date;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    //Getters
    public Long getComment_id() {
        return id;
    }

    public LocalDate getCreation_date() {
        return creationDate;
    }

    public String getContext() {
        return context;
    }

    public State getState() {
        return state;
    }

    public News getNews() {
        return news;
    }


    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", context='" + context + '\'' +
                ", creation_date=" + creationDate +
                ", state=" + state +
                ", news_id=" + news.getId() +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}

