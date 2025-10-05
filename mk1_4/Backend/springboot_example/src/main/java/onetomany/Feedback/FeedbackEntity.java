package onetomany.Feedback;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import onetomany.Users.User;

@Entity
public class FeedbackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int feedbackId;

    private String emailId;

    private String name;

    private String text;

    private float stars;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", updatable = true)
    @JsonIgnore
    private User user;


    // =============================== Constructors ================================== //


    public FeedbackEntity(String emailId, String name, String text, float stars) {
        this.setEmailId(emailId);
        this.setName(name);
        this.setText(text);
        this.setStars(stars);
    }

    public FeedbackEntity() {
        this.setEmailId("");
        this.setName("");
        this.setText("");
        this.setStars(-1);
    }

    public FeedbackEntity(String emailId, String name, float stars) {
        this.setEmailId(emailId);
        this.setName(name);
        this.setText("");
        this.setStars(stars);
    }

    public FeedbackEntity(String emailId, String name, String text) {
        this.setEmailId(emailId);
        this.setName(name);
        this.setText(text);
        this.setStars(stars);
    }


    // =============================== Getters and Setters for each field ================================== //


    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }
}
