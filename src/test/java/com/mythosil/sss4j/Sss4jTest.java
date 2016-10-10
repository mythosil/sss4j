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

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class Sss4jTest {

    @Test
    public void testSplitAndCombine_k2_n3() {
        byte[] secret = "this is secret".getBytes();
        int k = 2;
        int n = 3;

        List<Share> shares = Sss4j.split(secret, k, n);
        assertThat(shares.size(), is(n));

        for (int i = 0; i < n; i++) {
            List<Share> ls = new ArrayList<>(shares);
            ls.remove(i);

            byte[] combined = Sss4j.combine(ls);
            assertThat(combined, is(secret));
        }
    }

    @Test
    public void testSplitAndCombine_k11_n13() {
        byte[] secret = "this is secret".getBytes();
        int k = 11;
        int n = 13;

        List<Share> shares = Sss4j.split(secret, k, n);
        assertThat(shares.size(), is(n));

        for (int i = 0; i < n; i++) {
            List<Share> ls = new ArrayList<>(shares);
            ls.remove(i);

            byte[] combined = Sss4j.combine(ls);
            assertThat(combined, is(secret));
        }
    }

    @Test
    public void testSplitAndCombine_multibyte_string() {
        byte[] secret = "マルチバイト文字".getBytes();
        int k = 11;
        int n = 13;

        List<Share> shares = Sss4j.split(secret, k, n);
        assertThat(shares.size(), is(n));

        for (int i = 0; i < n; i++) {
            List<Share> ls = new ArrayList<>(shares);
            ls.remove(i);

            byte[] combined = Sss4j.combine(ls);
            assertThat(combined, is(secret));
        }
    }

    @Test
    public void testCombine() {
        // (2, 3)-threshold
        final byte[][] shareValues = {
                {(byte) 0x3d, (byte) 0x36, (byte) 0x88, (byte) 0xf9, (byte) 0xd8, (byte) 0x0c},
                {(byte) 0xef, (byte) 0xc3, (byte) 0xa8, (byte) 0x79, (byte) 0x02, (byte) 0x84},
                {(byte) 0xa1, (byte) 0x90, (byte) 0x43, (byte) 0xf2, (byte) 0xbf, (byte) 0xfc}
        };

        for (int i = 0; i < 3; i++) {
            List<Share> shares = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                if (i != j) {
                    shares.add(new Share(j + 1, shareValues[j]));
                }
            }
            byte[] combined = Sss4j.combine(shares);
            assertThat(combined, is("secret".getBytes()));
        }
    }

    @Test
    public void testCombine_insufficient_shares() {
        // (2, 3)-threshold
        final byte[][] shareValues = {
                {(byte) 172, (byte) 150, (byte) 225, (byte) 132, (byte) 142, (byte) 222},
                {(byte) 214, (byte) 152, (byte) 124, (byte) 133, (byte) 168, (byte) 59},
                {(byte) 9, (byte) 107, (byte) 254, (byte) 115, (byte) 67, (byte) 145}
        };

        for (int i = 0; i < 3; i++) {
            List<Share> shares = new ArrayList<>();
            shares.add(new Share(i + 1, shareValues[i]));

            byte[] combined = Sss4j.combine(shares);
            assertThat(combined, is(not("secret".getBytes())));
        }
    }

    @Test
    public void testSplitAndIssueAndCombine_k2_n3() {
        byte[] secret = "this is secret".getBytes();
        int k = 2;
        int n = 3;

        List<Share> shares = Sss4j.split(secret, k, n);
        assertThat(shares.size(), is(n));

        Share s3 = Sss4j.issue(shares, 4);
        shares.forEach(s -> {
            List<Share> _shares = Arrays.asList(s, s3);
            byte[] combined = Sss4j.combine(_shares);
            assertThat(combined, is(secret));
        });
    }

    @Test
    public void testSplitAndBreedAndCombine_k11_n12() {
        byte[] secret = "this is secret".getBytes();
        int k = 11;
        int n = 12;

        List<Share> shares = Sss4j.split(secret, k, n);
        assertThat(shares.size(), is(n));

        Share sx = Sss4j.issue(shares, 13);
        for (int i = 0; i < shares.size(); i++) {
            List<Share> _shares = new ArrayList<>(shares);
            _shares.remove(i);
            _shares.add(sx);

            byte[] combined = Sss4j.combine(_shares);
            assertThat(combined, is(secret));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitException_small_n() {
        Sss4j.split("test".getBytes(), 2, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitException_small_k() {
        Sss4j.split("test".getBytes(), 1, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitException_null_secret() {
        byte[] secret = null;
        Sss4j.split(secret, 3, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCombineException_null_shares() {
        Sss4j.combine(null);
    }

}
