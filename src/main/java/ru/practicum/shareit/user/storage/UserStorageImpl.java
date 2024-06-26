package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EmailAlreadyBusyException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class UserStorageImpl implements UserStorage {
    private int userId = 0;
    private Map<Integer, User> users = new HashMap<>();
    private Set<String> emails = new HashSet<>();

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.setId(createUserId());
        users.put(userId, user);
        emails.add(userDto.getEmail());
        return UserMapper.toUserDto(users.get(userId));
    }

    @Override
    public UserDto updateUser(int id, UserDto userDto) {
        emails.remove(users.get(id).getEmail());
        emails.add(userDto.getEmail());

        User user = UserMapper.toUser(userDto);
        users.put(id, user);

        return UserMapper.toUserDto(users.get(id));
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> usersList = new ArrayList<>(users.values());
        List<UserDto> dtoList = new ArrayList<>();
        for (User user : usersList) {
            dtoList.add(UserMapper.toUserDto(user));
        }
        return dtoList;
    }

    @Override
    public UserDto getUserById(int id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(int id) {
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    @Override
    public void checkEmailBusy(String email) {
        if (emails.contains(email)) {
            throw new EmailAlreadyBusyException("Этот Email уже используется.");
        }
    }

    private int createUserId() {
        return ++userId;
    }
}
