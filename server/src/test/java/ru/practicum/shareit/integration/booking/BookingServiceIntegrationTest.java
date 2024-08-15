package ru.practicum.shareit.integration.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class BookingServiceIntegrationTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private CommentRepository commentRepository;


    private User user1;
    private User user2;
    private Item item;
    private BookingRequestDto newBooking;


    @BeforeEach
    public void setUp() {
        user1 = new User();
        user1.setName("First User");
        user1.setEmail("firstUser@mail.ru");
        userRepository.save(user1);

        user2 = new User();
        user2.setName("Second User");
        user2.setEmail("secondUser@mail.ru");
        userRepository.save(user2);

        item = new Item();
        item.setName("New Item");
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setOwner(user1);
        itemRepository.save(item);

        newBooking = new BookingRequestDto();
        newBooking.setStart(LocalDateTime.now().plusMinutes(100));
        newBooking.setEnd(LocalDateTime.now().plusMinutes(200));
        newBooking.setItemId(item.getId());
    }

    @Test
    public void addBookingTest() {
        BookingDto savedBooking = bookingService.addBooking(user2.getId(), newBooking);

        assertEquals(savedBooking.getStart(), newBooking.getStart(), "Дата и время начала должны совпадать");
        assertEquals(savedBooking.getEnd(), newBooking.getEnd(), "Дата и время окончания должны совпадать");
        assertEquals(savedBooking.getItem(), ItemMapper.toItemDto(item), "Предмет бронирования должен соответствовать ожидаемому");
        assertEquals(savedBooking.getBooker(), UserMapper.toUserDto(user2), "Пользователь должен соответствовать ожидаемому");
        assertEquals(savedBooking.getStatus(), BookingStatus.WAITING, "Статус бронирования должен быть 'WAITING'");

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(user1.getId(), newBooking), "Должно выбрасываться NotFoundException при попытке забронировать свою же вещь");
        assertThrows(NotFoundException.class, () -> bookingService.addBooking(user2.getId() + 1, newBooking), "Должно выбрасываться NotFoundException для несуществующего ID пользователя");
    }

    @Test
    public void respondBookingTest() {
        BookingDto savedBooking = bookingService.addBooking(user2.getId(), newBooking);

        BookingDto approvedBooking = bookingService.respondBooking(user1.getId(), savedBooking.getId(), true);

        assertEquals(approvedBooking.getStatus(), BookingStatus.APPROVED, "Статус бронирования должен быть APPROVED после одобрения бронирования");
    }

    @Test
    public void getBookingTest() {
        BookingDto savedBooking = bookingService.addBooking(user2.getId(), newBooking);

        assertEquals(savedBooking, bookingService.getBooking(user1.getId(), savedBooking.getId()), "Должно возвращаться корректное бронирование для указанного пользователя и ID бронирования");
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(user1.getId(), savedBooking.getId() + 1), "Должно выбрасываться NotFoundException, если бронирование с таким ID не найдено");
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(user2.getId() + 1, savedBooking.getId()), "Должно выбрасываться NotFoundException, если пользователь с таким ID не найден");
    }

    @Test
    public void getAllBookingsByUserIdTest() {
        BookingDto savedBooking = bookingService.addBooking(user2.getId(), newBooking);

        assertEquals(savedBooking, bookingService.getAllBookingsByUserId(user2.getId(), BookingState.ALL).get(0), "Должно возвращаться сохранённое бронирование для указанного пользователя");
        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByUserId(user2.getId() + 1, BookingState.ALL), "Должно выбрасываться NotFoundException, если пользователь с таким ID не найден");
    }

    @Test
    public void getAllBookingsByOwnerIdTest() {
        BookingDto savedBooking = bookingService.addBooking(user2.getId(), newBooking);

        assertEquals(savedBooking, bookingService.getAllBookingsByOwnerId(user1.getId(), BookingState.ALL).get(0), "Должно возвращаться сохранённое бронирование для указанного пользователя");
        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByOwnerId(user2.getId() + 1, BookingState.ALL), "Должно выбрасываться NotFoundException, если пользователь с таким ID не найден");
    }
}
