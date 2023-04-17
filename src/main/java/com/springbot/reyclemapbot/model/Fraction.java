package com.springbot.reyclemapbot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "fractions")
public class Fraction {
    @Id
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "color")
    private String color;
}
