package com.project.app.elevator;

import com.project.app.util.FloorDirection;

import java.util.ArrayList;

/**
 * Interface that controls the Elevator and Operations
 */
public interface Elevator {

    /**
     * Simulates an elevator call-button being pressed. Handles user input based on floor and direction
     * @param floor int index of the floor
     * @param callDir FloorDirection direction being requested
     * @throws IllegalArgumentException Thrown for out-of-bounds index, requesting BOTH for any floor,
     *      DOWN on bottom floor, or UP on top floor
     * @throws NullPointerException Thrown if {@code callDir} is null
     */
    void callButtonPressed(int floor, FloorDirection callDir);

    /**
     * Handles interior elevator button being pressed. Calls proper method based on parameter.
     * @param button String that corresponds to elevator button
     * @throws IllegalArgumentException if invalid argument is given
     */
    void interiorButtonPressed(String button);

    /**
     * Allows certain users to access specific floors based on elevator security.
     * @param card String containing user info.
     * @param floors ArrayList of floors that user can access.
     */
    void authenticate(String card, ArrayList<Boolean> floors);

    /**
     * Simulates an elevator moving between floors.
     */
    void moveCurrentFloor();

}
