package ru.practicum.service.category;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    public List<CategoryDto> getCategories(Pageable pageable);

    public CategoryDto getCategoryById(Long catId);

    public CategoryDto createCategory(NewCategoryDto newCategoryDto);

    public CategoryDto updateCategory(Long catId, NewCategoryDto newCategoryDto);

    public void deleteCategoryById(Long categoryId);
}
