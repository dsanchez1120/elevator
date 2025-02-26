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
- **Open Door**: Opens elevator door if elevator is stopped and closed, keeps the elevator door open if already open
- **Close Door**: Closes elevator door (allegedly) if already open
- **Emergency Call**: Calls emergency services in case of emergency. Elevator stops at current floor, does not open door.
### Security
Some elevators, usually in office-buildings, require an RFID card/chip to use elevator (from ground-floor).
This application will have 3 options for elevators
- **No security:** Anyone can access any floor from any floor.
- **General Security:** Elevator restricts movement from ground-floor unless RFID chip has been scanned.
- **Specified Security:** Restricts movement from ground-floor unless RFID chip has been scanned. Restricts movement based on permissions


## Structure


## Testing
