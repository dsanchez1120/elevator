# Documentation
This documents the various classes that make up the elevator application, information about the UI, assumptions made, and features that weren't implemented

## Assumptions
This application assumes the following
- There is only one elevator car, rather than a system with multiple cars
- Elevator exterior call buttons are a simple "up" or "down" button on each floor
- Elevator interior button panel has the standard floor, open/close door, and emergency buttons
- Elevator should **eventually** stop at all requested floors
- Elevator should stop at floors according to pre-set rules and act consistently

## Unimplemented Features
- This application is for a one-elevator-car system. Many elevators have one computer system and many elevator cars. 
  If I had more time, I would have separated the Elevator object into a Computer object which contained multiple elevator
  objects. The computer would have logic to determine where to send elevators when idling, and which elevator to send 
  for a given user request.
- Various safety features. Detection of too much weight on elevator, handling users jumping on elevator, etc.
- Unit Tests for the UI. Writing JUnit tests for Java Swing Objects can be more complicated, and I only had time to write them for the elevator class.

## UI Guide
_Note: The specific methods used/called by the UI wil be explained in other sections. I will link to them wherever possible_

### Starting the UI
Executing the .jar file should start the UI. The UI will prompt the user to select [an elevator](#configelevators) and [a user.](#configusers)
Once these are selected, the main UI will be displayed.

### Information Section (Top)
This shows information about the elevator.
- Elevator Security Type (see [elevator security enum](#srcutilsecuritytypeenum))
- User Authentication (see [elevator authentication methods](#srcelevatorelevatorimpljava))
- Current elevator floor
- Current elevator direction
- Door open/closed status (see [door status enum](#srcutildoorstatusenum))
- Current user (see [user configuration](#configusers))
- Allowed Floors (see [user configuration](#configusers))

This information is updated when most UI actions are performed

### Elevator Section (Left)
This section displays a visual representation of the elevator and up/down buttons for each floor.
See the [section on the elevator object](#srcelevatorelevatorimpljava) for more information.

**The elevator**

The elevator representation is shown on the left. Each rectangle represents a floor of the building and contains a label for that floor.
If the elevator is on that floor, the rectangle will be yellow, otherwise it will be grey.

**Elevator call buttons**

Next to the elevator there are up and down buttons for each floor that represent the up and down buttons that request an elevator.
When an up or down button is pressed, the background will turn yellow, indicating that the floor will be visited in that direction.
When the elevator reaches that floor (in the requested direction) the background will turn back to the default grey.

### Control Panel (Right)
See the [section on the elevator object](#srcelevatorelevatorimpljava) for more information on buttons

This section displays the interior elevator buttons and other buttons that allow the user to control the simulation.

Each button does the following
- **Floor Buttons** These buttons correspond with the elevator floors and simulate the floor buttons on the inside of an elevator.
Pressing one of these buttons will turn its background yellow (until that floor is visited) to indicate that it is a requested floor.
- **Close Door Button** Closes the elevator door. Will be indicated in the information section
- **Open Door Button** Opens the elevator door. Will be indicated in the information section
- **Emergency Button** Simulates the emergency button in an elevator.
In this simulation, it sets elevator to stationary, clears requested floors, and closes the door.
- **Move Floor** Moves the elevator to the next available floor. Logic is explained in the [section on the elevator object](#srcelevatorelevatorimpljava)
- **Authenticate** Authenticates the selected user, allowing them to access floors depending on elevator security. 
See the [section on SecurityType](#securitytypeenum) for more information
- **Change User** Allows you to change the current elevator user. See the section on [user configuration](#configusers)
- **Change Elevator** Allows you to change the type of elevator being simulated. See the section on [elevator configuration](#configelevators)

### Maintenance Buttons (Bottom)
This section simulates a maintenance worker checking elevator logs. There are two buttons in this section
- **Show Maintenance Logs**: Displays the maintenance logs, which function as a history of actions taken by the user.
- **Clear Maintenance Logs**: Clears the maintenance logs. This is permanent.

## Configuration Files
This project contains multiple configuration files that are used by the UI.
These files set up elevators with different parameters and users with different permissions.

I've included explanations of every file to use as a reference guide for using the UI
### config/elevators/
JSON files that are used to create different elevator objects within the program

Each JSON object contains the following
- **Elevator Name**: The name of the elevator to be displayed by the UI (ex. "Elevator with 5 Floors")
- **Floors**: A list containing the names of the elevator's floors (ex. ```["L", "2", "3", "4", "5"]```)
- **Security Type**: The level of security used by the elevator. See [section on SecurityType](#securitytypeenum)
- **Default Floor**: Integer indicating the index of the elevator's default floor.
- **Authorized Users**: List containing the names of authorized users (ex. ```["Joe", "Sam"]```)
- **Current Floor**: Integer indicating the floor that the elevator will start on
- **Number of Floor**: Integer indicating the number of floors on the elevator.

The elevator files have the following naming scheme ```<SecurityType>_<Number of floors>.json```
Thus, ```none_8.json``` has ```SecurityType.NONE``` and 8 floors.

When the UI is started, you will select from the list of available elevator config files. This can be changed later in the program.

### config/users/
JSON files that represent a human user. Used by the UI exclusively.

JSON objects contains the following
- **Name**: String. Name of the user
- **allowedFloors**: Array of Booleans. Corresponds with elevator floors and [will only be relevant for](#securitytypeenum) ```SecurityType.SPECIFIED``` 
  - ```true``` if user can access the floor at that index
  - ```false``` if user can't access the floor at that index

There are four options for users that can be selected
- **Jeff**: No access to any floors. Is not on any elevator authorizedUsers list
- **Joe**: Can access all but second-to-last floor. On every elevator's authorizedUsers list
- **Sam**: Can access every-other floor. On every elevator's authorizedUsers list
- **Sarah**: Can access the lowest floor and default floors (these are the same if there are 4 or 5 floors)
and the highest floor.

**Note:** All users will be able to access the elevator if Security is NONE. 
Jeff will not be allowed access if Security is GENERAL or Specified

In the users directory, there are 3 sub-directories named with an integer indicating a number of elevator floors. 
The UI will show options for selecting a user that correspond with the number of floors on the elevator. 

A user is selected during the UI initialization.

## Main Code Files
### src/.../Main.java
**General**
The main class is called when the application is run. It creates a new simulation Object which handles the UI
**Methods**
  - Main: 
    - No arguments
    - Returns void
    - Called when human user runs application. Calls setup
  - Setup:
    - No arguments
    - Returns void
    - Called by main. Creates new Simulation object which sets up the UI
    - 
### src/.../Simulation/Simulation.java
Creates the application UI by creating a UI object and setting it to be visible by the User.
### src/.../Simulation/UI.java
The UI of the application that will be used to visualize the elevator object and demonstrate its functionality

**Global Variables**
- _Note: Not all global variables are included for the sake of readability. Most are components of the UI such as panels, buttons, and text areas_
- **Constants**: Title to be displayed on UI and paths to [elevator](#configelevators) and [users](#configusers) config directories
- **elevator**: Contains the ElevatorImpl object that is used throughout the UI. See [section on elevator object](#srcelevator)
- **user**: Contains the User object that is used when calling elevator methods. See [section on user object](#userjava)

**Methods**
- _Note: For the sake of readability, not all methods will be discussed. 
Most configure the graphics and behavior of the UI components. Comments within the methods will explain more specifics_
- **Constructor**: Calls methods that create the elevator and user objects, as well as methods that initialize the UI components.
- **buildElevator** and **buildUser**: Initializes the elevator and user objects
- **elevatorSetup** and **userSetup**: Allows changing elevator and user objects while UI is running.

### src/.../Simulation/User.java
Object representing a user of the elevator.
Global variables match the [user config file](#configusers)
- **Name**: String. Name of the user
- **allowedFloors**: Array of Booleans. Corresponds with elevator floors and [will only be relevant for](#securitytypeenum) ```SecurityType.SPECIFIED```
  - ```true``` if user can access the floor at that index
  - ```false``` if user can't access the floor at that index

### src/.../Elevator/ElevatorImpl.java
Object that represents the elevator.
Implements Elevator interface.

**Global Variables**
  - **floors**: ArrayList of strings. Contains a list of string representations for the elevator floors. 
    - ex. ```["L", "2", "3", "4"]```
  - **currentFloor**: Integer. The index of the current floor.
  - **defaultFloor**: Integer. The index of the default floor 
    - ex. ```L``
  - **securityType**: SecurityType. enum indicating the type of security used by the elevator. [See SecurityType enum](#securitytypeenum)
  - **direction**: Integer. Indicates the current direction of the elevator
    - ```-1``` = Elevator is going down
    - ```0``` = Elevator is stationary
    - ```1``` = Elevator is going up
  - **doorStatus**: DoorStatus. enum indicating if the door is open or closed. 
  - **floorsToVisit**: ArrayList of FloorDirection. Each index corresponds with ```floors```. 
  Indicates if the elevator should stop at that floor, and under what circumstances. [See FloorDirection enum](#srcutilfloordirectionenum)
  - **authenticated**: boolean. Indicates if the next floor-related button press is allowed.
  - **authorizedUsers**: ArrayList of strings. Indicates which users can be authenticated. 
  - **authorizedFloors**: ArrayList of booleans. Indicates which floors the most recently authenticated user is able to access.
  - **logger**: Logs important elevator actions. Can be viewed by in UI using "Show Maintenance Logs" button

**Methods**
- **callButtonPressed**
  - Simulates an up or down button being pressed on the outside of an elevator to call it to that floor.
  - Takes ```int floor``` and ```FloorDirection callDir``` as arguments
  - If arguments are valid and floor has not already been called in that direction. Updates ```floorsToVisit```
- **interiorButtonPressed**
  - Simulates an elevator button being pressed on the inside of the elevator. 
  - Takes ```String button``` as argument
  - This method will behave differently, depending on the argument, as there are different types of buttons
    - Floor Button: if ```button``` is in the ```floors``` arrayList, calls ```addFloor``` method with the index of requested floor.
    - Open Door Button: If ```button``` is ```open```, calls ```openDoor()``` method.
    - Close Door Button: If ```button``` is ```close```, calls ```closeDoor()``` method.
    - Emergency Button: If ```button``` is ```emergency```, calls ```callEmergencyServices()``` method.
  - If ```button``` is invalid, throws an ```IllegalArgumentException```
- **authenticate**
  - Simulates swiping an RFID card to authenticate before selecting a floor in an elevator.
  - Takes ```String card``` and ```ArrayList<Boolean> floors``` as arguments.
    - ```card``` is the name of the user which is checked against the ```authorizedUsers``` ArrayList.
    - ```floors``` is an ArrayList of floors that the user is allowed to access if authenticated
  - This method behaves differently depending on elevator ```SecurityType```
    - ```NONE```. Any user will be authenticated and allowed to access all floors.
    - ```GENERAL```. Users in ```authorizedUsers``` will be given access to all floors, other uses will be given no access.
    - ```SPECIFIED```. Users in ```authorizedUsers``` will be given access to the floors specified in ```floors``` argument
- **moveCurrentFloor**
  - Simulates an elevator moving between floors.
  - No arguments
  - Performs up 3 recursive searches to find the floor that the elevator should move to. 
  If a search succeeds, ```currentFloor``` is set to found index and subsequent searches will not be run
    - The first search is in the direction of the elevator. It checks for floors that match the current direction that are in the path of the current direction.
    - The second search checks in the opposite direction, beginning from the opposite end of the elevator.
    - The final search checks in the original direction, beginning at the opposite end of the elevator from step 2.
    - If no match is found (no floors have been requested), elevator will move to the default floor.
    - Ex. Elevator has 5 floors, is "going up", and on floor 3. 
      The first search checks floors 4 and 5 to see if they have been requested in the UP (or BOTH) direction. Search fails.
      The second search begins at floor 5 and checks if floors 5 - 1 (descending order) have been requested in the DOWN direction. Search fails.
      The final search begins at floor 1 and checks if floor 1 or 2 have been requested in the up direction. 
      Floor 2 was requested in the UP direction. Sets current floor to floor 2, direction stays "going up".
    - The search method is complicated, but ensures that the elevator will move in a consistent and fair direction, without "forgetting" certain floors.
  - Uses helper method ```findNextAvailableFloor``` to implement the above logic.
- **addFloor**
  - Private method called by ```interiorButtonPressed```
  - Takes ```int newFloor``` as argument. ```newFloor``` is the index of the requested floor
  - If ```newFloor``` is not allowed to accessed, does nothing.
  - If ```newFloor``` is allowed to be accessed, updates ```floorsToVisit``` with ```FloorDirection``` 
    returned by helper method ```chooseFloorDirection```
  - If ```newFloor``` is the current floor, opens the door.
- **chooseFloorDirection**
  - private method called by **addFloor**
  - takes ```int newFloor``` as argument. ```newFloor``` is the index of the requested floor
  - Returns ```FloorDirection``` enum based on the position of the requested floor with respect to the current floor.
  - If ```newFloor``` was requested in the opposite direction, it returns ```FloorDirection.BOTH```, 
    so that it will be visited when going up and going down.
    - Ex. If ```newFloor``` is above ```currentFloor``` and was requested going down. Returns ```FloorDirection.BOTH```
    - Ex. If ```newFloor``` is below ```currentFloor``` and has not been requested. Returns ```FloorDirection.DOWN```
- **openDoor**
  - Simulates pressing the elevator "open door" button.
  - Sets ```doorStatus``` enum to ```DoorStatus.OPEN```.
- **closeDoor**
  - Simulates pressing the elevator "close door" button.
  - Sets ```doorStatus``` enum to ```DoorStatus.CLOSE```.
  - _Side Note: I originally planned to have this button do nothing, as roughly 80% of elevator close door buttons are placebos_
- **getMaintenanceLogs**
  - Simulates a technician getting logs from elevator computer system if, for example, an error had occurred.
  - Used by UI to display logs that are stored in ```logs/app.log``` file.
  - Returns ```List<String>``` containing all logs generated.
- **clearmaintenanceLogs**
  - Clears the maintenance logs.
  - Called by UI during initialization to clear the logs created during unit testing.
  - Can be called by human user in UI
- **checkSecurity**
  - Called by ```addFloor``` and used during UI
  - Takes ```int desiredFloor``` argument which is an index of floor to check.
  - Returns a boolean indicating if ```desiredFloor``` is able to be accessed.
- **changeDirection**
  - Simulates an elevator changing directions. 
  - Direction changes according to the following conditions
    - If elevator is on lowest floor or is stationary and below the default floor. Sets elevator to going up (```direction == 1``)
    - Else if elevator is on highest floor or stationary. Sets direction to going down (```direction == -1``)
    - Else flips elevator direction (going down -> going up, going up -> going down).
- **callEmergencyServices**
  - Simulates pressing the emergency button in an elevator.
  - Closes elevator door, sets direction to 0 (stationary), and clears all floor requests
- **deauthenticate**
  - Removes authentication after floor has been requested
  - Sets ```authenticated``` to false
  - Sets all ```authorizedFloors``` booleans to false
### src/.../Util/SecurityType.enum
Enum object that indicates the elevator's security systems. Contains a few helper methods.

**Enum Constants**
- NONE
  - No security. Any user can access any floor from any floor
- GENERAL
  - Users must be authorized to access any floor from ```defaultFloor```. 
  - Users not on ```defaultFloor``` can access any other floor
- SPECIFIED
  - User must be authorized to access only a specified list of floors. 

**Enum Methods**
  - **authenticate**
    - Returns boolean indicating if a floor can be accessed depending on enum Constant
    - Takes ```boolean authorized``` as argument, which is ```true``` if user is allowed to access a floor, ```false``` if not.
    - If enum is ```NONE```, always returns true (any user can access any floor).
    - If enum is ```GENERAL``` OR ```SPECIFIED``` returns true if ```authorized``` is true.
    - This method was added to simplify ```ElevatorImpl``` object and clean up if/else statements.
  - **isAuthorized**
    - Verifies if requested floor is able to be accessed given the SecurityType and parameters
    - Takes 3 booleans as arguments
      - ```authenticated```: true if user is authenticated
      - ```aboveDefault``` : true if requested floor is above default floor
      - ```floorAuthorized```: true if user is authorized to access requested floor
    - Returns ```true``` if floor is able to be accessed, ```false``` if not. This is determined based on enum Constant
      - If enum is ```NONE```. Always returns ```true```
      - If enum is ```GENERAL```. Returns ```true``` if user is authenticated, or current floor is above default floor
      - If enum is ```SPECIFIED```. Returns ```true``` if user is authenticated and allowed to access requested floor.
    - This method was added to simplify ```ElevatorImpl``` object and clean up if/else statements.
### src/.../Util/FloorDirection.enum
Enum object that indicates at which directions the elevator should stop at a floor. Contains a few helper methods.

**Enum Constants**
- NONE
  - Indicates that floor shouldn't be visited (hasn't been requested)
- UP
  - Elevator should stop at floor when going up
- DOWN
  - Elevator should stop at floor when going down
- BOTH
  - Elevator should stop at floor when going up or going down (floor has been requested from both directions).
    
**Enum Methods**
- **visitFloor**
  - Determines what ```FloorDirection``` should be set to after being visited
  - Takes ```int direction``` as argument which is the current direction of the elevator
  - Returns ```NONE``` if enum is ```NONE```, ```UP```, or ```DOWN```
  - Returns ```UP``` or ```DOWN``` if enum is ```BOTH``` depending on ```direction``` argument.
  - The idea is that if the elevator is visiting a floor that has been requested in both directions, 
    it should stop a second time when going the opposite direction
  - This method was added to simplify ```ElevatorImpl``` object and clean up if/else statements.
- **chooseFloorDirection**
  - Determines which ```FloorDirection``` constant should be associated with the floor when requested
  - Takes ```FloorDirection directionFromCurrentFloor``` as argument which is the direction of the floor with respect to the current floor
    - Ex. if floor is above current floor, argument will be ```UP```
  - Returns 
    - ```directionFromCurrentFloor``` if no direction has been set
    - ```BOTH``` if floor has been requested in opposite direction or both directions
  - This method was added to simplify ```ElevatorImpl``` object and clean up if/else statements.
- **shouldStop**
  - Determines if elevator should stop at this floor
  - Takes ```int direction``` as argument
  - Returns true if floor is set to ```BOTH``` or ```direction``` matches ```FloorDirection```
  - This method was added to simplify ```ElevatorImpl``` object and clean up if/else statements.
### src/.../Util/DoorStatus.enum
Enum indicating if door is open or closed.
- OPEN
- CLOSED

## Testing
### src/../Elevator
#### Test Cases
- Call Button Pressed With Valid Inputs Should Add Floors Correctly
- Call Button Pressed With Invalid Inputs Should Fail
- Button Pressed With Valid Inputs Should Succeed
- Button Pressed With Invalid Inputs Should Fail
- Authenticate When SecurityType is NONE
- Authenticate When SecurityType is GENERAL
- Authenticate When SecurityType is SPECIFIED
- Move Current Floor With Normal Inputs
- Move Current Floor When Call Button is Pressed
- Button Pressed Add Floor when Security Type is NONE
- Button Pressed Add Floor when Security Type is GENERAL
- Button Pressed Add Floor when Security Type is SPECIFIED
- Change Direction

