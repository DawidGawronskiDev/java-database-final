package com.project.code.Service;

import com.project.code.Model.Customer;
import com.project.code.Model.OrderDetails;
import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.OrderDetailsRepository;
import com.project.code.Repo.OrderItemRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Repo.StoreRepository;
import com.project.code.Model.Inventory;
import com.project.code.Model.OrderItem;
import com.project.code.Model.PurchaseProductDTO;
import com.project.code.Model.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Transactional
    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {
        Customer customer = customerRepository.findByEmail(placeOrderRequest.getCustomerEmail());

        if (customer == null) {
            customer = new Customer();
            customer.setName(placeOrderRequest.getCustomerName());
            customer.setEmail(placeOrderRequest.getCustomerEmail());
            customer.setPhone(placeOrderRequest.getCustomerPhone());
            customer = customerRepository.save(customer);
        }

        Store store = storeRepository.findById(placeOrderRequest.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        OrderDetails orderDetails = new OrderDetails(
                customer, store, placeOrderRequest.getTotalPrice(), LocalDateTime.now());
        orderDetails = orderDetailsRepository.save(orderDetails);

        for (PurchaseProductDTO item : placeOrderRequest.getPurchaseProduct()) {
            Inventory inventory = inventoryRepository.findByProductIdandStoreId(item.getId(), store.getId());
            if (inventory == null) {
                throw new RuntimeException("Product " + item.getId() + " not in store");
            }
            if (inventory.getStockLevel() < item.getQuantity()) {
                throw new RuntimeException("Not enough stock for product " + item.getId());
            }

            inventory.setStockLevel(inventory.getStockLevel() - item.getQuantity());
            inventoryRepository.save(inventory);

            OrderItem orderItem = new OrderItem(
                    orderDetails, inventory.getProduct(), item.getQuantity(), item.getPrice());
            orderItemRepository.save(orderItem);
        }
}   
}
