package com.ordering.jan.service;


import com.ordering.jan.entity.Labor;
import com.ordering.jan.entity.User;
import com.ordering.jan.repository.LaborRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LaborService {

    @Autowired
    private LaborRepository laborRepository;

    @Autowired
    private UserService userService;

    public List<Labor> getAllLabor() {
        return laborRepository.findAll();
    }

    public void saveLabor(Labor labor, Long userId) {
        // If a userId was provided in the form, link the account
        if (userId != null) {
            User linkedUser = userService.findById(userId);
            labor.setSystemAccount(linkedUser);
        }
        laborRepository.save(labor);
    }

    public Labor findById(Long id) {
        return laborRepository.findById(id).orElse(null);
    }
}