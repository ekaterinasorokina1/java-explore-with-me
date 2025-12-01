package ru.practicum.mapper;

import ru.practicum.dto.HitDto;
import ru.practicum.model.EndpointHit;

public class EndpointHitMapper {
    public static HitDto toHitDto(EndpointHit endpointHit) {
        HitDto hitDto = new HitDto();
        hitDto.setId(endpointHit.getId());
        hitDto.setIp(endpointHit.getIp());
        hitDto.setApp(endpointHit.getApp());
        hitDto.setUri(endpointHit.getUri());
        hitDto.setTimestamp(endpointHit.getTimestamp());
        return hitDto;
    }

    public static EndpointHit toEntity(HitDto hitDto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(hitDto.getApp());
        endpointHit.setId(hitDto.getId());
        endpointHit.setUri(hitDto.getUri());
        endpointHit.setIp(hitDto.getIp());
        return endpointHit;
    }
}
