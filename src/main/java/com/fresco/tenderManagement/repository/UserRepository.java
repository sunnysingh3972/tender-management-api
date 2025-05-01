package com.fresco.tenderManagement.repository;



import com.fresco.tenderManagement.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface UserRepository extends JpaRepository<UserModel,Integer> {

    UserModel findByEmail(String email);

    //Add the required annotations to make the UserRepository
}

