package com.project.app.elevator;

import com.project.app.util.DoorStatus;
import com.project.app.util.FloorDirection;
import com.project.app.util.SecurityType;
import lombok.Builder;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    private final String PATH_TO_LOGS = "logs/app.log";

    private static final Logger logger = LogManager.getLogger(ElevatorImpl.class);

    /**
     * Simulates an elevator call-button being pressed. Handles user input based on floor and direction
     * @param floor int index of the floor
     * @param callDir FloorDirection direction being requested
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
     * Handles interior elevator button being pressed. Calls proper method based on parameter.
     * @param button String that corresponds to elevator button
     * @throws IllegalArgumentException if invalid argument is given
     */
    @Override
    public void interiorButtonPressed(String button) {
        logger.info("Interior Button: {} pressed", button);
        if (floors.contains(button)) {
            // Floor button pressed. Adds floor
            addFloor(floors.indexOf(button));
        } else if (button.equals("open")) {
            openDoor();
        } else if (button.equals("close")) {
            closeDoor();
        } else if (button.equals("emergency")) {
            callEmergencyServices();
        } else {
            // This block of code should not be reached.
            logger.error("Invalid argument given");
            throw new IllegalArgumentException("Invalid argument given");
        }
    }

    /**
     * Allows certain users to access specific floors based on elevator security.
     * @param card String containing user info.
     * @param floors ArrayList of floors that user can access.
     */
    @Override
    public void authenticate(String card, ArrayList<Boolean> floors) {
        logger.info("Attempting authentication for user:{}", card);
        // User is authenticated based on elevator's authorizedUsers ArrayList and securityType
        authenticated = securityType.authenticate(authorizedUsers.contains(card));
        if (securityType.equals(SecurityType.SPECIFIED) && authenticated) {
            // User can access specified floors if authenticated and security is SPECIFIED
            authorizedFloors = floors;
            logger.info("User Authenticated. Access to specific floors granted");
        } else {
            // User can access all or no floors based on if authentication succeeds.
            logger.info(authenticated ? "User Authenticated. Can access all floor" : "User Unknown. Access Denied");
            Collections.fill(authorizedFloors, authenticated);
        }
    }

    /**
     * Simulates an elevator moving between floors.
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
        /*
         * Performs up to 3 recursive searches. If a requested floor is found, moves to that floor.
         * First search checks for requested floors in current direction starting at currentFloor index
         * Second search checks for requested floors in opposite direction starting at min. or max. index
         *      depending on current direction.
         * Third search repeats the second search, but flips direction and starting index.
         * If no requested floors are found, elevator is set to default floor.
         */

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
        } else if (floorsToVisit.get(index).shouldStop(direction)) {
            // Updates floorDirection based on current status, updates currentFloor, and opens door
            floorsToVisit.set(index, floorsToVisit.get(index).visitFloor(direction));
            logger.info("Floor found. Moving elevator to floor:{}", index);
            currentFloor = index;
            openDoor();
            return true;
        }
        else {
            return findNextAvailableFloor(index + direction);
        }
    }

    // Called by interiorButtonPressed method. If authorized, requested floor (newFloor) will be added to floorButtons
    private void addFloor(int newFloor) {
        if (checkSecurity(newFloor)) {
            logger.info("User authorized");
            if (currentFloor != newFloor) {
                logger.info("Requesting Floor:{}", newFloor);
                floorsToVisit.set(newFloor, chooseFloorDirection(newFloor));
                deauthenticate();
            } else {
                logger.info("User attempted to request current floor.");
                openDoor();
            }
        }
    }

    // Helper method for addFloor. Determines which direction should be associated with floor
    private FloorDirection chooseFloorDirection(int newFloor) {
        FloorDirection dirInRelationToCurrentFloor = (currentFloor < newFloor) ? FloorDirection.UP : FloorDirection.DOWN;
        return floorsToVisit.get(newFloor).chooseFloorDirection(dirInRelationToCurrentFloor);
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

    // Returns maintenance logs as a list
    public List<String> getMaintenanceLogs() {
        try {
            return Files.readAllLines(Paths.get(PATH_TO_LOGS));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return new ArrayList<>(List.of("File could not be read"));
    }

    public void clearMaintenanceLogs() {
        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(PATH_TO_LOGS));
            writer.write("");
            writer.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    // Checks if user is authorized to access desiredFloor based on security.
    public boolean checkSecurity(int desiredFloor) {
        logger.info("Checking user authorization");
        boolean authorized = securityType.isAuthorized(
                authenticated,
                currentFloor > defaultFloor,
                authorizedFloors.get(desiredFloor));
        logger.info("User authorization {}", authorized ? "succeeded" : "failed");
        return authorized;
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

    // Simulates calling emergency services. Clears floor requests, sets direction to stationary, closes door
    private void callEmergencyServices() {
        direction = 0;
        closeDoor();
        Collections.fill(floorsToVisit, FloorDirection.NONE);
    }

    private void deauthenticate() {
        logger.info("User deauthenticated");
        authenticated = false;
        Collections.fill(authorizedFloors, false);
    }
}
