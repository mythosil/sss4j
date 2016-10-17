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

import java.util.List;
import java.util.Random;

/**
 * Polynomial on GF256
 *
 * @since 1.0.0
 */
public class GF256Polynomial {

    private final GF256 gf256;
    private final int[] coefficients;

    /**
     * To initialize polynomial with degree and intercept.
     *
     * @param degree       degree
     * @param intercept    intercept
     * @param random       random number generator
     * @param gf256        GF256 implementation
     */
    public GF256Polynomial(int degree, int intercept, Random random, GF256 gf256) {
        this.gf256 = gf256;
        this.coefficients = generate(degree, intercept, random);
    }

    /**
     * To initialize polynomial with coefficients.
     *
     * @param coefficients    coefficients
     * @param gf256           GF256 implementation
     */
    public GF256Polynomial(int[] coefficients, GF256 gf256) {
        this.gf256 = gf256;
        this.coefficients = coefficients;
    }

    /**
     * To get coefficients.
     *
     * @return coefficients
     */
    public int[] getCoefficients() {
        return coefficients;
    }

    /**
     * To get degree.
     *
     * @return degree
     */
    public int getDegree() {
        return coefficients.length - 1;
    }

    /**
     * To evaluate polynomial.
     *
     * @param x    x
     * @return value
     */
    public int evaluate(int x) {
        /*
         * Horner's scheme
         * p(x) = a_0 + x(a_1 + x(a_2 + \dots + x(a_{n-1} + a_{n}x) \dots ))
         */
        int ret = 0;
        for (int i = coefficients.length - 1; i >= 0; i--) {
            ret = gf256.add(gf256.mul(ret, x), coefficients[i]);
        }
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coefficients.length; i++) {
            sb.append(coefficients[i]).append("x^").append(i);
            if (i < coefficients.length - 1) {
                sb.append(" + ");
            }
        }
        return sb.toString();
    }

    /**
     * Lagrange interpolation.
     *
     * <pre>
     * p_n(x) =
     *  \sum^n_{j=0}
     *  \frac
     *  { (x-x_0)(x-x_1) \dots (x-x_{j-1})(x-x_{j+1}) \dots (x-x_n) }
     *  { (x_j-x_0)(x_j-x_1) \dots (x_j-x_{j-1})(x_j-x_{j+1}) \dots (x_j-x_n) }
     * </pre>
     *
     * @param points    points on polynomial
     * @param x         x
     * @param gf256     GF256 implementation
     * @return y
     */
    public static int interpolate(List<Point> points, int x, GF256 gf256) {
        int ret = 0;
        for (int i = 0, size = points.size(); i < size; i++) {
            Point pi = points.get(i);
            int weight = 1;
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    Point pj = points.get(j);
                    int numer = gf256.sub(x, pj.getX());
                    int denom = gf256.sub(pi.getX(), pj.getX());
                    int factor = gf256.div(numer, denom);
                    weight = gf256.mul(weight, factor);
                }
            }
            ret = gf256.add(ret, gf256.mul(weight, pi.getY()));
        }
        return ret;
    }

    /**
     * To generate polynomial.
     *
     * @param degree    degree of polynomial
     * @param intercept intercept
     * @param random    random number generator
     * @return coefficients (length = degree + 1)
     */
    private int[] generate(int degree, int intercept, Random random) {
        int[] ret = new int[degree + 1];
        ret[0] = intercept;
        for (int i = 1; i <= degree; i++) {
            ret[i] = random.nextInt(256);
        }
        return ret;
    }
}
