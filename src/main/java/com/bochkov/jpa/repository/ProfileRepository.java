package com.bochkov.jpa.repository;

import com.bochkov.jpa.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    @Modifying
    @Query(value = "update profiles set cash=(0.97 * random() + 0.1) * cash + cash", nativeQuery = true)
    @Transactional
    int cashUp();
}
