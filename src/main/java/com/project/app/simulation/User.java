package com.project.app.simulation;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder
public class User {
    private String name;
    private ArrayList<Boolean> authorizedFloors;

}
