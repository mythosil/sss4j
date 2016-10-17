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
package com.mythosil.sss4j;

/**
 * GF(2^8) interface.
 *
 * @since 1.0.0
 */
public interface GF256 {

    /**
     * To operate addition on GF256.
     *
     * @param x    addend (0-255)
     * @param y    addend (0-255)
     * @return sum (0-255)
     */
    int add(int x, int y);

    /**
     * To operate subtraction on GF256.
     *
     * @param x    minuend (0-255)
     * @param y    subtrahend (0-255)
     * @return difference (0-255)
     */
    int sub(int x, int y);

    /**
     * To operate multiplication on GF256.
     *
     * @param x    multiplicand (0-255)
     * @param y    multiplier (0-255)
     * @return product (0-255)
     */
    int mul(int x, int y);

    /**
     * To operate division on GF256.
     *
     * @param x    dividend (0-255)
     * @param y    divisor (1-255)
     * @return quotient (0-255)
     */
    int div(int x, int y);

}
