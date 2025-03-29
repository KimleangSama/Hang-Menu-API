package com.keakimleang.digital_menu.features.users.controllers;

import com.keakimleang.digital_menu.annotations.*;
import com.keakimleang.digital_menu.commons.controllers.*;
import com.keakimleang.digital_menu.commons.payloads.*;
import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.users.payloads.request.*;
import com.keakimleang.digital_menu.features.users.payloads.response.*;
import com.keakimleang.digital_menu.features.users.securities.*;
import com.keakimleang.digital_menu.features.users.services.*;
import com.keakimleang.digital_menu.utils.*;
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
        return callback.execute(() -> groupService.createGroup(user.user(), request),
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
        return callback.execute(() -> groupService.findAllUsersOfGroupId(user.user(), id),
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
        return callback.execute(() -> groupService.removeUserFromGroup(user.user(), request),
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
        return callback.execute(() -> groupService.addUserToGroup(user.user(), request),
                "Failed to add user to group",
                null);
    }

    @PostMapping("/mote")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<GroupResponse> promoteUserInGroup(
            @CurrentUser CustomUserDetails user,
            @RequestBody PromoteDemoteRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.user());
        return callback.execute(() -> groupService.promoteOrDemoteUserInGroup(user.user(), request),
                "User failed to promote",
                null);
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<GroupMemberResponse> registerUserToGroup(
            @CurrentUser CustomUserDetails user,
            @RequestBody RegisterToGroupRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.user());
        return callback.execute(() -> groupService.registerUserToGroup(user.user(), request),
                "Failed to register user to group",
                null);
    }
}
