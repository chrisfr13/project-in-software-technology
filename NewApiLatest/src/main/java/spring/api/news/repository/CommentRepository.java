package spring.api.news.repository;

import spring.api.news.model.News;
import org.springframework.data.repository.CrudRepository;


public interface CommentRepository extends CrudRepository<News,Long> {

}
