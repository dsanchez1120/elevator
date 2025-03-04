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
import java.util.Collections;
import java.util.List;

public class UI extends JFrame {
    // Constants
    private final int FRAME_X_DIMENSION = 600;
    private final int FRAME_Y_DIMENSION = 600;

    // Swing Components
    JFrame frame;
    JPanel elevatorPanel;
    JPanel statsPanel;
    JPanel controlPanel;
    JLabel elevatorLabel;
    JLabel statsLabel;
    JLabel controlLabel;

    // Elevator Variables
    private ElevatorImpl elevator;
    private User user;
    private String simulationName;
    private int numFloors;

    UI(String name, int X_DIM, int Y_DIM) {
        // Create and Configure Frame
        setTitle(name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(X_DIM, Y_DIM);
        setLayout(new BorderLayout());


        // Elevator Panel
        elevatorPanel = new JPanel();
        elevatorLabel = new JLabel("Elevator");
        elevatorPanel.setBackground(Color.blue);
        elevatorPanel.add(elevatorLabel, BorderLayout.NORTH);
        add(elevatorPanel, BorderLayout.CENTER);

        // Stats Panel
        statsPanel = new JPanel();
        statsLabel = new JLabel("Elevator Stats");
        statsPanel.setBackground(Color.red);
        statsPanel.add(statsLabel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.SOUTH);

        // Control Pale
        controlPanel = new JPanel();
        controlLabel = new JLabel("Control Panel");
        controlPanel.setBackground(Color.green);
        controlPanel.add(controlLabel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.EAST);
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
