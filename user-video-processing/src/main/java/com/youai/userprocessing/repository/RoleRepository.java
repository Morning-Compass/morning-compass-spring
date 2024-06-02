package com.youai.userprocessing.repository;

import com.youai.userprocessing.models.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String Name );

//    @Query("UPDATE ConfirmationToken confirmationToken SET confirmationToken.confirmedAt = ?2 WHERE confirmationToken.token = ?1")
}
