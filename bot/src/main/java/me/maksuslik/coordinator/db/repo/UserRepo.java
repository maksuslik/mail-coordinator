package me.maksuslik.coordinator.db.repo;

import me.maksuslik.coordinator.db.data.UserData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends CrudRepository<UserData, UUID> {
    @Query("SELECT f FROM users f " + "WHERE f.userId = :id")
    Optional<UserData> findById(Long id);
}
