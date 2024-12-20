package ru.vydrenkova.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vydrenkova.models.Sock;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Полученная информация о носках")
public class SockResponse {
    @Schema(description = "ID носков")
    private Long id;

    @Schema(description = "Цвет")
    private String color;

    @Schema(description = "Процентное содержание хлопка")
    private Integer cottonPercentage;

    @Schema(description = "Количество носков")
    private Integer amount;

    public static SockResponse toResponse(Sock sock){
        return SockResponse.builder()
                .id(sock.getId())
                .color(sock.getColor())
                .cottonPercentage(sock.getCottonPercentage())
                .amount(sock.getAmount())
                .build();
    }
}
