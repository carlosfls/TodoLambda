package org.carlosacademic.domain;

public record TodoDTO(int userId, int id, String title, boolean completed) {
}
