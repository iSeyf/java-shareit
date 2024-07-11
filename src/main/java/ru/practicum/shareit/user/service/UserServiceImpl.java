package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = repository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(long id, UserDto userDto) {
        User user = repository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден."));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            checkEmailIsCorrect(userDto);
            if (!userDto.getEmail().equals(user.getEmail())) {
                user.setEmail(userDto.getEmail());
            }
        }
        return UserMapper.toUserDto(repository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        return UserMapper.toUserDtoList(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(long id) {
        User user = repository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден."));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        repository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с таким ID не найден."));
        repository.deleteById(id);
    }

    private void checkEmailIsCorrect(UserDto userDto) {
        if (userDto.getEmail().contains(" ") || !userDto.getEmail().contains("@")) {
            throw new ValidationException("Некорректный Email.");
        }
    }
}
