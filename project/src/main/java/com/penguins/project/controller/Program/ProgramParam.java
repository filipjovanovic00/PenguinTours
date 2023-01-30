package com.penguins.project.controller.Program;

import com.penguins.project.controller.Location.LocationParam;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class ProgramParam {

    private String description;
    private String date;
    private Set<LocationParam> locations = new HashSet<>();
}