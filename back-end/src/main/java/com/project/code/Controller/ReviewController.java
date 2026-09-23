package com.project.code.Controller;

import com.project.code.Model.Customer;
import com.project.code.Model.Review;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/{storeId}/{productId}")
    public Map<String, Object> getReviews(@PathVariable Long storeId, @PathVariable Long productId) {
        List<Map<String, Object>> reviews = new ArrayList<>();

        for (Review review : reviewRepository.findByStoreIdAndProductId(storeId, productId)) {
            String customerName = customerRepository.findById(review.getCustomerId())
                    .map(Customer::getName)
                    .orElse("Unknown");

            Map<String, Object> filtered = new HashMap<>();
            filtered.put("comment", review.getComment());
            filtered.put("rating", review.getRating());
            filtered.put("customerName", customerName);
            reviews.add(filtered);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("reviews", reviews);
        return result;
    }
}