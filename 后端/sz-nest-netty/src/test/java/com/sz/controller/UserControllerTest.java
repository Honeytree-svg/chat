package com.sz.controller;


import com.sz.mapper.UsersMapperCustom;
import com.sz.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;




public class UserControllerTest {

    @Autowired
    private UsersMapperCustom usersMapperCustom;

    public void getUsers(){
        Users users=new Users();
        users.setUsername("sunzhe");
        users.setPassword("123");
        System.out.println(usersMapperCustom.selectOnlyOne(users));
    }
}
