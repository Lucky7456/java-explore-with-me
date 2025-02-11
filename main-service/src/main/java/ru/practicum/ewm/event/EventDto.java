package ru.practicum.ewm.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.user.UserShortDto;

import java.time.LocalDateTime;

public enum EventDto {;
    public interface Id { Long getId(); }
    public interface Annotation { @Size(min = 20, max = 2000) String getAnnotation(); }
    public interface Category { @Positive Long getCategory(); }
    public interface CategoryView { @Positive CategoryDto getCategory(); }
    public interface ConfirmedRequests { Long getConfirmedRequests(); }
    public interface CreatedOn { LocalDateTime getCreatedOn(); }
    public interface Description { @Size(min = 20, max = 7000) String getDescription(); }
    public interface EventDate { @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String getEventDate(); }
    public interface Initiator { UserShortDto getInitiator(); }
    public interface Location { LocationDto getLocation(); }
    public interface Paid { Boolean getPaid(); }
    public interface ParticipantLimit { @PositiveOrZero Long getParticipantLimit(); }
    public interface PublishedOn { LocalDateTime getPublishedOn(); }
    public interface RequestModeration { Boolean getRequestModeration(); }
    public interface State { EventState getState(); }
    public interface Title { @Size(min = 3, max = 120) String getTitle(); }
    public interface Views { Long getViews(); }
    public interface UserStateAction { UserActionState getStateAction(); }
    public interface AdminStateAction { AdminActionState getStateAction(); }

    public enum Request {;
        @Value public static class Create implements Annotation, Category, Description, EventDate, Location, Paid, ParticipantLimit, RequestModeration, Title {
            @NotBlank
            String annotation;
            Long category;
            @NotBlank
            String description;
            String eventDate;
            LocationDto location;
            Boolean paid;
            Long participantLimit;
            Boolean requestModeration;
            @NotBlank
            String title;
        }

        @Value public static class AdminUpdate implements Annotation, Category, Description, EventDate, Location, Paid, ParticipantLimit, RequestModeration, Title, AdminStateAction {
            String annotation;
            Long category;
            String description;
            String eventDate;
            LocationDto location;
            Boolean paid;
            Long participantLimit;
            Boolean requestModeration;
            String title;
            AdminActionState stateAction;
        }

        @Value public static class UserUpdate implements Annotation, Category, Description, EventDate, Location, Paid, ParticipantLimit, RequestModeration, Title, UserStateAction {
            String annotation;
            Long category;
            String description;
            String eventDate;
            LocationDto location;
            Boolean paid;
            Long participantLimit;
            Boolean requestModeration;
            String title;
            UserActionState stateAction;
        }
    }

    public enum Response  {;
        @Value public static class Public implements Id, Annotation, CategoryView, ConfirmedRequests, EventDate, Initiator, Paid, Title, Views {
            Long id;
            String annotation;
            CategoryDto category;
            Long confirmedRequests;
            String eventDate;
            UserShortDto initiator;
            Boolean paid;
            String title;
            Long views;
        }

        @Value public static class Private implements Id, Annotation, CategoryView, ConfirmedRequests, CreatedOn, Description, EventDate, Initiator, Location, Paid, ParticipantLimit, PublishedOn, RequestModeration, State, Title, Views {
            Long id;
            String annotation;
            CategoryDto category;
            Long confirmedRequests;
            LocalDateTime createdOn;
            String description;
            String eventDate;
            UserShortDto initiator;
            LocationDto location;
            Boolean paid;
            Long participantLimit;
            LocalDateTime publishedOn;
            Boolean requestModeration;
            EventState state;
            String title;
            Long views;
        }
    }
}
