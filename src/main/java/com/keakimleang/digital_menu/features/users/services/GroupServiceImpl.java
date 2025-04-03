package com.keakimleang.digital_menu.features.users.services;

import com.keakimleang.digital_menu.exceptions.ResourceForbiddenException;
import com.keakimleang.digital_menu.exceptions.ResourceNotFoundException;
import com.keakimleang.digital_menu.features.users.entities.Group;
import com.keakimleang.digital_menu.features.users.entities.GroupMember;
import com.keakimleang.digital_menu.features.users.entities.Role;
import com.keakimleang.digital_menu.features.users.entities.User;
import com.keakimleang.digital_menu.features.users.enums.AuthRole;
import com.keakimleang.digital_menu.features.users.payloads.request.AddOrRemoveGroupMemberRequest;
import com.keakimleang.digital_menu.features.users.payloads.request.CreateGroupRequest;
import com.keakimleang.digital_menu.features.users.payloads.request.PromoteDemoteRequest;
import com.keakimleang.digital_menu.features.users.payloads.request.RegisterToGroupRequest;
import com.keakimleang.digital_menu.features.users.payloads.response.GroupMemberResponse;
import com.keakimleang.digital_menu.features.users.payloads.response.GroupResponse;
import com.keakimleang.digital_menu.features.users.payloads.response.UserResponse;
import com.keakimleang.digital_menu.features.users.repos.GroupMemberRepository;
import com.keakimleang.digital_menu.features.users.repos.GroupRepository;
import com.keakimleang.digital_menu.features.users.repos.UserRepository;
import com.keakimleang.digital_menu.utils.RandomString;
import com.keakimleang.digital_menu.utils.ResourceOwner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final RoleServiceImpl roleServiceImpl;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public GroupResponse createGroup(User user, CreateGroupRequest request) {
        Group group = CreateGroupRequest.fromRequest(request);
        group.setCreatedBy(user.getId());
        return GroupResponse.fromEntity(groupRepository.save(group));
    }

    @Transactional
    public long count() {
        return groupRepository.count();
    }

    @Transactional
    public List<UserResponse> findAllUsersOfGroupId(User user, UUID groupId) {
        Group group = getGroupById(groupId);
        if (isManagerOrStaffOrCreator(user, group)) {
            throw new ResourceForbiddenException(user.getUsername(), Group.class);
        }
        List<GroupMember> groupMembers = groupMemberRepository.findByGroupId(group.getId());
        return groupMembers.stream()
                .map(groupMember -> UserResponse.fromEntity(groupMember.getUser()))
                .collect(Collectors.toList());
    }

    public GroupResponse promoteOrDemoteUserInGroup(User user, PromoteDemoteRequest request) {
        Group group = getGroupById(request.getGroupId());
        User userToPromote = getUserById(request.getUserId());

        Set<Role> availableRoles = roleServiceImpl.findByIds(request.getRoles()).stream()
                .filter(role -> roleServiceImpl.getRolesBasedOnUserRole(user)
                        .stream().anyMatch(roleResponse -> roleResponse.getId().equals(role.getId())))
                .collect(Collectors.toSet());

        if (availableRoles.isEmpty()) {
            throw new ResourceNotFoundException("Role", request.getRoles().toString());
        }

        userToPromote.setRoles(availableRoles);
        userRepository.save(userToPromote);

        return GroupResponse.fromEntity(group);
    }

    @CacheEvict(value = "groups", key = "#request.username")
    public GroupMemberResponse removeUserFromGroup(User user, AddOrRemoveGroupMemberRequest request) {
        Group group = getGroupById(request.getGroupId());
        if (isManagerOrStaffOrCreator(user, group)) {
            throw new ResourceForbiddenException(user.getUsername(), Group.class);
        }
        User userToRemove = getUserById(request.getUserId());
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(group.getId(), userToRemove.getId())
                .orElseThrow(() -> new ResourceNotFoundException("GroupMember",
                        request.getUserId().toString() + " and " + request.getGroupId().toString()));
        groupMemberRepository.delete(groupMember);
        return GroupMemberResponse.fromEntities(userToRemove, group);
    }

    @CacheEvict(value = "groups", key = "#request.username")
    public GroupMemberResponse addUserToGroup(User user, AddOrRemoveGroupMemberRequest request) {
        Group group = getGroupById(request.getGroupId());
        User userToAdd = getUserById(request.getUserId());
        return addGroupMember(group, userToAdd);
    }

    @Transactional
    @CacheEvict(value = "groups", key = "#request.username")
    public GroupMemberResponse registerUserToGroup(User user, RegisterToGroupRequest request) {
        Group group = getGroupById(request.getGroupId());
        if (!ResourceOwner.hasPermission(user, group)) {
            throw new ResourceForbiddenException(user.getUsername(), Group.class);
        }
        List<Role> roles = roleServiceImpl.findByNames(request.getRoles());
        User userToRegister = RegisterToGroupRequest.fromRequest(request, new HashSet<>(roles));
        userToRegister.setCreatedBy(user.getId());
        userToRegister.setEmail(RandomString.make(12) + "@email.com");
        userToRegister.setRaw(request.getPassword());
        userToRegister.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(userToRegister);
        return addGroupMember(group, userToRegister);
    }

    private Group getGroupById(UUID groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", groupId.toString()));
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
    }

    private boolean isManagerOrStaffOrCreator(User user, Group group) {
        return user.getRoles().stream().noneMatch(role -> role.getName().equals(AuthRole.manager) || role.getName().equals(AuthRole.staff))
                && !group.getCreatedBy().equals(user.getId());
    }

    private GroupMemberResponse addGroupMember(Group group, User user) {
        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(group);
        groupMember.setUser(user);
        groupMemberRepository.save(groupMember);
        return GroupMemberResponse.fromEntities(user, group);
    }

    @Transactional
    public Group findGroupByUser(User user) {
        return groupMemberRepository.findByUserId(user.getId())
                .map(GroupMember::getGroup)
                .orElseThrow(() -> new ResourceNotFoundException("Group", user.getId().toString()));
    }
}
