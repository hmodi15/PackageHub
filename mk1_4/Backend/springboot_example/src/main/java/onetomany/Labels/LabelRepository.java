package onetomany.Labels;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label_Image, Long> {
    Label_Image findById(int id);
}
