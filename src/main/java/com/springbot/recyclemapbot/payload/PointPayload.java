package com.springbot.recyclemapbot.payload;

import com.springbot.recyclemapbot.DTO.PointDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
public class PointPayload {

    private String address;

    private String title;

    private String url;

    private Set<String> fractions;

    public PointPayload(PointDTO pointDTO, Set<String> fractions){
        this.address = pointDTO.getAddress();
        this.title = pointDTO.getTitle();
        this.url = pointDTO.getUrl();
        this.fractions = fractions;
    }

    public String toNornalNames(){
        Set<String> fractionsNormal = new HashSet<>();
        for (String fraction: fractions){
            switch(fraction){
                case ("BUMAGA"):
                    fractionsNormal.add("бумага");
                    break;
                case ("PLASTIK"):
                    fractionsNormal.add("пластик");
                    break;
                case ("STEKLO"):
                    fractionsNormal.add("стекло");
                    break;
                case ("METALL"):
                    fractionsNormal.add("металл");
                    break;
                case ("TETRA_PAK"):
                    fractionsNormal.add("тетра пак");
                    break;
                case ("ODEZHDA"):
                    fractionsNormal.add("одежда");
                    break;
                case ("LAMPOCHKI"):
                    fractionsNormal.add("лампочки");
                    break;
                case ("KRYSHECHKI"):
                    fractionsNormal.add("крышечки");
                    break;
                case ("BYTOVAJA_TEHNIKA"):
                    fractionsNormal.add("бытовая техника");
                    break;
                case ("BATAREJKI"):
                    fractionsNormal.add("батарейки");
                    break;
                case ("SHINY"):
                    fractionsNormal.add("шины");
                    break;
                case ("OPASNYE_OTHODY"):
                    fractionsNormal.add("опасные отходы");
                    break;
                case ("INOE"):
                    fractionsNormal.add("иное");
                    break;
                default:
            }
        }
        return fractionsNormal.toString().replaceAll("\\[|\\]","");
    }
}
