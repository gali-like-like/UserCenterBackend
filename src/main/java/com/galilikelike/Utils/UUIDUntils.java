package com.galilikelike.Utils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class UUIDUntils {

    public static String randomUUID() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return (new UUID(random.nextLong(), random.nextLong())).toString().replace("-", "");
    }

}
