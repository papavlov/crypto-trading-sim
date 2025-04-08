package com.cryptotradingsim.backend.services;

import com.cryptotradingsim.backend.models.User;
import org.springframework.stereotype.Service;

import com.cryptotradingsim.backend.repositories.UserRepository;


import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private static final double INITIAL_BALANCE = 10000.0;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public User getUserById(int userId) {
        return userRepository.getUserById(userId);
    }

    public void createUser(String username) {
        userRepository.createUser(username, INITIAL_BALANCE);
    }

    public void resetBalance(int userId) {
        userRepository.updateBalance(userId, INITIAL_BALANCE);
    }

    public boolean updateBalanceForBuy(int userId, double cost) {
        User user = userRepository.getUserById(userId);
        if (user.getBalance() >= cost) {
            double newBalance = user.getBalance() - cost;
            userRepository.updateBalance(userId, newBalance);
            return true;
        } else {
            return false; //Not enough funds
        }
    }

    public void updateBalanceForSell(int userId, double gain) {
        User user = userRepository.getUserById(userId);
        double newBalance = user.getBalance() + gain;
        userRepository.updateBalance(userId, newBalance);
    }


}

