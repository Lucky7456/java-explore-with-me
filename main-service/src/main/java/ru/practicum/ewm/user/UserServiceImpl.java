package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);

        if (ids != null && !ids.isEmpty()) {
            return repository.findAllByIdIn(ids, pageable).stream()
                    .map(UserMapper::mapToUserDto)
                    .toList();
        }

        return repository.findAll(pageable).getContent().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto create(NewUserRequest newUserRequest) {
        return UserMapper.mapToUserDto(repository.save(UserMapper.mapToUser(newUserRequest)));
    }

    @Override
    public void delete(long id) {
        repository.findById(id).orElseThrow();
        repository.deleteById(id);
    }
}
