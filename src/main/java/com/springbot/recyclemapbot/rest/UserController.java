package com.springbot.recyclemapbot.rest;

import com.springbot.recyclemapbot.DTO.UserDTO;
import com.springbot.recyclemapbot.model.User;
import com.springbot.recyclemapbot.serviceImplementation.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserServiceImpl userService;

    @RequestMapping(value = "/user/{id}", method = RequestMethod.POST)
    public void saveUser(@PathVariable("id") Long chatId, @RequestBody UserDTO userDTO) throws IOException {
        User user = userDTO.UserDTOtoUser();
        this.userService.save(user);
    }

}
