package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private UserStorage storage;

    @Autowired
    public UserServiceImpl(UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        storage.checkEmailBusy(userDto.getEmail());
        return storage.createUser(userDto);
    }

    @Override
    public UserDto updateUser(int id, UserDto userDto) {
        UserDto updatedUser = getUserById(id);
        if (updatedUser == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            checkEmailIsCorrect(userDto);
            if (!userDto.getEmail().equals(updatedUser.getEmail())) {
                storage.checkEmailBusy(userDto.getEmail());
                updatedUser.setEmail(userDto.getEmail());
            }
        }
        return storage.updateUser(id, updatedUser);
    }

    @Override
    public List<UserDto> getUsers() {
        return storage.getUsers();
    }

    @Override
    public UserDto getUserById(int id) {
        return storage.getUserById(id);
    }

    @Override
    public void deleteUser(int id) {
        storage.deleteUser(id);
    }

    private void checkEmailIsCorrect(UserDto userDto) {
        if (userDto.getEmail().contains(" ") || !userDto.getEmail().contains("@")) {
            throw new ValidationException("Некорректный Email.");
        }
    }

}
