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
import spring.api.news.model.Topic;
import spring.api.news.repository.CommentRepository;
import spring.api.news.repository.NewsRepository;
import spring.api.news.repository.TopicRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NewsControllerTests {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MockMvc mvc;

    //SHOW ARTICLES test

    @Order(1)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void getArticles() throws Exception {
        mvc.perform(get("/api/news/articles")).andExpect(status().isOk());
    }

    //CREATE ARTICLE tests

    @Order(2)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void createArticle_TopicNotExist_statusOK() throws Exception {
        mvc.perform(get("/api/news/create?title=just a title&context=just a content&topicTitles=just a topic")).andExpect(status().isOk()).andExpect(content().string("Specified topic title does not exist"));
    }

    @Order(3)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void editArticle_notExist_statusOK() throws Exception {
        mvc.perform(get("/api/news/edit/4?context=just a content&topicTitles=just a title1, just another title2&title=just a new title"))
                .andExpect(status().isOk())
                .andExpect(content().string("The article with specified id does not exist"));
    }

    @Order(4)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void createArticle_TopicExist_statusOK() throws Exception {
        Topic topic = new Topic();
        topic.setTitle("just a topic");
        topic.setCreationDate(LocalDate.now());
        topicRepository.save(topic);

        mvc.perform(get("/api/news/create?title=just a title&context=just a content&topicTitles=just a topic"))
                .andExpect(status().isOk())
                .andExpect(content().string("Article created"));
    }

    //EDIT ARTICLE tests

    @Order(5)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void editArticle_updated_statusOK() throws Exception {
        mvc.perform(get("/api/news/edit/4?context=just a content&topicTitles=just a topic&title=just a new title"))
                .andExpect(status().isOk())
                .andExpect(content().string("Article updated"));
    }

    @Order(6)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void editArticle_sameTitle_statusOK() throws Exception {
        mvc.perform(get("/api/news/edit/4?context=just a content&topicTitles=just a topic&title=just a new title"))
                .andExpect(status().isOk())
                .andExpect(content().string("This title already exists"));
    }

    //SUBMIT ARTICLE test

    @Order(7)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void submit_articleNotExist_statusOK() throws Exception {
        mvc.perform(get("/api/news/submit/5"))
                .andExpect(status().isOk())
                .andExpect(content().string("The article with specified id does not exist"));
    }


    @Order(8)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void approveReject_wrongState_statusOK() throws Exception {
        mvc.perform(get("/api/news/approveReject/4?approved=true"))
                .andExpect(status().isOk())
                .andExpect(content().string("News should be has the submitted state."));
    }

    @Order(9)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void submit_articlexist_statusOK() throws Exception {
        mvc.perform(get("/api/news/submit/4"))
                .andExpect(status().isOk())
                .andExpect(content().string("State updated to \'SUBMITTED\'"));
    }


    @Order(10)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void editArticle_wrongState_statusOK() throws Exception {
        mvc.perform(get("/api/news/edit/4?context=just a content&topicTitles=just a topic&title=just a new new title"))
                .andExpect(status().isOk())
                .andExpect(content().string("The article with specified id cannot be edited (state not created)"));
    }

    //APPROVE-REJECT ARTICLE tests

    @Order(11)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void approveReject_articleNotExist_statusOK() throws Exception {
        mvc.perform(get("/api/news/approveReject/5?approved=true"))
                .andExpect(status().isOk())
                .andExpect(content().string("The article with specified id does not exist"));
    }

    @Order(12)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "JOURNALIST")
    void approveReject_journalistTryAccess_statusForbidden() throws Exception {
        mvc.perform(get("/api/news/approveReject/4?approved=true"))
                .andExpect(status().isForbidden());
    }

    //PUBLISH ARTICLE tests

    @Order(13)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void publish_wrongState_statusOK() throws Exception {
        mvc.perform(get("/api/news/publish/4"))
                .andExpect(status().isOk())
                .andExpect(content().string("Article should approved first from the administrator."));
    }


    @Order(14)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void approve_articleExist_statusOK() throws Exception {
        mvc.perform(get("/api/news/approveReject/4?approved=true"))
                .andExpect(status().isOk())
                .andExpect(content().string("Approved."));
    }


    @Order(16)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void publish_articleNotExist_statusOK() throws Exception {
        mvc.perform(get("/api/news/publish/5"))
                .andExpect(status().isOk())
                .andExpect(content().string("The article with specified id does not exist"));
    }

    @Order(17)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void publish_articleExists_statusOK() throws Exception {
        mvc.perform(get("/api/news/publish/4"))
                .andExpect(status().isOk())
                .andExpect(content().string("Published"));
    }

    @Order(18)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "JOURNALIST")
    void publish_journalistTryAccess_statusForbidden() throws Exception {
        mvc.perform(get("/api/news/publish/4"))
                .andExpect(status().isForbidden());
    }

    //VIEW BY ID (ARTICLE) tests

    @Order(19)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "JOURNALIST")
    void viewById_articleNotExist_statusOK() throws Exception {
        mvc.perform(get("/api/news/view/5"))
                .andExpect(status().isOk())
                .andExpect(content().string("The article with specified id does not exist"));
    }

    @Order(20)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "JOURNALIST")
    void viewById_articleExists_statusOK() throws Exception {
        mvc.perform(get("/api/news/view/4"))
                .andExpect(status().isOk());
    }

    //SEARCH ARTICLE tests

    @Order(21)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "JOURNALIST")
    void search_articleNotExist_statusOK() throws Exception {
        mvc.perform(get("/api/news/search?title=just a title"))
                .andExpect(status().isOk()).andExpect(content().string("No articles found"));
    }

    @Order(22)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "JOURNALIST")
    void search_articleExists_statusOK() throws Exception {
        mvc.perform(get("/api/news/search?title=just a new title"))
                .andExpect(status().isOk());
    }

    //SHOW ARTICLE'S COMMENTS tests

    @Order(23)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "JOURNALIST")
    void showComments_articleNotExist_statusOK() throws Exception {


        mvc.perform(get("/api/news/showComments/5?"))
                .andExpect(status().isOk()).andExpect(content().string("The article with specified id does not exist"));
    }

    @Order(24)
    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void showComments_NoComments_statusOK() throws Exception {
        Comment comment = new Comment();
        comment.setContext("Some context");
        comment.setCreation_date(LocalDate.now());
        comment.setState(Comment.State.CREATED);
        Optional<News> optionalNews= newsRepository.findById((long) 4);
        comment.setNews(optionalNews.get());
        commentRepository.save(comment);

        mvc.perform(get("/api/news/showComments/4"))
                .andExpect(status().isOk());
    }

}
