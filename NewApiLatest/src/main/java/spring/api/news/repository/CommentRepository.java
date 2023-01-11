package spring.api.news.repository;

import spring.api.news.model.Comment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface CommentRepository extends CrudRepository<Comment,Long> {
    List<Comment> findByOrderByCreationDateAsc();
}
