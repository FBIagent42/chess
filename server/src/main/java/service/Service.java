package service;

import java.util.UUID;

public interface Service {

    default String generateToken() {
        return UUID.randomUUID().toString();
    }
}
