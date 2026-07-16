package com.code.tarun.repository;

import com.code.tarun.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {

    Optional<Users> findByName(String username);

    boolean existsByName(String username);
}
