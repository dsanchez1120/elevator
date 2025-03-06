package com.project.app.simulation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.app.elevator.ElevatorImpl;
import com.project.app.util.DoorStatus;
import com.project.app.util.FloorDirection;
import com.project.app.util.SecurityType;

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
    private final String ELEVATOR_CONFIG_DIRECTORY_PATH = "config/elevator/";
    private final String USERS_CONFIG_DIRECTORY_PATH = "config/users/";
    private final String ELEVATOR_SIMULATION_TITLE = "Elevator Simulation";

    // Swing Components
    static JPanel elevatorPanel;
    static JLabel elevatorLabel;
    static JPanel elevatorPanelCenter;
    static ArrayList<JTextArea> floorSquares;
    static ArrayList<JButton> downButtons;
    static ArrayList<JButton> upButtons;

    static JPanel statsPanel;
    static JLabel statsLabel;
    static JTextArea allowedFloorsTA;
    static JPanel statsPanelCenter;
    static ArrayList<JTextArea> statsTextAreas;

    static JPanel controlPanel;
    static JPanel controlPanelCenter;
    static ArrayList<JButton> floorButtons;
    static JButton openDoorButton;
    static JButton emergencyButton;
    static JButton closeDoorButton;
    static JPanel controlPanelSouth;
    static JLabel controlLabel;

    // Elevator Variables
    private ElevatorImpl elevator;
    private User user;
    private String simulationName;
    private int numFloors;

    UI(int X_DIM, int Y_DIM) throws IOException {
        // Create and Configure Frame
        setTitle(ELEVATOR_SIMULATION_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(X_DIM, Y_DIM);
        setLayout(new BorderLayout());

        elevator = buildElevator(elevatorSetup());
        user = buildUser(userSetup());

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
        statsLabel = new JLabel(simulationName, SwingConstants.CENTER);
        statsPanel.setBackground(Color.LIGHT_GRAY);
        statsPanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        statsPanelCenter = new JPanel(new GridLayout(2, 3));
        initializeStatsTextAreas();
        for (JTextArea ta: statsTextAreas) {
            statsPanelCenter.add(ta);
        }
        allowedFloorsTA = new JTextArea("Allowed Floors: " + getAllowedFloorAsString());
        allowedFloorsTA.setBackground(Color.gray);
        allowedFloorsTA.setAlignmentX(0.5f);
        allowedFloorsTA.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        statsPanel.add(statsLabel, BorderLayout.NORTH);
        statsPanel.add(allowedFloorsTA, BorderLayout.SOUTH);
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
        floorSquares = new ArrayList<>();
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
                    if (elevator.checkSecurity(elevator.getFloors().indexOf(e.getActionCommand()))) {
                        elevator.interiorButtonPressed(e.getActionCommand());
                        updateFloorButtons(elevator.getFloors().indexOf(e.getActionCommand()));
                    }
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
                updateFloorSquares();
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
        // Initialize Buttons
        JButton moveFloorButton = new JButton("Move Floor");
        JButton changeUserButton = new JButton("Change User");
        JButton authorizeButton = new JButton("Authenticate");
        JButton changeElevatorButton = new JButton("Change Elevator");

        // Adds functionality to buttons
        moveFloorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elevator.moveCurrentFloor();
                updateFloorSquares();
                updateStats();
                updateFloorButtons(-1);
                updateUpCallButtons(-1);
                updateDownCallButtons(-1);
            }
        });
        changeUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    user = buildUser(userSetup());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                allowedFloorsTA.setText("Allowed Floors: " + getAllowedFloorAsString());
                updateStats();
            }
        });
        authorizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elevator.authenticate(user.getName(), (ArrayList<Boolean>) user.getAuthorizedFloors().clone());
                updateStats();
            }
        });
        changeElevatorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    elevator = buildElevator(elevatorSetup());
                    user = buildUser(userSetup());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                allowedFloorsTA.setText("Allowed Floors: " + getAllowedFloorAsString());
                updateStats();
                elevatorReset();
            }
        });

        // Add Buttons to Frame
        controlPanelSouth.add(moveFloorButton);
        controlPanelSouth.add(changeUserButton);
        controlPanelSouth.add(authorizeButton);
        controlPanelSouth.add(changeElevatorButton);
    }

    private void initializeStatsTextAreas() {
        statsTextAreas = new ArrayList<>(
                Arrays.asList(
                        new JTextArea("Security: " + elevator.getSecurityType().toString()),
                        new JTextArea("Current Floor: " + (elevator.getFloors().get(elevator.getCurrentFloor()))),
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

    private void updateFloorSquares() {
        for (int i = 0; i < numFloors; i++) {
            if (i == elevator.getCurrentFloor()) {
                floorSquares.get(i).setBackground(Color.YELLOW);
            } else {
                floorSquares.get(i).setBackground(Color.gray);
            }
        }
    }

    private void updateUpCallButtons(int index) {
        for (int i = 0; i < numFloors - 1; i++) {
            if (elevator.getFloorsToVisit().get(i).equals(FloorDirection.NONE) ||
                    elevator.getFloorsToVisit().get(i).equals(FloorDirection.DOWN)) {
                upButtons.get(i).setBackground(new Button().getBackground());
            } else if (i == index) {
                upButtons.get(i).setBackground(Color.YELLOW);
            }
        }
    }

    private void updateDownCallButtons(int index) {
        for (int i = 1; i < numFloors; i++) {
            if (elevator.getFloorsToVisit().get(i).equals(FloorDirection.NONE) ||
                    elevator.getFloorsToVisit().get(i).equals(FloorDirection.UP)) {
                downButtons.get(i).setBackground(new Button().getBackground());
            } else if (i == index) {
                downButtons.get(i).setBackground(Color.YELLOW);
            }
        }
    }

    private void updateFloorButtons(int index) {
        for (int i = 0; i < numFloors; i++) {
            if (elevator.getFloorsToVisit().get(i).equals(FloorDirection.NONE) ||
                    i == elevator.getCurrentFloor()) {
                floorButtons.get(i).setBackground(new JButton().getBackground());
            } else if (i == index) {
                floorButtons.get(i).setBackground(Color.YELLOW);
            }
        }
    }

    private void elevatorReset() {
        elevatorPanel.removeAll();
        initializeElevatorPanel();
        controlPanel.removeAll();
        initializeControlPanel();
        updateFloorSquares();
        revalidate();
    }

    private String elevatorSetup() {
        File elevatorDir = new File(ELEVATOR_CONFIG_DIRECTORY_PATH);
        File[] elevatorFilesUntransformed = elevatorDir.listFiles(
                pathname -> pathname.toString().endsWith(".json"));
        Object[] elevatorFiles = Arrays.stream(elevatorFilesUntransformed)
                .map(f -> f.toString().substring(
                        ELEVATOR_CONFIG_DIRECTORY_PATH.length(),
                        f.toString().length() - 5))
                .toArray();
        Object elevatorChoice = JOptionPane.showInputDialog(
                this,
                "Choose",
                "Menu",
                JOptionPane.PLAIN_MESSAGE,
                null,
                elevatorFiles,
                elevatorFiles[0]);
        return elevatorChoice.toString();
    }

    private String userSetup() {
        File userDir = new File(USERS_CONFIG_DIRECTORY_PATH + numFloors);
        File[] userFilesUntransformed = userDir.listFiles(
                pathname -> pathname.toString().endsWith(".json"));
        Object[] userFiles = Arrays.stream(userFilesUntransformed)
                .map(f -> f.toString().substring(
                        USERS_CONFIG_DIRECTORY_PATH.length() + 2,
                        f.toString().length() - 5))
                .toArray();
        Object userChoice = JOptionPane.showInputDialog(
                this,
                "Choose",
                "Menu",
                JOptionPane.PLAIN_MESSAGE,
                null,
                userFiles,
                userFiles[0]);
        return userChoice.toString();
    }

    private void updateStats() {
        statsTextAreas.get(0).setText("Security: " + elevator.getSecurityType().toString());
        statsTextAreas.get(1).setText("Current Floor: " + (elevator.getFloors().get(elevator.getCurrentFloor())));
        statsTextAreas.get(2).setText("Doors: " + elevator.getDoorStatus().toString());
        statsTextAreas.get(3).setText("Authentication: " + elevator.getAuthenticated().toString());
        statsTextAreas.get(4).setText("Direction: " + getDirectionAsString(elevator.getDirection()));
        statsTextAreas.get(5).setText("User: " + user.getName());
    }

    private String getAllowedFloorAsString() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < numFloors; i++) {
            if (user.getAuthorizedFloors().get(i)) {
                output.append(i == numFloors - 1 ? elevator.getFloors().get(i) : (elevator.getFloors().get(i) + ", "));
            }
        }
        return output.toString();
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
        JsonNode jsonNode = objectMapper.readTree(new File("config/elevator/" + elevatorName + ".json"));
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
        JsonNode jsonNode = objectMapper.readTree(new File("config/users/" + numFloors + "/" + userName + ".json"));
        return User.builder()
                .name(jsonNode.get("name").asText())
                .authorizedFloors(new ArrayList<>(
                        objectMapper.readValue(jsonNode.get("allowedFloors").toString(),
                                new TypeReference<java.util.List<Boolean>>() {})
                ))
                .build();
    }
}
