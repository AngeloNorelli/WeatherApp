package com.weatherapp;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class WeatherApp {
    private static JXMapViewer mapViewer;
    private static WeatherService weatherService = new WeatherService();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Interactive Weather Map");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            mapViewer = createMapViewer();
            frame.add(mapViewer);

            JPanel controlPanel = createControlPanel(frame);
            frame.add(controlPanel, BorderLayout.NORTH);

            frame.setVisible(true);
        });
    }

    private static JXMapViewer createMapViewer() {
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);

        JXMapViewer mapViewer = new JXMapViewer();
        mapViewer.setTileFactory(tileFactory);

        GeoPosition defaultCenter = new GeoPosition(52.23, 21.01);
        mapViewer.setZoom(13);
        mapViewer.setAddressLocation(defaultCenter);

        final boolean[] isDragging = {false};
        PanMouseInputListener panListener = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(panListener);
        mapViewer.addMouseMotionListener(panListener);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e)) {
                    isDragging[0] = false;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e) && !isDragging[0]) {
                    GeoPosition clickedPosition = mapViewer.convertPointToGeoPosition(e.getPoint());

                    List<String> weatherInfo = null;
                    try {
                        weatherInfo = weatherService.getWeatherForLocation(clickedPosition.getLatitude(), clickedPosition.getLongitude());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                    addWeatherMarker(mapViewer, clickedPosition, weatherInfo);
                }
            }
        });

        mapViewer.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e)) {
                    isDragging[0] = true;
                }
            }
        });

        return mapViewer;
    }

    private static JPanel createControlPanel(JFrame frame) {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        JTextField countryField = new JTextField(20);
        JButton fetchButton = new JButton("Move There");

        controlPanel.add(new JLabel("Enter Country:"));
        controlPanel.add(countryField);
        controlPanel.add(fetchButton);

        fetchButton.addActionListener(e -> {
            String country = countryField.getText();
            if (!country.isEmpty()) {
                updateMapWithWeather(country);
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter a valid country name.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return controlPanel;
    }

    private static void updateMapWithWeather(String country) {
        try {
            String isoCode = CountryService.getISOCode(country);

            GeoService geoService = new GeoService();
            GeoPosition countryPosition = geoService.getCountryCoordinates(isoCode);

            if (countryPosition != null) {
                mapViewer.setAddressLocation(countryPosition);
            } else {
                JOptionPane.showMessageDialog(null, "Country coordinates not found.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching data: " + ex.getMessage());
        }
    }

    private static void addWeatherMarker(JXMapViewer mapViewer, GeoPosition position, List<String> weatherInfo) {
        JWindow tooltip = new JWindow();
        tooltip.setLayout(new BorderLayout());

        String weatherText = String.join(", ", weatherInfo);
        tooltip.add(new JLabel(weatherText), BorderLayout.CENTER);
        tooltip.pack();

        Point locationOnScreed = mapViewer.getLocationOnScreen();
        tooltip.setLocation(locationOnScreed.x + 50, locationOnScreed.y + 50);
        tooltip.setVisible(true);

        new Timer(3000, e -> tooltip.setVisible(false)).start();
    }

    private static boolean isNearby(GeoPosition pos1, GeoPosition pos2, double tolerance) {
        return Math.abs(pos1.getLatitude() - pos2.getLatitude()) < tolerance &&
                Math.abs(pos1.getLongitude() - pos2.getLongitude()) < tolerance;
    }
}

