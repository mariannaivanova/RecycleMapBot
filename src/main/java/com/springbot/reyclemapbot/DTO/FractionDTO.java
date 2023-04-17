package com.springbot.reyclemapbot.DTO;

import com.springbot.reyclemapbot.model.Fraction;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FractionDTO {
    private Integer id;
    private String name;
    private String color;

    public FractionDTO(){};

    public FractionDTO(Integer id, String name, String color){
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Fraction FractionDTOtoFraction(){
        Fraction fraction = new Fraction();
        fraction.setId(this.id);
        fraction.setName(this.name);
        fraction.setColor(this.color);
        return fraction;
    }
}
