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

public interface PointDTO {
    String getAddress();
    String getTitle();
    String getUrl();
}
