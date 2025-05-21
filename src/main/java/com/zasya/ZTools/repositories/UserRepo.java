package com.zasya.ZTools.repositories;

import com.zasya.ZTools.DTO.UserLogin;
import com.zasya.ZTools.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    User findByEmail(String userName);
//    String findByPassword(String password);

}
