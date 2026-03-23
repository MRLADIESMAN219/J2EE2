package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Page<Product> searchProducts(String keyword, Integer categoryId, int page, int size, String sort) {
        Sort sortOption = buildSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortOption);

        String normalizedKeyword = keyword == null ? "" : keyword.trim();

        if (categoryId != null && normalizedKeyword.isBlank()) {
            return productRepository.findByCategory_Id(categoryId, pageable);
        }

        if (categoryId != null) {
            return productRepository.findByNameContainingIgnoreCaseAndCategory_Id(normalizedKeyword, categoryId, pageable);
        }

        return productRepository.findByNameContainingIgnoreCase(normalizedKeyword, pageable);
    }

    private Sort buildSort(String sort) {
        if ("priceAsc".equalsIgnoreCase(sort)) {
            return Sort.by("price").ascending();
        }
        if ("priceDesc".equalsIgnoreCase(sort)) {
            return Sort.by("price").descending();
        }
        return Sort.by("id").ascending();
    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
