package com.example.realestate.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public class GeometryUtil {

    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public static Point createPoint(Double longitude, Double latitude) {
        if (longitude == null || latitude == null) {
            return null;
        }
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    public static Point createPoint(String longitudeStr, String latitudeStr) {
        try {
            Double longitude = Double.parseDouble(longitudeStr);
            Double latitude = Double.parseDouble(latitudeStr);
            return createPoint(longitude, latitude);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}