package ru.practicum.ewm.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewCommentDto(@NotBlank @Size(min = 10, max = 2000) String text) {
}
