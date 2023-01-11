package spring.api.news;


import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import spring.api.news.model.News;
import spring.api.news.model.News;
import spring.api.news.model.State;
import spring.api.news.model.Topic;
import spring.api.news.repository.CommentRepository;
import spring.api.news.repository.NewsRepository;
import spring.api.news.repository.TopicRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TopicControllerTests {


    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MockMvc mvc;


    //GET TOPICS test

    @Order(1)
    @Test
    @WithMockUser(username = "adin", password = "pass", roles = "admin")
    void getTopics() throws Exception {
        mvc.perform(get("/api/topic/topics")).andExpect(status().isOk());
    }

    //CREATE TOPIC tests

    @Order(2)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void addTopic_ParentTopicNotExist_statusOK() throws Exception {
        mvc.perform(get("/api/topic/create?title=just a topic&parentTopic=just a parent topic"))
                .andExpect(status().isOk())
                .andExpect(content().string("Parent topic does not exist. You should create one."));
    }

    @Order(3)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void addTopic_success_statusOK() throws Exception {
        mvc.perform(get("/api/topic/create?title=just a parent topic"))
                .andExpect(status().isOk())
                .andExpect(content().string("Topic created"));
    }

    @Order(4)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void addTopic_WithParentTopic_statusOK() throws Exception {
        mvc.perform(get("/api/topic/create?title=just a topic&parentTopic=just a parent topic"))
                .andExpect(status().isOk())
                .andExpect(content().string("Topic created"));
    }

    //EDIT TOPIC tests

    @Order(5)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void editTopic_notExist_statusOK() throws Exception {
        mvc.perform(get("/api/topic/edit/10?title=just a topic11&parentTopic=just a parent topic"))
                .andExpect(status().isOk())
                .andExpect(content().string("The topic with specified id does not exist"));
    }

    @Order(5)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void editTopic_updated_statusOK() throws Exception {
        mvc.perform(get("/api/topic/edit/3?title=just a topic11&parentTopic=just a parent topic"))
                .andExpect(status().isOk())
                .andExpect(content().string("Topic updated"));
    }

    @Order(6)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void editTopic_sameTitle_statusOK() throws Exception {
        mvc.perform(get("/api/topic/edit/3?title=just a topic11&parentTopic=just a parent topic"))
                .andExpect(status().isOk())
                .andExpect(content().string("This title already exists"));
    }

    //APPROVE-REJECT TOPIC tests

    @Order(7)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void approveReject_TopicNotExist_statusOK() throws Exception {
        mvc.perform(get("/api/topic/approveReject/10?approved=true"))
                .andExpect(status().isOk())
                .andExpect(content().string("The topic with specified id does not exist"));
    }

    @Order(8)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void approve_topicExist_statusOK() throws Exception {
        mvc.perform(get("/api/topic/approveReject/3?approved=true"))
                .andExpect(status().isOk())
                .andExpect(content().string("Topic approved."));
    }

    @Order(9)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void approveReject_wrongState_statusOK() throws Exception {
        mvc.perform(get("/api/topic/approveReject/3?approved=true"))
                .andExpect(status().isOk())
                .andExpect(content().string("Topic should be has the created state."));
    }

    @Order(10)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "JOURNALIST")
    void approveReject_journalistTryAccess_statusForbidden() throws Exception {
        mvc.perform(get("/api/topic/approveReject/3?approved=true"))
                .andExpect(status().isForbidden());
    }

    //VIEW BY ID (TOPIC) tests

    @Order(12)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "JOURNALIST")
    void viewById_topicNotExist_statusOK() throws Exception {
        mvc.perform(get("/api/topic/view/10"))
                .andExpect(status().isOk())
                .andExpect(content().string("The topic with specified id does not exist"));
    }

    @Order(13)
    @Test
    @WithMockUser(username = "jour", password = "password", roles = "JOURNALIST")
    void viewById_topicExists_statusOK() throws Exception {
        mvc.perform(get("/api/topic/view/4"))
                .andExpect(status().isOk());
    }

    //SEARCH TOPIC tests

    @Order(14)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void search_topicNotExist_statusOK() throws Exception {
        mvc.perform(get("/api/topic/search?title=Just a random title"))
                .andExpect(status().isOk()).andExpect(content().string("No topics found"));
    }

    @Order(15)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void search_topicExists_statusOK() throws Exception {
        mvc.perform(get("/api/topic/search?title=just a topic"))
                .andExpect(status().isOk());
    }

    //SHOW TOPIC'S ARTICLES tests

    @Order(16)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "JOURNALIST")
    void showArticles_topicNotExist_statusOK() throws Exception {
        mvc.perform(get("/api/topic/showArticles/10?"))
                .andExpect(status().isOk()).andExpect(content().string("The topic with specified id does not exist"));
    }

    @Order(24)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void showArticles_NoArticles_statusOK() throws Exception {
        News news = new News();
        news.setContext("Some context");
        news.setCreation_date(LocalDate.now());
        news.setState(State.CREATED);
        Optional<Topic> optionalTopic= topicRepository.findById((long) 4);
        List<Topic> topics=new ArrayList<>();
        topics.add(optionalTopic.get());
        news.setTopics(topics);
        newsRepository.save(news);

        mvc.perform(get("/api/topic/showArticles/4"))
                .andExpect(status().isOk());
    }

}
