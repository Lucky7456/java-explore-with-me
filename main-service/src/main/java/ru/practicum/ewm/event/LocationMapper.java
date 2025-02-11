package ru.practicum.ewm.event;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LocationMapper {
    public static Location mapToLocation(LocationDto locationDto) {
        return new Location(0, locationDto.lat(), locationDto.lon());
    }

    public static LocationDto mapToLocationDto(Location location) {
        return new LocationDto(location.getLat(), location.getLon());
    }
}
