package com.selimhorri.app.service.Impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.exception.wrapper.CategoryNotFoundException;
import com.selimhorri.app.repository.CategoryRepository;
import com.selimhorri.app.service.impl.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void testFindById_Success() {
        Category category = Category.builder().categoryId(1).categoryTitle("Test Category").build();
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        CategoryDto result = categoryService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getCategoryId());
        assertEquals("Test Category", result.getCategoryTitle());
    }

    @Test
    void testFindById_NotFound() {
        when(categoryRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(999));
    }

    @Test
    void testFindAll_ReturnsList() {
        List<Category> categories = List.of(
            Category.builder().categoryId(1).categoryTitle("Category 1").build(),
            Category.builder().categoryId(2).categoryTitle("Category 2").build()
        );
        when(categoryRepository.findAll()).thenReturn(categories);

        List<CategoryDto> dtos = categoryService.findAll();

        assertEquals(2, dtos.size());
    }

    @Test
    void testSave_Category() {
        Category category = Category.builder().categoryId(1).categoryTitle("New Category").build();
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto dtoToSave = CategoryDto.builder().categoryTitle("New Category").build();
        CategoryDto savedDto = categoryService.save(dtoToSave);

        assertNotNull(savedDto);
        assertEquals("New Category", savedDto.getCategoryTitle());
    }

    @Test
    void testDeleteById() {
        Integer categoryId = 1;
        doNothing().when(categoryRepository).deleteById(categoryId);

        assertDoesNotThrow(() -> categoryService.deleteById(categoryId));
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }
}
