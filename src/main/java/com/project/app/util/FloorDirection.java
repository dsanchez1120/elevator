package com.project.app.util;

public enum FloorDirection {
    NONE,
    UP,
    DOWN,
    BOTH;

    /**
     * Determines what floorDirection should be set to when visited.
     * @param direction Current direction of elevator
     * @return Updated floorDirection
     */
    public FloorDirection visitFloor(int direction) {
        return switch (this) {
            // Floor should be skipped (NONE) or will only be visited in one direction.
            case NONE, UP, DOWN -> NONE;
            // Floor needs to be visited in both directions.
            // Sets to opposite of current direction so that it will be visited when direction changes.
            case BOTH -> direction == 1 ? DOWN : UP;
        };
    }

    /**
     * Determines which FloorDirection value should be associated with floor.
     * @param directionFromCurrentFloor The direction of floor from the current floor.
     * @return FloorDirection indicating which direction should be associated with floor.
     */
    public FloorDirection chooseFloorDirection(FloorDirection directionFromCurrentFloor) {
        return switch (this) {
            // Floor should be set to direction in relation to current floor
            case NONE -> directionFromCurrentFloor;
            // If floor will already be visited on current direction then no update, otherwise visit on both directions.
            case UP, DOWN -> directionFromCurrentFloor.equals(this) ? this : BOTH;
            // Floor will already be visited, no need to update
            case BOTH -> BOTH;
        };
    }

    /**
     * Returns boolean indicating if elevator should stop based on current value of enum
     * @param direction Current direction of elevator
     * @return boolean indicating if elevator should stop
     */
    public boolean shouldStop(int direction) {
        return switch (this) {
            // Floor not requested, should not stop.
            case NONE -> false;
            // Floor has been requested in one direction, should only stop in that direction.
            case UP -> direction == 1;
            case DOWN -> direction == -1;
            // Floor has been requested in both directions. Should stop at floor.
            case BOTH -> true;
        };
    }
}
