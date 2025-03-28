package com.keakimleang.digital_menu.features.users.repos;

import com.keakimleang.digital_menu.features.users.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    List<User> findByIdIn(List<UUID> ids);
}
