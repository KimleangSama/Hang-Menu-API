package com.keakimleang.digital_menu.features.users.repos;

import com.keakimleang.digital_menu.features.users.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {

}
