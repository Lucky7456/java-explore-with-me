package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.dto.StatsViewDto;
import ru.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("select new ru.practicum.ewm.dto.StatsViewDto(e.app as app, e.uri as uri, count(e.id) as hits) " +
            "from EndpointHit as e " +
            "where e.timestamp between :start and :end " +
            "and (:uris is null or e.uri in :uris) " +
            "group by e.app, e.uri " +
            "order by hits desc")
    List<StatsViewDto> findAllByTimestampBetweenAndUriIn(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.ewm.dto.StatsViewDto(e.app, e.uri, count(distinct e.ip) as hits) " +
            "from EndpointHit as e " +
            "where e.timestamp between :start and :end " +
            "and (:uris is null or e.uri in :uris) " +
            "group by e.app, e.uri " +
            "order by hits desc")
    List<StatsViewDto> findAllUniqueIpAndTimestampBetweenAndUriIn(LocalDateTime start, LocalDateTime end, List<String> uris);
}
