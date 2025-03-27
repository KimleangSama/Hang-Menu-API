package io.sovann.hang.api.features.users.controllers;

import io.sovann.hang.api.annotations.*;
import io.sovann.hang.api.commons.controllers.*;
import io.sovann.hang.api.commons.payloads.*;
import io.sovann.hang.api.constants.*;
import io.sovann.hang.api.features.users.payloads.request.*;
import io.sovann.hang.api.features.users.payloads.response.*;
import io.sovann.hang.api.features.users.securities.*;
import io.sovann.hang.api.features.users.services.*;
import io.sovann.hang.api.utils.*;
import java.util.*;
import lombok.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(APIURLs.GROUP)
@RequiredArgsConstructor
public class GroupController {
    private final GroupServiceImpl groupService;
    private final ControllerServiceCallback callback;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<GroupResponse> createGroup(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateGroupRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> groupService.createGroup(user.getUser(), request),
                "Group failed to create",
                null);
    }

    @GetMapping("/{id}/users")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<List<UserResponse>> findAllUsersOfGroupId(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> groupService.findAllUsersOfGroupId(user.getUser(), id),
                "Groups failed to fetch",
                null);
    }

    @DeleteMapping("/remove")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<GroupMemberResponse> removeUserFromGroup(
            @CurrentUser CustomUserDetails user,
            @RequestBody AddOrRemoveGroupMemberRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> groupService.removeUserFromGroup(user.getUser(), request),
                "Failed to remove user from group",
                null);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<GroupMemberResponse> addUserToGroup(
            @CurrentUser CustomUserDetails user,
            @RequestBody AddOrRemoveGroupMemberRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> groupService.addUserToGroup(user.getUser(), request),
                "Failed to add user to group",
                null);
    }

    @PostMapping("/mote")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<GroupResponse> promoteUserInGroup(
            @CurrentUser CustomUserDetails user,
            @RequestBody PromoteDemoteRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> groupService.promoteOrDemoteUserInGroup(user.getUser(), request),
                "User failed to promote",
                null);
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<GroupMemberResponse> registerUserToGroup(
            @CurrentUser CustomUserDetails user,
            @RequestBody RegisterToGroupRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> groupService.registerUserToGroup(user.getUser(), request),
                "Failed to register user to group",
                null);
    }
}
