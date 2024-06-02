package com.youai.userprocessing.repository;

import com.youai.userprocessing.models.ConfirmationToken;
import com.youai.userprocessing.models.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long>{
    Optional<ConfirmationToken> findByToken(String token);

    @Modifying
    @Query("UPDATE ConfirmationToken confirmationToken SET confirmationToken.confirmedAt = ?2 WHERE confirmationToken.token = ?1")
    void updateConfirmedAt(String token, Date confirmedAt);
}
