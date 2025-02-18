package io.sovann.hang.api.features.users.controllers;

import io.sovann.hang.api.annotations.CurrentUser;
import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.features.commons.controllers.ControllerServiceCallback;
import io.sovann.hang.api.features.commons.payloads.BaseResponse;
import io.sovann.hang.api.features.users.payloads.response.RoleResponse;
import io.sovann.hang.api.features.users.securities.CustomUserDetails;
import io.sovann.hang.api.features.users.services.RoleServiceImpl;
import io.sovann.hang.api.utils.SoftEntityDeletable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(APIURLs.ROLE)
@RequiredArgsConstructor
public class RoleController {
    private final RoleServiceImpl roleService;
    private final ControllerServiceCallback callback;

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<List<RoleResponse>> getRoles(
            @CurrentUser CustomUserDetails user
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> roleService.getRolesBasedOnUserRole(user.getUser()),
                "Roles failed to fetch",
                null);
    }
}
