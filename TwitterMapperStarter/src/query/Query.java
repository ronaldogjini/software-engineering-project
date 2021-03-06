package query;

import filters.Filter;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.MapMarkerCircle;
import twitter4j.Status;
import ui.MapMarkerRich;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.List;

import static util.ImageRetriever.imageFromURL;
import static util.LocationCoordinates.statusCoordinate;

public class Query implements Observer {
    // The map on which to display markers when the query matches
    private final JMapViewer map;
    // Each query has its own "layer" so they can be turned on and off all at once
    private Layer layer;
    // The color of the outside area of the marker
    private final Color color;
    // The string representing the filter for this query
    private final String queryString;
    // The filter parsed from the queryString
    private final Filter filter;
    // The checkBox in the UI corresponding to this query (so we can turn it on and off and delete it)
    private JCheckBox checkBox;

    public Color getColor() {
        return color;
    }
    public String getQueryString() {
        return queryString;
    }
    public Filter getFilter() {
        return filter;
    }
    public Layer getLayer() {
        return layer;
    }
    public JCheckBox getCheckBox() {
        return checkBox;
    }
    public void setCheckBox(JCheckBox checkBox) {
        this.checkBox = checkBox;
    }
    public void setVisible(boolean visible) {
        layer.setVisible(visible);
    }
    public boolean getVisible() { return layer.isVisible(); }
    private List<MapMarkerCircle> allQueryMarkers;

    public Query(String queryString, Color color, JMapViewer map) {
        this.queryString = queryString;
        this.filter = Filter.parse(queryString);
        this.color = color;
        this.layer = new Layer(queryString);
        this.map = map;
        allQueryMarkers = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Query: " + queryString;
    }

    public void terminate() {
        for (MapMarkerCircle individualMarker : allQueryMarkers) {
            map.removeMapMarker(individualMarker);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Status newStatus  = (Status) arg;
        if (filter.matches(newStatus)) {
            MapMarkerRich newMarker = createMarkerRich(newStatus);
            allQueryMarkers.add(newMarker);
            map.addMapMarker(newMarker);
        }
    }

    private MapMarkerRich createMarkerRich(Status status) {
        Coordinate coordinate = statusCoordinate(status);
        String imageURL = status.getUser().getProfileImageURL();
        Image image = imageFromURL(imageURL);
        String text = status.getText();

        return new MapMarkerRich(layer, color, coordinate, text, image, imageURL);

    }
}

