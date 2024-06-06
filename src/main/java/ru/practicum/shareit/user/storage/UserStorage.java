package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(int id, UserDto userDto);

    List<UserDto> getUsers();

    UserDto getUserById(int id);

    void deleteUser(int id);

}
