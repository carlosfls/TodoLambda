package org.carlosacademic.domain;

public record TodoFullDTO(
        TodoDTO todo,
        UserDTO user
) {
}
