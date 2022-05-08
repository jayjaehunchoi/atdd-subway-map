package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성하면 201 created를 반환하고 Location header에 url resource를 반환한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");

        // when
        ExtractableResponse<Response> response = httpPostTest(params, "/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성하면 400 bad-request가 발생한다.")
    @Test
    void createLineWithDuplicateName() {

        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");

        httpPostTest(params, "/lines");
        ExtractableResponse<Response> response = httpPostTest(params, "/lines");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("전체 노선을 조회하면 200 ok와 노선 정보를 반환한다.")
    @Test
    void getLines() {
        Map<String, String> newBundangLine = new HashMap<>();
        newBundangLine.put("name", "신분당선");
        newBundangLine.put("color", "bg-red-600");

        ExtractableResponse<Response> newBundangPostResponse = httpPostTest(newBundangLine, "/lines");

        Map<String, String> bundangLine = new HashMap<>();
        bundangLine.put("name", "분당선");
        bundangLine.put("color", "bg-green-600");

        ExtractableResponse<Response> bundangPostResponse = httpPostTest(bundangLine, "/lines");

        ExtractableResponse<Response> response = httpGetTest("/lines");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(newBundangPostResponse, bundangPostResponse).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("단건 노선을 조회하면 200 OK와 노선 정보를 반환한다")
    @Test
    void getLine() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");

        ExtractableResponse<Response> createResponse = httpPostTest(params, "/lines");

        long id = Long.parseLong(createResponse.header(HttpHeaders.LOCATION).split("/")[2]);

        ExtractableResponse<Response> getResponse = httpGetTest("/lines/" + id);
        long responseId = getResponse.jsonPath().getLong("id");
        assertThat(id).isEqualTo(responseId);
    }

    @DisplayName("노선을 수정하면 200 OK를 반환한다.")
    @Test
    void updateLine() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");

        ExtractableResponse<Response> createResponse = httpPostTest(params, "/lines");

        long id = Long.parseLong(createResponse.header(HttpHeaders.LOCATION).split("/")[2]);

        Map<String, String> updateParam = new HashMap<>();
        updateParam.put("name", "다른분당선");
        updateParam.put("color", "bg-red-600");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(updateParam)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선을 제거하면 204 No Content를 반환한다.")
    @Test
    void deleteStation() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");

        ExtractableResponse<Response> createResponse = httpPostTest(params, "/lines");

        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
