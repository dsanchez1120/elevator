package com.project.app.elevator;

import lombok.Data;

import java.util.ArrayList;

/**
 * Class that controls the Elevator and Operations
 */
public interface Elevator {

    void buttonPressed(String button);

    void authenticate(String card, ArrayList<Boolean> floors);

    void moveCurrentFloor();

    void addFloor(int newFloor);

    void openDoor();

    void closeDoor();

    boolean checkSecurity(int desiredFloor);

    void changeDirection();

    void callEmergencyServices();

}
