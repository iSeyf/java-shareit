package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailAlreadyBusyException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private UserStorage storage;
    private Set<String> emails = new HashSet<>();

    @Autowired
    public UserServiceImpl(UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        checkEmailBusy(userDto.getEmail());
        UserDto createdUser = storage.createUser(userDto);
        emails.add(userDto.getEmail());
        return createdUser;
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
                checkEmailBusy(userDto.getEmail());
                emails.remove(updatedUser.getEmail());
                updatedUser.setEmail(userDto.getEmail());
                emails.add(updatedUser.getEmail());
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
        UserDto userDto = getUserById(id);
        storage.deleteUser(id);
        emails.remove(userDto.getEmail());
    }

    private void checkEmailIsCorrect(UserDto userDto) {
        if (userDto.getEmail().contains(" ") || !userDto.getEmail().contains("@")) {
            throw new ValidationException("Некорректный Email.");
        }
    }

    private void checkEmailBusy(String email) {
        if (emails.contains(email)) {
            throw new EmailAlreadyBusyException("Этот Email уже используется.");
        }
    }

}
