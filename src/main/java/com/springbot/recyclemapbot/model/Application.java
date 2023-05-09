package com.springbot.recyclemapbot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.geo.Point;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="chat_id")
    private Long chatId;

    private Point geom;

    private String title;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "applications_fractions",
            joinColumns = @JoinColumn(name = "application_id"),
            inverseJoinColumns = @JoinColumn(name = "fraction_id"))
    private Set<Fraction> fractions = new HashSet<>();

    public Set<Fraction> getFractions() {
        return fractions;
    }

    public void setFractions(Set<Fraction> fractions) {
        this.fractions = fractions;
    }
}
