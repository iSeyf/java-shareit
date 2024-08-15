package ru.practicum.shareit.integration.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class ItemRequestServiceIntegrationTest {
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private User user;
    private Item item;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setName("First User");
        user.setEmail("firstUser@mail.ru");
        userRepository.save(user);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Item Description");
        item.setAvailable(true);
        item = itemRepository.save(item);
    }

    @Test
    public void addItemRequestTest() {
        InputItemRequestDto requestDto = new InputItemRequestDto("Description");
        ItemRequestDto savedRequest = itemRequestService.addItemRequest(requestDto, user.getId());
        assertEquals(savedRequest.getDescription(), requestDto.getDescription(), "Описание запроса должно соответствовать переданному.");
    }

    @Test
    public void getUserRequestsTest() {
        InputItemRequestDto requestDto = new InputItemRequestDto("Description");
        ItemRequestDto savedRequest = itemRequestService.addItemRequest(requestDto, user.getId());
        savedRequest.setItems(new ArrayList<>());
        List<ItemRequestDto> userRequests = itemRequestService.getUserRequests(user.getId());
        assertNotNull(userRequests, "Список запросов не должен быть null.");
        assertEquals(1, userRequests.size(), "Список запросов должен содержать 1 элемент.");
        assertEquals(requestDto.getDescription(), userRequests.get(0).getDescription(), "Описание запроса должно соответствовать сохраненному.");
    }

    @Test
    public void getAllRequestsTest() {
        InputItemRequestDto inputRequest1 = new InputItemRequestDto("Description 1");
        itemRequestService.addItemRequest(inputRequest1, user.getId());
        InputItemRequestDto inputRequest2 = new InputItemRequestDto("Description 2");
        itemRequestService.addItemRequest(inputRequest2, user.getId());


        List<ItemRequestDto> requests = itemRequestService.getAllRequests(user.getId(), 0, 10);

        assertNotNull(requests, "Список запросов не должен быть null.");
        assertEquals(2, requests.size(), "Список запросов должен содержать 2 элемента.");
        assertTrue(requests.stream().anyMatch(r -> r.getDescription().equals("Description 1")), "В списке запросов должен быть запрос с описанием 'Description 1'.");
        assertTrue(requests.stream().anyMatch(r -> r.getDescription().equals("Description 2")), "В списке запросов должен быть запрос с описанием 'Description 2'.");
    }

    @Test
    public void getRequestByIdTest() {
        InputItemRequestDto requestDto = new InputItemRequestDto("Description");
        ItemRequestDto savedRequest = itemRequestService.addItemRequest(requestDto, user.getId());
        ItemRequestDto request = itemRequestService.getRequestById(user.getId(), savedRequest.getId());
        savedRequest.setItems(new ArrayList<>());
        assertNotNull(request, "Запрос не должен быть null.");
        assertEquals(savedRequest.getDescription(), request.getDescription(), "Описание запроса должно соответствовать сохраненному.");

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(user.getId(), savedRequest.getId() + 1),
                "Должно выбрасываться NotFoundException для несуществующего запроса.");
    }
}
