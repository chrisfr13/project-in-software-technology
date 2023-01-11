package spring.api.news.repository;

import spring.api.news.model.News;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;


public interface NewsRepository extends CrudRepository<News,Long> {

    List<News> findByOrderByStateDescCreationDateAsc();

    Optional<News> findByTitle(String title);

}
