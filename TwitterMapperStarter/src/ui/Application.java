package ui;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import query.Query;
import twitter.LiveTwitterSource;
import twitter.TwitterSource;
import twitter.TwitterSourceFactory;
import util.SphericalGeometry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.Timer;

/**
 * The Twitter viewer application
 * Derived from a JMapViewer demo program written by Jan Peter Stotz
 */
public class Application extends JFrame {
    // The content panel, which contains the entire UI
    private final ContentPanel contentPanel;
    // The provider of the tiles for the map, we use the Bing source
    private BingAerialTileSource bing;
    // All of the active queries
    private List<Query> queries = new ArrayList<>();
    // The source of tweets, a TwitterSource, either live or playback
    private TwitterSource twitterSource;

    private void initialize() {
        // To use the recorded twitter stream, use the following line
        // The number passed to the constructor is a speedup value:
        //  1.0 - play back at the recorded speed
        //  2.0 - play back twice as fast

        TwitterSourceFactory sourceFactory = new TwitterSourceFactory();
        twitterSource = sourceFactory.createTwitterSource("LIVE");

        queries = new ArrayList<>();
    }

    /**
     * A new query has been entered via the User Interface
     * @param   query   The new query object
     */
    public void addQuery(Query query) {
        queries.add(query);
        Set<String> allterms = getQueryTerms();
        twitterSource.setFilterTerms(allterms);
        contentPanel.addQuery(query);
        twitterSource.addObserver(query);
    }

    /**
     * return a list of all terms mentioned in all queries. The live twitter source uses this
     * to request matching tweets from the Twitter API.
     * @return
     */
    private Set<String> getQueryTerms() {
        Set<String> termsList = new HashSet<>();
        for (Query q : queries) {
            termsList.addAll(q.getFilter().terms());
        }
        return termsList;
    }

    /**
     * Constructs the {@code Application}.
     */
    public Application() {
        super("Twitter content viewer");
        setSize(300, 300);
        initialize();


        // Do UI initialization
        contentPanel = new ContentPanel(this);

        initializeApplicationUI();
        configureInitialMapSettings();
        checkBingMapsLoaded();
        createMouseMotionListener();

    }

    // How big is a single pixel on the map?  We use this to compute which tweet markers
    // are at the current most position.
    private double pixelWidth(Point p) {
        ICoordinate center = map().getPosition(p);
        ICoordinate edge = map().getPosition(new Point(p.x + 1, p.y));
        return SphericalGeometry.distanceBetween(center, edge);
    }

    // Get those layers (of tweet markers) that are visible because their corresponding query is enabled
    private Set<Layer> getVisibleLayers() {
        Set<Layer> layersList = new HashSet<>();
        for (Query q : queries) {
            if (q.getVisible()) {
                layersList.add(q.getLayer());
            }
        }
        return layersList;
    }

    // Get all the markers at the given map position, at the current map zoom setting
    private List<MapMarker> getMarkersCovering(ICoordinate pos, double pixelWidth) {
        List<MapMarker> markersList = new ArrayList<>();
        Set<Layer> visibleLayers = getVisibleLayers();
        for (MapMarker m : map().getMapMarkerList()) {
            if (!visibleLayers.contains(m.getLayer())) continue;
            double distance = SphericalGeometry.distanceBetween(m.getCoordinate(), pos);
            if (distance < m.getRadius() * pixelWidth) {
                markersList.add(m);
            }
        }
        return markersList;
    }

    public JMapViewer map() {
        return contentPanel.getViewer();
    }

    /**
     * @param args Application program arguments (which are ignored)
     */
    public static void main(String[] args) {
        new Application().setVisible(true);
    }

    // Update which queries are visible after any checkBox has been changed
    public void updateVisibility() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                System.out.println("Recomputing visible queries");
                for (Query q : queries) {
                    JCheckBox box = q.getCheckBox();
                    Boolean state = box.isSelected();
                    q.setVisible(state);
                }
                map().repaint();
            }
        });
    }

    // A query has been deleted, remove all traces of it
    public void terminateQuery(Query query) {
        queries.remove(query);
        query.terminate();
        Set<String> allterms = getQueryTerms();
        twitterSource.setFilterTerms(allterms);
        twitterSource.deleteObserver(query);
    }

    private void initializeApplicationUI() {
        bing = new BingAerialTileSource();
        setLayout(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void configureInitialMapSettings() {
        // Always have map markers showing.
        map().setMapMarkerVisible(true);
        // Always have zoom controls showing,
        // and allow scrolling of the map around the edge of the world.
        map().setZoomContolsVisible(true);
        map().setScrollWrapEnabled(true);

        // Use the Bing tile provider
        map().setTileSource(bing);
    }

    private void checkBingMapsLoaded() {
        //NOTE This is so that the map eventually loads the tiles once Bing attribution is ready.
        Coordinate coord = new Coordinate(0, 0);
        Timer bingTimer = new Timer();
        TimerTask bingAttributionCheck = new TimerTask() {
            @Override
            public void run() {
                // This is the best method we've found to determine when the Bing data has been loaded.
                // We use this to trigger zooming the map so that the entire world is visible.
                if (!bing.getAttributionText(0, coord, coord).equals("Error loading Bing attribution data")) {
                    map().setZoom(2);
                    bingTimer.cancel();
                }
            }
        };
        bingTimer.schedule(bingAttributionCheck, 100, 200);
    }

    private void createMouseMotionListener() {
        map().addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                ICoordinate pos = map().getPosition(p);

                map().setToolTipText("");
                List<MapMarker> markers = getMarkersCovering(pos, pixelWidth(p));
                if (!markers.isEmpty()) {
                    displayTooltipInfo(markers);
                }
            }
        });
    }

    public void displayTooltipInfo(List<MapMarker> markers) {
        String tooltipContent = "<html>";
        for (MapMarker marker : markers) {
            MapMarkerRich markerRich = (MapMarkerRich) marker;
            tooltipContent += "<p><img src=" + markerRich.getImageLink() + ">";
            tooltipContent += markerRich.getContent() + "</p>";
        }
        tooltipContent+="</html>";
        map().setToolTipText(tooltipContent);
    }
}
