# Design Document
This is the design document for the elevator application.

## General
**Basic:** An elevator brings a user from one floor to a different, user-specified floor.

**Detailed:**

1. User calls elevator using **up** or **down** button 
2. Elevator goes to user floor 
3. Elevator opens door, user enters 
4. User authenticates (usually through RFID card) if required 
5. One or more users presses button indicating which floor elevator should go to, or another button (see specifics)
6. Elevator door closes if not already closed 
7. Elevator moves in the direction in which it was called 
8. Elevator stops if it reaches a floor for which a button was pressed or somebody on that floor calls the elevator (in the direction in which the elevator was moving).
9. Elevator repeats steps 5-7 until no buttons have been pressed
10. Elevator moves to floor using the opposite direction if it has been called, or waits to be called.

## Specifics
### Elevator Buttons
- **Call Buttons:** 1-2 per floor. Indicates to application that elevator should stop at that floor if direction of elevator equals call button direction
- **Floor Number:** 1-**n**, where **n** is the number of floors. Inside of elevator, indicates which floor elevator should go to.
- **Open Door**: A. Opens elevator door if elevator is stopped and closed, keeps the elevator door open if already open
- **Close Door**: C. As with the majority of US elevators, this button is nothing more than a placebo.
- **Emergency Call**: E. Calls emergency services in case of emergency. Elevator stops at current floor, does not open door.
### Security
Some elevators, usually in office-buildings, require an RFID card/chip to use elevator (from ground-floor).
This application will have 3 options for elevators
- **No security:** Anyone can access any floor from any floor.
- **General Security:** Elevator restricts movement from ground-floor unless RFID chip has been scanned.
- **Specified Security:** Restricts movement from ground-floor unless RFID chip has been scanned. Restricts movement based on permissions


## Application Structure
### config/users
Contains json files that represent a human user.
Json objects contains the following
- **Building**: String. The building that this user corresponds to
- **Card**: boolean. Does the user have an access card?
- **Access**: List of Strings. List of floor names user has access to
### config/buildings
Contains json files that represent a "building" that houses the elevator.
Json objects contains the following
- **Number of floors**: Integer. The number of floors that the elevator is able to access
- **Floor names**: List of Strings. List of floor names (G1, M, 5, etc.)
- **Main floor**: Integer. The default floor of the elevator
- **Type of Security**: String. Described in Specifics/Security
### src/.../Main
#### Main.java
- **Global Variables**
  - Elevator. Elevator. The elevator object used by the simulation
  - Simulation. Simulation. The simulation object that handles interactions with the elevator
- **Methods**
  - Main: 
    - No arguments
    - Returns void
    - Called when human user runs application. Calls other methods that run application.
  - Setup:
    - No arguments
    - Returns void
    - Called by main. Sets up simulation and elevator based on human input
  - Run:
    - No arguments
    - Returns void
    - Called by main. Runs simulation.
### src/.../Simulation
#### Simulation.java
*Will be added later after work on elevator is completed
- **Global Variables**
- **Methods**
### src/.../Elevator
#### Elevator.java
- **Global Variables**
  - floors: Array of strings. Contains a list of string representations for the elevator floors. 
    - ex. ```["L", "2", "3", "4"]```
  - currentFloor: Integer. The index of the current floor.
  - defaultFloor: Integer. "default" floor 
    - ex. ```L``
  - securityType: SecurityType. enum indicating the type of security used by the elevator
  - direction: Integer. Indicates the current direction of the elevator
    - ```-1``` = Elevator is going down
    - ```0``` = Elevator is stationary
    - ```1``` = Elevator is going up
  - doorStatus: DoorStatus. enum indicating if the door is open or closed. 
  - floorsToVisit: Array of FloorDirection. Each index corresponds with ```floors```. Indicates if elevator should stop at floor.
  - authenticated: boolean. Indicates if the next floor-related button press is allowed.
  - authorizedUsers: Array of strings. Indicates which users can be authenticated
  - authorizedFloors: Array of booleans. Indicates which floors the most recently authenticated user is able to access.
- **Methods**
  - moveCurrentFloor
    - No arguments
    - Returns void
    - Sets ```currentFloor``` to next available floor, based on checking ```floorsToVisit```.
  - interiorButtonPressed
    - button:string
    - Returns void
    - Simulates an elevator button being pressed on the inside.
      - Calls functions based on ```button``` input.
        - Calls ```addFloor``` if ```button``` corresponds to a floor
        - Calls ```openDoor``` if ```button``` is "A"
        - Calls ```closeDoor``` if ```button``` is "C"
        - Calls ```callEmergencyServices``` if ```button```is "E"
    - Throws: ```IllegalArgumentException``` if an invalid argument is given
  - callButtonPressed
    - Integer:floor, FloorDirection callDir
    - Returns void
    - Simulates a button on the exterior of the elevator being pressed
      - Sets ```floorsToVisit``` at ```floor``` index to proper ```FloorDirection``` value
  - authenticate
    - card:string, floors:Array of booleans
    - Returns void
    - Checks ```card``` against ```authorizedUsers```. If there's a match, sets ```authorized``` to ```true``` and sets ```authorizedFloors``` to ```floors```.
  - addFloor
    - newFloor:integer
    - Returns void
    - Sets ```floorButtons``` at ```newFloor``` index to ```true```.
    - If ```newFloor``` equals ```currentFloor```, doesn't update ```floorsToVisit```, sets ```doorStatus``` to ```OPEN```.
  - openDoor
    - No arguments
    - Returns void
    - Sets doorStatus to ```DoorStatus.OPEN``` if not already that.
  - closeDoor
    - No arguments
    - Returns void
    - Sets doorStatus to ```DoorStatus.CLOSED``` if not already that.
  - checkSecurity
    - desiredFloor: int
    - Returns boolean
    - Returns ```true``` if ```securityType``` is ```NONE```, ```desiredFloor``` is in ```authorizedFloors```, or user is allowed to access ```desiredFloor```
  - changeDirection
    - No arguments
    - Returns void
    - Changes Direction to ```UP``` or ```DOWN```, depending on what it currently is. 
      - ```STATIONARY``` will change direction towards ```defaultFloor``` or ```UP``` if on default floor 
  - callEmergencyServices
    - No arguments
    - Returns void
    - Sets every item in ```floorButtons``` to ```false```, ```direction``` to ```STATIONARY```, ```doorStatus``` to ```CLOSED```.
### src/.../Util
#### SecurityType.enum
- NONE
  - No security. Any user can access any floor from any floor
- GENERAL
  - Users must be authorized to access any floor from ```defaultFloor```. 
  - Users not on ```defaultFloor``` can access any other floor
- SPECIFIED
  - User must be authorized to access only a specified list of floors. 
#### FloorDirection.enum
- NONE
  - Indicates that floor shouldn't be stopped up (hasn't been requested)
- UP
  - Floor should be stopped at when elevator is going up
- DOWN
  - Floor should be stopped at when elevator is going down
- BOTH
  - Floor should be stopped at when elevator is going either direction
#### DoorStatus.enum
- OPEN
- CLOSED

## Testing
### src/../Elevator
#### Test Cases
- Call Button Pressed With Valid Inputs Should Add Floors Correctly
- Call Button Pressed With Invalid Inputs Should Succeed
- Button Pressed With Valid Inputs Should Succeed
- Button Pressed With Invalid Inputs Should Fail
- Authenticate When SecurityType is NONE
- Authenticate When SecurityType is GENERAL
- Authenticate When SecurityType is SPECIFIED
- Move Current Floor
- Button Pressed Add Floor when Security Type is NONE
- Button Pressed Add Floor when Security Type is GENERAL
- Button Pressed Add Floor when Security Type is SPECIFIED
- Change Directions
- 
