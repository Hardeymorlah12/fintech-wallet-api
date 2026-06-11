package com.hardeymorlah.walletapi.repository;

import com.hardeymorlah.walletapi.entity.RefreshToken;
import com.hardeymorlah.walletapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Repository
public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    @Transactional
    @Modifying
    void deleteByUser(User user);
}
