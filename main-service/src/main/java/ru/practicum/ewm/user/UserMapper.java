package ru.practicum.ewm.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User mapToUser(NewUserRequest newUserRequest) {
        return new User(
                0,
                newUserRequest.getName(),
                newUserRequest.getEmail()
        );
    }

    public static UserShortDto mapToShortUserDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }
}
