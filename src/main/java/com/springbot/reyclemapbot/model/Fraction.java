package com.springbot.reyclemapbot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

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

    @ManyToMany(mappedBy = "fractions")
    Set<Points> points;
}
