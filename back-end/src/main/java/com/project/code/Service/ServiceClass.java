package com.project.code.Service;

import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceClass {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    // false if inventory already exists
    public boolean validateInventory(Inventory inventory) {
        return getInventoryId(inventory) == null;
    }

    // false if a product with the same name exists
    public boolean validateProduct(Product product) {
        return productRepository.findByName(product.getName()) == null;
    }

    // false if no product with this id
    public boolean ValidateProductId(long id) {
        return productRepository.existsById(id);
    }

    public Inventory getInventoryId(Inventory inventory) {
        return inventoryRepository.findByProductIdandStoreId(
                inventory.getProduct().getId(),
                inventory.getStore().getId());
    }
}