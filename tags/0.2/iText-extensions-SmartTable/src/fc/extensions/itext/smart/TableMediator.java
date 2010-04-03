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

package fc.extensions.itext.smart;

/**
 * The TableMediator will mediate tables in horizontal,
 * it also offer two kinds of addCell method (return boolean or throws exception).
 *
 * @author Andersen
 */
public class TableMediator {

    protected SmartTable[] tables = null;

    /**
     * mediate just one table.
     *
     * @param table The original table, it's attributes will be referenced by mediator.
     * @throws Exception
     */
    public TableMediator(SmartTable table) throws Exception {
        this(table, 1, 0f);
    }

    /**
     * mediate multi tables.
     *
     * @param table The original table, it's attributes will be referenced by mediator.
     * @param count How many tables in horizontal will be mediated.
     * @param gap The gap between tables.
     * @throws Exception
     */
    public TableMediator(SmartTable table, int count, float gap) throws Exception {
        this.tables = new SmartTable[count];
        float left = table.getPosition().getLeft();
        float width = (table.getPosition().getWidth() - (gap * (float)count)) / (float)count;
        for (int i = 0; i < tables.length; i++) {
            tables[i] = new SmartTable(table);
            tables[i].getPosition().setLeft(left);
            tables[i].getPosition().setRight(left + width);
            left += (width + gap);
        }
        tables[0].setReplicatorPosition(table.getPosition());
    }

    public final void addEmptyCellEx() throws TableWasFullException {
        for (int i = 0; i < tables.length; i++) {
            tables[i].addEmptyCell();
            break;
        }
    }

    public final boolean addEmptyCell() {
        for (int i = 0; i < tables.length; i++) {
            try {
                tables[i].addEmptyCell();
                break;
            } catch (TableWasFullException tex) {
                if (i == (tables.length - 1)) {
                    return false;
                }
            }

        }
        return true;
    }

    public final void addCellEx(String s) throws TableWasFullException {
        for (int i = 0; i < tables.length; i++) {
            tables[i].addCell(s);
            break;
        }
    }

    public final boolean addCell(String s) {
        for (int i = 0; i < tables.length; i++) {
            try {
                tables[i].addCell(s);
                break;
            } catch (TableWasFullException tex) {
                if (i == (tables.length - 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    public final void addCellEx(Cell cell) throws TableWasFullException {
        for (int i = 0; i < tables.length; i++) {
            tables[i].addCell(cell);
            break;
        }
    }

    public final boolean addCell(Cell cell) throws Exception {
        for (int i = 0; i < tables.length; i++) {
            try {
                tables[i].addCell(cell);
                break;
            } catch (TableWasFullException tex) {
                if (i == (tables.length - 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    public final void addAnsiCellEx(String s) throws TableWasFullException {
        for (int i = 0; i < tables.length; i++) {
            tables[i].addAnsiCell(s);
            break;
        }
    }


    public final boolean addAnsiCell(String s) {
        for (int i = 0; i < tables.length; i++) {
            try {
                tables[i].addAnsiCell(s);
                break;
            } catch (TableWasFullException tex) {
                if (i == (tables.length - 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    public final boolean addCrossRowCell(String content, float maxCellWidth) {
        for (int i = 0; i < tables.length; i++) {
            try {
                tables[i].addCrossRowCell(content, maxCellWidth);
                break;
            } catch (TableWasFullException tex) {
                if (i == (tables.length - 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    public final void addCrossRowCellEx(String content, float maxCellWidth) throws TableWasFullException {
        for (int i = 0; i < tables.length; i++) {
            tables[i].addCrossRowCell(content, maxCellWidth);
            break;
        }
    }

    public final boolean flush() {
        boolean flushExeuted = false;
        for (int i = 0; i < tables.length; i++) {
            if (tables[i].isCreated() && !tables[i].isFlushed()) {
                tables[i].flush();
                flushExeuted = true;
            }
        }
        return flushExeuted;
    }

    public final boolean flushOnce() {
        boolean flushExeuted = false;
        for (int i = 0; i < tables.length; i++) {
            if (tables[i].isCreated() && !tables[i].isFlushed()) {
                tables[i].flush();
                flushExeuted = true;
                break;
            }
        }
        return flushExeuted;
    }
}
