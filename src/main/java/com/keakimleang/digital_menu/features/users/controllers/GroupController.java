package com.keakimleang.digital_menu.features.users.controllers;

import com.keakimleang.digital_menu.annotations.CurrentUser;
import com.keakimleang.digital_menu.commons.controllers.ControllerServiceCallback;
import com.keakimleang.digital_menu.commons.payloads.BaseResponse;
import com.keakimleang.digital_menu.constants.APIURLs;
import com.keakimleang.digital_menu.features.users.payloads.request.AddOrRemoveGroupMemberRequest;
import com.keakimleang.digital_menu.features.users.payloads.request.CreateGroupRequest;
import com.keakimleang.digital_menu.features.users.payloads.request.PromoteDemoteRequest;
import com.keakimleang.digital_menu.features.users.payloads.request.RegisterToGroupRequest;
import com.keakimleang.digital_menu.features.users.payloads.response.GroupMemberResponse;
import com.keakimleang.digital_menu.features.users.payloads.response.GroupResponse;
import com.keakimleang.digital_menu.features.users.payloads.response.UserResponse;
import com.keakimleang.digital_menu.features.users.securities.CustomUserDetails;
import com.keakimleang.digital_menu.features.users.services.GroupServiceImpl;
import com.keakimleang.digital_menu.utils.SoftEntityDeletable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
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
    @PreAuthorize("hasAnyRole('admin', 'manager', 'staff')")
    public BaseResponse<List<UserResponse>> findAllUsersOfGroupId(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> groupService.findAllUsersOfGroupId(user.user(), id),
                "Groups failed to fetch",
                null);
    }

    @PostMapping("/remove")
    @PreAuthorize("hasAnyRole('admin', 'manager', 'staff')")
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
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> groupService.promoteOrDemoteUserInGroup(user.user(), request),
                "User failed to promote",
                null);
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('admin', 'manager', 'staff')")
    public BaseResponse<GroupMemberResponse> registerUserToGroup(
            @CurrentUser CustomUserDetails user,
            @RequestBody RegisterToGroupRequest request
    ) {
        log.info("Registering user to group {}", request);
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> groupService.registerUserToGroup(user.user(), request),
                "Failed to register user to group",
                null);
    }
}
