package com.keakimleang.digital_menu.commons.entities;

import java.time.*;
import java.util.*;

public interface EntityDeletable {
    UUID getDeletedBy();

    LocalDateTime getDeletedAt();
}
