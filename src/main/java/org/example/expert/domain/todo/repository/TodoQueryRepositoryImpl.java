package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.QTodoProjection;
import org.example.expert.domain.todo.dto.TodoProjection;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Todo findByIdByDsl(long todoId) {
        return queryFactory
                .select(todo)
                .from(todo)
                .join(todo.user, user).fetchJoin()
                .where(
                        todoIdEq(todoId)
                ).fetchOne();
    }

    @Override
    public Page<TodoProjection> searchTodoTitleList(Pageable pageable, String title, String nickname, LocalDate startedAt, LocalDate endedAt) {
        List<TodoProjection> result = queryFactory
                .select(
                        new QTodoProjection(
                                todo.title,
                                manager.countDistinct(),
                                comment.countDistinct()
                        )
                )
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .leftJoin(todo.comments, comment)
                .where(
                        titleContains(title),
                        managerNicknameContains(nickname),
                        createdDateBetween(startedAt, endedAt)
                )
                .groupBy(todo.id)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(Wildcard.count) // select count(*)
                .from(todo)
                .where(
                        titleContains(title),
                        managerNicknameContains(nickname),
                        createdDateBetween(startedAt, endedAt)
                )
                .fetchOne();

        return new PageImpl<>(result, pageable, count);
    }

    private BooleanExpression todoIdEq(Long todoId) {
        return todoId != null ? todo.id.eq(todoId) : null;
    }

    private BooleanExpression titleContains(String title) {
        return StringUtils.hasText(title) ? todo.title.contains(title) : null;
    }

    private BooleanExpression managerNicknameContains(String nickname) {
        return StringUtils.hasText(nickname) ? user.nickname.contains(nickname) : null;
    }

    private BooleanExpression createdDateBetween(LocalDate startedAt, LocalDate endedAt) {
        if (startedAt != null && endedAt != null) {
            return todo.createdAt.between(startedAt.atStartOfDay(), endedAt.atTime(23, 59, 59));
        }
        return null;
    }
}