package io.sovann.hang.api.features.users.services;

import io.sovann.hang.api.exceptions.ResourceForbiddenException;
import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.users.entities.Group;
import io.sovann.hang.api.features.users.entities.GroupMember;
import io.sovann.hang.api.features.users.entities.Role;
import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.enums.AuthRole;
import io.sovann.hang.api.features.users.payloads.request.AddOrRemoveGroupMemberRequest;
import io.sovann.hang.api.features.users.payloads.request.CreateGroupRequest;
import io.sovann.hang.api.features.users.payloads.request.PromoteDemoteRequest;
import io.sovann.hang.api.features.users.payloads.request.RegisterToGroupRequest;
import io.sovann.hang.api.features.users.payloads.response.GroupMemberResponse;
import io.sovann.hang.api.features.users.payloads.response.GroupResponse;
import io.sovann.hang.api.features.users.payloads.response.UserResponse;
import io.sovann.hang.api.features.users.repos.GroupMemberRepository;
import io.sovann.hang.api.features.users.repos.GroupRepository;
import io.sovann.hang.api.features.users.repos.UserRepository;
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
        if (!isManagerOrCreator(user, group)) {
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
        List<Role> roles = roleServiceImpl.findByIds(request.getRoles());
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        User userToRegister = RegisterToGroupRequest.fromRequest(request, new HashSet<>(roles));
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

    private boolean isManagerOrCreator(User user, Group group) {
        return user.getRoles().stream().anyMatch(role -> role.getName().equals(AuthRole.manager))
                || group.getCreatedBy().equals(user.getId());
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
