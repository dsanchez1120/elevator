package com.project.app.elevator;

import com.project.app.util.DoorStatus;
import com.project.app.util.FloorDirection;
import com.project.app.util.SecurityType;
import lombok.Builder;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;

@Data
@Builder
public class ElevatorImpl implements Elevator {
    // Global Variables
    private ArrayList<String> floors;
    private SecurityType securityType;
    private int defaultFloor;
    private ArrayList<FloorDirection> floorsToVisit;
    private ArrayList<Boolean> authorizedFloors;
    private ArrayList<String> authorizedUsers;
    private DoorStatus doorStatus;
    private Boolean authenticated;
    private int direction;
    private int currentFloor;

    private static final Logger logger = LogManager.getLogger(ElevatorImpl.class);

    /**
     * Simulates an elevator call-button being pressed
     * Checks for invalid inputs (out-of-bounds index, requesting BOTH or null on any floor,
     *      DOWN on bottom floor, or UP on top floor)
     * Does nothing if requested floor already has the requested direction or is set to BOTH
     * Sets floor to requested direction if requested direction is NONE
     * Sets floor to BOTH if requested direction is opposite of current floor's direction.
     * @param floor int that corresponds to the index of the floor
     * @param callDir int that corresponds with the direction being requested
     * @throws IllegalArgumentException Thrown for out-of-bounds index, requesting BOTH for any floor,
     *      DOWN on bottom floor, or UP on top floor
     * @throws NullPointerException Thrown if {@code callDir} is null
     */
    @Override
    public void callButtonPressed(int floor, FloorDirection callDir) {
        // Check for invalid inputs - if these fail, it indicates an error in the code
        logger.info("Call Button Pressed. Floor:{}; Call Direction:{}", floor, callDir.toString());
        if ((floor == 0 && callDir.equals(FloorDirection.DOWN)) ||
                (floor == floorsToVisit.size() - 1 && callDir.equals(FloorDirection.UP)) ||
                (floor < 0 || floor >= floorsToVisit.size()) ||
                (callDir == FloorDirection.BOTH) || (callDir == FloorDirection.NONE)) {
            logger.error("Failed: Invalid Argument Provided");
            throw new IllegalArgumentException("Invalid Argument Given");
        } else if (callDir == null) {
            logger.error("Failed: Null Argument Given");
            throw new NullPointerException("FloorDirection argument is null");
        }

        if (floorsToVisit.get(floor).equals(callDir) || floorsToVisit.get(floor).equals(FloorDirection.BOTH)) {
            logger.info("Floor:{} already called in Direction:{}", floor, callDir.toString());
            return;
        } else if (floor == currentFloor) {
            logger.info("Floor:{} is current floor", floor);
            openDoor();
        } else if (floorsToVisit.get(floor) == FloorDirection.NONE) {
            logger.info("Floor:{} hasn't been requested. Requesting floor:{} in direction:{}", floor, floor, callDir.toString());
            floorsToVisit.set(floor, callDir);
        } else {
            logger.info("Floor:{} has been requested. Requesting floor:{} in direction:BOTH", floor, floor);
            floorsToVisit.set(floor, FloorDirection.BOTH);
        }
    }

    /**
     * Simulates an elevator button being pressed
     * If {@code button} corresponds with an item in {@code floors}, that floor is added to {@code floorButtons}
     * If {@code button} is {@code "open"}, opens elevator door
     * If {@code} button is {@code "code"}, nothing happens (as with a real elevator)
     * If {@code} button is {@code "emergency"}, simulates emergency actions (closes doors, stops elevator movement)
     * Otherwise throws an {@code IllegalArgumentException}
     * @param button String that corresponds to elevator button
     * @throws IllegalArgumentException if invalid argument is given
     */
    @Override
    public void interiorButtonPressed(String button) {
        logger.info("Interior Button: {} pressed", button);
        if (floors.contains(button)) {
            addFloor(floors.indexOf(button));
        } else if (button.equals("open")) {
            openDoor();
        } else if (button.equals("close")) {
            closeDoor();
        } else if (button.equals("emergency")) {
            callEmergencyServices();
        } else {
            logger.error("Invalid argument given");
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
        logger.info("Attempting authentication for user:{}", card);
        if (securityType.equals(SecurityType.NONE)) {
            logger.info("Elevator Security is NONE. User:{} is allowed access to all floors", card);
            authenticated = true;
            Collections.fill(authorizedFloors, true);
        } else if (authorizedUsers.contains(card)) {
            authenticated = true;
            logger.info("User:{} authenticated successfully", card);
            if (securityType.equals(SecurityType.SPECIFIED)) {
                authorizedFloors = floors;
                logger.info("Access to specific floors granted");
            } else {
                Collections.fill(authorizedFloors, true);
                logger.info("Access to all floors granted");
            }
        } else {
            authenticated = false;
            Collections.fill(authorizedFloors, false);
            logger.info("User: {} is not allowed to access elevator", card);
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
        logger.info("Attempting to move elevator.");
        closeDoor();
        // Changes elevator to move UP or DOWN if it is set to stationary.
        if (direction == 0) {
            logger.info("Elevator is STATIONARY");
            changeDirection();
        }

        logger.info("Searching for available floor in current direction");
        if (!findNextAvailableFloor(currentFloor)) {
            logger.info("Match not found in current direction");
            logger.info("Changing direction and checking from opposite end");
            direction = direction * -1;
            if (!findNextAvailableFloor(direction == -1 ? floors.size() - 1 : 0)) {
                // Change direction and check from opposite end
                logger.info("Match not found in opposite direction");
                logger.info("Changing direction and checking from opposite end");
                direction = direction * -1;
                if (!findNextAvailableFloor(direction == -1 ? floors.size() - 1 : 0)) {
                    // If no floors can be found in entire array, reset current floor and direction.
                    logger.info("No requested floors found. Resetting elevator to default floor and direction");
                    currentFloor = defaultFloor;
                    changeDirection();
                }
            }
        }
    }

    // Recursive helper method for moveCurrentFloor
    private boolean findNextAvailableFloor(int index) {
        if (index == floors.size() || index == -1) {
            return false;
        } else if (shouldStop(floorsToVisit.get(index))) {
            // If elevator has been requested from both directions, it should stop at floor on opposite direction visit
            if (floorsToVisit.get(index).equals(FloorDirection.BOTH)) {
                floorsToVisit.set(index, direction == 1 ? FloorDirection.DOWN : FloorDirection.UP);
            } else {
                floorsToVisit.set(index, FloorDirection.NONE);
            }
            logger.info("Floor found. Moving elevator to floor:{}", index);
            currentFloor = index;
            openDoor();
            return true;
        }
        else {
            return findNextAvailableFloor(index + direction);
        }
    }

    // Helper method for findNextAvailableFloor
    private boolean shouldStop(FloorDirection floorDir) {
        return switch (floorDir) {
            case NONE -> false;
            case UP -> direction == 1;
            case DOWN -> direction == -1;
            case BOTH -> true;
        };
    }

    // Called by interiorButtonPressed method. If authorized, requested floor (newFloor) will be added to floorButtons
    private void addFloor(int newFloor) {
        if (checkSecurity(newFloor)) {
            logger.info("User authorized");
            if (currentFloor != newFloor) {
                logger.info("Requesting Floor:{}", newFloor);
                floorsToVisit.set(newFloor, chooseFloorDirection(newFloor));
                logger.info("Deauthenticating user");
                deauthenticate();
            } else {
                logger.info("User attempted to request current floor.");
                openDoor();
            }
        }
    }

    // Helper method for addFloor. Determines which direction should be associated with floor
    private FloorDirection chooseFloorDirection(int newFloor) {
        FloorDirection dirInRelationToCurrentFloor = currentFloor < newFloor ? FloorDirection.UP : FloorDirection.DOWN;
        return switch (floorsToVisit.get(newFloor)) {
            // Floor will already be visited, no need to update
            case BOTH -> FloorDirection.BOTH;
            // Floor should be set to direction in relation to current floor
            case NONE -> dirInRelationToCurrentFloor;
            // If floor will already be visited on current direction then no update, else visit on both directions.
            case UP -> dirInRelationToCurrentFloor.equals(FloorDirection.UP) ? FloorDirection.UP : FloorDirection.BOTH;
            case DOWN -> dirInRelationToCurrentFloor.equals(FloorDirection.DOWN) ? FloorDirection.DOWN : FloorDirection.BOTH;
        };
    }

    private void openDoor() {
        if (!doorStatus.equals(DoorStatus.OPEN)) {
            logger.info("Opening Door");
            doorStatus = DoorStatus.OPEN;
        }
    }

    private void closeDoor() {
        if (!doorStatus.equals(DoorStatus.CLOSED)) {
            logger.info("Closing Door");
            doorStatus = DoorStatus.CLOSED;
        }
    }

    // Checks if user is authorized to access desiredFloor based on security.
    public boolean checkSecurity(int desiredFloor) {
        logger.info("Checking user authorization");
        return securityType.equals(SecurityType.NONE) ||
                (securityType.equals(SecurityType.GENERAL) && (authenticated || currentFloor > defaultFloor)) ||
                authenticated && authorizedFloors.get(desiredFloor);
    }


    protected void changeDirection() {
        logger.info("Changing elevator direction");
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
        Collections.fill(floorsToVisit, FloorDirection.NONE);
    }

    private void deauthenticate() {
        authenticated = false;
        Collections.fill(authorizedFloors, false);
    }
}
