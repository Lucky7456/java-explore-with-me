package ru.practicum.ewm.category;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAll(int from, int size);

    CategoryDto findById(long id);

    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto update(long id, CategoryDto categoryDto);

    void delete(long id);
}
