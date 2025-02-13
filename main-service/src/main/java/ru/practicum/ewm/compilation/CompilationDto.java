package ru.practicum.ewm.compilation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.ewm.event.EventDto;

import java.util.List;

@Data
@AllArgsConstructor
public class CompilationDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private List<EventDto.Response.Public> events;

    private boolean pinned;

    @NotBlank
    private String title;
}
