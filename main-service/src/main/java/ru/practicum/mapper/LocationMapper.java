package ru.practicum.mapper;

import ru.practicum.model.Location;

public class LocationMapper {
    public static Location toLocation(float lat, float lon) {
        Location location = new Location();
        location.setLat(lat);
        location.setLon(lon);
        return location;
    }
}
