package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    @Positive
    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 200)
    private String description;

    @NonNull
    private LocalDate releaseDate;

    @Positive
    private Integer duration;

    @Positive
    private Integer rate;

    @NotNull
    private Mpa mpa;

    private List<Genre> genres = new ArrayList<>();

    @JsonIgnore
    private Set<Integer> likeIds = new HashSet<>();
}

