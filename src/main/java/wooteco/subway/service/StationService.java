package wooteco.subway.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationRepository;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.utils.exception.NameDuplicatedException;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public StationResponse save(final StationRequest stationRequest) {
        validateDuplicateName(stationRepository.findByName(stationRequest.getName()));

        Station station = stationRepository.save(new Station(stationRequest.getName()));
        return new StationResponse(station.getId(), station.getName());
    }

    private void validateDuplicateName(final Optional<Station> station) {
        station.ifPresent(s -> {
            throw new NameDuplicatedException(NameDuplicatedException.NAME_DUPLICATE_MESSAGE);
        });
    }

    public List<StationResponse> showStations() {
        return stationRepository.findAll().stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public void deleteStation(final Long id) {
        stationRepository.deleteById(id);
    }
}
