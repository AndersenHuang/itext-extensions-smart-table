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

import com.itextpdf.text.DocumentException;
import fc.extensions.itext.Writer;
import com.itextpdf.text.pdf.PdfPTable;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private int defaultFontSize = 8;
    private boolean autoFlush = true;

    public SmartTable(Writer client, Position position, int columns, int rows) throws Exception {
        this(client, position, columns, rows, 9, 0f, null);
    }

    public SmartTable(Writer client, Position position, int columns, int rows, int defaultFontSize, float borderWidth) throws Exception {
        this(client, position, columns, rows, defaultFontSize, borderWidth, null);
    }

    public SmartTable(Writer client, Position position, int columns, int rows, int defaultFontSize, float borderWidth, int[] columnWidthsScale) throws Exception {
        this.writer = client;
        this.position = position;
        this.columns = columns;
        this.rows = rows;
        this.borderWidth = borderWidth;
        this.setColumnWidthsScale(columnWidthsScale);
        this.defaultFontSize = defaultFontSize;
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

    public int getDefaultFontSize() {
        return defaultFontSize;
    }

    public void setDefaultFontSize(int defaultFontSize) {
        this.defaultFontSize = defaultFontSize;
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

    public void setColumnWidthsScale(int[] columnWidthsScale) throws Exception {
        if (columnWidthsScale.length != this.columns) {
            throw new Exception("the number of widths is different than the number of columns");
        }
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

    private void create() {
        if (!created) {
            if (columnWidthsScale == null || columnWidthsScale.length == 0) {
                columnWidthsScale = new int[columns];
                for (int i = 0; i < columnWidthsScale.length; i++) {
                    columnWidthsScale[i] = 1;
                }
            }
            try {
                table = this.writer.createTable(columns, position.getWidth(), columnWidthsScale);
            } catch (DocumentException ex) {
                Logger.getLogger(SmartTable.class.getName()).log(Level.SEVERE, null, ex);
            }
            table.getDefaultCell().setBorderWidth(borderWidth);
            table.getDefaultCell().setFixedHeight(rowFixedHeight);
            table.getDefaultCell().setPadding(0.2F);
            created = true;
        }
    }

    private void checkFlush() throws TableFlushedException {
        if (autoFlush) {
            if (cellCounter > columns * rows) {
                throw new TableFlushedException("cant add Cell after flushed! cellCounter: " + cellCounter + ",columns: " + columns + ",rows:" + rows);
            }
            if (!flushed && cellCounter == columns * rows) {
                flush();
            }
        }
    }

    private boolean isFull() {
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

    private void checkCreate() {
        if (!created) {
            create();
        }
    }

    /**
     * add an empty cell
     *
     * @throws TableWasFullException
     * @throws TableFlushedException
     */
    public void addEmptyCell() throws TableWasFullException, TableFlushedException {
        checkCreate();
        this.writer.addEmptyCell(table, borderWidth);
        cellCounter++;
        checkFlush();
    }

    /**
     * add a cell with DBCS content, use table's borderwidth, fontsize and etc,.
     * 
     * @param content
     * @throws TableWasFullException
     * @throws TableFlushedException
     */
    public void addCell(String content) throws TableWasFullException, TableFlushedException {
        checkCreate();
        if (isFull()) {
            throw new TableWasFullException();
        } else {
            cellCounter++;
            this.writer.addCell(table, content, defaultFontSize, borderWidth, 1);
            checkFlush();
        }
    }

    /**
     * add a cell with ANSI char content, use table's borderwidth, fontsize and etc,.
     *
     * @param content
     * @throws TableWasFullException
     * @throws TableFlushedException
     */
    public void addEngCell(String content) throws TableWasFullException, TableFlushedException {
        checkCreate();
        if (isFull()) {
            throw new TableWasFullException();
        } else {
            cellCounter ++;
            this.writer.addEngCell(table, content, defaultFontSize, borderWidth, 1);
            checkFlush();
        }
    }

    /**
     * add a cell by Cell object, use it's attributes.
     *
     * @param cell
     * @throws TableWasFullException
     * @throws TableFlushedException
     */
    public void addCell(Cell cell) throws TableWasFullException, TableFlushedException {
        checkCreate();
        cellCounter += cell.getColspan();
        if (isFull()) {
            throw new TableWasFullException();
        } else {
            this.writer.addCell(table, cell);
            checkFlush();
        }
    }

    /**
     * add a cell, wrap the content into next row.
     *
     * @param content
     * @param maxCellWidth
     * @throws TableWasFullException
     * @throws TableFlushedException
     */
    public void addCrossRowCell(String content, float maxCellWidth) throws TableWasFullException, TableFlushedException {
        Cell cell = new Cell(content);
        cell.setColspan(1);
        cell.setMaxWidth(maxCellWidth);
        checkCreate();
        float stringWidth = writer.getStringWidth(cell.getContent(), cell.getFontSize());
        if (stringWidth > cell.getMaxWidth()) {
            cellCounter += (1 + (int) (stringWidth / cell.getMaxWidth()) * this.columns);
        } else {
            cellCounter++;
        }
        if (isFull()) {
            throw new TableWasFullException();
        } else {
            this.writer.addCell(table, cell);
            checkFlush();
        }
    }

    /**
     * render this table.
     */
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
