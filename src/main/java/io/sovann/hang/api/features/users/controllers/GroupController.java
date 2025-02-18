package io.sovann.hang.api.features.users.controllers;

import io.sovann.hang.api.annotations.CurrentUser;
import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.features.commons.controllers.ControllerServiceCallback;
import io.sovann.hang.api.features.commons.payloads.BaseResponse;
import io.sovann.hang.api.features.commons.payloads.PageMeta;
import io.sovann.hang.api.features.users.payloads.request.AddOrRemoveGroupMemberRequest;
import io.sovann.hang.api.features.users.payloads.request.CreateGroupRequest;
import io.sovann.hang.api.features.users.payloads.request.PromoteDemoteRequest;
import io.sovann.hang.api.features.users.payloads.request.RegisterToGroupRequest;
import io.sovann.hang.api.features.users.payloads.response.GroupMemberResponse;
import io.sovann.hang.api.features.users.payloads.response.GroupResponse;
import io.sovann.hang.api.features.users.payloads.response.UserResponse;
import io.sovann.hang.api.features.users.securities.CustomUserDetails;
import io.sovann.hang.api.features.users.services.GroupServiceImpl;
import io.sovann.hang.api.utils.SoftEntityDeletable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> groupService.createGroup(user.getUser(), request),
                "Group failed to create",
                null);
    }

    @GetMapping("/{id}/users")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<List<UserResponse>> getUsers(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        PageMeta pageMeta = new PageMeta(page, size, groupService.countUsers(id));
        return callback.execute(() -> groupService.getUsers(user.getUser(), id, page, size),
                "Groups failed to fetch",
                pageMeta);
    }

    @DeleteMapping("/remove")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<GroupMemberResponse> removeUser(
            @CurrentUser CustomUserDetails user,
            @RequestBody AddOrRemoveGroupMemberRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> groupService.removeUser(user.getUser(), request),
                "Failed to remove user from group",
                null);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<GroupMemberResponse> addUser(
            @CurrentUser CustomUserDetails user,
            @RequestBody AddOrRemoveGroupMemberRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> groupService.addUser(user.getUser(), request),
                "Failed to add user to group",
                null);
    }

    @PostMapping("/mote")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<GroupResponse> promoteUser(
            @CurrentUser CustomUserDetails user,
            @RequestBody PromoteDemoteRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> groupService.promoteOrDemoteUser(user.getUser(), request),
                "User failed to promote",
                null);
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<GroupMemberResponse> registerUser(
            @CurrentUser CustomUserDetails user,
            @RequestBody RegisterToGroupRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> groupService.registerUser(user.getUser(), request),
                "Failed to register user to group",
                null);
    }
}
