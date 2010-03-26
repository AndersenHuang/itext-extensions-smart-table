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

import fch.extensions.itext.ITextClient;

public class TableMediator {

    protected SmartTable[] tables = null;
    private Position position = null;

    public TableMediator(ITextClient client, Position position, int columns, int rows, int borderWidth, int[] columnWidthsScale) throws Exception {
        tables = new SmartTable[1];
        tables[0] = new SmartTable(client, new Position(position), columns, rows, borderWidth, columnWidthsScale);
        tables[0].setReplicatorPosition(position);
        this.position = position;
    }

    public TableMediator(ITextClient client, Position position, int columns, int rows, int borderWidth, int[] columnWidthsScale, float gapSize) throws Exception {
        tables = new SmartTable[2];
        tables[0] = new SmartTable(client, new Position(position), columns, rows, borderWidth, columnWidthsScale);
        tables[0].setReplicatorPosition(position);
        tables[1] = new SmartTable(client, new Position(position), columns, rows, borderWidth, columnWidthsScale);
        float width = position.getWidth() / 2F + gapSize;
        tables[0].getPosition().setRight(position.getRight() - width);
        tables[1].getPosition().setLeft(position.getLeft() + width);
        this.position = position;
    }

    public TableMediator(SmartTable table, Position position, int splitSize, float gapSize) throws Exception {
        this.position = position;
        this.tables = new SmartTable[splitSize];
        float left = position.getLeft();
        float width = (position.getWidth() - (gapSize * (float) splitSize)) / (float) splitSize;
        for (int i = 0; i < tables.length; i++) {
            tables[i] = new SmartTable(table);
            tables[i].getPosition().setLeft(left);
            tables[i].getPosition().setRight(left + width);
            left += (width + gapSize);
        }
        tables[0].setReplicatorPosition(position);
    }

    public final void addCell(EasyCell cell) throws Exception {
        for (int i = 0; i < tables.length; i++) {
            if (!tables[i].isFlushed()) {
                tables[i].addCell(cell);
                break;
            }
        }
    }

    public final boolean addWrapCell(EasyCell cell) throws Exception {
        int full = 0;
        for (int i = 0; i < tables.length; i++) {
            if (!tables[i].isFlushed()) {
                if (tables[i].addWrapCell(cell)) {
                    break;
                } else {
                    if (i == (tables.length - 1)) {
                        return false;
                    }
                }
            } else {
                full++;
            }
        }
        if (full == tables.length) {
            return false;
        }
        return true;
    }

    public final boolean addSmartCell(EasyCell cell) throws Exception {
        for (int i = 0; i < tables.length; i++) {
            try {
                tables[i].addSmartCell(cell);
                break;
            } catch (TableFlushedException tbex) {
            } catch (TableFullException tex) {
                if (i == (tables.length - 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    public final void addSmartCellEx(EasyCell cell) throws Exception {
        for (int i = 0; i < tables.length; i++) {
            try {
                tables[i].addSmartCell(cell);
                break;
            } catch (TableFlushedException tbex) {
            } catch (TableFullException tex) {
                if (i == (tables.length - 1)) {
                    throw tex;
                }
            }
        }
    }

    public final boolean addSmartEngCell(EasyCell cell) throws Exception {
        for (int i = 0; i < tables.length; i++) {
            try {
                tables[i].addSmartEngCell(cell);
                break;
            } catch (TableFlushedException tbex) {
            } catch (TableFullException tex) {
                if (i == (tables.length - 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    public final void addSmartEngCellEx(EasyCell cell) throws Exception {
        for (int i = 0; i < tables.length; i++) {
            try {
                tables[i].addSmartEngCell(cell);
                break;
            } catch (TableFlushedException tbex) {
            } catch (TableFullException tex) {
                if (i == (tables.length - 1)) {
                    throw tex;
                }
            }
        }
    }

    public final void addEngCell(EasyCell cell) throws Exception {
        for (int i = 0; i < tables.length; i++) {
            if (!tables[i].isFlushed()) {
                tables[i].addEngCell(cell);
                break;
            }
        }
    }

    public final void addEmptyCell(float borderWidth) throws Exception {
        for (int i = 0; i < tables.length; i++) {
            if (!tables[i].isFlushed()) {
                tables[i].addEmptyCell(borderWidth);
                break;
            }
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
