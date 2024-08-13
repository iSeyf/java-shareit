package ru.practicum.shareit.integration.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;
    private UserDto user;
    private UserDto user2;

    @BeforeEach
    public void setUp() {
        user = new UserDto();
        user.setName("User Name");
        user.setEmail("user@mail.ru");

        user2 = new UserDto();
        user2.setName("Second Name");
        user2.setEmail("secondUser@mail.ru");

        wrongUser = new UserDto();
        user.setName("");
    }

    @Test
    public void createUserTest() {
        UserDto savedUser = userService.createUser(user);

        assertNotEquals(0, savedUser.getId(), "ID сохраненного пользователя не должен быть равен 0.");
        assertEquals(user.getName(), savedUser.getName(), "Имя пользователя должно совпадать.");
        assertEquals(user.getEmail(), savedUser.getEmail(), "Email пользователя должен совпадать.");
    }

    @Test
    public void updateUserTest() {
        UserDto savedUser = userService.createUser(user);

        UserDto userDto = new UserDto();
        userDto.setName("User");
        userDto.setEmail("userEmail@mail.ru");

        UserDto updatedUser = userService.updateUser(savedUser.getId(), userDto);

        assertEquals(userDto.getName(), updatedUser.getName(), "Имя пользователя должно быть обновлено.");
        assertEquals(userDto.getEmail(), updatedUser.getEmail(), "Email пользователя должен быть обновлен.");

        assertThrows(NotFoundException.class, () -> userService.updateUser(savedUser.getId() + 1, userDto),
                "Должно выбрасываться NotFoundException для несуществующего пользователя.");
    }

    @Test
    public void getUsersTest() {
        UserDto savedUser = userService.createUser(user);
        UserDto savedUser2 = userService.createUser(user2);
        List<UserDto> expectedUsers = List.of(savedUser, savedUser2);

        List<UserDto> users = userService.getUsers();

        assertNotNull(users, "Список пользователей не должен быть null.");
        assertEquals(expectedUsers.size(), users.size(), "Размер списка пользователей должен совпадать.");
        assertTrue(users.containsAll(expectedUsers), "Список пользователей должен содержать все добавленные пользователи.");
    }

    @Test
    public void getUserByIdTest() {
        UserDto savedUser = userService.createUser(user);

        UserDto foundUser = userService.getUserById(savedUser.getId());

        assertEquals(savedUser, foundUser, "Данные пользователя должны совпадать.");
        assertThrows(NotFoundException.class, () -> userService.getUserById(savedUser.getId() + 1),
                "Должно выбрасываться NotFoundException для несуществующего пользователя.");
    }

    @Test
    public void deleteUserTest() {
        UserDto savedUser = userService.createUser(user);

        assertEquals(savedUser, userService.getUserById(savedUser.getId()), "Пользователь должен быть доступен до удаления.");

        userService.deleteUser(savedUser.getId());
        assertThrows(NotFoundException.class, () -> userService.getUserById(savedUser.getId()), "Должно выбрасываться NotFoundException для удаленного пользователя.");
    }
}
