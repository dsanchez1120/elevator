package com.project.app.util;

public enum SecurityType {
    NONE,
    GENERAL,
    SPECIFIED;

    /**
     * Returns if user is authorized to access floor depending on Enum Constant
     * @param authorized boolean indicating if user is allowed to access building
     * @return boolean indicating if user is authenticated
     */
    public boolean authenticate(boolean authorized) {
        return switch (this) {
            case NONE -> true;
            case GENERAL, SPECIFIED -> authorized;
        };
    }

    /**
     * Verifies if requested floor is able to be accessed given the SecurityType and parameters
     * @param authenticated boolean true if user is authenticated
     * @param aboveDefault boolean true if requested floor is above default floor
     * @param floorAuthorized boolean true if user is authorized to access requested floor
     * @return true if user is authorized to access floor, false if not
     */
    public boolean isAuthorized(boolean authenticated, boolean aboveDefault, boolean floorAuthorized) {
        return switch (this) {
            case NONE -> true;
            case GENERAL -> (authenticated || aboveDefault);
            case SPECIFIED -> (authenticated && floorAuthorized);
        };
    }
}
