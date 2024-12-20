package ru.vydrenkova.utils.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.vydrenkova.models.Sock;
import ru.vydrenkova.utils.constraints.Constraints;


public class SockSpecification {

    public static Specification<Sock> hasColor(String color) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Constraints.COLOR_FIELD_NAME), color);
    }

    public static Specification<Sock> hasCottonPercentage(String operator, Integer cottonPart) {
        return (root, query, criteriaBuilder) -> {
            switch (operator) {
                case Constraints.MORE_THAN_OPERATION_NAME:
                    return criteriaBuilder.greaterThan(root.get(Constraints.COTTON_PERCENTAGE_FIELD_NAME), cottonPart);
                case Constraints.LESS_THAN_OPERATION_NAME:
                    return criteriaBuilder.lessThan(root.get(Constraints.COTTON_PERCENTAGE_FIELD_NAME), cottonPart);
                case Constraints.EQUAL_OPERATION_NAME:
                    return criteriaBuilder.equal(root.get(Constraints.COTTON_PERCENTAGE_FIELD_NAME), cottonPart);
                default:
                    throw new IllegalArgumentException("Неподдерживаемый оператор: " + operator);
            }
        };
    }

    public static Specification<Sock> cottonPercentageBetween(int from, int to) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("cottonPercentage"), from, to);
    }
}
