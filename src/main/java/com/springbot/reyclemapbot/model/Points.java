package com.springbot.reyclemapbot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.geo.Point;

import java.util.HashSet;
import java.util.Set;


@Entity
@Data
@Getter
@Setter
@Table(name = "points")
public class Points {
    @Id
    private Long id;

    private String url;

    @Column(name = "address")
    private String address;

    @Column(name = "title")
    private String title;

    @Column(name = "geom")
    private Point geom;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "points_fractions",
            joinColumns = @JoinColumn(name = "point_id"),
            inverseJoinColumns = @JoinColumn(name = "fraction_id"))
    private Set<Fraction> fractions = new HashSet<>();

    public Set<Fraction> getFractions() {
        return fractions;
    }

    public void setFractions(Set<Fraction> fractions) {
        this.fractions = fractions;
    }


}


