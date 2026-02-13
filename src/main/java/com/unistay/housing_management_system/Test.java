package com.unistay.housing_management_system;

import com.unistay.housing_management_system.entity.User;
import org.springframework.beans.factory.annotation.Autowired;

public class Test {

    @Autowired
    User user = new User();

    public void print()
    {
        user.setEmail("kkk");
        System.out.println(user.getEmail());
    }
}
