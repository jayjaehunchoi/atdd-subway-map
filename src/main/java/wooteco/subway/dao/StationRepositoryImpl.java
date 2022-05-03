package wooteco.subway.dao;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;
import wooteco.subway.utils.exception.NameDuplicatedException;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StationRepositoryImpl implements StationRepository {

    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;


    public StationRepositoryImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
    }

    public static Station saveLegacy(Station station) {
        validateDuplicateName(station);
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private static void validateDuplicateName(Station station) {
        if (stations.contains(station)) {
            throw new NameDuplicatedException("[ERROR] 중복된 이름이 존재합니다.");
        }
    }

    @Override
    public Station save(final Station station) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", station.getName());
        Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Station(id, station.getName());
    }

    public static List<Station> findAllLegacy() {
        return stations;
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public static void deleteAll() {
        stations = new ArrayList<>();
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        RowMapper<Station> stationRowMapper = rowMapper();
        return namedParameterJdbcTemplate.query(sql, stationRowMapper);
    }

    @Override
    public void deleteById(final Long id) {
        String sql = "DELETE FROM station WHERE id = :id";
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource("id", id));
    }

    @Override
    public Station findByName(final String name) {
        String sql = "SELECT * FROM station WHERE name = :name";
        SqlParameterSource parameters = new MapSqlParameterSource("name", name);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, parameters, rowMapper());
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    @Override
    public Station findById(Long id) {
        String sql = "SELECT * FROM station WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, parameters, rowMapper());
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    private RowMapper<Station> rowMapper() {
        return (resultSet, rowNum) -> {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            return new Station(id, name);
        };
    }
}