package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {
    public static Comment toComment(InputCommentDto commentDto, Item item, User author, LocalDateTime created) {
        return new Comment(commentDto.getText(), item, author, created);
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getItem().getId(), comment.getAuthor().getName(), comment.getCreated());
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> comments) {
        List<CommentDto> commentDtoList = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtoList.add(toCommentDto(comment));
        }
        return commentDtoList;
    }
}
