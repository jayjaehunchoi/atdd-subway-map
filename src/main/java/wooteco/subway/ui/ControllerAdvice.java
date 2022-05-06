package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.ErrorResponse;
import wooteco.subway.utils.exception.SubwayException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<ErrorResponse> nameDuplicatedExceptionHandler(SubwayException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }
}
