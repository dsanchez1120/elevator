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

    ArrayList<Boolean> boolArr0000 = new ArrayList<>(
            Arrays.asList(
                    false,
                    false,
                    false,
                    false));

    ArrayList<Boolean> boolArr0100 = new ArrayList<>(
            Arrays.asList(
                    false,
                    true,
                    false,
                    false));
    ArrayList<Boolean> boolArr0101 = new ArrayList<>(
            Arrays.asList(
                    false,
                    true,
                    false,
                    true));
    ArrayList<Boolean> boolArr0001 = new ArrayList<>(
            Arrays.asList(
                    false,
                    false,
                    false,
                    true));
    ArrayList<Boolean> boolArr1101 = new ArrayList<>(
            Arrays.asList(
                    true,
                    true,
                    false,
                    true));
    ArrayList<Boolean> boolArr1111 = new ArrayList<>(
            Arrays.asList(
                    true,
                    true,
                    true,
                    true));

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
                .defaultFloor(0)
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
                .authenticated(false)
                .currentFloor(0)
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Button Pressed With Valid Inputs Should Succeed")
    void buttonPressed_behavesProperlyWithValidInputs() {
        // Should add floor 2 to floorButtons array
        elevator.buttonPressed("2");
        Assertions.assertEquals(true, elevator.getFloorButtons().get(elevator.getFloors().indexOf("2")));
        // Should add floor "r" to floorButtons array
        elevator.setFloors(new ArrayList<>(
                Arrays.asList(
                        "1",
                        "2",
                        "3",
                        "R")));
        elevator.buttonPressed("R");
        Assertions.assertEquals(true, elevator.getFloorButtons().get(elevator.getFloors().indexOf("R")));

        // Door should open when "open" button is pressed
        elevator.buttonPressed("open");
        Assertions.assertEquals(DoorStatus.OPEN, elevator.getDoorStatus());

        // Close door is a placebo, door should stay open when "close" button is pressed
        elevator.buttonPressed("close");
        Assertions.assertEquals(DoorStatus.OPEN, elevator.getDoorStatus());

        // Door should close and elevator should be set to STATIONARY when emergency is pressed
        elevator.buttonPressed("emergency");
        Assertions.assertEquals(DoorStatus.CLOSED, elevator.getDoorStatus());
        Assertions.assertEquals(Direction.STATIONARY, elevator.getDirection());
    }

    @Test
    @DisplayName("Button Pressed With Invalid Inputs Should Fail")
    void buttonPressed_failsWithInvalidInputs() {
        Assertions.assertThrows(NullPointerException.class,
                () -> {
                    elevator.buttonPressed(null);
                }
        );
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    elevator.buttonPressed("bad input");
                })
        ;
    }

    @Test
    @DisplayName("Authenticate When SecurityType is NONE")
    void authenticate_whenSecurityTypeIsNone() {
        // Check for correct behavior if security is of type None
        // All users should be able to access all floors
        elevator.setSecurityType(SecurityType.NONE);
        elevator.authenticate("Joe", boolArr0101);
        Assertions.assertTrue(elevator.getAuthenticated());
        Assertions.assertEquals(elevator.getAuthorizedFloors(), boolArr1111);
        elevator.authenticate("Larry", boolArr1111);
        Assertions.assertTrue(elevator.getAuthenticated());
        Assertions.assertEquals(elevator.getAuthorizedFloors(), boolArr1111);
    }

    @Test
    @DisplayName("Authenticate When SecurityType is GENERAL")
    void authenticate_whenSecurityTypeIsGeneral() {
        // Check for correct behavior if security is of type General
        // Authorized users should recieve access to all floors
        // Unauthorized users should not be given any access
        elevator.setSecurityType(SecurityType.GENERAL);
        elevator.authenticate("Joe", boolArr0101);
        Assertions.assertTrue(elevator.getAuthenticated());
        Assertions.assertEquals(elevator.getAuthorizedFloors(), boolArr1111);
        elevator.authenticate("Larry", boolArr1111);
        Assertions.assertFalse(elevator.getAuthenticated());
        Assertions.assertNotEquals(elevator.getAuthorizedFloors(), boolArr1111);
    }

    @Test
    @DisplayName("Authenticate When SecurityType is SPECIFIED")
    void authenticate_whenSecurityTypeIsSpecified() {
        // Check for correct behavior if security is of type SPECIFIED
        // Authorized users should recieve access to their authorized floors
        // Unauthorized users should not be given any access
        elevator.setSecurityType(SecurityType.SPECIFIED);
        elevator.authenticate("Joe", boolArr0101);
        Assertions.assertTrue(elevator.getAuthenticated());
        Assertions.assertEquals(elevator.getAuthorizedFloors(), boolArr0101);
        elevator.authenticate("Larry", boolArr1111);
        Assertions.assertFalse(elevator.getAuthenticated());
        Assertions.assertNotEquals(elevator.getAuthorizedFloors(), boolArr1111);
    }

    @Test
    @DisplayName("Move Current Floor")
    void moveCurrentFloor() {
        elevator.setDirection(Direction.UP);
        elevator.setFloorButtons(boolArr0001);
        elevator.moveCurrentFloor();
        Assertions.assertEquals(3, elevator.getCurrentFloor());
        Assertions.assertEquals(false, elevator.getFloorButtons().get(3));

        elevator.setFloorButtons(boolArr0100);
        elevator.moveCurrentFloor();
        Assertions.assertEquals(1, elevator.getCurrentFloor());
        Assertions.assertEquals(false, elevator.getFloorButtons().get(1));

        elevator.moveCurrentFloor();
        Assertions.assertEquals(0, elevator.getCurrentFloor());
        Assertions.assertEquals(false, elevator.getFloorButtons().get(3));
    }

    @Test
    @DisplayName("Add Floor when Security Type is NONE")
    void addFloor_whenSecurityTypeIsNone() {
        elevator.setSecurityType(SecurityType.NONE);
        // If attempting to add current floor to floorButtons, doorStatus should be OPEN,
        // floorButtons at newFloor should be set to false
        elevator.setCurrentFloor(2);
        elevator.addFloor(2);
        Assertions.assertEquals(DoorStatus.OPEN, elevator.getDoorStatus());
        Assertions.assertEquals(false, elevator.getFloorButtons().get(2));
        // Should set floor 3 to true
        elevator.addFloor(3);
        Assertions.assertEquals(true, elevator.getFloorButtons().get(3));
        // Floor 3 should remain true in the event of a redundant call
        elevator.addFloor(3);
        Assertions.assertEquals(true, elevator.getFloorButtons().get(3));
    }

    @Test
    @DisplayName("Add Floor when Security Type is GENERAL")
    void addFloor_whenSecurityTypeIsGeneral() {
        elevator.setSecurityType(SecurityType.GENERAL);
        // User should not be allowed to use elevator from default floor if not authenticated
        elevator.setCurrentFloor(elevator.getDefaultFloor());
        elevator.setAuthenticated(false);
        elevator.addFloor(2);
        Assertions.assertFalse(elevator.getFloorButtons().get(2));

        // User should be allowed to use elevator from default floor if authenticated
        // Authenticated should be set to false
        elevator.setAuthenticated(true);
        elevator.addFloor(2);
        Assertions.assertTrue(elevator.getFloorButtons().get(2));
        Assertions.assertFalse(elevator.getAuthenticated());

        // User should be allowed to use elevator from non-default floor if not authenticated
        elevator.setAuthenticated(false);
        elevator.setCurrentFloor(2);
        elevator.addFloor(1);
        elevator.addFloor(3);
        Assertions.assertTrue(elevator.getFloorButtons().get(1));
        Assertions.assertTrue(elevator.getFloorButtons().get(3));

        // User should be allowed to use elevator from non-default floor if authenticated (even though authentication would be unnecessary)
        elevator.setAuthenticated(true);
        elevator.setCurrentFloor(1);
        elevator.setFloorButtons(boolArr0100);
        elevator.addFloor(2);
        elevator.addFloor(3);
        Assertions.assertTrue(elevator.getFloorButtons().get(2));
        Assertions.assertTrue(elevator.getFloorButtons().get(3));
    }

    @Test
    @DisplayName("Add Floor when Security Type is SPECIFIED")
    void addFloor_whenSecurityTypeIsSpecified() {
        elevator.setSecurityType(SecurityType.SPECIFIED);
        elevator.setAuthorizedFloors(boolArr0101);

        // User should not be allowed to use elevator if not authenticated
        elevator.setCurrentFloor(0);
        elevator.setAuthenticated(false);
        elevator.addFloor(2);
        Assertions.assertFalse(elevator.getFloorButtons().get(2));

        // Authenticated user should not be allowed to access unauthorized floors
        elevator.setAuthenticated(true);
        elevator.addFloor(2);
        Assertions.assertFalse(elevator.getFloorButtons().get(2));

        // Authenticated user should be allowed to access authorized floors
        elevator.setAuthenticated(true);
        elevator.addFloor(1);
        Assertions.assertTrue(elevator.getFloorButtons().get(1));
        Assertions.assertFalse(elevator.getAuthenticated());
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

        // On SPECIFIED security, user can only access authorized floors (agnostic of current floor)
        elevator.setSecurityType(SecurityType.SPECIFIED);
        elevator.setCurrentFloor(1);
        elevator.setAuthorizedFloors(boolArr1101);
        elevator.setAuthenticated(true);
        Assertions.assertFalse(elevator.checkSecurity(2));
        Assertions.assertTrue(elevator.checkSecurity(3));
        elevator.setCurrentFloor(2);
        Assertions.assertTrue(elevator.checkSecurity(3));
        Assertions.assertFalse(elevator.checkSecurity(2));
        elevator.setAuthenticated(false);
        Assertions.assertFalse(elevator.checkSecurity(2));
        elevator.setCurrentFloor(3);
        Assertions.assertFalse(elevator.checkSecurity(3));
    }

    @Test
    @DisplayName("Change Direction")
    void changeDirection() {
        elevator.setDirection(Direction.STATIONARY);
        // Changes direction to UP if on default floor or lowest floor
        elevator.changeDirection();
        Assertions.assertEquals(Direction.UP, elevator.getDirection());
        elevator.setDirection(Direction.DOWN);
        elevator.setCurrentFloor(0);
        elevator.changeDirection();
        Assertions.assertEquals(Direction.UP, elevator.getDirection());
        // Direction stays UP if on minimum floor
        elevator.changeDirection();
        Assertions.assertEquals(Direction.UP, elevator.getDirection());
        // Direction swtiches from UP TO DOWN
        elevator.setCurrentFloor(2);
        elevator.changeDirection();
        Assertions.assertEquals(Direction.DOWN, elevator.getDirection());
        // Directions switches to DOWN if stationary and above default floor
        elevator.setDirection(Direction.STATIONARY);
        elevator.changeDirection();
        Assertions.assertEquals(Direction.DOWN, elevator.getDirection());
        // Direction switches from DOWN to UP
        elevator.changeDirection();
        Assertions.assertEquals(Direction.UP, elevator.getDirection());
    }

    @Test
    @DisplayName("Call Emergency Services")
    void callEmergencyServices() {
        elevator.setCurrentFloor(3);
        elevator.setDirection(Direction.UP);
        elevator.setDoorStatus(DoorStatus.OPEN);
        elevator.setFloorButtons(boolArr0101);
        // Calling emergency services should reset floorButtons, direction to STATIONARY, and doorStatus to CLOSED
        elevator.callEmergencyServices();
        Assertions.assertEquals(Direction.STATIONARY, elevator.getDirection());
        Assertions.assertEquals(DoorStatus.CLOSED, elevator.getDoorStatus());
        Assertions.assertEquals(boolArr0000, elevator.getFloorButtons());
    }
}