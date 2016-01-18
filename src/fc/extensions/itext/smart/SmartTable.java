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
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.beanutils.locale.LocaleBeanUtils;

/**
 * A PdfPTable wrapper supports more functions.
 * 
 * @author Administrator
 */
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
    private int cellFontSize = 9;
    private boolean autoFlush = true;

    public SmartTable(Writer client, Position position, int columns, int rows, int cellFontSize, float borderWidth) throws Exception {
        this(client, position, columns, rows, cellFontSize, borderWidth, null);
    }

    public SmartTable(Writer client, Position position, int columns, int rows, int cellFontSize, float borderWidth, int[] columnWidthsScale) throws Exception {
        this.writer = client;
        this.position = position;
        this.columns = columns;
        this.rows = rows;
        this.borderWidth = borderWidth;
        this.cellFontSize = cellFontSize;

        if (columnWidthsScale != null) {
            if (columnWidthsScale.length != this.columns) {
                throw new Exception("the number of widths is different than the number of columns");
            }
            this.columnWidthsScale = columnWidthsScale;
        }
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
        return cellFontSize;
    }

    public void setDefaultFontSize(int defaultFontSize) {
        this.cellFontSize = defaultFontSize;
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

    public int[] getColumnWidthsScale() {
        return columnWidthsScale;
    }

    public void setColumnWidthsScale(int[] columnWidthsScale) {
        this.columnWidthsScale = columnWidthsScale;
    }

    private void create() {
        if (!created) {
            if (columnWidthsScale == null || columnWidthsScale.length == 0) {
                columnWidthsScale = new int[columns];
                Arrays.fill(columnWidthsScale, 1);
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

    private void checkFlush() {
        if (autoFlush) {
            if (cellCounter > columns * rows) {
                Logger.getLogger(SmartTable.class.getName()).log(Level.SEVERE, "this case shouldn't be happened. cellCounter: " + cellCounter + ",columns: " + columns + ",rows:" + rows, new TableFlushedException());
                return;
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
     */
    public void addEmptyCell() throws TableWasFullException {
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
     */
    public void addCell(String content) throws TableWasFullException {
        checkCreate();
        if (isFull()) {
            throw new TableWasFullException();
        } else {
            cellCounter++;
            this.writer.addCell(table, content, cellFontSize, borderWidth, 1);
            checkFlush();
        }
    }

    /**
     * add a cell with ANSI char content, use table's borderwidth, fontsize and etc,.
     *
     * @param content
     * @throws TableWasFullException
     */
    public void addAnsiCell(String content) throws TableWasFullException {
        checkCreate();
        if (isFull()) {
            throw new TableWasFullException();
        } else {
            cellCounter ++;
            this.writer.addAnsiCell(table, content, cellFontSize, borderWidth, 1);
            checkFlush();
        }
    }

    /**
     * add a cell by Cell object, use it's attributes.
     *
     * @param cell
     * @throws TableWasFullException
     */
    public void addCell(Cell cell) throws TableWasFullException {
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
     */
    public void addCrossRowCell(String content, float maxCellWidth) throws TableWasFullException {
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
