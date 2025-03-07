package com.project.app.elevator;

import com.project.app.util.DoorStatus;
import com.project.app.util.FloorDirection;
import com.project.app.util.SecurityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;

class ElevatorTest {

    ArrayList<FloorDirection> arr0000 = new ArrayList<>(
            Arrays.asList(
                    FloorDirection.NONE,
                    FloorDirection.NONE,
                    FloorDirection.NONE,
                    FloorDirection.NONE));

    ArrayList<FloorDirection> arr0D00 = new ArrayList<>(
            Arrays.asList(
                    FloorDirection.NONE,
                    FloorDirection.DOWN,
                    FloorDirection.NONE,
                    FloorDirection.NONE));
    ArrayList<Boolean> boolArr0101 = new ArrayList<>(
            Arrays.asList(
                    false,
                    true,
                    false,
                    true));
    ArrayList<FloorDirection> arr000U = new ArrayList<>(
            Arrays.asList(
                    FloorDirection.NONE,
                    FloorDirection.NONE,
                    FloorDirection.NONE,
                    FloorDirection.UP));
    ArrayList<Boolean> boolArr1111 = new ArrayList<>(
            Arrays.asList(
                    true,
                    true,
                    true,
                    true));

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
                .floorsToVisit(new ArrayList<>(
                        Arrays.asList(
                                FloorDirection.NONE,
                                FloorDirection.NONE,
                                FloorDirection.NONE,
                                FloorDirection.NONE))
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
                .direction(0)
                .doorStatus(DoorStatus.CLOSED)
                .authenticated(false)
                .currentFloor(0)
                .build();
    }

    @Test
    @DisplayName("Call Button Pressed With Valid Inputs Should Add Floors Correctly")
    void callButtonPressed_addsFloorsCorrectlyWithValidInputs() {
        elevator.callButtonPressed(1, FloorDirection.UP);
        elevator.callButtonPressed(2, FloorDirection.DOWN);

        // Non-current floors should be added according to their call directions.
        Assertions.assertEquals(FloorDirection.UP, elevator.getFloorsToVisit().get(1));
        Assertions.assertEquals(FloorDirection.DOWN, elevator.getFloorsToVisit().get(2));

        // Doors should open if current floor is called, current floor should not be added
        elevator.callButtonPressed(0, FloorDirection.UP);
        Assertions.assertEquals(DoorStatus.OPEN, elevator.getDoorStatus());
        Assertions.assertEquals(FloorDirection.NONE, elevator.getFloorsToVisit().get(0));

        // Floor should be set to BOTH if DOWN and called UP or UP and called DOWN
        elevator.callButtonPressed(1, FloorDirection.DOWN);
        elevator.callButtonPressed(2, FloorDirection.UP);
        Assertions.assertEquals(FloorDirection.BOTH, elevator.getFloorsToVisit().get(1));
        Assertions.assertEquals(FloorDirection.BOTH, elevator.getFloorsToVisit().get(2));
    }

    @Test
    @DisplayName("Call Button Pressed With Invalid Inputs Should Succeed")
    void callButtonPressed_failsWithInvalidInputs() {
        Assertions.assertThrows(NullPointerException.class,
            () -> elevator.callButtonPressed(0, null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> elevator.callButtonPressed(-1, FloorDirection.UP));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> elevator.callButtonPressed(0, FloorDirection.DOWN));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> elevator.callButtonPressed(3, FloorDirection.UP));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> elevator.callButtonPressed(4, FloorDirection.DOWN));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> elevator.callButtonPressed(2, FloorDirection.BOTH));
    }

    @Test
    @DisplayName("Button Pressed With Valid Inputs Should Succeed")
    void interiorButtonPressed_behavesProperlyWithValidInputs() {
        // Should add floor 2 to floorButtons array
        elevator.interiorButtonPressed("2");
        Assertions.assertEquals(FloorDirection.UP, elevator.getFloorsToVisit().get(elevator.getFloors().indexOf("2")));
        // Should add floor "r" to floorButtons array
        elevator.setFloors(new ArrayList<>(
                Arrays.asList(
                        "1",
                        "2",
                        "3",
                        "R")));
        elevator.interiorButtonPressed("R");
        Assertions.assertEquals(FloorDirection.UP, elevator.getFloorsToVisit().get(elevator.getFloors().indexOf("R")));

        // Door should open when "open" button is pressed
        elevator.interiorButtonPressed("open");
        Assertions.assertEquals(DoorStatus.OPEN, elevator.getDoorStatus());

        // Door should close when "close" button is pressed
        elevator.interiorButtonPressed("close");
        Assertions.assertEquals(DoorStatus.CLOSED, elevator.getDoorStatus());

        // Door should close and elevator should be set to STATIONARY when emergency is pressed
        elevator.interiorButtonPressed("emergency");
        Assertions.assertEquals(DoorStatus.CLOSED, elevator.getDoorStatus());
        Assertions.assertEquals(0, elevator.getDirection());
        Assertions.assertEquals(arr0000, elevator.getFloorsToVisit());
    }

    @Test
    @DisplayName("Button Pressed With Invalid Inputs Should Fail")
    void interiorButtonPressed_failsWithInvalidInputs() {
        Assertions.assertThrows(NullPointerException.class,
                () -> elevator.interiorButtonPressed(null)
        );
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> elevator.interiorButtonPressed("bad input"))
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
    @DisplayName("Move Current Floor With Normal Inputs")
    void moveCurrentFloor_withNormalInputs() {
        elevator.setDirection(1);
        elevator.setFloorsToVisit(arr000U);
        elevator.moveCurrentFloor();
        Assertions.assertEquals(3, elevator.getCurrentFloor());
        Assertions.assertEquals(FloorDirection.NONE, elevator.getFloorsToVisit().get(3));
        Assertions.assertEquals(DoorStatus.OPEN, elevator.getDoorStatus());

        elevator.setFloorsToVisit(arr0D00);
        elevator.moveCurrentFloor();
        Assertions.assertEquals(1, elevator.getCurrentFloor());
        Assertions.assertEquals(FloorDirection.NONE, elevator.getFloorsToVisit().get(1));
        Assertions.assertEquals(DoorStatus.OPEN, elevator.getDoorStatus());

        elevator.moveCurrentFloor();
        Assertions.assertEquals(0, elevator.getCurrentFloor());
        Assertions.assertEquals(FloorDirection.NONE, elevator.getFloorsToVisit().get(3));
        Assertions.assertEquals(DoorStatus.CLOSED, elevator.getDoorStatus());
    }

    @Test@DisplayName("Move Current Floor When Call Button is Pressed")
    void moveCurrentFloor_whenCallButtonIsPressed() {
        elevator.callButtonPressed(2, FloorDirection.DOWN);
        elevator.moveCurrentFloor();
        Assertions.assertEquals(2, elevator.getCurrentFloor());
        Assertions.assertEquals(-1, elevator.getDirection());

        elevator.setCurrentFloor(0);
        elevator.setDirection(0);
        elevator.callButtonPressed(1, FloorDirection.DOWN);
        elevator.callButtonPressed(1, FloorDirection.UP);
        elevator.callButtonPressed(3, FloorDirection.DOWN);
        elevator.moveCurrentFloor();
        Assertions.assertEquals(1, elevator.getCurrentFloor());
        Assertions.assertEquals(FloorDirection.DOWN, elevator.getFloorsToVisit().get(1));
        elevator.moveCurrentFloor();
        Assertions.assertEquals(3, elevator.getCurrentFloor());
        elevator.moveCurrentFloor();
        Assertions.assertEquals(1, elevator.getCurrentFloor());

    }

    @Test
    @DisplayName("Button Pressed Add Floor when Security Type is NONE")
    void interiorButtonPressed_addFloorWhenSecurityTypeIsNone() {
        elevator.setSecurityType(SecurityType.NONE);
        // If attempting to add current floor to floorButtons, doorStatus should be OPEN,
        // floorButtons at newFloor should be set to NONE
        elevator.setCurrentFloor(1);
        elevator.interiorButtonPressed("2");
        Assertions.assertEquals(DoorStatus.OPEN, elevator.getDoorStatus());
        Assertions.assertEquals(FloorDirection.NONE, elevator.getFloorsToVisit().get(1));
        // Should set floor 3 to UP
        elevator.interiorButtonPressed("3");
        Assertions.assertEquals(FloorDirection.UP, elevator.getFloorsToVisit().get(2));
        // Floor 3 should remain UP in the event of a redundant call
        elevator.interiorButtonPressed("3");
        Assertions.assertEquals(FloorDirection.UP, elevator.getFloorsToVisit().get(2));
    }

    @Test
    @DisplayName("Button Pressed Add Floor when Security Type is GENERAL")
    void interiorButtonPressed_addFloorWhenSecurityTypeIsGeneral() {
        elevator.setSecurityType(SecurityType.GENERAL);
        // User should not be allowed to use elevator from default floor if not authenticated
        elevator.setCurrentFloor(elevator.getDefaultFloor());
        elevator.setAuthenticated(false);
        elevator.interiorButtonPressed("3");
        Assertions.assertEquals(FloorDirection.NONE, elevator.getFloorsToVisit().get(2));

        // User should not be allowed to use elevator if current floor is less than default floor and unauthenticated
        elevator.setDefaultFloor(1);
        elevator.setCurrentFloor(0);
        elevator.interiorButtonPressed("2");
        Assertions.assertEquals(FloorDirection.NONE, elevator.getFloorsToVisit().get(1));

        // User should be allowed to use elevator from below default floor if authenticated
        // Authenticated should be set to false
        elevator.setAuthenticated(true);
        elevator.interiorButtonPressed("2");
        Assertions.assertEquals(FloorDirection.UP, elevator.getFloorsToVisit().get(1));

        // User should be allowed to use elevator from default floor if authenticated
        // Authenticated should be set to false
        elevator.setDefaultFloor(0);
        elevator.setCurrentFloor(0);
        elevator.setFloorsToVisit(arr0000);
        elevator.setAuthenticated(true);
        elevator.interiorButtonPressed("3");
        Assertions.assertEquals(FloorDirection.UP, elevator.getFloorsToVisit().get(2));
        Assertions.assertFalse(elevator.getAuthenticated());

        // User should be allowed to use elevator from non-default floor if not authenticated
        elevator.setFloorsToVisit(arr0000);
        elevator.setAuthenticated(false);
        elevator.setCurrentFloor(2);
        elevator.interiorButtonPressed("2");
        elevator.interiorButtonPressed("4");
        Assertions.assertEquals(FloorDirection.DOWN, elevator.getFloorsToVisit().get(1));
        Assertions.assertEquals(FloorDirection.UP, elevator.getFloorsToVisit().get(3));

        // User should be allowed to use elevator from non-default floor if authenticated (even though authentication would be unnecessary)
        elevator.setFloorsToVisit(arr0000);
        elevator.setAuthenticated(true);
        elevator.setCurrentFloor(1);
        elevator.setFloorsToVisit(arr0D00);
        elevator.interiorButtonPressed("1");
        Assertions.assertEquals(FloorDirection.DOWN, elevator.getFloorsToVisit().get(0));
    }

    @Test
    @DisplayName("Button Pressed Add Floor when Security Type is SPECIFIED")
    void interiorButtonPressed_addFloorWhenSecurityTypeIsSpecified() {
        elevator.setSecurityType(SecurityType.SPECIFIED);
        elevator.setAuthorizedFloors(boolArr0101);

        // User should not be allowed to use elevator if not authenticated
        elevator.setCurrentFloor(0);
        elevator.setAuthenticated(false);
        elevator.interiorButtonPressed("2");
        Assertions.assertEquals(FloorDirection.NONE, elevator.getFloorsToVisit().get(2));

        // Authenticated user should not be allowed to access unauthorized floors
        elevator.setAuthenticated(true);
        elevator.interiorButtonPressed("3");
        Assertions.assertEquals(FloorDirection.NONE, elevator.getFloorsToVisit().get(2));

        // Authenticated user should be allowed to access authorized floors
        elevator.setAuthenticated(true);
        elevator.interiorButtonPressed("2");
        Assertions.assertEquals(FloorDirection.UP, elevator.getFloorsToVisit().get(1));
        Assertions.assertFalse(elevator.getAuthenticated());
    }

    @Test
    @DisplayName("Change Direction")
    void changeDirection() {
        elevator.setDirection(0);
        // Changes direction to UP if on default floor or lowest floor
        elevator.changeDirection();
        Assertions.assertEquals(1, elevator.getDirection());
        elevator.setDirection(-1);
        elevator.setCurrentFloor(0);
        elevator.changeDirection();
        Assertions.assertEquals(1, elevator.getDirection());
        // Direction stays UP if on minimum floor
        elevator.changeDirection();
        Assertions.assertEquals(1, elevator.getDirection());
        // Direction swtiches from UP TO DOWN
        elevator.setCurrentFloor(2);
        elevator.changeDirection();
        Assertions.assertEquals(-1, elevator.getDirection());
        // Directions switches to DOWN if stationary and above default floor
        elevator.setDirection(0);
        elevator.changeDirection();
        Assertions.assertEquals(-1, elevator.getDirection());
        // Direction switches from DOWN to UP
        elevator.changeDirection();
        Assertions.assertEquals(1, elevator.getDirection());
    }

}