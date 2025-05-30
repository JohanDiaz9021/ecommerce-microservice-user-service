package com.selimhorri.app.resource;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.service.CategoryService;
import com.selimhorri.app.service.ProductService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {CategoryResource.class, ProductResource.class})
public class ResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    // 1. GET /api/categories/{id}
    @Test
    void whenGetCategoryById_thenReturnCategory() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Category Test")
                .build();

        when(categoryService.findById(1)).thenReturn(categoryDto);

        mockMvc.perform(get("/api/categories/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryTitle").value("Category Test"));
    }

    // 2. POST /api/categories
    @Test
    void whenPostCategory_thenCreateCategory() throws Exception {
        CategoryDto inputDto = CategoryDto.builder()
                .categoryTitle("New Category")
                .build();

        CategoryDto savedDto = CategoryDto.builder()
                .categoryId(10)
                .categoryTitle("New Category")
                .build();

        when(categoryService.save(Mockito.any(CategoryDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(10))
                .andExpect(jsonPath("$.categoryTitle").value("New Category"));
    }

    // 3. GET /api/products/{id}
    @Test
    void whenGetProductById_thenReturnProduct() throws Exception {
        ProductDto productDto = ProductDto.builder()
                .productId(1)
                .productTitle("Product Test")
                .build();

        when(productService.findById(1)).thenReturn(productDto);

        mockMvc.perform(get("/api/products/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.productTitle").value("Product Test"));
    }

    // 4. POST /api/products
    @Test
    void whenPostProduct_thenCreateProduct() throws Exception {
        ProductDto inputDto = ProductDto.builder()
                .productTitle("New Product")
                .build();

        ProductDto savedDto = ProductDto.builder()
                .productId(20)
                .productTitle("New Product")
                .build();

        when(productService.save(Mockito.any(ProductDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(20))
                .andExpect(jsonPath("$.productTitle").value("New Product"));
    }

    // 5. DELETE /api/categories/{id}
    @Test
    void whenDeleteCategoryById_thenReturnTrue() throws Exception {
        Integer categoryId = 1;
        doNothing().when(categoryService).deleteById(categoryId);

        mockMvc.perform(delete("/api/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(categoryService, times(1)).deleteById(categoryId);
    }
}
