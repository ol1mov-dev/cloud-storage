package com.example.cloudstorage.repos;

import com.example.cloudstorage.models.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u JOIN FETCH u.role r JOIN FETCH r.authorities WHERE u.email = :email")
    Optional<User> findByEmailWithAuthorities(@Param("email") String email);

    boolean existsByEmail(@NotBlank String email);
}
