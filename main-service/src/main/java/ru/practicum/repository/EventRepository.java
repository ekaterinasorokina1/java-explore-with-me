package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends CrudRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long id, Long userId);

    Optional<Event> findByIdAndState(Long id, EventState state);

    Optional<Event> getByCategoryId(Long catId);

    @Query("SELECT e FROM Event AS e " +
            "WHERE (?1 IS NULL OR e.initiator.id IN ?1) " +
            "AND (?2 IS NULL OR e.state IN ?2) " +
            "AND (?3 IS NULL OR e.category.id IN ?3) " +
            "AND (e.eventDate >= ?4)" +
            "AND (e.eventDate <= ?5)"
    )
    List<Event> findAllEvents(List<Long> users,
                              List<EventState> states,
                              List<Long> categories,
                              LocalDateTime rangeStart,
                              LocalDateTime rangeEnd,
                              Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (?1 IS NULL OR LOWER(e.description) LIKE concat('%',CAST(?1 AS text),'%') OR LOWER(e.annotation) LIKE concat('%',cast(?1 AS text),'%')) " +
            "AND (?2 IS NULL OR e.category.id IN ?2) " +
            "AND (?3 IS NULL OR e.paid = ?3) " +
            "AND (e.eventDate >= ?4)" +
            "AND (e.eventDate <= ?5)" +
            "AND (?6 IS NULL OR ?6 = false " +
            "OR e.participantLimit IS NULL OR e.participantLimit = 0 " +
            "OR e.confirmedRequests < e.participantLimit)")
    List<Event> findPublishedEvents(String text,
                                    List<Long> categories,
                                    Boolean paid,
                                    LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd,
                                    Boolean onlyAvailable,
                                    Pageable pageable);
}
