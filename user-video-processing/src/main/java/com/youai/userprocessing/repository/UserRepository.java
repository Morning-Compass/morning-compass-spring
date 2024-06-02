package com.youai.userprocessing.repository;

import com.youai.userprocessing.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

//    UPDATE UserEntity user SET user.verifiedAccount = TRUE where user.email = ?1
    @Modifying
    @Query("UPDATE UserEntity user SET user.verifiedAccount = TRUE where user.email = ?1")
    void updateUserVerifiedAccount(String email);
}
