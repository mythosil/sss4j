/**
 * Copyright 2016 Akito Tabira
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mythosil.sss4j.impl;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DefaultGF256Test {

    private DefaultGF256 gf256;

    @Before
    public void setUp() {
        this.gf256 = new DefaultGF256();
    }

    @Test
    public void testAdd() {
        int result = gf256.add(1, 2);
        assertThat(result, is(3));

        result = gf256.add(7, 11);
        assertThat(result, is(12));
    }

    @Test
    public void testSub() {
        int result = gf256.sub(1, 2);
        assertThat(result, is(3));

        result = gf256.sub(7, 11);
        assertThat(result, is(12));
    }

    @Test
    public void testMul() {
        int result = gf256.mul(3, 5);
        assertThat(result, is(15));

        result = gf256.mul(65, 193);
        assertThat(result, is(203));

        result = gf256.mul(0, 1);
        assertThat(result, is(0));

        result = gf256.mul(1, 0);
        assertThat(result, is(0));
    }

    @Test
    public void testDiv() {
        int result = gf256.div(15, 3);
        assertThat(result, is(5));

        result = gf256.div(203, 193);
        assertThat(result, is(65));

        result = gf256.div(0, 10);
        assertThat(result, is(0));

        try {
            gf256.div(1, 0);
            fail();
        } catch (ArithmeticException e) {
            assertThat(e.getMessage(), is("div by zero"));
        }
    }

    @Test(expected = ArithmeticException.class)
    public void testDivByZero() {
        gf256.div(10, 0);
    }

    @Test
    public void testExp() {
        int[] expTable = createExpTable();
        for (int i = 0, size = expTable.length; i < size; i++) {
            assertThat(gf256.EXP[i], is(expTable[i]));
        }
    }

    @Test
    public void testLog() {
        int[] logTable = createLogTable();
        for (int i = 0, size = logTable.length; i < size; i++) {
            assertThat(gf256.LOG[i], is(logTable[i]));
        }
    }

    private int[] createExpTable() {
        // Ref: Reed-Solomon implementation in ZXing
        // https://github.com/zxing/zxing/blob/master/core/src/main/java/com/google/zxing/common/reedsolomon/GenericGF.java

        int primitive = 0x11D; // 100011101
        int size = 256;

        int[] ret = new int[size];
        int ex = 1;
        for (int i = 0; i < size; i++) {
            ret[i] = ex;
            ex <<= 1; // generator = 2
            if (ex >= size) {
                ex ^= primitive;
                ex &= size - 1;
            }
        }
        return ret;
    }

    private int[] createLogTable() {
        int[] expTable = createExpTable();
        int size = expTable.length;

        int[] ret = new int[size];
        for (int i = 0; i < size; i++) {
            ret[expTable[i]] = i;
        }
        ret[0] = ret[1] = 0;
        return ret;
    }

}
