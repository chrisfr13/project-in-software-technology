package spring.api.news.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import spring.api.news.model.*;
import spring.api.news.repository.CommentRepository;
import spring.api.news.repository.NewsRepository;
import spring.api.news.repository.TopicRepository;
import spring.api.news.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private TopicRepository topicRepository;


    @Autowired
    private CommentRepository commentRepository;

    //Μέθοδος που επιστρέφει τα άρθρα ανάλογα με τον χρήστη
    @GetMapping(path = "articles")
    public @ResponseBody List<News> findAll(@RequestParam(required = false) State state, @RequestParam(required = false) LocalDate fromDate, @RequestParam(required = false) LocalDate toDate) {
        User user = getUser();
        List<News> news = newsRepository.findByOrderByStateDescCreationDateAsc();
        List<News> returnableNews = new ArrayList<>();
        //Έλεγχος αν έχουν δωθεί οι κατάλληλες παράμετροι από τον χρήστη
        if (state == null && fromDate == null && toDate == null) {
            //Έλεγχος ρόλου χρήστη και εισαγωγή του κατάληλου θέματα στην λίστα επιστροφής
            for (News n : news) {
                if (user == null) {
                    if (n.getState() == State.PUBLISHED)
                        returnableNews.add(n);
                }
                else if (user.getRoles().equals("ROLE_JOURNALIST")) {
                    if (user.getNews().contains(n))
                        returnableNews.add(n);
                }
                else if (user.getRoles().equals("ROLE_ADMIN"))
                    returnableNews.add(n);
            }
        }
        //Έλεγχος αν έχουν δωθεί οι κατάλληλες παράμετροι από τον χρήστη
        if (state != null && fromDate == null && toDate == null) {
            //Έλεγχος ρόλου χρήστη και εισαγωγή του κατάληλου θέματα στην λίστα επιστροφής
            for (News n : news) {
                if (user == null) {
                    if (n.getState() == State.PUBLISHED)
                        returnableNews.add(n);
                } else if (user.getRoles().equals("ROLE_JOURNALIST")) {
                    if (user.getNews().contains(n))
                        if (n.getState() == state)
                            returnableNews.add(n);
                } else if (user.getRoles().equals("ROLE_ADMIN"))
                    if (n.getState() == state) returnableNews.add(n);
            }
        }
        //Έλεγχος αν έχουν δωθεί οι κατάλληλες παράμετροι από τον χρήστη
        if (state == null && fromDate != null && toDate != null) {
            //Έλεγχος ρόλου χρήστη και εισαγωγή του κατάληλου θέματα στην λίστα επιστροφής
            for (News n : news) {
                if (n.getCreation_date().compareTo(fromDate) >= 0 && n.getCreation_date().compareTo(toDate) < 0)
                    if (user == null) {
                        if (n.getState() == State.PUBLISHED)
                            returnableNews.add(n);
                    } else if (user.getRoles().equals("ROLE_JOURNALIST")) {
                        if (user.getNews().contains(n))
                            returnableNews.add(n);
                    } else if (user.getRoles().equals("ROLE_ADMIN"))
                        returnableNews.add(n);
            }
        }

        //Έλεγχος αν έχουν δωθεί οι κατάλληλες παράμετροι από τον χρήστη
        if (state != null && fromDate != null && toDate != null) {
            //Έλεγχος ρόλου χρήστη και εισαγωγή του κατάληλου θέματα στην λίστα επιστροφής
            for (News n : news) {
                if (n.getCreation_date().compareTo(fromDate) >= 0 && n.getCreation_date().compareTo(toDate) < 0)
                    if (user == null) {
                        if (n.getState() == State.PUBLISHED)
                            returnableNews.add(n);
                    } else if (user.getRoles().equals("ROLE_JOURNALIST")) {
                        if (user.getNews().contains(n))
                            if (n.getState() == state)
                                returnableNews.add(n);
                    } else if (user.getRoles().equals("ROLE_ADMIN"))
                        if (n.getState() == state) returnableNews.add(n);
            }
        }

        return returnableNews;
    }

    //Μέθοδος για την δημιουργία και την προσθήκη είδησης στο σύστημα
    @PreAuthorize("hasRole('JOURNALIST') or hasRole('ADMIN')")
    @GetMapping(path = "/create")
    public @ResponseBody String createNews(@RequestParam String title, @RequestParam String context,
                                           @RequestParam String[] topicTitles) {

        User user = getUser();
        Optional<News> optionalNews = newsRepository.findByTitle(title);
        //Έλεγχος αν υπάρχει ήδη θέμα με τον συγκεκριμένο τίτλο
        if (optionalNews.isEmpty()) {

            //Δημιουργία άρθρου
            News news = new News();
            LocalDate date = LocalDate.now();
            news.setTitle(title);
            news.setContext(context);
            news.setCreation_date(date);
            news.setState(State.CREATED);
            news.setUser(user);

            for (String topicTitle : topicTitles) {

                Topic topic = topicRepository.findByTitle(topicTitle);
                if (topic == null)
                    return "Specified topic title does not exist";
                else {
                    topic.addArticle(news);
                }

            }
            //Αποθήκευση της είδησης στο repository
            newsRepository.save(news);

            //Προσθήκη της είδησης στον κατάλληλο χρήστη
            user.getNews().add(news);

            //Αποθήκευση των αλλάγών του χρήστη
            userRepository.save(user);
            return "Article created";

        }
        return "Article with this title already exists";

    }


    //Μέθοδος για την επεξεργασία υπάρχουσας είδησης με χρήση του ID της
    @PreAuthorize("hasRole('JOURNALIST') or hasRole('ADMIN')")
    @GetMapping(path = "/edit/{id}")
    public @ResponseBody String editNews(@PathVariable("id") long id, @RequestParam String title, @RequestParam String context, @RequestParam String[] topicTitles) {
        Optional<News> optionalNews = newsRepository.findById(id);

        //Έλεγχος αν υπάρχει ήδη η είδηση στο σύστημα
        if (optionalNews.isPresent()) {
            News newsObj = null;
            Optional<News> newsExist = newsRepository.findByTitle(title);

            User user = getUser();
            //Εύρεση της είδησης στον συγκεκριμένο χρήστη
            List<News> userNews = user.getNews();
            for (News news : userNews) {
                if (news.getId() == id) {
                    newsObj = news;
                    break;
                }
            }

            //Έλεγχος αν υπάρχει είδηση με τον ίδιο τίτλο
            if (newsExist.isPresent())
                return "This title already exists";
            if (newsObj == null) {
                return "Article for this user does not exist";
            }

            //Έλεγχος της κατάστασης της είδησης
            if (newsObj.getState() == State.CREATED) {
                newsObj.setTitle(title);
                newsObj.setContext(context);

                //Εύρεση των θεμάτων που έχει εισάγει ο χρήστης αν υπάρχουν στο σύστημα
                for (String topicTitle : topicTitles) {
                    Topic topic = topicRepository.findByTitle(topicTitle);
                    if (topic == null)
                        return "Specified topic title does not exist";
                    else {
                        topic.addArticle(newsObj);
                    }
                }

                //Αποθήκευση της είδησης στο repository
                newsRepository.save(newsObj);
                return "Article updated";
            } else return "The article with specified id cannot be edited (state not created)";
        }
        return "The article with specified id does not exist";
    }

    //Μέθοδος για την υποβολή της είδησης (αλλάγης της κατάστασης σε ΥΠΟΒΕΒΛΗΜΕΝΗ)
    @PreAuthorize("hasRole('JOURNALIST') or hasRole('ADMIN')")
    @GetMapping(path = "/submit/{id}")
    public @ResponseBody String submit(@PathVariable("id") long id) {
        Optional<News> optionalNews = newsRepository.findById(id);

        //Έλεγχος αν υπάρχει ήδη η είδηση στο σύστημα
        if (optionalNews.isPresent()) {

            User userObj = getUser();
            News newsObj = null;
            //Εύρεση της είδησης στον συγκεκριμένο χρήστη
            for (News news : userObj.getNews()) {
                if (news.getId() == id) {
                    newsObj = news;
                    break;
                }
            }
            if (newsObj != null) {
                //Αλλαγή της κατάστασης τη είδησης και αποθήκευση στο σύστημα(Βάση δεδομένων)
                newsObj.setState(State.SUBMITTED);
                newsRepository.save(newsObj);
                return "State updated to \'SUBMITTED\'";
            }
            return "Article not found for this user";
        }
        return "The article with specified id does not exist";
    }

    //Μέθοδος που χρησιμοποιείται για να γίνει απόδοχή ή απόρριψη μίας είδησης από το σύστημα
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/approveReject/{id}")
    public @ResponseBody String approveReject(@PathVariable("id") long id, @RequestParam(defaultValue = "false") boolean approved, @RequestParam(required = false) String reason) {
        Optional<News> optionalNews = newsRepository.findById(id);
        News newsObj;

        //Έλεγχος αν υπάρχει ήδη η είδηση στο σύστημα
        if (optionalNews.isPresent()) {
            newsObj = optionalNews.get();

            //Έλεγχος της κατάστασης της είδησης
            if (newsObj.getState() == State.SUBMITTED) {

                if (approved) {
                    //Αλλαγή της κατάστασης της είδησης και αποθήκευση της
                    newsObj.setState(State.APPROVED);
                    newsObj.setNotApprovedMessage(null);
                    newsRepository.save(newsObj);
                    return "Approved.";
                }

                newsObj.setState(State.CREATED);
                newsObj.setNotApprovedMessage(reason);
                newsRepository.save(newsObj);
                return "Rejected.";
            }

            return "News should be has the submitted state.";
        }
        return "The article with specified id does not exist";
    }

    //Μέθοδος για την δημοσιεύση της είδησης
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/publish/{id}")
    public @ResponseBody String publish(@PathVariable("id") long id) {
        Optional<News> optionalNews = newsRepository.findById(id);
        News newsObj;

        //Έλεγχος αν υπάρχει ήδη η είδηση στο σύστημα
        if (optionalNews.isPresent()) {
            newsObj = optionalNews.get();

            //Έλεγχος της κατάστασης της είδησης
            if (newsObj.getState() == State.APPROVED) {
                //Αλλαγή της κατάστασης της είδησης
                newsObj.setState(State.PUBLISHED);
                newsRepository.save(newsObj);
                return "Published";
            }
            return "Article should approved first from the administrator.";
        }
        return "The article with specified id does not exist";
    }

    //Μέθοδος για την εμφάνιση των στοιχείων μία είδησης βάσει το ID της
    @GetMapping(path = "/view/{id}")
    public @ResponseBody String viewById(@PathVariable("id") long id) {
        Optional<News> news = newsRepository.findById(id);
        News newsObj;
        User userObj = getUser();

        //Έλεγχος αν υπάρχει ήδη η είδηση στο σύστημα
        if (news.isPresent()) {
            newsObj = news.get();

            //Έλεγχος της κατάστασης της είδησης
            if (newsObj.getState() == State.PUBLISHED)
                return newsObj.toString();
            else if (userObj != null)
                if (userObj.getRoles().equals("ROLE_JOURNALIST")) {
                    for (News n : userObj.getNews()) {
                        if (n.getId() == id) return newsObj.toString();
                    }
                } else if (userObj.getRoles().equals("ROLE_ADMIN")) {
                    return newsObj.toString();
                }
        }

        return "The article with specified id does not exist";
    }

    //Μέθοδος για την αναζήτηση είδησης βάσει του τίτλου της ή και του περιεχομένου της
    @GetMapping(path = "/search")
    public @ResponseBody String searchNews(@RequestParam String title, @RequestParam(required = false) String context) {
        //Αποθήκευση όλων των ειδήσεων
        Iterable<News> optionalNews = newsRepository.findAll();
        List<News> returnableNews = new ArrayList<>();
        List<String> titleArr = Arrays.asList(title.split(" "));
        List<String> contextArr = null;

        User userObj = getUser();

        //Έλεγχος αν έχει δωθεί περιεχόμενο για την αναζήση της είδησης
        if (context != null)
            contextArr = Arrays.asList(context.split(" "));

        //Εύρεση των ειδήσεων βάσει των πεδίων που δώθηκαν από τον χρήστη
        for (News n : optionalNews) {
            List<String> articleTitle = Arrays.asList(n.getTitle().split(" "));
            List<String> articleContext = Arrays.asList(n.getContext().split(" "));

            boolean allContainsTitle = titleArr.containsAll(articleTitle);
            boolean allContainsContext = true;
            if (context != null)
                allContainsContext = contextArr.containsAll(articleContext);


            if (allContainsTitle && allContainsContext)
                if (n.getState() == State.PUBLISHED)
                    returnableNews.add(n);
                else if (userObj.getRoles().equals("ROLE_JOURNALIST")) {
                    System.out.println("Journalist");
                    for (News nn : userObj.getNews()) {
                        if (nn.getId() == n.getId())
                            returnableNews.add(n);
                    }
                } else if (userObj.getRoles().equals("ROLE_ADMIN")) {
                    returnableNews.add(n);
                }
        }
        if (returnableNews.isEmpty()) return "No articles found";
        return returnableNews.toString();
    }


    //Μέθοδος για την εμφάνιση των σχολίων μίας είδησης βασει του ID της
    @GetMapping(path = "/showComments/{id}")
    public @ResponseBody String showComments(@PathVariable("id") long id) {
        List<Comment> sortedComments = commentRepository.findByOrderByCreationDateAsc();
        List<Comment> returnedComments = new ArrayList<>();
        User user = getUser();
        //Έλεγχος αν υπάρχουν σχόλια για την συγκεκριμένη είδηση
        if (!sortedComments.isEmpty()) {
            //Έλεγχος ρόλου χρήστη και εισαγωγή του κατάληλου θέματα στην λίστα επιστροφής
            for (Comment c : sortedComments) {
                if (c.getNews().getId() == id) {
                    if (user != null)
                        if (user.getRoles().equals("ROLE_ADMIN"))
                            returnedComments.add(c);
                        else if (c.getNews().getState() == State.PUBLISHED) {
                            if (c.getState() == Comment.State.APPROVED)
                                returnedComments.add(c);
                        }
                }
            }
            if (returnedComments.isEmpty()) return "No comments found";
            return returnedComments.toString();
        }
        return "The article with specified id does not exist";

    }

    //Μέθοδος για την επιστροφή των στοιχείων του χρήστη που έχει συνδεθεί στο σύστημα
    private User getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }

}