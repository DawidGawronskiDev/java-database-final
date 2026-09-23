package com.project.code.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "product", uniqueConstraints = @UniqueConstraint(columnNames = "sku"))
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String name;

    @NotNull
    private String category;

    @NotNull
    private Double price;

    @NotNull
    private String sku;

    @OneToMany(mappedBy = "product")
    @JsonManagedReference("inventory-product")
    private List<Inventory> inventory;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public List<Inventory> getInventory() {
        return inventory;
    }

    public void setInventory(List<Inventory> inventory) {
        this.inventory = inventory;
    }
}