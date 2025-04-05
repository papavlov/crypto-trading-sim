package com.cryptotradingsim.backend.repositories;

import com.cryptotradingsim.backend.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //RowMapper to map DB result to User object
    private final RowMapper<User> userRowMapper = (rs, rowNum) -> new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getDouble("balance")
    );

    //Get all users
    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users", userRowMapper);
    }

    //get user by ID
    public User getUserById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?", userRowMapper, id);
    }

    //add new user
    public void createUser(String username, double balance) {
        jdbcTemplate.update("INSERT INTO users (username, balance) VALUES (?, ?)", username, balance);
    }

    //update balance
    public void updateBalance(int userId, double newBalance) {
        jdbcTemplate.update("UPDATE users SET balance = ? WHERE id = ?", newBalance, userId);
    }

    public void resetBalance(int userId, double initialBalance) {
        jdbcTemplate.update("UPDATE users SET balance = ? WHERE id = ?", initialBalance, userId);
    }
}
