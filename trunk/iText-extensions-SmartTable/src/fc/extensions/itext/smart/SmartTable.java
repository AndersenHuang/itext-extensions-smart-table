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

import fc.extensions.itext.Writer;
import com.itextpdf.text.pdf.PdfPTable;
import org.apache.commons.beanutils.locale.LocaleBeanUtils;

public final class SmartTable {

    private boolean created = false,  flushed = false;
    private float borderWidth = 0f;
    private int columns = 0;
    private int rows = 0;
    private PdfPTable table = null;
    private Writer writer = null;
    private int[] columnWidthsScale = null;
    private int cellCounter = 0;
    private Position position = null;
    private Position replicatorPosition = null;
    private int rowFixedHeight = 14;
    private int colDefaultFontSize = 8;
    private boolean autoFlush = true;

    public SmartTable() {
    }

    public SmartTable(Writer client) {
        this.writer = client;
    }

    public SmartTable(Writer client, Position position, int columns, int rows) {
        this(client, position, columns, rows, 0f, null);
    }

    public SmartTable(Writer client, Position position, int columns, int rows, float borderWidth) {
        this(client, position, columns, rows, borderWidth, null);
    }

    public SmartTable(Writer client, Position position, int columns, int rows, float borderWidth, int[] columnWidthsScale) {
        this.writer = client;
        this.position = position;
        this.columns = columns;
        this.rows = rows;
        this.borderWidth = borderWidth;
        this.columnWidthsScale = columnWidthsScale;
    }

    public SmartTable(SmartTable tableObject) throws Exception {
        LocaleBeanUtils.copyProperties(this, tableObject);
        this.position = new Position(tableObject.getPosition());
    }

    public boolean isAutoFlush() {
        return autoFlush;
    }

    public void setAutoFlush(boolean autoFlush) {
        this.autoFlush = autoFlush;
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer Writer) {
        this.writer = Writer;
    }

    public int getColDefaultFontSize() {
        return colDefaultFontSize;
    }

    public void setColDefaultFontSize(int colDefaultFontSize) {
        this.colDefaultFontSize = colDefaultFontSize;
    }

    public int getRowFixedHeight() {
        return rowFixedHeight;
    }

    public void setRowFixedHeight(int rowFixedHeight) {
        this.rowFixedHeight = rowFixedHeight;
    }

    public void setReplicatorPosition(Position position) {
        this.replicatorPosition = position;
    }

    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public boolean isFlushed() {
        return flushed;
    }

    public void setFlushed(boolean flushed) {
        this.flushed = flushed;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int[] getColumnWidthsScale() {
        return columnWidthsScale;
    }

    public void setColumnWidthsScale(int[] columnWidthsScale) {
        this.columnWidthsScale = columnWidthsScale;
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void create() throws Exception {
        if (!created) {
            if (columnWidthsScale == null || columnWidthsScale.length == 0) {
                columnWidthsScale = new int[columns];
                for (int i = 0; i < columnWidthsScale.length; i++) {
                    columnWidthsScale[i] = 1;
                }
            }
            table = this.writer.createTable(columns, position.getWidth(), columnWidthsScale);
            table.getDefaultCell().setBorderWidth(borderWidth);
            table.getDefaultCell().setFixedHeight(rowFixedHeight);
            table.getDefaultCell().setPadding(0.2F);
            created = true;
        }
    }

    private void checkFlush() throws Exception {
        if (autoFlush) {
            if (cellCounter > columns * rows) {
                throw new TableFlushedException("cant addCell after flushed! cellCounter: " + cellCounter + ",columns: " + columns + ",rows:" + rows);
            }

            if (!flushed && cellCounter == columns * rows) {
                flush();
            }
        }
    }

    private boolean isFull() throws Exception {
        if (autoFlush) {
            if (flushed) {
                return true;
            }
            if (cellCounter > columns * rows) {
                flush();
                return true;
            }
        }
        return false;
    }

    private void checkCreate() throws Exception {
        if (!created) {
            create();
        }
    }

    public void addCell(String content) throws Exception {
        addCell(content, 0F);
    }

    public void addEmptyCell() throws Exception {
        addEmptyCell(0F);
    }

    public void addEmptyCell(float borderWidth) throws Exception {
        checkCreate();
        this.writer.addEmptyCell(table, borderWidth);
        cellCounter++;
        checkFlush();
    }

    public void addCell(String content, float borderWidth) throws Exception {
        checkCreate();
        this.writer.addCell(table, content, 8, borderWidth, 1, 8F);
        cellCounter++;
        checkFlush();
    }

    public void addCell(Cell cell) throws Exception {
        checkCreate();
        this.writer.addCell(table, cell);
        cellCounter += cell.getColspan();
        checkFlush();
    }

    public void addEngCell(Cell cell) throws Exception {
        checkCreate();
        this.writer.addEngCell(table, cell);
        cellCounter += cell.getColspan();
        checkFlush();
    }

    public boolean addWrapCell(Cell cell) throws Exception {
        if (cell.getColspan() > 1) {
            throw new Exception("addWrapCell(): cell's column span must equal to 1 !!!");
        }
        if (cell.getMaxWidth() <= 0) {
            throw new Exception("addWrapCell(): cell's Max Width must be inited !!!");
        }
        checkCreate();
        float stringWidth = writer.getStringWidth(cell.getContent(), cell.getFontSize());
        if (stringWidth > cell.getMaxWidth()) {
            cellCounter += (1 + (int) (stringWidth / cell.getMaxWidth()) * this.columns);
        } else {
            cellCounter++;
        }
        if (isFull()) {
            return false;
        } else {
            this.writer.addCell(table, cell);
            checkFlush();
            return true;
        }
    }

    public void addWrapCellEx(Cell cell) throws Exception {
        if (cell.getColspan() > 1) {
            throw new Exception("addWrapCell(): cell's column span must equal to 1 !!!");
        }
        if (cell.getMaxWidth() <= 0) {
            throw new Exception("addWrapCell(): cell's Max Width must be inited !!!");
        }
        checkCreate();
        float stringWidth = writer.getStringWidth(cell.getContent(), cell.getFontSize());
        if (stringWidth > cell.getMaxWidth()) {
            cellCounter += (1 + (int) (stringWidth / cell.getMaxWidth()) * this.columns);
        } else {
            cellCounter++;
        }
        if (isFull()) {
            throw new TableFullException();
        } else {
            this.writer.addCell(table, cell);
            checkFlush();
        }
    }
    
    
    public void addSmartCell(Cell cell) throws Exception {
        checkCreate();
        cellCounter += cell.getColspan();
        if (isFull()) {
            throw new TableFullException();
        } else {
            this.writer.addCell(table, cell);
            checkFlush();
        }
    }

    public void addSmartEngCell(Cell cell) throws Exception {
        checkCreate();
        cellCounter += cell.getColspan();
        if (isFull()) {
            throw new TableFullException();
        } else {
            this.writer.addEngCell(table, cell);
            checkFlush();
        }
    }

    public void flush() {
        if (!flushed) {
            float bottom = this.writer.flushTable(table, position.getLeft(), position.getTop());
            position.setTop(bottom);
            if (replicatorPosition != null) {
                replicatorPosition.setTop(Math.min(replicatorPosition.getTop(), position.getTop()));
            }
            flushed = true;
        }
    }

}
