package spring.api.news.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import spring.api.news.model.News;
import spring.api.news.model.Topic;
import spring.api.news.repository.NewsRepository;
import spring.api.news.repository.TopicRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private TopicRepository topicRepository;


    @GetMapping(path = "all")
    public Iterable findAll() {
        return newsRepository.findAll();
    }

    @GetMapping(path = "/create")
    public @ResponseBody String createNews(@RequestParam String title, @RequestParam String context,
                                           @RequestParam String[] topicTitles) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        News news = new News();
        LocalDate date = LocalDate.now(); // Create a date object
        news.setTitle(title);
        news.setContext(context);
        news.setCreation_date(date);
        news.setState(News.State.CREATED);
        List<Topic> topics = addTopics(topicTitles);
        news.setTopics(topics);
        newsRepository.save(news);

        return "News created";

    }

    @GetMapping(path = "/edit/{id}")
    public @ResponseBody String editNews(@PathVariable("id") long id, @RequestParam String title, @RequestParam String context, @RequestParam String[] topicTitles) {
        List<Topic> topics;
        Optional<News> news = newsRepository.findById(id);
        News newsObj = news.get();
        Optional<News> newsExist = newsRepository.findByTitle(title);

        //Check if there is a news with same title
        if (newsExist.isPresent())
            return "This title already exists";
        else {
            //Check if state is the appropriate one
            if (newsObj.getState() == News.State.CREATED) {
                newsObj.setTitle(title);
                newsObj.setContext(context);
                topics = addTopics(topicTitles);
                newsObj.setTopics(topics);
                newsRepository.save(newsObj);
                return "Updated";
            } else return "The news with specific id cannot be edited (state not created)";
        }

    }

    @GetMapping(path = "/submit/{id}")
    public @ResponseBody String submit(@PathVariable("id") long id) {
        Optional<News> news = newsRepository.findById(id);
        News newsObj = news.get();
        newsObj.setState(News.State.SUBMITTED);
        newsRepository.save(newsObj);
        return "State changed";
    }

    @GetMapping(path = "/approveReject/{id}")
    public @ResponseBody String approveReject(@PathVariable("id") long id, @RequestParam(defaultValue = "false") boolean approved, @RequestParam(required = false) String reason) {
        Optional<News> news = newsRepository.findById(id);
        News newsObj = news.get();
        if (newsObj.getState() == News.State.SUBMITTED) {
            if (approved) {
                newsObj.setState(News.State.APPROVED);
                newsObj.setNotApprovedMessage(null);
                newsRepository.save(newsObj);
                return "Approved.";
            }

            newsObj.setState(News.State.CREATED);
            newsObj.setNotApprovedMessage(reason);
            newsRepository.save(newsObj);
            return "Declined.";
        }

        return "News should be has the submitted state.";
    }


    @GetMapping(path = "/publish/{id}")
    public @ResponseBody String publish(@PathVariable("id") long id) {
        Optional<News> news = newsRepository.findById(id);
        News newsObj = news.get();
        if (newsObj.getState() == News.State.APPROVED) {
            newsObj.setState(News.State.PUBLISHED);
            newsRepository.save(newsObj);
            return "Published";
        }
        return "News should approved first from the moderator.";
    }

    @GetMapping(path = "/view/{id}")
    public @ResponseBody String viewById(@PathVariable("id") long id) {
        Optional<News> news = newsRepository.findById(id);
        News newsObj = news.get();

        if (newsObj.getState() == News.State.PUBLISHED)
            return newsObj.toString();
        else return "You don't have access to view this news";
    }

    public List<Topic> addTopics(String[] topicTitles) {
        List<Topic> topics = new ArrayList<>();

        for (String topicTitle : topicTitles) {
            Topic topic = topicRepository.findByTitle(topicTitle);
            if (topic == null) {
                topic = new Topic();
                topic.setTitle(topicTitle);
                topic.setCreationDate(LocalDate.now());
                topicRepository.save(topic);
            }
            topics.add(topic);

        }
        return topics;
    }
}