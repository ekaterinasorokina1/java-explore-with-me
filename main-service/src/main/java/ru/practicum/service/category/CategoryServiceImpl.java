package ru.practicum.service.category;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepo;
    private final EventRepository eventRepository;

    @Override
    public List<CategoryDto> getCategories(Pageable pageable) {
        return StreamSupport.stream(categoryRepo.findAll(pageable).spliterator(), false)
                .map(CategoryMapper::toDto).toList();
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        return CategoryMapper.toDto(categoryRepo
                .findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + catId + " не найдена")));
    }

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        checkIdCategoryNameExist(newCategoryDto.getName());
        Category category = CategoryMapper.toCategory(newCategoryDto);

        return CategoryMapper.toDto(categoryRepo.save(category));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, NewCategoryDto newCategoryDto) {
        Category category = categoryRepo.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + catId + " не найдена"));
        if (!category.getName().equals(newCategoryDto.getName())) {
            checkIdCategoryNameExist(newCategoryDto.getName());
            category.setName(newCategoryDto.getName());
        }

        return CategoryMapper.toDto(categoryRepo.save(category));
    }

    @Transactional
    @Override
    public void deleteCategoryById(Long catId) {
        categoryRepo.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + catId + " не найдена"));
        if (eventRepository.getByCategoryId(catId).isPresent()) {
            throw new ConflictException("Существуют события, связанные с категорией" + catId);
        }
        categoryRepo.deleteById(catId);
    }

    private void checkIdCategoryNameExist(String name) {
        if (categoryRepo.findCategoryByName(name).isPresent()) {
            throw new ConflictException(name + " уже существует");
        }
    }

}
