package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validation.ValidationUtil;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = repository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        User user = ValidationUtil.checkUser(id, repository);

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
    public List<UserDto> getUsers() {
        return UserMapper.toUserDtoList(repository.findAll());
    }

    @Override
    public UserDto getUserById(long id) {
        User user = ValidationUtil.checkUser(id, repository);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(long id) {
        ValidationUtil.checkUser(id, repository);
        repository.deleteById(id);
    }

    private void checkEmailIsCorrect(UserDto userDto) {
        if (userDto.getEmail().contains(" ") || !userDto.getEmail().contains("@")) {
            throw new ValidationException("Некорректный Email.");
        }
    }
}
