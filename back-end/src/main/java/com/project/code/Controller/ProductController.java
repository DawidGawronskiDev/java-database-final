package com.project.code.Controller;

import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ServiceClass serviceClass;

    @PostMapping
    public String addProduct(@RequestBody Product product) {
        if (!serviceClass.validateProduct(product)) {
            return "Product already exists";
        }

        try {
            productRepository.save(product);
            return "Product saved";
        } catch (DataIntegrityViolationException e) {
            return "Product could not be saved: SKU must be unique and required fields must not be null";
        }
    }

    @GetMapping("/product/{id}")
    public Map<String, Object> getProductbyId(@PathVariable long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("products", productRepository.findById(id).orElse(null));
        return result;
    }

    @PutMapping
    public Map<String, String> updateProduct(@RequestBody Product product) {
        productRepository.save(product);

        Map<String, String> result = new HashMap<>();
        result.put("message", "Product updated successfully");
        return result;
    }

    @GetMapping("/category/{name}/{category}")
    public Map<String, Object> filterbyCategoryProduct(@PathVariable String name, @PathVariable String category) {
        List<Product> products;

        if (name.equals("null") && category.equals("null")) {
            products = productRepository.findAll();
        } else if (name.equals("null")) {
            products = productRepository.findByCategory(category);
        } else if (category.equals("null")) {
            products = productRepository.findProductBySubName(name);
        } else {
            products = productRepository.findProductBySubNameAndCategory(name, category);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("products", products);
        return result;
    }

    @GetMapping
    public Map<String, Object> listProduct() {
        Map<String, Object> result = new HashMap<>();
        result.put("products", productRepository.findAll());
        return result;
    }

    @GetMapping("filter/{category}/{storeid}")
    public Map<String, Object> getProductbyCategoryAndStoreId(@PathVariable String category, @PathVariable long storeid) {
        Map<String, Object> result = new HashMap<>();
        result.put("product", productRepository.findProductByCategory(category, storeid));
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteProduct(@PathVariable long id) {
        Map<String, String> result = new HashMap<>();

        if (!serviceClass.ValidateProductId(id)) {
            result.put("message", "Product not present in database");
            return result;
        }

        inventoryRepository.deleteByProductId(id);
        productRepository.deleteById(id);

        result.put("message", "Product deleted successfully");
        return result;
    }

    @GetMapping("/searchProduct/{name}")
    public Map<String, Object> searchProduct(@PathVariable String name) {
        Map<String, Object> result = new HashMap<>();
        result.put("products", productRepository.findProductBySubName(name));
        return result;
    }
}