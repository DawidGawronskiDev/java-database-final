package com.project.code.Controller;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ServiceClass serviceClass;

    @PutMapping
    public String updateInventory(@RequestBody CombinedRequest combinedRequest) {
        Product product = combinedRequest.getProduct();
        Inventory inventory = combinedRequest.getInventory();

        if (!serviceClass.ValidateProductId(product.getId())) {
            return "Invalid product.";
        }

        inventory.setProduct(product);
        Inventory existing = serviceClass.getInventoryId(inventory);
        if (existing == null) {
            return "No data available";
        }

        existing.setStockLevel(inventory.getStockLevel());
        inventoryRepository.save(existing);
        return "Inventory updated.";
    }

    @PostMapping
    public String saveInventory(@RequestBody Inventory inventory) {
        if (!serviceClass.validateInventory(inventory)) {
            return "Inventory already exists.";
        }

        inventoryRepository.save(inventory);
        return "Inventory saved.";
    }

    @GetMapping("/{storeId}")
    public Map<String, Object> getAllProducts(@PathVariable long storeId) {
        List<Product> products = inventoryRepository.findByStore_Id(storeId)
                .stream()
                .map(Inventory::getProduct)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        return response;
    }


    @GetMapping("/filter/{category}/{name}")
    public Map<String, Object> getProductName(@PathVariable String category, @PathVariable String name) {
        // ponytail: filters in memory, use repository queries if the product table gets big
        List<Product> products = productRepository.findAll()
                .stream()
                .filter(p -> category.equals("null") || p.getCategory().equals(category))
                .filter(p -> name.equals("null") || p.getName().equals(name))
                .toList();
    
        Map<String, Object> response = new HashMap<>();
        response.put("product", products);
        return response;
    }

    @GetMapping("/search/{name}/{storeId}")
    public Map<String, Object> searchProduct(@PathVariable String name, @PathVariable long storeId) {
        Map<String, Object> response = new HashMap<>();
        response.put("product", productRepository.findByNameLike(storeId, name));
        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> removeProduct(@PathVariable long id) {
        Map<String, String> response = new HashMap<>();

        if (!serviceClass.ValidateProductId(id)) {
            response.put("message", "Product not present in database");
            return response;
        }

        inventoryRepository.deleteByProductId(id);
        productRepository.deleteById(id);

        response.put("message", "Product deleted successfully");
        return response;
    }

    @GetMapping("/validate/{quantity}/{storeId}/{productId}")
    public boolean validateQuantity(@PathVariable Integer quantity,
                                    @PathVariable long storeId,
                                    @PathVariable long productId) {
        Inventory inventory = inventoryRepository.findByProductIdandStoreId(productId, storeId);
        return inventory != null && inventory.getStockLevel() >= quantity;
    }
}
