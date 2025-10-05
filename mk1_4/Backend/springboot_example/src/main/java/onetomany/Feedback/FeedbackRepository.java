package onetomany.Feedback;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {
    FeedbackEntity findByfeedbackId(int id);
    FeedbackEntity findByemailId(String email);
    void deleteByfeedbackId(int id);
}
