package ru.practicum.shareit.integration.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnauthorizedCommentException;
import ru.practicum.shareit.item.dto.InputCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class ItemServiceIntegrationTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User user;
    private User user2;
    private ItemRequest itemRequest;
    private ItemDto itemDto1;
    private ItemDto itemDto2;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setName("First User");
        user.setEmail("firstUser@mail.ru");
        userRepository.save(user);

        user2 = new User();
        user2.setName("Second User");
        user2.setEmail("secondUser@mail.ru");
        userRepository.save(user2);

        itemRequest = new ItemRequest();
        itemRequest.setRequestor(user2);
        itemRequest.setDescription("Description");
        itemRequest.setCreated(LocalDateTime.now());
        requestRepository.save(itemRequest);

        itemDto1 = new ItemDto();
        itemDto1.setName("Test Item");
        itemDto1.setDescription("Test Description");
        itemDto1.setAvailable(true);

        itemDto2 = new ItemDto();
        itemDto2.setName("Test Item 2");
        itemDto2.setDescription("Test Description 2");
        itemDto2.setAvailable(true);
    }

    @Test
    public void addItemTest() {
        itemDto1.setRequestId(itemRequest.getId());
        ItemDto savedItem1 = itemService.addItem(itemDto1, user.getId());

        assertEquals(savedItem1.getId(), 1, "ID сохраненного элемента должен быть 1.");
        assertEquals(itemDto1.getName(), savedItem1.getName(),
                "Имя сохраненного элемента должно совпадать с именем исходного элемента.");

        assertThrows(NotFoundException.class, () -> itemService.addItem(itemDto2, 3),
                "Должно выбрасываться NotFoundException, если пользователь с таким ID не найден.");

        ItemDto itemDto3 = new ItemDto();
        itemDto3.setName("Test Item 3");
        itemDto3.setDescription("Test Description 3");
        itemDto3.setAvailable(true);
        itemDto3.setRequestId((long) 2);

        assertThrows(NotFoundException.class, () -> itemService.addItem(itemDto3, user2.getId()),
                "Должно выбрасываться NotFoundException, если предмет с таким ID не найден.");
    }

    @Test
    public void updateItemTest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        ItemDto savedItem = itemService.addItem(itemDto, user.getId());

        ItemDto updatedItem = new ItemDto();
        updatedItem.setName("Updated Item");
        updatedItem.setDescription("Updated Description");
        ItemDto newItem = itemService.updateItem(savedItem.getId(), updatedItem, user.getId());

        assertEquals(newItem.getName(), updatedItem.getName(),
                "Имя обновленного элемента должно совпадать с новым именем.");
        assertEquals(newItem.getDescription(), updatedItem.getDescription(),
                "Описание обновленного элемента должно совпадать с новым описанием.");
        assertThrows(NotFoundException.class, () -> itemService.updateItem(savedItem.getId() + 1, updatedItem, user.getId()),
                "Должно выбрасываться NotFoundException, если элемент с таким ID не найден.");
        assertThrows(NotFoundException.class, () -> itemService.updateItem(savedItem.getId(), updatedItem, user2.getId()),
                "Должно выбрасываться NotFoundException, если пользователь пытается обновить элемент, который ему не принадлежит.");
    }

    @Test
    public void getItemById() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        ItemDto savedItem = itemService.addItem(itemDto, user.getId());
        savedItem.setComments(new ArrayList<>());
        itemService.getItemById(savedItem.getId(), user.getId());

//мб
        assertEquals(savedItem, itemService.getItemById(savedItem.getId(), user.getId()),
                "Элемент должен быть получен корректно по ID, если запрос делает пользователь, который является владельцем элемента.");
        assertThrows(NotFoundException.class, () -> itemService.getItemById(savedItem.getId() + 1, user.getId()),
                "Должно выбрасываться NotFoundException, если элемент с таким ID не найден.");
        assertThrows(NotFoundException.class, () -> itemService.getItemById(savedItem.getId(), user2.getId() + 1),
                "Должно выбрасываться NotFoundException, если пользователь с таким ID не найден.");
    }

    @Test
    public void getItemsTest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemService.addItem(itemDto, user.getId());

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Test Item2");
        itemDto2.setDescription("Test Description2");
        itemDto2.setAvailable(true);
        itemService.addItem(itemDto2, user.getId());

        List<ItemDto> itemsList = itemService.getItems(user.getId());
        assertEquals(itemsList.size(), 2, "Список элементов пользователя должен содержать два элемента.");
    }

    @Test
    public void searchItemTest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemService.addItem(itemDto, user.getId());

        List<ItemDto> itemsList = itemService.searchItem("test");

        assertEquals(itemsList.size(), 1, "Список элементов, найденных по запросу 'test', должен содержать один элемент.");
    }

    @Test
    public void addCommentTest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        Item savedItem = ItemMapper.toItem(itemService.addItem(itemDto, user.getId()), user);
        ItemDto savedItem2 = itemService.addItem(itemDto2, user2.getId());

        bookingRepository.save(new Booking(LocalDateTime.now().minusMinutes(70), LocalDateTime.now().minusMinutes(10),
                savedItem, user2, BookingStatus.APPROVED));
        InputCommentDto commentDto = new InputCommentDto();
        commentDto.setText("New Comment");
        InputCommentDto secondCommentDto = new InputCommentDto();
        secondCommentDto.setText("Second Comment");

        itemService.addComment(savedItem.getId(), commentDto, user2.getId());

        assertEquals(itemService.getItemById(savedItem.getId(), user.getId()).getComments().size(), 1,
                "Должно быть 1 комментарий у элемента после добавления комментария.");
        assertThrows(UnauthorizedCommentException.class,
                () -> itemService.addComment(savedItem2.getId(), secondCommentDto, user.getId()),
                "Должно выбрасываться UnauthorizedCommentException, если пользователь пытается добавить комментарий к элементу, который ему не принадлежит.");
        assertThrows(NotFoundException.class,
                () -> itemService.addComment(savedItem2.getId() + 1, commentDto, user.getId()),
                "Должно выбрасываться NotFoundException, если элемент с таким ID не найден.");
        assertThrows(NotFoundException.class,
                () -> itemService.addComment(savedItem.getId(), commentDto, user2.getId() + 1),
                "Должно выбрасываться NotFoundException, если пользователь с таким ID не найден.");
    }
}
