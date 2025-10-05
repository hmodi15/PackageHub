package onetomany.Rooms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Room findById(int id);
    List<Room> findAllByBuildingId(int building_id);
    void deleteByid(int id);
}
