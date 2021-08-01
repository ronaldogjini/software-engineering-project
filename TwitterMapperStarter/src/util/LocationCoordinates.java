package util;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import twitter4j.GeoLocation;
import twitter4j.Status;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

/**
 * Helpful methods that don't clearly fit anywhere else.
 */
public class LocationCoordinates {
    public static GeoLocation statusLocation(Status status) {
        GeoLocation bottomRight = status.getPlace().getBoundingBoxCoordinates()[0][0];
        GeoLocation topLeft = status.getPlace().getBoundingBoxCoordinates()[0][2];
        double newLat = (bottomRight.getLatitude() + topLeft.getLatitude())/2;
        double newLon = (bottomRight.getLongitude() + topLeft.getLongitude())/2;
        return new GeoLocation(newLat, newLon);
    }

    public static Coordinate GeoLocationToCoordinate(GeoLocation loc) {
        return new Coordinate(loc.getLatitude(), loc.getLongitude());
    }

    public static Coordinate statusCoordinate(Status status) {
        GeoLocation bottomRight = status.getPlace().getBoundingBoxCoordinates()[0][0];
        GeoLocation topLeft = status.getPlace().getBoundingBoxCoordinates()[0][2];
        double newLat = (bottomRight.getLatitude() + topLeft.getLatitude())/2;
        double newLon = (bottomRight.getLongitude() + topLeft.getLongitude())/2;
        return new Coordinate(newLat, newLon);
    }


}
