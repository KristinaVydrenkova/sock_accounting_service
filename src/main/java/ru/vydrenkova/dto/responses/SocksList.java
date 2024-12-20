package ru.vydrenkova.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Информация о загруженных носках")
public class SocksList {
    @Schema(description = "Список загруженных носков")
    private List<SockResponse> sockList;
}
