package ru.yandex.practicum.filmorate.comparator;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class UserComparator implements Comparator<Film> {
    @Override
    public int compare(Film o1, Film o2) {
        return Long.compare(o1.getLikes().size(), o2.getLikes().size());
    }
}
