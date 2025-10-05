package onetomany.Users;

import onetomany.Credentials.Credentials;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 *
 * @author Harsh Modi
 *
 */

@EnableJpaRepositories
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findBycredentials(Credentials credentials);
    User findByname(String name);
    User findById(int id);
    void deleteById(int id);

    List<User> findAllByRoomId(int room_id);
}
