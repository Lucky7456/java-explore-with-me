package ru.practicum.ewm.user;

import java.util.List;

public interface UserService {
    List<UserDto> getAll(List<Long> ids, int from, int size);

    UserDto create(NewUserRequest userDto);

    void delete(long id);
}
