/**
 * $Id$ [$Rev$]
 * $Author$
 * $Date$
 * $URL$
 *
 * Copyright (c) 2009 by Andersen (F.C. Huang).
 *
 * The contents of this file is available under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package fch.extensions.itext.ptable;

public final class Position {

    private float left = -1F;
    private float bottom = -1F;
    private float right = -1F;
    private float top = -1F;
    private int rows = 0;

    public Position() {
    }

    public Position(Position position) {
        this.left = position.getLeft();
        this.bottom = position.getBottom();
        this.right = position.getRight();
        this.top = position.getTop();
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    @Override
    public String toString() {
        return "[left: " + left + " ,bottom: " + bottom + " ,right: " + right + " ,top: " + top + " ,rows: " + rows + "]";
    }

    public void decreaseTop(float offset) {
        this.top -= offset;
    }

    public float getHeight() {
        return top - bottom;
    }

    public float getWidth() {
        return right - left;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int iRows) {
        this.rows = iRows;
    }

    /**
     * decreaseRows
     * 
     * @param iRows
     * @return true:該頁未填滿,可以繼續使用 false:該頁已填滿,不可以繼續使用
     */
    public boolean decreaseRows(int iRows) {
        if (rows < iRows) {
            return false;
        } else {
            rows = rows - iRows;
            return true;
        }
    }

    public void decreaseRowsEx(int iRows) throws NoMoreRowsException {
        rows -= iRows;
        if (rows <= 0) {
            throw new NoMoreRowsException();
        }
    }

    public void increaseRows(int iRows) {
        rows = rows + iRows;
    }
}
