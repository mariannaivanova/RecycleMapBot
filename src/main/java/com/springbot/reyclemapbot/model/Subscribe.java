package com.springbot.reyclemapbot.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.geo.Point;

@Entity
@Data
@Table(name = "subscribes")
public class Subscribe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name="chatId")
    private Long chatId;

    private Point geom;
}
