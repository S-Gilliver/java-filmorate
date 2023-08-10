package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {

    private Integer id;

    @Email(message = "Некорректно указан email")
    private String email;

    private String login;

    private String name;

    @Past(message = "Некорректно указана дата рождения")
    private LocalDate birthday;
}

