package onetomany.Packages;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {
    Package findById(int id);
    int findByuser_id(int id);
    void deleteById(int id);

}
