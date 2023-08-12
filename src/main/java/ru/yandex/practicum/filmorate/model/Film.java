package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
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

