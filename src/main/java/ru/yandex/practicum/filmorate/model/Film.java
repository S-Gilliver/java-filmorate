package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Builder;
import lombok.NonNull;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class Film {

    @Positive
    private Integer id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @NonNull
    private LocalDate releaseDate;

    @Positive
    private Integer duration;

}

