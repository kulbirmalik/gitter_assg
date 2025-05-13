package org.gitter.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConstantsTest {

    @Test
    void testGitterDirConstant() {
        assertEquals(".gitter", Constants.GITTER_DIR, "GITTER_DIR should be '.gitter'");
    }
}
