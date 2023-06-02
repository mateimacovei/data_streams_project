package com.example.fdserver.model.streams;

public enum IncidentType {
    MAX_TEMP, // cpu temp > 100
    NO_FLOW, // low water flow
    BAD_CONTACT // big difference between average cpu temp and water temp
}
