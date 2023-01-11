package spring.api.news.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import spring.api.news.model.Comment;
import spring.api.news.model.News;
import spring.api.news.model.User;
import spring.api.news.repository.CommentRepository;
import spring.api.news.repository.NewsRepository;
import spring.api.news.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    NewsRepository newsRepository;


    //Μέθοδος για την δημιουργία σχολίου για μία συγκεκριμένη είδηση με βάση το αναγνωριστικό της
    @GetMapping(path = "/create")
    public @ResponseBody String createComment(@RequestParam long article_id, @RequestParam String context,
                                              @RequestParam(required = false) String name) {

        Optional<News> optionalNews = newsRepository.findById(article_id);

        //Έλεγχος αν υπάρχει το άρθρο με το συγκεκριμένο αναγνωριστικό
        if (optionalNews.isPresent()) {
            News newsObj = optionalNews.get();
            User user = getUser();
            //Δημιουργία σχολίου
            Comment comment = new Comment();
            comment.setContext(context);
            comment.setCreation_date(LocalDate.now());
            comment.setState(Comment.State.CREATED);

            //Έλεγχος αν έχει συνδεθεί ταυτοποιημένο χρήστης ή όχι
            if (user == null) {
                if (name != null)
                    comment.setFullName(name);
            } else {
                if (user.getLastName() != null && user.getFirstName() != null)
                    comment.setFullName(user.getFirstName() + " " + user.getLastName());
            }
            comment.setNews(newsObj);
            List<Comment> newsComments = newsObj.getComments();
            newsComments.add(comment);
            newsObj.setComments(newsComments);
            commentRepository.save(comment);

            newsRepository.save(newsObj);
            return "Comment created successfully";
        }
        return "Article with specified id does not exist";
    }


    //Μέθοδος για την επεξεργασία ενός σχολίου με βάση το αναγνωριστικό του
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/edit/{id}")
    public @ResponseBody String editComment(@PathVariable("id") long id, @RequestParam String context) {
        Optional<Comment> optionalComment = commentRepository.findById(id);

        //Έλεγχος αν υπάρχει το σχόλιο με το συγκεκριμένο αναγνωριστικό
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            comment.setContext(context);
            commentRepository.save(comment);
            return "Comment updated";
        }
        return "The comment with specified id does not exist";
    }

    //Μέθοδος για την απόρριψη ή αποδοχή ενός σχολίου
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/approveReject/{id}")
    public @ResponseBody String approveReject(@PathVariable("id") long id, @RequestParam long articleId, @RequestParam(defaultValue = "false") boolean approved) {
        Optional<Comment> optionalComment = commentRepository.findById(id);

        //Έλεγχος αν υπάρχει το σχόλιο με το συγκεκριμένο αναγνωριστικό
        if (optionalComment.isPresent()) {
            Comment commentObj = optionalComment.get();

            //Έλεγχος αν υπάρχει η είδηση με το συγκεκριμένο αναγνωριστικό
            if (articleId == commentObj.getNews().getId()) {

                //Έλεγχος της κατάστασης του σχολίου
                if (commentObj.getState() == Comment.State.CREATED) {
                    if (approved) {
                        commentObj.setState(Comment.State.APPROVED);

                        commentRepository.save(commentObj);
                        return "Comment approved.";
                    } else {
                        commentRepository.deleteById(id);
                        return "Comment rejected and deleted from the system.";
                    }
                }
                return "Comment should have the created state";
            }
            return "Comment with specified article does not found";
        }
        return "The comment with specified id does not exist";

    }

    //Μέθοδος για την επιστροφή των στοιχείων του χρήστη που έχει συνδεθεί στο σύστημα
    private User getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }
}
