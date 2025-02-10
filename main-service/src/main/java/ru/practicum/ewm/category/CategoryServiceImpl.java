package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> findAll(int from, int size) {
        return repository.findAll(PageRequest.of(from, size)).getContent().stream().map(CategoryMapper::mapToCategoryDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto findById(long id) {
        return CategoryMapper.mapToCategoryDto(repository.findById(id).orElseThrow());
    }

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        return CategoryMapper.mapToCategoryDto(repository.save(CategoryMapper.mapToCategory(newCategoryDto)));
    }

    @Override
    public CategoryDto update(long id, CategoryDto categoryDto) {
        Category category = repository.findById(id).orElseThrow();
        category.setName(categoryDto.getName());
        return CategoryMapper.mapToCategoryDto(repository.save(category));
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }
}
