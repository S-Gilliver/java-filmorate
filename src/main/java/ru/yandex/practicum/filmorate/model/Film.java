package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    private Integer id;

    @NotEmpty(message = "Некорректно введено название фильма")
    private String name;

    @Size(min = 0, max = 200, message = "Некорректно введено описание фильма")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Некорректно введена продолжительность фильма")
    private Integer duration;

}

