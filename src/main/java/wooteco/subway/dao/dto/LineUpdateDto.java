package wooteco.subway.dao.dto;

public class LineUpdateDto {

    private final Long id;
    private final String name;
    private final String color;

    public LineUpdateDto(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}