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

/**
 * Matrix on GF256.
 *
 * <li>num of rows: N
 * <li>num of columns: N+1
 *
 * @since 1.0.0
 */
public class GF256Matrix {

    private final GF256 gf256;
    private final int[][] data;

    /**
     * Constructor
     *
     * @param data    raw data
     */
    public GF256Matrix(int[][] data, GF256 gf256) {
        if (data == null
                || data.length == 0
                || data.length + 1 != data[0].length) {
            throw new IllegalArgumentException("matrix should be NxN+1 (N > 0)");
        }
        this.gf256 = gf256;
        this.data = deepcopy(data);
    }

    /**
     * Constructor
     *
     * @param points    Points
     */
    public GF256Matrix(List<Point> points, GF256 gf256) {
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException();
        }
        int rowNum = points.size();
        int colNum = rowNum + 1;
        int[][] mat = new int[rowNum][colNum];
        for (int i = 0; i < rowNum; i++) {
            Point p = points.get(i);
            mat[i][colNum - 1] = p.getY();
            mat[i][colNum - 2] = 1;
            for (int j = colNum - 3; j >= 0; j--) {
                mat[i][j] = gf256.mul(mat[i][j + 1], p.getX());
            }
        }
        this.gf256 = gf256;
        this.data = mat;
    }

    /**
     * To get num of rows.
     *
     * @return num of rows
     */
    public int getNumberOfRows() {
        return data.length;
    }

    /**
     * To get num of columns.
     *
     * @return num of columns
     */
    public int getNumberOfColumns() {
        return data[0].length;
    }

    /**
     * To get raw data.
     *
     * @return raw data
     */
    public int[][] getData() {
        return data;
    }

    /**
     * To get row data.
     *
     * @param index    index of row
     * @return row data
     */
    public int[] getRow(int index) {
        int rowNum = getNumberOfRows();
        if (index < 0 || index >= rowNum) {
            throw new IndexOutOfBoundsException();
        }
        int colNum = getNumberOfColumns();
        int[] ret = new int[colNum];
        System.arraycopy(data[index], 0, ret, 0, colNum);
        return ret;
    }

    /**
     * To get column data.
     *
     * @param index    index of column
     * @return column data
     */
    public int[] getColumn(int index) {
        int colNum = getNumberOfColumns();
        if (index < 0 || index >= colNum) {
            throw new IndexOutOfBoundsException();
        }
        int rowNum = getNumberOfRows();
        int[] ret = new int[rowNum];
        for (int i = 0; i < rowNum; i++) {
            ret[i] = data[i][index];
        }
        return ret;
    }

    /**
     * To get last column data.
     *
     * @return column data
     */
    public int[] getLastColumn() {
        return getColumn(getNumberOfColumns() - 1);
    }

    /**
     * To solve (Gaussian elimination).
     *
     * @return solved matrix
     */
    public GF256Matrix solve() {
        GF256Matrix m = eliminateForward();
        m = m.substituteBackward();
        m = m.identify();
        return m;
    }

    /**
     * Forward elimination.
     *
     * @return forward-eliminated matrix
     */
    /* package */ GF256Matrix eliminateForward() {
        int rowNum = getNumberOfRows();
        int colNum = getNumberOfColumns();
        int[][] mat = deepcopy(data);
        for (int i = 0; i < rowNum - 1; i++) {
            if (mat[i][i] == 0) {
                int j;
                for (j = i + 1; j < rowNum; j++) {
                    if (mat[j][i] != 0) {
                        break;
                    }
                }
                if (j >= rowNum) {
                    break;
                }
                int[] tmp = new int[colNum];
                System.arraycopy(mat[i], 0, tmp, 0, colNum);
                System.arraycopy(mat[j], 0, mat[i], 0, colNum);
                System.arraycopy(tmp, 0, mat[j], 0, colNum);
            }
            for (int j = i + 1; j < rowNum; j++) {
                int divider = gf256.div(mat[i][i], mat[j][i]);
                for (int k = 0; k < colNum; k++) {
                    mat[j][k] = gf256.sub(mat[j][k], gf256.div(mat[i][k], divider));
                }
            }
        }
        return new GF256Matrix(mat, gf256);
    }

    /**
     * Backward substitution.
     *
     * @return backward-substituted matrix
     */
    /* package */ GF256Matrix substituteBackward() {
        int rowNum = getNumberOfRows();
        int colNum = getNumberOfColumns();
        int[][] mat = deepcopy(data);
        for (int i = rowNum - 1; i > 0; i--) {
            for (int j = i - 1; j >= 0; j--) {
                int k = colNum - 1;
                int divider = gf256.div(mat[i][i], mat[j][i]);
                mat[j][i] = 0;
                mat[j][k] = gf256.sub(mat[j][k], gf256.div(mat[i][k], divider));
            }
        }
        return new GF256Matrix(mat, gf256);
    }

    /**
     * 単位行列化
     *
     * @return 単位行列化された後の行列
     */
    /* package */ GF256Matrix identify() {
        int rowNum = getNumberOfRows();
        int colNum = getNumberOfColumns();
        int[][] mat = deepcopy(data);
        for (int i = 0; i < rowNum; i++) {
            int divider = mat[i][i];
            for (int j = 0; j < colNum; j++) {
                mat[i][j] = gf256.div(mat[i][j], divider);
            }
        }
        return new GF256Matrix(mat, gf256);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] row : data) {
            for (int v : row) {
                sb.append(v).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        GF256Matrix m = (GF256Matrix) obj;
        if (getNumberOfRows() != m.getNumberOfRows() ||
                getNumberOfColumns() != m.getNumberOfColumns()) {
            return false;
        }
        int[][] mdata = m.getData();
        for (int i = 0; i < getNumberOfRows(); i++) {
            for (int j = 0; j < getNumberOfColumns(); j++) {
                if (data[i][j] != mdata[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int[][] deepcopy(int[][] data) {
        int rowNum = data.length;
        int colNum = data[0].length;
        int[][] ret = new int[rowNum][colNum];
        for (int i = 0; i < rowNum; i++) {
            System.arraycopy(data[i], 0, ret[i], 0, colNum);
        }
        return ret;
    }

}
