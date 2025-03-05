package com.project.app.simulation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.app.elevator.ElevatorImpl;
import com.project.app.util.DoorStatus;
import com.project.app.util.FloorDirection;
import com.project.app.util.SecurityType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Simulation {
    // UI Constants
    private final int FRAME_X_DIMENSION = 600;
    private final int FRAME_Y_DIMENSION = 600;

    // UI Variables
    private static UI frame;

    // General Variables
    private ElevatorImpl elevator;
    private User user;
    private String simulationName;
    private int numFloors;

    public Simulation(String elevatorName, String userName) throws IOException {
//        this.elevator = buildElevator(elevatorName);
//        this.user = buildUser(userName);

        frame = new UI(simulationName, FRAME_X_DIMENSION, FRAME_Y_DIMENSION);

        frame.setVisible(true);

    }

    private ElevatorImpl buildElevator(String elevatorName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File("config/elevator/" + elevatorName));
        simulationName = jsonNode.get("ElevatorName").asText();
        numFloors = jsonNode.get("numFloors").asInt();
        return ElevatorImpl.builder()
                .floors(new ArrayList<>(
                            objectMapper.readValue(jsonNode.get("floors").toString(),
                            new TypeReference<List<String>>() {})))
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
