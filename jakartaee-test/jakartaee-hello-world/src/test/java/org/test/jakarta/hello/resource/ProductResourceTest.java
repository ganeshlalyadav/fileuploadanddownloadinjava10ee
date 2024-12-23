package org.test.jakarta.hello.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.test.jakarta.hello.model.Product;
import org.test.jakarta.hello.repository.ProductRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductResourceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductResource productResource;

    @Test
    public void testCreateProduct() {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);

        productResource.create(product);

        verify(productRepository).create(product);
    }

    @Test
    public void testFindAllProducts() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(100.0);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(200.0);

        List<Product> productList = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(productList);

        List<Product> foundProducts = productResource.findAll();

        assertEquals(2, foundProducts.size());
    }
    @Test
    public void testFindProductById() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setPrice(100.0);
        when(productRepository.find(1L)).thenReturn(product);
        Product foundProduct = productResource.find(1L);
        assertEquals(1L, foundProduct.getId());
    }

    @Test
    public void testUpdateProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setPrice(100.0);
        when(productRepository.update(product)).thenReturn(product);
        Product updatedProduct = productResource.update(1L, product);
        assertEquals(1L, updatedProduct.getId());
    }
    @Test
    public void testDeleteProduct() {
        productResource.delete(1L);
        verify(productRepository).delete(1L);
    }
    @Test
    public void testGetFilteredProducts() {
        List<Product> productList = Arrays.asList(new Product(), new Product());
        when(productRepository.filterByRSQL(any())).thenReturn(productList);
        List<Product> filteredProducts = productResource.getFilteredProducts("name==Test");
        assertEquals(2, filteredProducts.size());
    }


}
