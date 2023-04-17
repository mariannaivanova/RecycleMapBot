package com.springbot.reyclemapbot.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.springbot.reyclemapbot.DTO.FractionDTO;
import com.springbot.reyclemapbot.DTO.UserDTO;
import com.springbot.reyclemapbot.model.Fraction;
import com.springbot.reyclemapbot.model.User;
import com.springbot.reyclemapbot.serviceImplementation.FractionServiceImpl;
import com.springbot.reyclemapbot.serviceImplementation.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URL;

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
