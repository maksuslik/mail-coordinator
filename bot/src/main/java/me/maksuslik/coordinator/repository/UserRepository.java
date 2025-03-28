package me.maksuslik.coordinator.repository;

import me.maksuslik.coordinator.entity.UserData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<UserData, UUID> {

    @Query("""
            SELECT f FROM users f
            WHERE f.userId = :id
            """)
    Optional<UserData> findById(Long id);

    @Query("""
            SELECT f FROM users f
            WHERE f.email = :email
            """)
    Optional<UserData> findByEmail(String email);
}
