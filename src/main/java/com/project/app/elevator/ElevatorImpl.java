package com.project.app.elevator;

import com.project.app.util.Direction;
import com.project.app.util.DoorStatus;
import com.project.app.util.SecurityType;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder
public class ElevatorImpl implements Elevator {
    // Global Variables
    private ArrayList<String> floors;
    private SecurityType securityType;
    private int defaultFloor;

    private ArrayList<Boolean> floorButtons;
    private ArrayList<Boolean> authorizedFloors;
    private ArrayList<String> authorizedUsers;
    private Direction direction;
    private DoorStatus doorStatus;
    private Boolean authenticated;
    private int currentFloor;

    @Override
    public void buttonPressed(String button) {

    }

    @Override
    public void authenticate(String card, ArrayList<Boolean> floors) {

    }

    @Override
    public void moveCurrentFloor() {}

    @Override
    public void addFloor(int newFloor) {}

    @Override
    public void openDoor() {}

    @Override
    public void closeDoor() {}

    @Override
    public boolean checkSecurity(int desiredFloor) {
        return false;
    }

    @Override
    public void changeDirection() {}

    @Override
    public void callEmergencyServices() {}

}
