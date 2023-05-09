package com.springbot.recyclemapbot.config;


//import org.springframework.data.geo.Point;
//import org.locationtech.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Coordinate;
//import org.locationtech.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.PrecisionModel;


        import java.util.ArrayList;
import java.util.List;


public class GeometryUtil {

    public static final int SRID = 4326; //LatLng

   // public static GeometryFactory geometryFactory = new GeometryFactory();


   /* public static Point parseLocation(String str) {
        String[] words = str.replaceAll("[\\()a-zA-Z]", "").split(" ");
        List<Double> coordinates = new ArrayList<Double>();
        for (String word : words) {
                    double d = Double.parseDouble(word);
                    coordinates.add(d);
        }
        return new Point(coordinates.get(0), coordinates.get(1));
    }*/


    private static GeometryFactory factory4326
            = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);

    public static Point parseLocation(String str) {
        String[] words = str.replaceAll("[\\()a-zA-Z]", "").split(" ");
        List<Double> coordinates = new ArrayList<Double>();
        for (String word : words) {
            double d = Double.parseDouble(word);
            coordinates.add(d);
        }
        return factory4326.createPoint(new Coordinate(coordinates.get(0), coordinates.get(1)));
    }
}