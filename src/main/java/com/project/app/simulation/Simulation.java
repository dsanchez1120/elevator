package com.project.app.simulation;
import java.io.IOException;

public class Simulation {

    public Simulation() throws IOException {
        // UI Variables
        int FRAME_Y_DIMENSION = 600;
        // UI Constants
        int FRAME_X_DIMENSION = 600;
        UI frame = new UI(FRAME_X_DIMENSION, FRAME_Y_DIMENSION);
        frame.setVisible(true);

    }
}
