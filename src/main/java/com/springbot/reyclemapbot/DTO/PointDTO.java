package com.springbot.reyclemapbot.DTO;

import com.springbot.reyclemapbot.model.Points;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.math.Fraction;
/*import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;*/

import java.util.List;

@Getter
@Setter
public class PointDTO {
    private Long id;
    private Double x;
    private Double y;
    private Integer pointId;
    private String address;
    private String title;
    private Boolean restricted;
/*    private RatingDTO rating;
    private List<FractionDTO> fractions;*/
    public PointDTO(){}

    public Points PointDTOToPoints(){
        Points point = new Points();
     /*   Coordinate c1 = new Coordinate(this.x, this.y);
        GeometryFactory geometryFactory = new GeometryFactory();//static заранее
        Point geom = geometryFactory.createPoint(c1);
        point.setGeom(geom);*/
        point.setId(this.pointId);
        point.setAddress(this.address);
        point.setTitle(this.title);
        point.setRestricted(this.restricted);
        return point;
    }
}
