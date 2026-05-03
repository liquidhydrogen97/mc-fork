package com.liquidhydrogen.mcfork;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Smoke test for the mc-fork mod skeleton. Verifies that the entry-point class exposes
 * the canonical mod ID constant and an initialized SLF4J logger. Establishes that the
 * JUnit 5 + JaCoCo test infrastructure is wired before any game logic lands.
 */
class SmokeTest {

    @Test
    void modIdIsMcFork() {
        assertEquals("mc_fork", McForkMod.MOD_ID);
    }

    @Test
    void loggerIsInitialized() {
        assertNotNull(McForkMod.LOGGER);
    }

    @Test
    void constructorCompletesWithoutThrowing() {
        // The skeleton constructor does not dereference its arguments; passing nulls
        // exercises the body for coverage. Subsequent contracts that read from the bus
        // or container will need real fakes here.
        assertDoesNotThrow(() -> new McForkMod(null, null));
    }
}
