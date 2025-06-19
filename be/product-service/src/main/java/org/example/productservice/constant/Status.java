package org.example.productservice.constant;

import lombok.Getter;

@Getter
public enum Status {
    ACCEPTED("1"),
    REJECTED("2"),
    PENDING("3"),
    IN_PROGRESS("4");

    private final String status;

    Status(String status) {
        this.status = status;
    }
}
