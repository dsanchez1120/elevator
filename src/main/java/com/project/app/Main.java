package com.project.app;

import com.project.app.simulation.Simulation;

import java.io.IOException;

public class Main {
    public static Simulation sim;
    public static void setup() throws IOException {
        sim = new Simulation("none_5.json", "joe.json");
    }

    public static void main(String[] args) throws IOException {
        setup();
    }
}
