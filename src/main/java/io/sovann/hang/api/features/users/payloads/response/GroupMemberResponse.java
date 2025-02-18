package io.sovann.hang.api.features.users.payloads.response;

import io.sovann.hang.api.features.users.entities.Group;
import io.sovann.hang.api.features.users.entities.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GroupMemberResponse {
    private UserResponse user;
    private GroupResponse group;

    public static GroupMemberResponse fromEntities(User userToRemove, Group group) {
        GroupMemberResponse response = new GroupMemberResponse();
        response.setUser(UserResponse.fromEntity(userToRemove));
        response.setGroup(GroupResponse.fromEntity(group));
        return response;
    }
}
