package onetomany.Feedback;

import io.swagger.v3.oas.annotations.Operation;
import onetomany.Credentials.CredentialsRepository;
import onetomany.Users.User;
import onetomany.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
public class FeedbackController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    CredentialsRepository credentialsRepository;

    @Autowired
    FeedbackRepository feedbackRepository;

    public FeedbackController(UserRepository userRepository, CredentialsRepository credentialsRepository) {
        this.userRepository = userRepository;
        this.credentialsRepository = credentialsRepository;
    }


    // =============================== Mappings ================================== //


    @Operation(summary = "Get the feedback by ID")
    @GetMapping(path = "/feedback/{id}")
    private FeedbackEntity getFeedbackById(@PathVariable int id) {
        return feedbackRepository.findByfeedbackId(id);
    }

    @Operation(summary = "Makes a feedback")
    @PostMapping(path = "/feedback")
    private FeedbackEntity makeFeedback(@RequestBody Map<String, String> feed) {
        String emailId = feed.get("emailId");
        String name = feed.get("name");
        String text = feed.get("text");
        User user = userRepository.findBycredentials(credentialsRepository.findByemailId(emailId));
        FeedbackEntity feedback = new FeedbackEntity(emailId, name, text);
        feedback.setUser(user);
        feedbackRepository.save(feedback);
        return feedback;
    }

    @Operation(summary = "Makes a feedback")
    @PostMapping(path = "/feedback/super")
    private FeedbackEntity makeSuperFeedback(@RequestBody Map<String, String> feed) {
        String emailId = feed.get("emailId");
        String name = feed.get("name");
        String text = feed.get("text");
        float stars = Float.parseFloat((feed.get("stars")));
        User user = userRepository.findBycredentials(credentialsRepository.findByemailId(emailId));
        FeedbackEntity feedback = new FeedbackEntity(emailId, name, text, stars);
        feedback.setUser(user);
        feedbackRepository.save(feedback);
        return feedback;
    }

    @Operation(summary = "Makes a rating")
    @PostMapping(path = "/ratings")
    private FeedbackEntity makeRating(@RequestBody Map<String, String> feed) {
        String emailId = feed.get("emailId");
        String name = feed.get("name");
        float stars = Float.parseFloat(feed.get("stars"));
        User user = userRepository.findBycredentials(credentialsRepository.findByemailId(emailId));
        FeedbackEntity feedback = new FeedbackEntity(emailId, name, stars);
        feedback.setUser(user);
        feedbackRepository.save(feedback);
        return feedback;
    }

    @Operation(summary = "Delete the feedback by ID")
    @DeleteMapping(path = "/feedback/delete/{id}")
    private void deleteFeedback(@PathVariable int id) {
        feedbackRepository.delete(feedbackRepository.findByfeedbackId(id));
    }
}
