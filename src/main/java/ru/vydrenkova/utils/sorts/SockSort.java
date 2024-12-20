package ru.vydrenkova.utils.sorts;

import org.springframework.data.domain.Sort;
import ru.vydrenkova.exceptions.InvalidSortException;

public class SockSort {

    public static Sort byField(String sortedBy) {
        if ("color".equalsIgnoreCase(sortedBy)) {
            return Sort.by("color");
        } else if ("cotton".equalsIgnoreCase(sortedBy)) {
            return Sort.by("cottonPercentage");
        } else {
            throw new InvalidSortException("Нельзя сортировать по: " + sortedBy);
        }
    }
}
