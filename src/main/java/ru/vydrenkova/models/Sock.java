package ru.vydrenkova.models;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "socks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"color", "cottonPercentage"})
})
public class Sock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String color;
    private Integer cottonPercentage;
    private Integer amount;
}
