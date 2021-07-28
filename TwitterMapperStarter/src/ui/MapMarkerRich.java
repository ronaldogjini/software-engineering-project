package ui;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.MapMarkerCircle;
import org.openstreetmap.gui.jmapviewer.Style;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class MapMarkerRich extends MapMarkerCircle {


    private static final int markerSize = 50;
    private final Coordinate coordinate;
    private final Image image;
    private final String content;
    private final int markerBorderSize = 5;
    private Color color;


    public MapMarkerRich(Layer layer, Color color, Coordinate coordinate,  String content, Image image) {
        super(layer, null, coordinate, markerSize, STYLE.FIXED, getDefaultStyle());
        this.coordinate = coordinate;
        this.color = color;
        this.image = image;
        this. content = content;
    }

    @Override
    public void paint(Graphics g, Point position, int radius) {
        int imageSize = markerSize;
        int halfImageSize = imageSize/2;

        int xImage = position.x - halfImageSize;
        int yImage = position.y - halfImageSize;

        int markerSize =  imageSize + markerBorderSize * 2;

        int x = xImage - markerBorderSize;
        int y = yImage - markerBorderSize;

        g.setClip(new Ellipse2D.Float(x, y, markerSize, markerSize));
        g.setColor(color);
        g.fillOval(x, y, markerSize, markerSize);
        g.setClip(new Ellipse2D.Float(xImage, yImage, imageSize, imageSize));
        g.drawImage(image, xImage, yImage, imageSize, imageSize, null);

    }

}
