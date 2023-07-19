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
                    fractionsNormal.add("\uD83D\uDDDE бумага");
                    break;
                case ("PLASTIK"):
                    fractionsNormal.add("\uD83E\uDD64 пластик");
                    break;
                case ("STEKLO"):
                    fractionsNormal.add("\uD83E\uDED9 стекло");
                    break;
                case ("METALL"):
                    fractionsNormal.add("⛓ металл");
                    break;
                case ("TETRA_PAK"):
                    fractionsNormal.add("\uD83E\uDDC3 тетра пак");
                    break;
                case ("ODEZHDA"):
                    fractionsNormal.add("\uD83D\uDC55 одежда");
                    break;
                case ("LAMPOCHKI"):
                    fractionsNormal.add("\uD83D\uDCA1 лампочки");
                    break;
                case ("KRYSHECHKI"):
                    fractionsNormal.add("\uD83D\uDD73 крышечки");
                    break;
                case ("BYTOVAJA_TEHNIKA"):
                    fractionsNormal.add("\uD83C\uDF9B бытовая техника");
                    break;
                case ("BATAREJKI"):
                    fractionsNormal.add("\uD83D\uDD0B батарейки");
                    break;
                case ("SHINY"):
                    fractionsNormal.add("⚙️ шины");
                    break;
                case ("OPASNYE_OTHODY"):
                    fractionsNormal.add("⚠️ опасные отходы");
                    break;
                case ("INOE"):
                    fractionsNormal.add("\uD83E\uDD14 иное");
                    break;
                default:
            }
        }
        return fractionsNormal.toString().replaceAll("\\[|\\]","");
    }


}
