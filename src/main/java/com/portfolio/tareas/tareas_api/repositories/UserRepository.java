package com.portfolio.tareas.tareas_api.repositories;

import com.portfolio.tareas.tareas_api.models.AppUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AppUser, UUID> {

	Optional<AppUser> findByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);

	boolean existsByUsernameIgnoreCase(String username);

	boolean existsByEmailIgnoreCase(String email);
}
