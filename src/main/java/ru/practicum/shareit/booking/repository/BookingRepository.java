package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(long userId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndBefore(long userId, LocalDateTime currentDateTime);

    List<Booking> findAllByBookerIdAndStartAfter(long userId, LocalDateTime currentDateTime);

    List<Booking> findAllByBookerIdAndStatus(long userId, BookingStatus status);

    List<Booking> findAllByItemOwnerId(long ownerId);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndEndBefore(long ownerId, LocalDateTime endTime);

    List<Booking> findAllByItemOwnerIdAndStartAfter(long ownerId, LocalDateTime currentDateTime);

    List<Booking> findAllByItemOwnerIdAndStatus(long ownerId, BookingStatus status);

    List<Booking> findByItemIdAndBookerIdAndEndBeforeAndStatus(Long itemId, Long bookerId, LocalDateTime end, BookingStatus status);

    List<Booking> findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime currentTime, BookingStatus status);

    List<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime currentTime, BookingStatus status);
}
