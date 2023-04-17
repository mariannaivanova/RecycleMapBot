package com.springbot.reyclemapbot.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.geo.Point;
/*
import org.locationtech.jts.geom.Point;*/

@Entity
@Data
@Table(name = "points")
public class Points {
    @Id
    private Integer id;

    @Column(name = "address")
    private String address;

    @Column(name = "title")
    private String title;

    @Column(name = "restricted")
    private Boolean restricted;

    @Column(name = "geom")
    private Point geom;
}


