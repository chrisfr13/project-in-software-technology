package spring.api.news.repository;

import org.springframework.data.repository.CrudRepository;
import spring.api.news.model.Topic;

import java.util.List;

public interface TopicRepository extends CrudRepository<Topic,Long> {
Topic findByTitle(String title);
}
