package com.fahad.tech.venture.FahadTechVentureDemoLogin.DBhelper;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    User login(String username, String password);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    int countUsersWithUsername(String username);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    /*@Query("SELECT * FROM users")
    List<User> getAllUsers();*/

   /* @Query("SELECT * FROM users ORDER BY username ASC")
    List<User> getAllUsers();*/

    @Query("SELECT * FROM users ORDER BY signupTimestamp DESC")
    List<User> getAllUsers();

}
