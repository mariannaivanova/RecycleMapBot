package com.springbot.reyclemapbot.DTO;

import com.springbot.reyclemapbot.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
     private Long chatId;

     private String firstName;

     private String lastName;

     private String userName;

     public User UserDTOtoUser(){
         User user = new User();
         user.setChatId(this.chatId);
         user.setFirstName(this.firstName);
         user.setLastName(this.lastName);
         user.setUserName(this.userName);
         return user;
     }
}
