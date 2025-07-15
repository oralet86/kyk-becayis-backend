package com.sazark.kykbecayis.core.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;

public final class EnvConfig {
    private static final Dotenv env = Dotenv.load();

    @Getter
    private static final String JWT_SECRET = env.get("JWT_SECRET");

    private EnvConfig() {
    }
}
