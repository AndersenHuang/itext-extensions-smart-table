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

import java.util.logging.Level;
import java.util.logging.Logger;

public class TableMediator {

    protected SmartTable[] tables = null;

    public TableMediator(SmartTable t) throws Exception {
        this(t, 1, 0f);
    }

    public TableMediator(SmartTable table, int splitSize, float gapSize) throws Exception {
        this.tables = new SmartTable[splitSize];
        float left = table.getPosition().getLeft();
        float width = (table.getPosition().getWidth() - (gapSize * (float) splitSize)) / (float) splitSize;
        for (int i = 0; i < tables.length; i++) {
            tables[i] = new SmartTable(table);
            tables[i].getPosition().setLeft(left);
            tables[i].getPosition().setRight(left + width);
            left += (width + gapSize);
        }
        tables[0].setReplicatorPosition(table.getPosition());
    }

    public final void addEmptyCell(float borderWidth) throws Exception {
        for (int i = 0; i < tables.length; i++) {
            if (!tables[i].isFlushed()) {
                tables[i].addEmptyCell();
                break;
            }
        }
    }

    public final void addCell(String s) {
        for (int i = 0; i < tables.length; i++) {
            if (!tables[i].isFlushed()) {
                try {
                    tables[i].addCell(s);
                } catch (Exception ex) {}
                break;
            }
        }
    }

    public final void addEngCell(String s) {
        for (int i = 0; i < tables.length; i++) {
            if (!tables[i].isFlushed()) {
                try {
                    tables[i].addEngCell(s);
                } catch (Exception ex) {}
                break;
            }
        }
    }

    public final boolean addSmartCell(Cell cell) throws Exception {
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

    public final void addSmartCellEx(Cell cell) throws Exception {
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
