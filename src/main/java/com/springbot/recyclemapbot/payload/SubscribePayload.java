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
        String f = "";
        for (String fraction : fractionsNormal) {
            f += fraction + "\n";
        }
        //return fractionsNormal.toString().replaceAll("\\[|\\]","");
        return f;
    }

    public String showPoints(){
        String answer = "";
        for (String pointAddress: pointsAddress){
            answer +=  "• " +pointAddress + "\n";
        }
        return answer;
    }

    public String getText(){
        String answer = "";
        answer += toNornalNames() + "\n";
        if (this.distance != null) {
            answer +=  (int)(this.distance/(3.6*60)) + " минут";
        }
        answer += "\n\n";
        answer += showPoints();
        return answer;
    }

}
