package com.springbot.recyclemapbot.payload;

import com.springbot.recyclemapbot.DTO.SubscribeString;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class SubscribePayload {

    Long id;

    String location;
    Double distance;

    Set<String> fractions;

    List<String> pointsAddress;

    public SubscribePayload(SubscribeString subscribeString, Set<String> fractions, List<String> pointsAddress){
        this.id = subscribeString.getId();
        this.location = subscribeString.getLocation();
        this.distance = subscribeString.getDistance();
        this.fractions = fractions;
        this.pointsAddress = pointsAddress;
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

    public String showPoints(){
        String answer = "";
        for (String pointAddress: pointsAddress){
            answer += pointAddress + "\n";
        }
        return answer;
    }

    public String getText(){
        String answer = "";
        answer += this.location + "\n";
        if (this.distance != null) {
            answer += this.distance.toString() + "\n";
        }
        answer += "\n";
        answer += toNornalNames() + "\n";
        answer += "\n";
        answer += showPoints();
        return answer;
    }

}
