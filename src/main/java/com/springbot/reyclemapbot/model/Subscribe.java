package com.springbot.reyclemapbot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.geo.Point;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "subscribes")
public class Subscribe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="chat_id")
    private Long chatId;

    private Point geom;

    private Double dist;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "subscribes_points",
            joinColumns = @JoinColumn(name = "subscribe_id"),
            inverseJoinColumns = @JoinColumn(name = "point_id"))
    private Set<Points> points = new HashSet<>();

    public Set<Points> getPoints() {
        return points;
    }

    public void setPoints(Set<Points> points) {
        this.points = points;
    }
}
