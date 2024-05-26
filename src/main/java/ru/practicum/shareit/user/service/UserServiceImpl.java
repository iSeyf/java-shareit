package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        return storage.createUser(userDto);
    }

    @Override
    public UserDto updateUser(int id, UserDto userDto) {
        return storage.updateUser(id, userDto);
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
}
