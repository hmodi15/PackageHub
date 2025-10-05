package onetomany.Buildings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author Harsh Modi
 * 
 */

@Repository
public interface BuildingRepository extends JpaRepository<Buildings, Long> {
    Buildings findById(int id);
    Buildings findByName(String name);

}
