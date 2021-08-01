package ui;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import query.Query;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ContentPanel extends JPanel {
    private JSplitPane topLevelSplitPane;
    private JSplitPane querySplitPane;
    private JPanel newQueryPanel;
    private JPanel existingQueryList;
    private JMapViewer map;

    private Application app;

    public ContentPanel(Application app) {
        this.app = app;
        initializeMap();

        // NOTE: We wrap existingQueryList in a container so it gets a pretty border.
        JPanel layerPanelContainer = new JPanel();
        existingQueryList = new JPanel();

        addBorderQueryList(layerPanelContainer);
        createQuerySplitPane(layerPanelContainer);
        createTopLevelSplitPane();

        add(topLevelSplitPane, "Center");
        revalidate();
        repaint();
    }

    // Add a new query to the set of queries and update the UI to reflect the new query.
    public void addQuery(Query query) {

        JPanel newQueryPanel = newQueryPanel();
        JPanel colorPanel = newColorPanel(query);
        JButton removeButton = addRemoveButton(query, newQueryPanel);

        GridBagConstraints constraints = new GridBagConstraints();
        newQueryPanel.add(colorPanel, constraints);

        JCheckBox checkbox = addCheckbox(query);
        query.setCheckBox(checkbox);

        fillQueryPanel(newQueryPanel,checkbox, removeButton);

        existingQueryList.add(newQueryPanel);
        validate();
    }

    public JMapViewer getViewer() {
        return map;
    }

    private void initializeMap() {
        map = new JMapViewer();
        map.setMinimumSize(new Dimension(100, 50));
        setLayout(new BorderLayout());
        newQueryPanel = new NewQueryPanel(app);
    }

    private void addBorderQueryList(JPanel layerPanelContainer) {
        existingQueryList.setLayout(new javax.swing.BoxLayout(existingQueryList, javax.swing.BoxLayout.Y_AXIS));
        layerPanelContainer.setLayout(new BorderLayout());
        layerPanelContainer.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Current Queries"),
                        BorderFactory.createEmptyBorder(5,5,5,5)));
        layerPanelContainer.add(existingQueryList, BorderLayout.NORTH);
    }

    private void createQuerySplitPane(JPanel layerPanelContainer) {
        querySplitPane = new JSplitPane(0);
        querySplitPane.setDividerLocation(150);
        querySplitPane.setTopComponent(newQueryPanel);
        querySplitPane.setBottomComponent(layerPanelContainer);
    }

    private void createTopLevelSplitPane() {
        topLevelSplitPane = new JSplitPane(1);
        topLevelSplitPane.setDividerLocation(150);
        topLevelSplitPane.setLeftComponent(querySplitPane);
        topLevelSplitPane.setRightComponent(map);
    }

    private JButton addRemoveButton(Query query, JPanel newQueryPanel) {
        JButton removeButton = new JButton("X");
        removeButton.setPreferredSize(new Dimension(30, 20));
        removeButton.setMargin(new Insets(0,0,0,0));
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.terminateQuery(query);
                query.terminate();
                existingQueryList.remove(newQueryPanel);
                revalidate();
            }
        });

        return removeButton;
    }

    private JCheckBox addCheckbox(Query query) {
        JCheckBox checkbox = new JCheckBox(query.getQueryString());
        checkbox.setSelected(true);
        checkbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.updateVisibility();
            }
        });
        return checkbox;
    }

    private JPanel newColorPanel(Query query) {
        JPanel colorPanel = new JPanel();
        colorPanel.setBackground(query.getColor());
        colorPanel.setPreferredSize(new Dimension(30, 30));

        return colorPanel;
    }

    private JPanel newQueryPanel() {
        JPanel newQueryPanel = new JPanel();
        newQueryPanel.setLayout(new GridBagLayout());
        return newQueryPanel;
    }

    private void fillQueryPanel(JPanel newQueryPanel, JCheckBox checkbox, JButton removeButton) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        newQueryPanel.add(checkbox, constraints);
        newQueryPanel.add(removeButton);

    }

}