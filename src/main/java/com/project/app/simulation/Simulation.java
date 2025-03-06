package com.project.app.simulation;
import java.io.IOException;

public class Simulation {
    // UI Constants
    private final int FRAME_X_DIMENSION = 600;
    private final int FRAME_Y_DIMENSION = 600;

    // UI Variables
    private static UI frame;

    public Simulation(String elevatorName, String userName) throws IOException {
        frame = new UI(FRAME_X_DIMENSION, FRAME_Y_DIMENSION);
        frame.setVisible(true);

    }
}
