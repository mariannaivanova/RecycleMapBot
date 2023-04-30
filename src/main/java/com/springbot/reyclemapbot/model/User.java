package com.springbot.reyclemapbot.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    private Long chatId;

    private String firstName;

    private String lastName;

    private String username;


}
