package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.TodoProjection;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TodoQueryRepository {
    Todo findByIdByDsl(long todoId);
    Page<TodoProjection> searchTodoTitleList(Pageable pageable, String title, String nickname, LocalDate startedAt, LocalDate endedAt);
}
