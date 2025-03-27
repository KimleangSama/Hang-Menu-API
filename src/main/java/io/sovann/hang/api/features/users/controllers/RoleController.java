package io.sovann.hang.api.features.users.controllers;

import io.sovann.hang.api.annotations.*;
import io.sovann.hang.api.commons.controllers.*;
import io.sovann.hang.api.commons.payloads.*;
import io.sovann.hang.api.constants.*;
import io.sovann.hang.api.features.users.payloads.response.*;
import io.sovann.hang.api.features.users.securities.*;
import io.sovann.hang.api.features.users.services.*;
import io.sovann.hang.api.utils.*;
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
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> roleService.getRolesBasedOnUserRole(user.getUser()),
                "Roles failed to fetch",
                null);
    }
}
