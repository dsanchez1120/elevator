package com.project.app.elevator;

import com.project.app.util.Direction;
import com.project.app.util.DoorStatus;
import com.project.app.util.SecurityType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;

class ElevatorTest {

    @Mock
    private ElevatorImpl elevator;

    @BeforeEach
    void setUp() {
        elevator = ElevatorImpl.builder()
                .floors(new ArrayList<>(
                        Arrays.asList(
                                "1",
                                "2",
                                "3",
                                "4"))
                )
                .securityType(SecurityType.NONE)
                .defaultFloor(1)
                .floorButtons(new ArrayList<>(
                        Arrays.asList(
                                false,
                                false,
                                false,
                                false))
                )
                .authorizedFloors(new ArrayList<>(
                        Arrays.asList(
                                false,
                                false,
                                false,
                                false))
                )
                .authorizedUsers(new ArrayList<>(
                        Arrays.asList(
                                "Joe",
                                "Sarah"))
                )
                .direction(Direction.STATIONARY)
                .doorStatus(DoorStatus.CLOSED)
                .authenticated(true)
                .currentFloor(1)
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Button Pressed")
    void buttonPressed() {
        elevator.buttonPressed("2");
        Assertions.assertEquals(true, elevator.getFloorButtons().get(1));

        elevator.buttonPressed("C");
        Assertions.assertEquals(DoorStatus.CLOSED, elevator.getDoorStatus());

        elevator.buttonPressed("A");
        Assertions.assertEquals(DoorStatus.OPEN, elevator.getDoorStatus());

        elevator.buttonPressed("E");
        Assertions.assertEquals(DoorStatus.CLOSED, elevator.getDoorStatus());
        Assertions.assertEquals(Direction.STATIONARY, elevator.getDirection());

    }

    @Test
    @DisplayName("Authenticate")
    void authenticate() {
        ArrayList<Boolean> arr1 = new ArrayList<>(
                Arrays.asList(
                        false,
                        true,
                        false,
                        true));
        ArrayList<Boolean> arr2 = new ArrayList<>(
                Arrays.asList(
                        true,
                        true,
                        true,
                        true));

        // Check for correct behavior if security is of type None
        // All users should be able to access all floors
        elevator.setSecurityType(SecurityType.NONE);
        elevator.authenticate("Joe", arr1);
        Assertions.assertTrue(elevator.getAuthenticated());
        Assertions.assertEquals(elevator.getAuthorizedFloors(), arr2);
        elevator.authenticate("Larry", arr2);
        Assertions.assertTrue(elevator.getAuthenticated());
        Assertions.assertEquals(elevator.getAuthorizedFloors(), arr2);

        // Check for correct behavior if security is of type General
        // Authorized users should recieve access to all floors
        // Unauthorized users should not be given any access
        elevator.setSecurityType(SecurityType.GENERAL);
        elevator.authenticate("Joe", arr1);
        Assertions.assertTrue(elevator.getAuthenticated());
        Assertions.assertEquals(elevator.getAuthorizedFloors(), arr2);
        elevator.authenticate("Larry", arr2);
        Assertions.assertFalse(elevator.getAuthenticated());
        Assertions.assertNotEquals(elevator.getAuthorizedFloors(), arr2);

        // Check for correct behavior if security is of type SPECIFIED
        // Authorized users should recieve access to their authorized floors
        // Unauthorized users should not be given any access
        elevator.setSecurityType(SecurityType.SPECIFIED);
        elevator.authenticate("Joe", arr1);
        Assertions.assertTrue(elevator.getAuthenticated());
        Assertions.assertEquals(elevator.getAuthorizedFloors(), arr1);
        elevator.authenticate("Larry", arr2);
        Assertions.assertFalse(elevator.getAuthenticated());
        Assertions.assertNotEquals(elevator.getAuthorizedFloors(), arr2);
    }

    @Test
    @DisplayName("Move Current Floor")
    void moveCurrentFloor() {
        elevator.setDirection(Direction.UP);
        elevator.setFloorButtons(new ArrayList<>(
                Arrays.asList(
                        false,
                        false,
                        false,
                        true)));
        elevator.moveCurrentFloor();
        Assertions.assertEquals(4, elevator.getCurrentFloor());
        Assertions.assertEquals(false, elevator.getFloorButtons().get(3));

        elevator.setFloorButtons(new ArrayList<>(
                Arrays.asList(
                        false,
                        true,
                        false,
                        false)));
        elevator.moveCurrentFloor();
        Assertions.assertEquals(2, elevator.getCurrentFloor());
        Assertions.assertEquals(false, elevator.getFloorButtons().get(1));

        elevator.moveCurrentFloor();
        Assertions.assertEquals(0, elevator.getCurrentFloor());
        Assertions.assertEquals(false, elevator.getFloorButtons().get(3));
    }

    @Test
    @DisplayName("Add Floor")
    void addFloor() {
        elevator.setCurrentFloor(2);
        elevator.addFloor(2);
        Assertions.assertEquals(false, elevator.getFloorButtons().get(1));
        elevator.addFloor(3);
        Assertions.assertEquals(true, elevator.getFloorButtons().get(2));
        elevator.addFloor(4);
        Assertions.assertEquals(true, elevator.getFloorButtons().get(3));
    }

    @Test
    @DisplayName("Open Door")
    void openDoor() {
        elevator.setDoorStatus(DoorStatus.CLOSED);
        elevator.openDoor();
        Assertions.assertEquals(DoorStatus.OPEN, elevator.getDoorStatus());
        elevator.openDoor();
        Assertions.assertEquals(DoorStatus.OPEN, elevator.getDoorStatus());
    }

    @Test
    @DisplayName("Close Door")
    void closeDoor() {
        elevator.setDoorStatus(DoorStatus.OPEN);
        elevator.closeDoor();
        Assertions.assertEquals(DoorStatus.CLOSED, elevator.getDoorStatus());
        elevator.closeDoor();
        Assertions.assertEquals(DoorStatus.CLOSED, elevator.getDoorStatus());
    }

    @Test
    @DisplayName("Check Security")
    void checkSecurity() {
        ArrayList<Boolean> arr1 = new ArrayList<>(
                Arrays.asList(
                        true,
                        true,
                        false,
                        true));

        // On NONE security, user should be able to access any floor
        elevator.setSecurityType(SecurityType.NONE);
        Assertions.assertTrue(elevator.checkSecurity(2));

        // On GENERAL security, user can only access floors 2-n if authenticated.
        // If current floor is 2-n, authentication not required.
        elevator.setSecurityType(SecurityType.GENERAL);
        elevator.setAuthenticated(true);
        Assertions.assertTrue(elevator.checkSecurity(3));
        elevator.setAuthenticated(false);
        Assertions.assertFalse(elevator.checkSecurity(3));
        elevator.setCurrentFloor(3);
        Assertions.assertTrue(elevator.checkSecurity(2));

        // On SPECIFIED security, user can only access authorized floors
        elevator.setSecurityType(SecurityType.SPECIFIED);
        elevator.setCurrentFloor(1);
        elevator.setAuthorizedFloors(arr1);
        elevator.setAuthenticated(true);
        Assertions.assertTrue(elevator.checkSecurity(2));
        Assertions.assertFalse(elevator.checkSecurity(3));
        elevator.setCurrentFloor(2);
        Assertions.assertTrue(elevator.checkSecurity(4));
        Assertions.assertFalse(elevator.checkSecurity(3));
        elevator.setAuthenticated(false);
        Assertions.assertFalse(elevator.checkSecurity(3));
        elevator.setCurrentFloor(3);
        Assertions.assertFalse(elevator.checkSecurity(2));
    }

    @Test
    @DisplayName("Change Direction")
    void changeDirection() {
        elevator.setDirection(Direction.STATIONARY);
        // Changes direction to UP if on default floor
        elevator.changeDirection();
        Assertions.assertEquals(Direction.UP, elevator.getDirection());
        // Direction stays UP if on minimum floor
        elevator.changeDirection();
        Assertions.assertEquals(Direction.UP, elevator.getDirection());
        // Direction swtiches from UP TO DOWN
        elevator.setCurrentFloor(4);
        elevator.changeDirection();
        Assertions.assertEquals(Direction.DOWN, elevator.getDirection());
        // Direction switches DOWN if not on default floor
        elevator.setDirection(Direction.STATIONARY);
        Assertions.assertEquals(Direction.DOWN, elevator.getDirection());
        // Direction switches from DOWN to UP
        elevator.changeDirection();
        Assertions.assertEquals(Direction.UP, elevator.getDirection());
    }

    @Test
    @DisplayName("Call Emergency Services")
    void callEmergencyServices() {
        ArrayList<Boolean> arr1 = new ArrayList<>(
                Arrays.asList(
                        false,
                        true,
                        false,
                        true));
        ArrayList<Boolean> arr2 = new ArrayList<>(
                Arrays.asList(
                        false,
                        false,
                        false,
                        false));
        elevator.setCurrentFloor(3);
        elevator.setDirection(Direction.UP);
        elevator.setDoorStatus(DoorStatus.OPEN);
        elevator.setFloorButtons(arr1);
        // Calling emergency services should reset floorButtons, direction to STATIONARY, and close door
        elevator.callEmergencyServices();
        Assertions.assertEquals(Direction.STATIONARY, elevator.getDirection());
        Assertions.assertEquals(DoorStatus.CLOSED, elevator.getDoorStatus());
        Assertions.assertEquals(arr2, elevator.getFloorButtons());
    }
}