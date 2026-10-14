package vn.campushub.event;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import vn.campushub.event.dto.EventDetailResponse;
import vn.campushub.event.dto.EventSummaryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventSummaryResponse> searchUpcoming(String query, String category) {
        String normalizedQuery = normalize(query);
        String normalizedCategory = normalize(category);
        return eventRepository.searchUpcoming(OffsetDateTime.now(), normalizedQuery, normalizedCategory)
                .stream()
                .map(this::toSummary)
                .toList();
    }

    public EventDetailResponse getUpcoming(UUID id) {
        Event event = eventRepository.findUpcomingById(id, OffsetDateTime.now())
                .orElseThrow(() -> new EventNotFoundException(id));
        EventSummaryResponse response = toSummary(event);
        return new EventDetailResponse(
                response.id(), response.title(), response.category(), response.description(),
                response.location(), response.startsAt(), response.endsAt(), response.capacity(),
                response.registeredCount(), response.remainingSeats(), response.registrationOpen());
    }

    private EventSummaryResponse toSummary(Event event) {
        return new EventSummaryResponse(
                event.getId(), event.getTitle(), event.getCategory(), event.getDescription(),
                event.getLocation(), event.getStartsAt(), event.getEndsAt(), event.getCapacity(),
                0, event.getCapacity(), event.isRegistrationOpen());
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
