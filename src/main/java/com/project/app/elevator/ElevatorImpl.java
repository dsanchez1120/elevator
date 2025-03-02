package com.project.app.elevator;

import com.project.app.util.Direction;
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
    private Direction direction;
    private DoorStatus doorStatus;
    private Boolean authenticated;
    private int currentFloor;

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
            // Throwing exception as this indicates an error in the code
            throw new IllegalArgumentException("Invalid argument given");
        }
    }

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

    // FIXME Could be made private
    // FIXME Simplify Door Status to trinary system
        // FIXME:  1 = UP
        // FIXME: -1 = DOWN
        // FIXME:  0 = STATIONARY
    @Override
    public void moveCurrentFloor() {
        closeDoor();
        int moveDir = -1;
        if (direction.equals(Direction.STATIONARY)) {
            changeDirection();
        } else if (direction.equals(Direction.UP)) {
            moveDir = 1;
        }

        if (!findNextAvailableFloor(currentFloor, moveDir)) {
            // Switches direction of elevator and retries
            changeDirection();
            if (!findNextAvailableFloor(currentFloor, moveDir * -1)) {
                currentFloor = defaultFloor;
                changeDirection();
            }
        }
    }

    private boolean findNextAvailableFloor(int index, int dir) {
        if (index == floors.size() || index == -1) {
            return false;
        } else if (floorButtons.get(index)) {
            floorButtons.set(index, false);
            currentFloor = index;
            openDoor();
            return true;
        }
        else {
            return findNextAvailableFloor(index + dir, dir);
        }
    }

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
                (direction.equals(Direction.STATIONARY) && currentFloor <= defaultFloor))) {
            direction = Direction.UP;
        } else if (currentFloor == floors.size() - 1 || direction.equals(Direction.STATIONARY)) {
            direction = Direction.DOWN;
        } else if (direction.equals(Direction.DOWN)) {
            direction = Direction.UP;
        } else {
            direction = Direction.DOWN;
        }
    }

    private void callEmergencyServices() {
        direction = Direction.STATIONARY;
        closeDoor();
        Collections.fill(floorButtons, false);
    }

    private void deauthenticate() {
        authenticated = false;
        Collections.fill(authorizedFloors, false);
    }
}
