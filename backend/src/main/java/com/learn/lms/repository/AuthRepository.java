package com.learn.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learn.lms.model.User;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {
    public User findByUsername(String username);

    public User findByEmail(String email);
}
