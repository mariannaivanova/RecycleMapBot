package com.springbot.reyclemapbot.config;


import com.springbot.reyclemapbot.model.Points;
import org.springframework.data.geo.Point;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class GeometryUtil {

    public static final int SRID = 4326; //LatLng

   // public static GeometryFactory geometryFactory = new GeometryFactory();


    public static Point parseLocation(String str) {
        String[] words = str.replaceAll("[\\()a-zA-Z]", "").split(" ");
        List<Double> coordinates = new ArrayList<Double>();
        for (String word : words) {
                    double d = Double.parseDouble(word);
                    coordinates.add(d);
        }
        return new Point(coordinates.get(0), coordinates.get(1));
    }
}