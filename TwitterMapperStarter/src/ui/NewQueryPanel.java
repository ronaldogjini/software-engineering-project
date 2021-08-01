package ui;

import query.Query;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

/**
 * A UI panel for entering new queries.
 */
public class NewQueryPanel extends JPanel {
    private final JTextField newQuery = new JTextField(10);
    private final JLabel queryLabel = new JLabel("Enter Search: ");
    private final JPanel colorSetter;
    private final Application app;
    private Random random;

    public NewQueryPanel(Application app) {
        this.app = app;
        this.colorSetter = new JPanel();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        queryLabel.setLabelFor(newQuery);
        GridBagConstraints constraints = new GridBagConstraints();


        addNewQueryComponent(constraints);
        add(Box.createRigidArea(new Dimension(5, 5)));

        JLabel colorLabel = new JLabel("Select Color: ");
        colorSetter.setBackground(getRandomColor());

        addColorSetterComponent(constraints, colorLabel);
        add(Box.createRigidArea(new Dimension(5, 5)));

        JButton addQueryButton = new JButton("Add New Search");
        addQueryButtonComponent(constraints, addQueryButton);

        setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("New Search"),
                        BorderFactory.createEmptyBorder(5,5,5,5)));

        createListenerAddQueryButton(addQueryButton);
        // This makes the "Enter" key submit the query.
        app.getRootPane().setDefaultButton(addQueryButton);
        addListenerColorSetter();
    }

    private void addQuery(String newQuery) {
        Query query = new Query(newQuery, colorSetter.getBackground(), app.map());
        app.addQuery(query);
        colorSetter.setBackground(getRandomColor());
    }

    public Color getRandomColor() {
        random = new Random();
        // Pleasant colors: https://stackoverflow.com/questions/4246351/creating-random-colour-in-java#4246418
        final float hue = random.nextFloat();
        final float saturation = (random.nextInt(2000) + 1000) / 10000f;
        final float luminance = 0.9f;
        return Color.getHSBColor(hue, saturation, luminance);
    }

    private void createListenerAddQueryButton(JButton addQueryButton) {
        addQueryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!newQuery.getText().equals("")) {
                    addQuery(newQuery.getText().toLowerCase());
                    newQuery.setText("");
                }
            }
        });
    }

    private void addListenerColorSetter() {
        colorSetter.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Color newColor = JColorChooser.showDialog(
                            app,
                            "Choose Background Color",
                            colorSetter.getBackground());
                    // newColor is "null" if user clicks "Cancel" button on JColorChooser popup.
                    if (newColor != null) {
                        colorSetter.setBackground(newColor);
                    }
                }
            }
        });
    }

    private void addColorSetterComponent(GridBagConstraints constraints, JLabel colorLabel) {
        constraints.gridwidth = GridBagConstraints.RELATIVE;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridy = 1;
        constraints.gridx = 0;
        add(colorLabel, constraints);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        colorSetter.setMaximumSize(new Dimension(200, 20));
        add(colorSetter, constraints);
    }

    private void addNewQueryComponent(GridBagConstraints constraints) {
        constraints.gridwidth = GridBagConstraints.RELATIVE;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridy = 0;
        constraints.gridx = 0;
        add(queryLabel, constraints);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        newQuery.setMaximumSize(new Dimension(200, 20));
        constraints.gridx = 1;
        add(newQuery, constraints);
    }

    private void addQueryButtonComponent(GridBagConstraints constraints, JButton addQueryButton) {
        constraints.gridx = GridBagConstraints.RELATIVE;       //aligned with button 2
        constraints.gridwidth = 2;   //2 columns wide
        constraints.gridy = GridBagConstraints.RELATIVE;       //third row
        add(addQueryButton, constraints);
    }
}
