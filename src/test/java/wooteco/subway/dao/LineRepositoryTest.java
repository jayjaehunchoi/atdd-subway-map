package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wooteco.subway.domain.Line;
import wooteco.subway.utils.exception.NameDuplicatedException;

public class LineRepositoryTest extends RepositoryTest {

    @Autowired
    private LineRepository lineRepository;

    @DisplayName("노선을 저장한다.")
    @Test
    void save() {
        Line line = new Line("분당선", "bg-red-600");
        Line savedLine = lineRepository.save(line);

        assertAll(
            () -> assertThat(savedLine.getId()).isNotNull(),
            () -> assertThat(savedLine).isEqualTo(line)
        );
    }

    @DisplayName("노선 저장시 유니크 키에 위반되면 에러를 발생한다")
    @Test
    void saveUniqueException() {
        Line line = new Line("분당선", "bg-red-600");
        lineRepository.save(line);

        assertThatThrownBy(() -> lineRepository.save(line))
                .isInstanceOf(NameDuplicatedException.class);
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void findAll() {
        Line line1 = new Line("분당선", "bg-red-600");
        Line savedLine1 = lineRepository.save(line1);
        Line line2 = new Line("신분당선", "bg-red-600");
        Line savedLine2 = lineRepository.save(line2);

        List<Line> lines = lineRepository.findAll();

        assertAll(
            () -> assertThat(lines).hasSize(2),
            () -> assertThat(lines).containsExactly(savedLine1, savedLine2)
        );
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void findById() {
        Line savedLine = lineRepository.save(new Line("분당선", "bg-red-600"));
        Line line = lineRepository.findById(savedLine.getId());

        assertThat(line).isEqualTo(savedLine);
    }

    @DisplayName("이름으로 노선을 조회한다.")
    @Test
    void findByName() {
        Line savedLine = lineRepository.save(new Line("분당선", "bg-red-600"));
        Line line = lineRepository.findByName("분당선").orElse(null);

        assertThat(line).isEqualTo(savedLine);
    }

    @DisplayName("이름으로 노선을 조회시 없을 경우 빈 Optional을 반환한다.")
    @Test
    void findByNameNull() {
        assertThat(lineRepository.findByName("분당선").isEmpty()).isTrue();
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void update() {
        Line savedLine = lineRepository.save(new Line("분당선", "bg-red-600"));
        lineRepository.update(new Line(savedLine.getId(), "신분당선", "bg-yellow-600"));
        Line findUpdateLine = lineRepository.findById(savedLine.getId());

        assertAll(
            () -> assertThat(findUpdateLine.getName()).isEqualTo("신분당선"),
            () -> assertThat(findUpdateLine.getColor()).isEqualTo("bg-yellow-600")
        );
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteById() {
        Line savedLine = lineRepository.save(new Line("분당선", "bg-red-600"));
        lineRepository.deleteById(savedLine.getId());

        assertThat(lineRepository.findAll()).isEmpty();
    }
}
