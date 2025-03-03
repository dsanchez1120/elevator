package com.project.app.elevator;

import com.project.app.util.FloorDirection;

import java.util.ArrayList;

/**
 * Interface that controls the Elevator and Operations
 */
public interface Elevator {

    /**
     * Simulates an elevator call-button being pressed
     * @param floor int that corresponds to the index of the floor
     * @param callDir int that corresponds with the direction being requested
     */
    void callButtonPressed(int floor, FloorDirection callDir);

    /**
     * Simulates an elevator button being pressed from the inside
     * If {@code button} corresponds with an item in {@code floors}, that floor is added to {@code floorButtons}
     * If {@code button} is {@code "open"}, opens elevator door
     * If {@code} button is {@code "code"}, nothing happens (as with a real elevator)
     * If {@code} button is {@code "emergency"}, simulates emergency actions (closes doors, stops elevator movement)
     * Otherwise throws an {@code IllegalArgumentException}
     * @param button String that corresponds to elevator button
     */
    void interiorButtonPressed(String button);

    /**
     * Simulates an elevator authentication system.
     * If elevator has no security system, {@code SecurityType.NONE}, user is redundantly allowed to access all floors
     * If elevator has general security, {@code SecurityType.GENERAL},
     *      user is allowed to access all floors if {@code card} is in {@code authorizedUsers}
     * If elevator has specific security, {@code SecurityType.SPECIFIC},
     *      user is allowed to access floors in {@code floors} argument if {@code card} is in {@code authorizedUsers}
     * If elevator security is not {@code SecurityType.NONE}, and {@code card} is not in {@code authorizedUsers}
     *      user is not allowed to access any floors.
     * @param card Simulates swiping an RFID card. Contains a string that is checked against {@code authorizedUsers}
     * @param floors The floors that the user is allowed to access. Structure of array matches {@code floorButtons} global array.
     */
    void authenticate(String card, ArrayList<Boolean> floors);

    /**
     * Simulates an elevator moving between floors.
     * Closes elevator doors and according to {@code direction} global variable, recursively chooses next floor based on
     * {@code floorButtons} global variable. If no floor in that direction has been requested, the opposite direction
     * will be recursively searched. In either case, if a requested floor is found, {@code currentFloor} global variable
     * is set to the proper index and elevator doors are opened. If no requested floor is found, elevator moves to
     * {@code defaultFloor} (global variable).
     */
    void moveCurrentFloor();

}
