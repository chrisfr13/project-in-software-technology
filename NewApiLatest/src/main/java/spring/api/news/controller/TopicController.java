package spring.api.news.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import spring.api.news.model.News;
import spring.api.news.model.Topic;
import spring.api.news.repository.TopicRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/topic")
public class TopicController {

    @Autowired
    private TopicRepository topicRepository;
    @GetMapping(path = "all")
    public Iterable findAll() {
        return topicRepository.findAll();
    }
    @GetMapping(path = "/create")
    public @ResponseBody String createTopic(@RequestParam String title,
                                           @RequestParam(required = false) String parentTopic) {

        //!!!Στο create αν υπάρχει parentTopic θα πρέπει να προστίθεται το συγκεκριμένο θέμα ως child!!!

        Topic topic=new Topic();
        LocalDate date = LocalDate.now();
        topic.setTitle(title);
        topic.setCreationDate(date);
        topic.setState(Topic.State.CREATED);
        Topic pTopic = topicRepository.findByTitle(parentTopic);
        if(pTopic==null){
            pTopic=new Topic();
            pTopic.setTitle(parentTopic);
            pTopic.setCreationDate(date);
            topicRepository.save(pTopic);
        }
        topic.setParentTopic(pTopic);
        topicRepository.save(topic);

        return "Topic created";

    }

    @GetMapping(path = "/edit/{id}")
    public @ResponseBody String editTopic(@PathVariable("id") long id, @RequestParam(required = false) String title, @RequestParam(required = false) String parentTopic) {

        Optional<Topic> topic = topicRepository.findById(id);
        Topic topicObj = topic.get();
        Topic pTopic = topicRepository.findByTitle(parentTopic);
        Topic topicExists=topicRepository.findByTitle(title);

        if(topicExists!=null) return "Topic with specified title already exists";
        else {
            if (topicObj.getState() == Topic.State.CREATED) {
                if(pTopic==null){
                    pTopic=new Topic();
                    pTopic.setTitle(parentTopic);
                    LocalDate date = LocalDate.now();
                    pTopic.setCreationDate(date);
                    topicRepository.save(pTopic);
                }
                topicObj.setTitle(title);
                topicObj.setParentTopic(pTopic);
                topicRepository.save(topicObj);
                return "Updated";
            } else return "The news with specified id cannot be edited (state not created)";
        }

    }


    @GetMapping(path = "/approveReject/{id}")
    public @ResponseBody String approveReject(@PathVariable("id") long id, @RequestParam(defaultValue = "false") boolean approved) {
        Optional<Topic> topic = topicRepository.findById(id);
        Topic topicObj = topic.get();
        if (topicObj.getState() == Topic.State.CREATED) {
            if (approved) {
                topicObj.setState(Topic.State.APPROVED);

                topicRepository.save(topicObj);
                return "Topic approved.";
            }
            else {
                topicRepository.deleteById(id);
                return "Topic rejected and deleted from the system.";
            }
        }
        return "News should be has the submitted state.";
    }

    @GetMapping(path = "/view/{id}")
    public @ResponseBody String viewById(@PathVariable("id") long id) {
        Optional<Topic> topic = topicRepository.findById(id);
        Topic topicObj = topic.get();

        if (topicObj.getState() == Topic.State.APPROVED)
            return topicObj.toString();
        else return "You don't have access to view this topic.";
    }






}
