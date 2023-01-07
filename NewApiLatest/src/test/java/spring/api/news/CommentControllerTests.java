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
import spring.api.news.model.Comment;
import spring.api.news.model.News;
import spring.api.news.model.State;
import spring.api.news.repository.CommentRepository;
import spring.api.news.repository.NewsRepository;
import spring.api.news.repository.TopicRepository;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommentControllerTests {


    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MockMvc mvc;


    //CREATE COMMENT tests

    @Order(2)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void addComment_articleNotExist_statusOK() throws Exception {
        mvc.perform(get("/api/comment/create?article_id=5&context=Just a comment"))
                .andExpect(status().isOk())
                .andExpect(content().string("Article with specified id does not exist"));
    }


    @Order(3)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void addComment_success_statusOK() throws Exception {
        News news=new News();
        news.setTitle("just a title");
        news.setContext("Just a context");
        news.setCreation_date(LocalDate.now());
        news.setState(State.CREATED);
        newsRepository.save(news);
        mvc.perform(get("/api/comment/create?article_id=3&context=Just a comment"))
                .andExpect(status().isOk())
                .andExpect(content().string("Comment created successfully"));
    }

    //EDIT COMMENT tests

    @Order(4)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void editComment_notExist_statusOK() throws Exception {
        mvc.perform(get("/api/comment/edit/10?context=just a new context"))
                .andExpect(status().isOk())
                .andExpect(content().string("The comment with specified id does not exist"));
    }

    @Order(5)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void editComment_updated_statusOK() throws Exception {
        mvc.perform(get("/api/comment/edit/4?context=just a new context"))
                .andExpect(status().isOk())
                .andExpect(content().string("Comment updated"));
    }

    //APPROVE-REJECT COMMENT tests

    @Order(6)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void approveReject_TopicNotExist_statusOK() throws Exception {
        mvc.perform(get("/api/comment/approveReject/6?approved=true&articleId=5"))
                .andExpect(status().isOk())
                .andExpect(content().string("The comment with specified id does not exist"));
    }

    @Order(7)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void approve_topicExist_statusOK() throws Exception {
        mvc.perform(get("/api/comment/approveReject/4?approved=true&articleId=3"))
                .andExpect(status().isOk())
                .andExpect(content().string("Comment approved."));
    }

    @Order(8)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void approveReject_wrongState_statusOK() throws Exception {
        mvc.perform(get("/api/comment/approveReject/4?approved=true&articleId=3"))
                .andExpect(status().isOk())
                .andExpect(content().string("Comment should have the created state"));
    }

    @Order(9)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "JOURNALIST")
    void approveReject_journalistTryAccess_statusForbidden() throws Exception {
        mvc.perform(get("/api/comment/approveReject/4?approved=true&articleId=3"))
                .andExpect(status().isForbidden());
    }



}