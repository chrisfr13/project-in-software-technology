package spring.api.news.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import spring.api.news.model.*;
import spring.api.news.repository.TopicRepository;
import spring.api.news.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/topic")
public class TopicController {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;


    //Μέθοδος για την εμφάνιση των θεμάτων στον χρήστη
    @GetMapping(path = "/topics")
    public @ResponseBody String showTopics() {
        List<Topic> sortedTopics = topicRepository.findByOrderByTitleAscStateAsc();
        List<Topic> returnedTopics = new ArrayList<>();
        User user = getUser();

        //Έλεγχος αν υπάρχουν διαθέσιμα θέματα
        if (!sortedTopics.isEmpty()) {
            //Έλεγχος ρόλου χρήστη και εισαγωγή του κατάληλου θέματα στην λίστα επιστροφής
            for (Topic t : sortedTopics) {
                if (user == null) {
                    if (t.getState() == Topic.State.APPROVED)
                        returnedTopics.add(t);
                } else if (user.getRoles().equals("ROLE_JOURNALIST")) {
                    if (user.getTopics().contains(t)) returnedTopics.add(t);
                } else if (user.getRoles().equals("ROLE_ADMIN")) {
                    returnedTopics.add(t);

                }

            }
            return returnedTopics.toString();
        }
        return "Article with specified id does not found";

    }


    //Μέθοδος για την δημιουργία θέματος και εισαγωγή αυτού στην βάση δεδομένων
    @PreAuthorize("hasRole('JOURNALIST') or hasRole('ADMIN')")
    @GetMapping(path = "/create")
    public @ResponseBody String createTopic(@RequestParam String title,
                                            @RequestParam(required = false) String parentTopic) {
        User user = getUser();
        Topic newTopic = new Topic();
        List<Topic> userTopics;

        Topic topic = topicRepository.findByTitle(title);
        //Έλεγχος αν υπάρχει θέμα με τον ίδιο τίτλο-όνομα
        if (topic != null) return "Topic with same title already exists";
        else {
            //Έλεγχος αν υπάρχει το πατρικό θέμα
            if (parentTopic != null) {
                Topic pTopic = topicRepository.findByTitle(parentTopic);
                if (pTopic == null) return "Parent topic does not exist. You should create one.";
                else {
                    boolean pTopicExist = false;
                    for (Topic t : user.getTopics()) {
                        if (pTopic.getTitle().equals(t.getTitle())) {
                            pTopicExist = true;
                            break;
                        }
                    }
                    if (pTopicExist) {
                        newTopic.setTitle(title);
                        LocalDate date = LocalDate.now();

                        newTopic.setCreationDate(date);
                        newTopic.setState(Topic.State.CREATED);
                        newTopic.setParentTopic(pTopic);
                        newTopic.setUser(user);

                        userTopics = user.getTopics();
                        userTopics.add(newTopic);
                        user.setTopics(userTopics);
                        topicRepository.save(newTopic);
                        userRepository.save(user);
                        return "Topic created";
                    }
                    return "Parent topic does not find for the specified user";
                }
            } else {
                newTopic.setTitle(title);
                LocalDate date = LocalDate.now();

                newTopic.setCreationDate(date);
                newTopic.setState(Topic.State.CREATED);
                newTopic.setUser(user);
                userTopics = user.getTopics();
                userTopics.add(newTopic);
                user.setTopics(userTopics);
                topicRepository.save(newTopic);
                userRepository.save(user);
                return "Topic created";
            }
        }

    }

    //Μέθοδος για την επεξεργασία ενός θέματος με βάση το αναγνωριστικό του
    @PreAuthorize("hasRole('JOURNALIST') or hasRole('ADMIN')")
    @GetMapping(path = "/edit/{id}")
    public @ResponseBody String editTopic(@PathVariable("id") long id, @RequestParam String title, @RequestParam(required = false) String parentTopic) {
        Topic topicObj = null;
        Optional<Topic> optionalTopic = topicRepository.findById(id);
        Topic topic = topicRepository.findByTitle(title);
        User user = getUser();
        List<Topic> userTopics = user.getTopics();

        //Έλεγχος αν υπάρχει το θέμα με το συγκεκριμένο αναγνωριστικό
        if (optionalTopic.isPresent()) {
            //Αναζήτηση του θέματα αν ανήκει στον συνδεδεμένο χρήστη
            for (Topic t : userTopics) {
                if (t.getId() == id) {
                    topicObj = t;
                    break;
                }
            }
            if (topic != null)
                return "This title already exists";
            if (topicObj == null) {
                return "Topic for this user does not exist";
            }

            //Έλεγχος της κατάστασης του θέματος
            if (topicObj.getState() == Topic.State.CREATED) {
                topicObj.setTitle(title);
                if (parentTopic != null) {
                    Topic pTopic = topicRepository.findByTitle(parentTopic);
                    for (Topic t : userTopics) {
                        if (t.getId() == pTopic.getId()) {
                            topicObj.setParentTopic(pTopic);
                            break;
                        }
                    }
                }
                topicRepository.save(topicObj);
                return "Topic updated";
            } else return "The topic with specific id cannot be edited (state not created)";
        }
        return "The topic with specified id does not exist";

    }

    //Μέθοδος για την απόρριψη ή αποδοχή ενός θέματος
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/approveReject/{id}")
    public @ResponseBody String approveReject(@PathVariable("id") long id, @RequestParam(defaultValue = "false") boolean approved) {
        Optional<Topic> topic = topicRepository.findById(id);

        //Έλεγχος αν υπάρχει το θέμα με το συγκεκριμένο αναγνωριστικό
        if (topic.isPresent()) {
            Topic topicObj = topic.get();

            //Έλεγχος της κατάστασης του θέματος
            if (topicObj.getState() == Topic.State.CREATED) {
                if (approved) {
                    topicObj.setState(Topic.State.APPROVED);

                    topicRepository.save(topicObj);
                    return "Topic approved.";
                } else {
                    topicRepository.deleteById(id);
                    return "Topic rejected and deleted from the system.";
                }
            }
            return "Topic should be has the created state.";
        }
        return "The topic with specified id does not exist";

    }

    //Μέθοδος για την εμφάνιση των στοιχείων ενός θέματος βάσει το αναγνωριστικού του
    @GetMapping(path = "/view/{id}")
    public @ResponseBody String viewById(@PathVariable("id") long id) {
        Optional<Topic> topic = topicRepository.findById(id);
        Topic topicObj;
        User userObj = getUser();

        //Έλεγχος αν υπάρχει το θέμα με το συγκεκριμένο αναγνωριστικό
        if (topic.isPresent()) {
            topicObj = topic.get();
            //Έλεγχος της κατάστασης του θέματος
            if (topicObj.getState() == Topic.State.APPROVED)
                return topicObj.toString();
            else if (userObj != null)
                if (userObj.getRoles().equals("ROLE_JOURNALIST")) {
                    for (Topic t : userObj.getTopics()) {
                        if (t.getId() == id) return topicObj.toString();
                    }
                } else if (userObj.getRoles().equals("ROLE_ADMIN")) {
                    return topicObj.toString();
                }
        }

        return "The topic with specified id does not exist";
    }

    //Μέθοδος για την αναζήτηση θέματος βάσει του τίτλου του
    @GetMapping(path = "/search")
    public @ResponseBody String searchTopic(@RequestParam String title) {
        //Επιστρέφει όλα τα θέματα
        Iterable<Topic> optionalTopics = topicRepository.findAll();
        List<Topic> returnableTopics = new ArrayList<>();
        List<String> titleArr = Arrays.asList(title.split(" "));

        User userObj = getUser();

        //Εύρεση των θεμάτων βάσει των πεδίων που δώθηκαν από τον χρήστη
        for (Topic t : optionalTopics) {
            List<String> articleTitle = Arrays.asList(t.getTitle().split(" "));

            boolean allContainsTitle = titleArr.containsAll(articleTitle);

            if (allContainsTitle)
                if (t.getState() == Topic.State.APPROVED)
                    returnableTopics.add(t);
                else if (userObj.getRoles().equals("ROLE_JOURNALIST")) {
                    System.out.println("Journalist");
                    for (News nn : userObj.getNews()) {
                        if (nn.getId() == t.getId())
                            returnableTopics.add(t);
                    }
                } else if (userObj.getRoles().equals("ROLE_ADMIN")) {
                    returnableTopics.add(t);
                }
        }
        if(returnableTopics.isEmpty()) return "No topics found";
        return returnableTopics.toString();
    }
    //Μέθοδος για την εμφάνιση των άρθρων ενός θέματος βασει του ID του
    @GetMapping(path = "/showArticles/{id}")
    public @ResponseBody String showArticles(@PathVariable("id") long id) {
        Optional<Topic> optionalTopic = topicRepository.findById(id);
        User user = getUser();
        List<News> returnableList = new ArrayList<>();

        //Έλεγχος αν υπάρχει το θέμα με το συγκεκριμένο αναγνωριστικό στην βάση δεδομένωνο
        if (optionalTopic.isPresent()) {
            Topic topic = optionalTopic.get();
            //Έλεγχος ρόλου χρήστη και εισαγωγή της κατάλληλης είδησης στην λίστα επιστροφής
            for (News n : topic.getNews()) {
                if (user == null) {
                    if (topic.getState() == Topic.State.APPROVED) {
                        if (n.getState() == State.PUBLISHED)
                            returnableList.add(n);
                    } else return "You don't have access for this topic";
                } else if (user.getRoles().equals("ROLE_JOURNALIST")) {
                    if (topic.getState() == Topic.State.APPROVED || user.getTopics().contains(topic)) {
                        if (user.getNews().contains(n)) returnableList.add(n);
                    } else
                        return "You don't have access for this topic";

                } else if (user.getRoles().equals("ROLE_ADMIN")) {
                    returnableList.add(n);
                }
            }
            return returnableList.toString();
        }
        return "The topic with specified id does not exist";
    }

    //Μέθοδος για την επιστροφή των στοιχείων του χρήστη που έχει συνδεθεί στο σύστημα
    private User getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }

}
