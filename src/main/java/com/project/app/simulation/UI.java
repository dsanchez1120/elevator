package com.project.app.simulation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.app.elevator.ElevatorImpl;
import com.project.app.util.DoorStatus;
import com.project.app.util.FloorDirection;
import com.project.app.util.SecurityType;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UI extends JFrame {
    // Constants
    private final int FRAME_X_DIMENSION = 600;
    private final int FRAME_Y_DIMENSION = 600;

    // Swing Components
    JFrame frame;
    JPanel elevatorPanel;
    JLabel elevatorLabel;
    JPanel elevatorPanelCenter;
    JPanel statsPanel;
    JLabel statsLabel;
    JPanel statsPanelCenter;
    JPanel controlPanel;
    JPanel controlPanelCenter;
    JPanel controlPanelSouth;
    JLabel controlLabel;

    ArrayList<JTextArea> statsTextAreas;


    // Elevator Variables
    private ElevatorImpl elevator;
    private User user;
    private String simulationName;
    private int numFloors;

    UI(String name, int X_DIM, int Y_DIM) throws IOException {
        // Create and Configure Frame
        setTitle(name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(X_DIM, Y_DIM);
        setLayout(new BorderLayout());

        elevator = buildElevator("none_1.json");
        user = buildUser("joe.json");

        initializeElevatorPanel();
        initializeStatsPanel();
        initializeControlPanel();
    }

    private void initializeControlPanel() {
        controlPanel = new JPanel(new BorderLayout());
        controlLabel = new JLabel("Control Panel", SwingConstants.CENTER);
        controlPanel.setBackground(Color.LIGHT_GRAY);
        controlPanel.add(controlLabel, BorderLayout.NORTH);
        controlPanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        controlPanelCenter = new JPanel(new BorderLayout());
        initializeControlPanelCenter();
        controlPanelCenter.setBackground(Color.LIGHT_GRAY);
        controlPanelSouth = new JPanel(new GridLayout(2, 2));
        initializeControlPanelSouth();
        controlPanelSouth.setBackground(Color.LIGHT_GRAY);
        controlPanel.add(controlPanelCenter, BorderLayout.CENTER);
        controlPanel.add(controlPanelSouth, BorderLayout.SOUTH);
        add(controlPanel, BorderLayout.EAST);
    }

    private void initializeStatsPanel() {
        statsPanel = new JPanel(new BorderLayout());
        statsLabel = new JLabel("Elevator Data", SwingConstants.CENTER);
        statsPanel.setBackground(Color.LIGHT_GRAY);
        statsPanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        statsPanelCenter = new JPanel(new GridLayout(2, 3));
        initializeStatsTextAreas();
        for (JTextArea ta: statsTextAreas) {
            statsPanelCenter.add(ta);
        }
        statsPanel.add(statsLabel, BorderLayout.NORTH);
        statsPanel.add(statsPanelCenter, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.NORTH);
    }

    private void initializeElevatorPanel() {
        elevatorPanel = new JPanel(new BorderLayout());
        elevatorLabel = new JLabel("Elevator", SwingConstants.CENTER);
        elevatorPanel.setBackground(Color.LIGHT_GRAY);
        elevatorPanel.add(elevatorLabel, BorderLayout.NORTH);
        elevatorPanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        elevatorPanelCenter = new JPanel(new GridBagLayout());
        elevatorPanelCenter.setBackground(Color.LIGHT_GRAY);
        initializeElevatorPanelCenter();
        elevatorPanel.add(elevatorPanelCenter, BorderLayout.CENTER);
        add(elevatorPanel, BorderLayout.CENTER);
    }

    private void initializeElevatorPanelCenter() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.weighty = 0.5;
        ArrayList<JTextArea> floorSquares = new ArrayList<>();
        ArrayList<JButton> downButtons = new ArrayList<>();
        ArrayList<JButton> upButtons = new ArrayList<>();
        for (String i: elevator.getFloors()) {
            floorSquares.add(new JTextArea(i));
            downButtons.add(new JButton("Down"));
            upButtons.add(new JButton("Up"));
        }
        for (int i = 0; i < numFloors; i++) {
            int gridy = numFloors - (i + 1);
            // Elevator Floor Square
            c.gridx = 0;
            c.gridy = gridy;
            floorSquares.get(i).setBorder(BorderFactory.createLineBorder(Color.black, 2));
            floorSquares.get(i).setText(elevator.getFloors().get(i));
            floorSquares.get(i).setBackground(elevator.getCurrentFloor() != i ? Color.gray : Color.YELLOW);
            floorSquares.get(i).setEditable(false);
            elevatorPanelCenter.add(floorSquares.get(i), c);
            // Down Button
            c.gridx = 1;
            c.gridy = gridy;
            if (i != 0) {
                elevatorPanelCenter.add(downButtons.get(i), c);
            }
            // Up Button
            c.gridx = 2;
            c.gridy = gridy;
            if (i != numFloors - 1) {
                elevatorPanelCenter.add(upButtons.get(i), c);
            }
        }

    }

    // Initializes floor and function buttons
    private void initializeControlPanelCenter() {
        ArrayList<JButton> floorButtons = new ArrayList<>();
        JButton openDoorButton = new JButton("Open Door");
        JButton emergencyButton = new JButton("Emergency");
        JButton closeDoorButton = new JButton("Close Door");
        JPanel floorButtonsPanel = new JPanel(new GridLayout(0, 3));
        JPanel functionButtonsPanel = new JPanel(new GridLayout(1, 3));
        for (String i: elevator.getFloors()) {
            floorButtons.add(new JButton(i));
        }
        for (int i = numFloors - 1; i > -1; i--) {
            floorButtonsPanel.add(floorButtons.get(i));
        }
        functionButtonsPanel.add(closeDoorButton);
        functionButtonsPanel.add(emergencyButton);
        functionButtonsPanel.add(openDoorButton);
        floorButtonsPanel.setBackground(Color.LIGHT_GRAY);
        functionButtonsPanel.setBackground(Color.LIGHT_GRAY);
        controlPanelCenter.add(floorButtonsPanel, BorderLayout.CENTER);
        controlPanelCenter.add(functionButtonsPanel, BorderLayout.SOUTH);
    }

    // Initializes Simulation Control Buttons
    private void initializeControlPanelSouth() {
        JButton moveFloorButton = new JButton("Move Floor");
        JButton changeUserButton = new JButton("Change User");
        JButton authorizeButton = new JButton("Authorize");
        JButton changeElevatorButton = new JButton("Change Elevator");

        controlPanelSouth.add(moveFloorButton);
        controlPanelSouth.add(changeUserButton);
        controlPanelSouth.add(authorizeButton);
        controlPanelSouth.add(changeElevatorButton);
    }

    // Initializes stats values
    private void initializeStatsTextAreas() {
        statsTextAreas = new ArrayList<>(
                Arrays.asList(
                        new JTextArea("Security: " + elevator.getSecurityType().toString()),
                        new JTextArea("Current Floor: " + elevator.getCurrentFloor() + 1),
                        new JTextArea("Doors: " + elevator.getDoorStatus().toString()),
                        new JTextArea("Authentication: " + elevator.getAuthenticated().toString()),
                        new JTextArea("Direction: " + getDirectionAsString(elevator.getDirection())),
                        new JTextArea("User: " + user.getName()))
                );
        for (int i = 0; i < statsTextAreas.size(); i++) {
            statsTextAreas.get(i).setEditable(false);
            statsTextAreas.get(i).setBorder(BorderFactory.createLineBorder(Color.black));
            statsTextAreas.get(i).setBackground(Color.gray);
        }
    }

    // Updates Stats Values when called
    private void updateStats() {
        statsTextAreas.get(0).setText("Security: " + elevator.getSecurityType().toString());
        statsTextAreas.get(1).setText("Current Floor: " + elevator.getCurrentFloor() + 1);
        statsTextAreas.get(2).setText("Doors: " + elevator.getDoorStatus().toString());
        statsTextAreas.get(3).setText("Authentication: " + elevator.getAuthenticated().toString());
        statsTextAreas.get(4).setText("Direction: " + getDirectionAsString(elevator.getDirection()));
        statsTextAreas.get(5).setText("Doors: " + user.getName());
    }

    private String getDirectionAsString(int direction) {
        return switch (direction) {
            case 1 -> "UP";
            case 0 -> "STATIONARY";
            case -1 -> "DOWN";
            default -> "ERROR";
        };
    }

    private ElevatorImpl buildElevator(String elevatorName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File("config/elevator/" + elevatorName));
        simulationName = jsonNode.get("ElevatorName").asText();
        numFloors = jsonNode.get("numFloors").asInt();
        return ElevatorImpl.builder()
                .floors(new ArrayList<>(
                        objectMapper.readValue(jsonNode.get("floors").toString(),
                                new TypeReference<java.util.List<String>>() {})))
                .securityType(SecurityType.valueOf(jsonNode.get("securityType").asText()))
                .defaultFloor(jsonNode.get("defaultFloor").asInt())
                .floorsToVisit(new ArrayList<>(Collections.nCopies(numFloors, FloorDirection.NONE)))
                .authorizedFloors(new ArrayList<>(Collections.nCopies(numFloors, false)))
                .authorizedUsers(new ArrayList<>(
                        objectMapper.readValue(jsonNode.get("authorizedUsers").toString(),
                                new TypeReference<List<String>>() {})))
                .doorStatus(DoorStatus.CLOSED)
                .authenticated(false)
                .direction(0)
                .currentFloor(jsonNode.get("currentFloor").asInt())
                .build();
    }

    private User buildUser(String userName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File("config/users/" + userName));
        return User.builder()
                .name(jsonNode.get("name").asText())
                .authorizedFloors(new ArrayList<>(Collections.nCopies(numFloors, false)))
                .build();
    }
}
