package com.keakimleang.digital_menu.features.users.controllers;

import com.keakimleang.digital_menu.annotations.*;
import com.keakimleang.digital_menu.commons.controllers.*;
import com.keakimleang.digital_menu.commons.payloads.*;
import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.users.payloads.response.*;
import com.keakimleang.digital_menu.features.users.securities.*;
import com.keakimleang.digital_menu.features.users.services.*;
import com.keakimleang.digital_menu.utils.*;
import java.util.*;
import lombok.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(APIURLs.ROLE)
@RequiredArgsConstructor
public class RoleController {
    private final RoleServiceImpl roleService;
    private final ControllerServiceCallback callback;

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<List<RoleResponse>> findRolesOfUser(
            @CurrentUser CustomUserDetails user
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.user());
        return callback.execute(() -> roleService.getRolesBasedOnUserRole(user.user()),
                "Roles failed to fetch",
                null);
    }
}
