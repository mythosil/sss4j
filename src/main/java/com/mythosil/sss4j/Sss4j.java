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

import com.mythosil.sss4j.impl.DefaultGF256;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Secret Sharing Scheme for Java (SSS4J).
 *
 * <p>
 * Implementation of (K,N)-threshold Shamir's Secret Sharing Scheme on GF(2^8).
 *
 * @since 1.0.0
 */
public class Sss4j {

    private Sss4j() {
        // make its constructor private
    }

    /**
     * To split secret on default GF256 implementation.
     *
     * @param secret binary representation of secret
     * @param k      K
     * @param n      N
     * @return shares
     */
    public static List<Share> split(byte[] secret, int k, int n) {
        return split(secret, k, n, new DefaultGF256());
    }

    /**
     * To split secret.
     *
     * @param secret binary representation of secret
     * @param k      K
     * @param n      N
     * @param gf256  GF256 implementation
     * @return shares
     */
    public static List<Share> split(byte[] secret, int k, int n, GF256 gf256) {
        // validate split parameters
        if (n < 3 || n > 255) {
            throw new IllegalArgumentException("n should be 3-255");
        } else if (k < 2 || k > 255) {
            throw new IllegalArgumentException("k should be 1-255");
        } else if (k > n) {
            throw new IllegalArgumentException("n should be larger than k");
        } else if (secret == null) {
            throw new IllegalArgumentException("secret should not be null");
        } else if (secret.length == 0) {
            throw new IllegalArgumentException("secret should not be empty");
        } else if (gf256 == null) {
            throw new IllegalArgumentException("gf256 should not be null");
        }

        List<Share> shares = new ArrayList<>();
        int degree = k - 1;
        Random random = new SecureRandom();
        byte[][] shareValues = new byte[n][secret.length];

        // split each secret byte to n pieces of shares.
        for (int i = 0; i < secret.length; i++) {
            // prepare polynomial
            int intercept = secret[i] & 0xFF;
            GF256Polynomial p = new GF256Polynomial(degree, intercept, random, gf256);

            // split
            for (int x = 1; x <= n; x++) {
                int y = p.evaluate(x);
                shareValues[x - 1][i] = (byte) y;
            }
        }

        for (int x = 1; x <= n; x++) {
            Share s = new Share(x, shareValues[x - 1]);
            shares.add(s);
        }

        return shares;
    }

    /**
     * To combine shares on default GF256 implementation.
     *
     * @param shares    shares
     * @return secret (binary representation)
     */
    public static byte[] combine(List<Share> shares) {
        return combine(shares, new DefaultGF256());
    }

    /**
     * To combine shares.
     *
     * @param shares    shares
     * @param gf256     GF256 implementation
     * @return secret (binary representation)
     */
    public static byte[] combine(List<Share> shares, GF256 gf256) {
        // validate combine parameters
        if (shares == null) {
            throw new IllegalArgumentException("shares should not be null");
        } else if (shares.isEmpty()) {
            throw new IllegalArgumentException("shares should not be empty");
        } if (gf256 == null) {
            throw new IllegalArgumentException("gf256 should not be null");
        }

        Share first = shares.get(0);
        int secretLength = first.getValue().length;
        byte[] secret = new byte[secretLength];

        for (int i = 0; i < secretLength; i++) {
            List<Point> points = new ArrayList<>();
            for (Share s : shares) {
                int x = s.getIndex();
                int y = s.getValue()[i] & 0xFF;
                Point p = new Point(x, y);
                points.add(p);
            }
            secret[i] = (byte) GF256Polynomial.interpolate(points, 0, gf256);
        }

        return secret;
    }

    /**
     * To issue new share on default GF256 implementation.
     *
     * @param shares    shares
     * @param index     index value for new share
     * @return new share
     */
    public static Share issue(List<Share> shares, int index) {
        return issue(shares, index, new DefaultGF256());
    }

    /**
     * To issue new share.
     *
     * @param shares    shares
     * @param index     index value for new share
     * @param gf256     GF256 implementation
     * @return new share
     */
    public static Share issue(List<Share> shares, int index, GF256 gf256) {
        // validate issue parameters
        if (shares == null) {
            throw new IllegalArgumentException("shares should not be null");
        } else if (shares.isEmpty()) {
            throw new IllegalArgumentException("shares should not be empty");
        } else if (index <= 0) {
            throw new IllegalArgumentException("index should be larger than 0");
        } else if (gf256 == null) {
            throw new IllegalArgumentException("gf256 should not be null");
        }

        byte[] secret = combine(shares, gf256);
        byte[] shareValue = new byte[secret.length];
        for (int i = 0; i < secret.length; i++) {
            final int j = i;
            List<Point> points = shares.stream().map(s -> {
                int x = s.getIndex();
                int y = s.getValue()[j] & 0xFF;
                if (x == index) {
                    throw new IllegalArgumentException("index already exists");
                }
                Point p = new Point(x, y);
                return p;
            }).collect(Collectors.toList());
            GF256Polynomial polynomial = createPolynomialFromPoints(points, gf256);
            shareValue[i] = (byte) polynomial.evaluate(index);
        }
        Share share = new Share(index, shareValue);
        return share;
    }

    private static GF256Polynomial createPolynomialFromPoints(List<Point> points, GF256 gf256) {
        GF256Matrix mat = new GF256Matrix(points, gf256);
        int[] col = mat.solve().getLastColumn();
        int[] coefficients = new int[col.length];
        for (int i = 0; i < col.length; i++) {
            coefficients[col.length - 1 - i] = col[i];
        }
        GF256Polynomial polynomial = new GF256Polynomial(coefficients, gf256);
        return polynomial;
    }

}
