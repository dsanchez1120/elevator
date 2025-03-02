package com.project.app.elevator;

import com.project.app.util.DoorStatus;
import com.project.app.util.SecurityType;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;

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
    private DoorStatus doorStatus;
    private Boolean authenticated;
    private int direction;
    private int currentFloor;

    /**
     * Simulates an elevator button being pressed
     * If {@code button} corresponds with an item in {@code floors}, that floor is added to {@code floorButtons}
     * If {@code button} is {@code "open"}, opens elevator door
     * If {@code} button is {@code "code"}, nothing happens (as with a real elevator)
     * If {@code} button is {@code "emergency"}, simulates emergency actions (closes doors, stops elevator movement)
     * Otherwise throws an {@code IllegalArgumentException}
     * @param button String that corresponds to elevator button
     */
    @Override
    public void buttonPressed(String button) {
        if (floors.contains(button)) {
            addFloor(floors.indexOf(button));
        } else if (button.equals("open")) {
            openDoor();
        } else if (button.equals("close")) {
            System.out.println("Placebo-button pressed. User wonders why the door is taking a few seconds to close");
        } else if (button.equals("emergency")) {
            callEmergencyServices();
        } else {
            throw new IllegalArgumentException("Invalid argument given");
        }
    }

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
    @Override
    public void authenticate(String card, ArrayList<Boolean> floors) {
        if (securityType.equals(SecurityType.NONE)) {
            authenticated = true;
            Collections.fill(authorizedFloors, true);
        }
        else if (authorizedUsers.contains(card)) {
            authenticated = true;
            if (securityType.equals(SecurityType.SPECIFIED)) {
                authorizedFloors = floors;
            } else {
                Collections.fill(authorizedFloors, true);
            }
        } else {
            authenticated = false;
            Collections.fill(authorizedFloors, false);
        }
    }

    /**
     * Simulates an elevator moving between floors.
     * Closes elevator doors and according to {@code direction} global variable, recursively chooses next floor based on
     * {@code floorButtons} global variable. If no floor in that direction has been requested, the opposite direction
     * will be recursively searched. In either case, if a requested floor is found, {@code currentFloor} global variable
     * is set to the proper index and elevator doors are opened. If no requested floor is found, elevator moves to
     * {@code defaultFloor} (global variable).
     */
    @Override
    public void moveCurrentFloor() {
        closeDoor();
        // Changes elevator to move UP or DOWN if it is set to stationary.
        if (direction == 0) {
            changeDirection();
        }
        if (!findNextAvailableFloor(currentFloor)) {
            changeDirection();
            if (!findNextAvailableFloor(currentFloor)) {
                currentFloor = defaultFloor;
                changeDirection();
            }
        }
    }
    // Recursive helper method for moveCurrentFloor
    private boolean findNextAvailableFloor(int index) {
        if (index == floors.size() || index == -1) {
            return false;
        } else if (floorButtons.get(index)) {
            floorButtons.set(index, false);
            currentFloor = index;
            openDoor();
            return true;
        }
        else {
            return findNextAvailableFloor(index + direction);
        }
    }

    // Called by buttonPressed method. If authorized, requested floor (newFloor) will be added to floorButtons
    private void addFloor(int newFloor) {
        if (checkSecurity(newFloor)) {
            if (currentFloor != newFloor) {
                floorButtons.set(newFloor, true);
                deauthenticate();
            } else {
                floorButtons.set(newFloor, false);
                openDoor();
            }
        }
    }

    private void openDoor() {
        if (!doorStatus.equals(DoorStatus.OPEN)) {
            doorStatus = DoorStatus.OPEN;
        }
    }

    private void closeDoor() {
        if (!doorStatus.equals(DoorStatus.CLOSED)) {
            doorStatus = DoorStatus.CLOSED;
        }
    }

    private boolean checkSecurity(int desiredFloor) {
        return securityType.equals(SecurityType.NONE) ||
                (securityType.equals(SecurityType.GENERAL) && (authenticated || currentFloor != defaultFloor)) ||
                authenticated && authorizedFloors.get(desiredFloor);
    }

    protected void changeDirection() {
        if (((currentFloor == 0) ||
                (direction == 0 && currentFloor <= defaultFloor))) {
            direction = 1;
        } else if (currentFloor == floors.size() - 1 || direction == 0) {
            direction = -1;
        } else {
            direction = direction * -1;
        }
    }

    private void callEmergencyServices() {
        direction = 0;
        closeDoor();
        Collections.fill(floorButtons, false);
    }

    private void deauthenticate() {
        authenticated = false;
        Collections.fill(authorizedFloors, false);
    }
}
