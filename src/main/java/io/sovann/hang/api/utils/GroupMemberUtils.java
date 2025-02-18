package io.sovann.hang.api.utils;

import io.sovann.hang.api.features.users.entities.GroupMember;
import io.sovann.hang.api.features.users.entities.User;

import java.util.Set;

public class GroupMemberUtils {
    public static boolean isUserIsMemberOfGroup(User user, Set<GroupMember> members) {
        for (GroupMember member : members) {
            if (member.getUser().getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }
}
