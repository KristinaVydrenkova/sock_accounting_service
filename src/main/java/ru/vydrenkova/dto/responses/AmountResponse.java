package ru.vydrenkova.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Информация о количестве носков на складе")
public class AmountResponse {
    @Schema(description = "Количество носков на складе")
    private Integer amount;
}
