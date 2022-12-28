package spring.api.news.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="Comments")
public class Comment {

    @Id
    @GeneratedValue
    private long id;
    private String context;
    private Date creation_date;

    enum State{CREATED,CHECKED};
    private State state;

    @ManyToOne
    @JoinColumn(name="news_id")
    private News news;

    public Comment() {
    }

    //Setters
    public void setComment_id(Long comment_id) {
        this.id = comment_id;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setCreation_date(Date creation_date) {
        this.creation_date = creation_date;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setNews(News news) {
        this.news = news;
    }

    //Getters
    public Long getComment_id() {
        return id;
    }

    public Date getCreation_date() {
        return creation_date;
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
}

