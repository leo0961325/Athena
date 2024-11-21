package com.athena.controller;

import com.athena.service.User.UserService;
import com.athena.vo.AddUserReq;
import com.athena.vo.AddUserResp;
import com.athena.vo.GetUserResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    private final UserService userService;

    @Autowired
    public TestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String test() {
        return "test success !!!!!";
    }

    @PostMapping("/insert")
    public AddUserResp insert(@RequestBody AddUserReq request) {

        return userService.addUser(request);
    }

    @GetMapping("/get/{id}")
    public GetUserResp get(@PathVariable("id") Integer id) {

        return userService.getUser(id);
    }
}
