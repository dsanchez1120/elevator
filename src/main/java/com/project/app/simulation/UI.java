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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    ArrayList<JButton> downButtons;
    ArrayList<JButton> upButtons;

    JPanel statsPanel;
    JLabel statsLabel;
    JPanel statsPanelCenter;

    JPanel controlPanel;
    JPanel controlPanelCenter;
    ArrayList<JButton> floorButtons;
    JButton openDoorButton;
    JButton emergencyButton;
    JButton closeDoorButton;
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
        downButtons = new ArrayList<>();
        upButtons = new ArrayList<>();
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
                downButtons.get(i).setBackground(new Button().getBackground());
                downButtons.get(i).setOpaque(true);
                downButtons.get(i).addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println(e.getSource());
                       elevator.callButtonPressed(downButtons.indexOf(e.getSource()), FloorDirection.DOWN);
                       updateDownCallButtons(downButtons.indexOf(e.getSource()));
                       updateStats();
                    }
                });
                elevatorPanelCenter.add(downButtons.get(i), c);
            }
            // Up Button
            c.gridx = 2;
            c.gridy = gridy;
            if (i != numFloors - 1) {
                upButtons.get(i).setBackground(new Button().getBackground());
                upButtons.get(i).setOpaque(true);
                upButtons.get(i).addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println(e.getSource());
                        elevator.callButtonPressed(upButtons.indexOf(e.getSource()), FloorDirection.UP);
                        updateUpCallButtons(upButtons.indexOf(e.getSource()));
                        updateStats();
                    }
                });
                elevatorPanelCenter.add(upButtons.get(i), c);
            }
        }

    }

    private void initializeControlPanelCenter() {
        floorButtons = new ArrayList<>();
        openDoorButton = new JButton("Open Door");
        emergencyButton = new JButton("Emergency");
        closeDoorButton = new JButton("Close Door");
        JPanel floorButtonsPanel = new JPanel(new GridLayout(0, 3));
        JPanel functionButtonsPanel = new JPanel(new GridLayout(1, 3));
        for (String i: elevator.getFloors()) {
            floorButtons.add(new JButton(i));
        }
        for (int i = numFloors - 1; i > -1; i--) {
            floorButtons.get(i).setOpaque(true);
            floorButtons.get(i).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    elevator.interiorButtonPressed(e.getActionCommand());
                    updateFloorButtons(elevator.getFloors().indexOf(e.getActionCommand()));
                    updateStats();
                }
            });
            floorButtonsPanel.add(floorButtons.get(i));
        }
        openDoorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elevator.interiorButtonPressed("open");
                updateStats();
            }
        });
        emergencyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elevator.interiorButtonPressed("emergency");
                updateStats();
                updateDownCallButtons(-1);
                updateUpCallButtons(-1);
                updateFloorButtons(-1);
            }
        });
        closeDoorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elevator.interiorButtonPressed("close");
                updateStats();
            }
        });
        functionButtonsPanel.add(closeDoorButton);
        functionButtonsPanel.add(emergencyButton);
        functionButtonsPanel.add(openDoorButton);
        floorButtonsPanel.setBackground(Color.LIGHT_GRAY);
        functionButtonsPanel.setBackground(Color.LIGHT_GRAY);
        controlPanelCenter.add(floorButtonsPanel, BorderLayout.CENTER);
        controlPanelCenter.add(functionButtonsPanel, BorderLayout.SOUTH);
    }

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

    private void updateUpCallButtons(int index) {
        for (int i = 0; i < numFloors - 1; i++) {
            if (elevator.getFloorsToVisit().get(i).equals(FloorDirection.NONE)) {
                upButtons.get(i).setBackground(new Button().getBackground());
            } else if (i == index) {
                upButtons.get(i).setBackground(Color.YELLOW);
            }
        }
    }

    private void updateDownCallButtons(int index) {
        for (int i = 1; i < numFloors; i++) {
            if (elevator.getFloorsToVisit().get(i).equals(FloorDirection.NONE)) {
                downButtons.get(i).setBackground(new Button().getBackground());
            } else if (i == index) {
                downButtons.get(i).setBackground(Color.YELLOW);
            }
        }
    }

    private void updateFloorButtons(int index) {
        for (int i = 0; i < numFloors; i++) {
            if (elevator.getFloorsToVisit().get(i).equals(FloorDirection.NONE)) {
                floorButtons.get(i).setBackground(new JButton().getBackground());
            } else if (i == index) {
                floorButtons.get(i).setBackground(Color.YELLOW);
            }
        }
    }

    private void updateStats() {
        statsTextAreas.get(0).setText("Security: " + elevator.getSecurityType().toString());
        statsTextAreas.get(1).setText("Current Floor: " + elevator.getCurrentFloor() + 1);
        statsTextAreas.get(2).setText("Doors: " + elevator.getDoorStatus().toString());
        statsTextAreas.get(3).setText("Authentication: " + elevator.getAuthenticated().toString());
        statsTextAreas.get(4).setText("Direction: " + getDirectionAsString(elevator.getDirection()));
        statsTextAreas.get(5).setText("User: " + user.getName());
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
