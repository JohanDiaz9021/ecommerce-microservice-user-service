package com.selimhorri.app.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserFlowE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseCategoryUrl() {
        return "http://localhost:" + port + "/api/categories";
    }

    private String baseProductUrl() {
        return "http://localhost:" + port + "/api/products";
    }

    @Test
    public void createAndGetCategory() {
        CategoryDto category = new CategoryDto();
        category.setCategoryTitle("Flow Category");

        CategoryDto created = restTemplate.postForObject(baseCategoryUrl(), category, CategoryDto.class);
        assertThat(created).isNotNull();
        assertThat(created.getCategoryId()).isNotNull();

        CategoryDto fetched = restTemplate.getForObject(baseCategoryUrl() + "/" + created.getCategoryId(), CategoryDto.class);
        assertThat(fetched).isNotNull();
        assertThat(fetched.getCategoryTitle()).isEqualTo("Flow Category");
    }

    @Test
    public void createAndUpdateCategory() {
        CategoryDto category = new CategoryDto();
        category.setCategoryTitle("To Update");

        CategoryDto created = restTemplate.postForObject(baseCategoryUrl(), category, CategoryDto.class);
        assertThat(created).isNotNull();

        created.setCategoryTitle("Updated Category");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CategoryDto> entity = new HttpEntity<>(created, headers);

        ResponseEntity<CategoryDto> response = restTemplate.exchange(baseCategoryUrl() + "/" + created.getCategoryId(),
                HttpMethod.PUT, entity, CategoryDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCategoryTitle()).isEqualTo("Updated Category");
    }

    @Test
    public void createAndDeleteCategory() {
        CategoryDto category = new CategoryDto();
        category.setCategoryTitle("To Delete");

        CategoryDto created = restTemplate.postForObject(baseCategoryUrl(), category, CategoryDto.class);
        assertThat(created).isNotNull();

        restTemplate.delete(baseCategoryUrl() + "/" + created.getCategoryId());

        ResponseEntity<String> response = restTemplate.getForEntity(baseCategoryUrl() + "/" + created.getCategoryId(), String.class);
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    public void createAndGetProduct() {
        ProductDto product = new ProductDto();
        product.setProductTitle("Flow Product");

        ProductDto created = restTemplate.postForObject(baseProductUrl(), product, ProductDto.class);
        assertThat(created).isNotNull();
        assertThat(created.getProductId()).isNotNull();

        ProductDto fetched = restTemplate.getForObject(baseProductUrl() + "/" + created.getProductId(), ProductDto.class);
        assertThat(fetched).isNotNull();
        assertThat(fetched.getProductTitle()).isEqualTo("Flow Product");
    }

    @Test
    public void createAndUpdateProduct() {
        ProductDto product = new ProductDto();
        product.setProductTitle("To Update Product");

        ProductDto created = restTemplate.postForObject(baseProductUrl(), product, ProductDto.class);
        assertThat(created).isNotNull();

        created.setProductTitle("Updated Product");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProductDto> entity = new HttpEntity<>(created, headers);

        ResponseEntity<ProductDto> response = restTemplate.exchange(baseProductUrl() + "/" + created.getProductId(),
                HttpMethod.PUT, entity, ProductDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getProductTitle()).isEqualTo("Updated Product");
    }

}
