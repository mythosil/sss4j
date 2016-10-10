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
 * Representation of share.
 *
 * @since 1.0.0
 */
public class Share {

    private final int index;
    private final byte[] value;

    public Share(int index, byte[] value) {
        this.index = index;
        this.value = value;
    }

    /**
     * To get the index of the share.
     *
     * @return index of the share
     */
    public int getIndex() {
        return index;
    }

    /**
     * To get the value of the share.
     *
     * @return value of the share
     */
    public byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(index + ":");
        for (byte b : value) {
            sb.append(b & 0xFF);
        }
        return sb.toString();
    }

}