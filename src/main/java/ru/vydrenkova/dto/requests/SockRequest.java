package ru.vydrenkova.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Параметры носков")
public class SockRequest {
    @Schema(description = "Цвет носков", example = "blue")
    private String color;

    @Schema(description = "Процентное содержание хлопка", example = "70", minimum = "0", maximum = "100")
    private Integer cottonPercentage;

    @Schema(description = "Количество носков", example = "100", minimum = "1")
    private Integer amount;
}
