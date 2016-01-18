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

import com.itextpdf.text.pdf.PdfPCell;

public class Cell {

    public static final int ALIGN_LEFT = PdfPCell.ALIGN_LEFT;
    public static final int ALIGN_CENTER = PdfPCell.ALIGN_CENTER;
    public static final int ALIGN_RIGHT = PdfPCell.ALIGN_RIGHT;
    
    public static final int ALIGN_TOP = PdfPCell.ALIGN_TOP;
    public static final int ALIGN_MIDDLE = PdfPCell.ALIGN_MIDDLE;
    public static final int ALIGN_BOTTOM = PdfPCell.ALIGN_BOTTOM;

    public static final class BorderType {
        private BorderType() {}
        public static final int LEFT = PdfPCell.LEFT;
        public static final int TOP = PdfPCell.TOP;
        public static final int RIGHT = PdfPCell.RIGHT;
        public static final int BOTTOM = PdfPCell.BOTTOM;
        public static final int BOX = PdfPCell.BOX;
        public static final int NO_BORDER = PdfPCell.NO_BORDER;
    }

    public static final class FontType {
        private FontType() {}
        public static final int NORMAL = 1;
        public static final int DBCS = 2;
    }

    private PdfPCell pdfPCell = null;
    private String content = "";
    private int fontSize = 8;
    private float maxWidth = -1F;
    private int fontType = FontType.NORMAL;

    public Cell() {
        this("", 0F, 8, 1);
    }

    public Cell(String content) {
        this(content, 0F, 8, 1);
    }

    public Cell(String content, float borderWidth) {
        this(content, borderWidth, 8, 1);
    }

    public Cell(String content, int columnSpan) {
        this(content, 0F, 8, columnSpan);
    }

    public Cell(String content, float borderWidth, int fontSize) {
        this(content, borderWidth, fontSize, 1);
    }

    public Cell(String content, float borderWidth, int fontSize, int columnSpan) {
        this.content = content;
        this.fontSize = fontSize;
        pdfPCell = new PdfPCell();
        pdfPCell.setBorderWidth(borderWidth);
        pdfPCell.setColspan(columnSpan);
    }

    public PdfPCell getPdfPCell() {
        return pdfPCell;
    }
    
    public int getFontType() {
        return fontType;
    }

    public void setFontType(int fontType) {
        this.fontType = fontType;
    }

    public void setFollowingIndent(float followingindent) {
        pdfPCell.setFollowingIndent(followingindent);
    }
    
    public void setIndent(float indent) {
        pdfPCell.setIndent(indent);
    }

    public void setLeading(float fixedLeading, float multipliedLeading) {
        pdfPCell.setLeading(fixedLeading, multipliedLeading);
    }
    
    /**
     * Enables/Disables the border on the specified sides. The border is
     * specified as an integer bitwise combination of the constants: <CODE>
     * LEFT, RIGHT, TOP, BOTTOM</CODE>.
     */
    public void setBorder(int borderType) {
        pdfPCell.setBorder(borderType);
    }

    /**
     * The padding is the space between the content of a cell and its borders
     * 
     * @param padding
     */
    public void setPadding(float padding) {
        pdfPCell.setPadding(padding);
    }
    
    public float getPaddingLeft() {
        return pdfPCell.getPaddingLeft();
    }

    public void setPaddingLeft(float padding) {
        pdfPCell.setPaddingLeft(padding);
    }
    
    public float getPaddingRight() {
        return pdfPCell.getPaddingRight();
    }

    public void setPaddingRight(float padding) {
        pdfPCell.setPaddingRight(padding);
    }

    public void setPaddingTop(float padding) {
        pdfPCell.setPaddingTop(padding);
    }

    public void setPaddingBottom(float padding) {
        pdfPCell.setPaddingBottom(padding);
    }    
    
    public void setUseBorderPadding(boolean use) {
        pdfPCell.setUseBorderPadding(use);
    }
    
    public void setUseAscender(boolean use) {
        pdfPCell.setUseAscender(use);
    }

    public void setUseDescender(boolean use) {
        pdfPCell.setUseDescender(use);
    }    
    
    public void setHorizontalAlignment(int horizontalAlignment) {
        pdfPCell.setHorizontalAlignment(horizontalAlignment);
    }

    public void setVerticalAlignment(int verticalAlignment) {
        pdfPCell.setVerticalAlignment(verticalAlignment);
    }    

    public float getFixedHeight() {
        return pdfPCell.getFixedHeight();
    }

    public void setFixedHeight(float fixedHeight) {
        pdfPCell.setFixedHeight(fixedHeight);
    }

    public void setBorderWidth(float borderWidth) {
        pdfPCell.setBorderWidth(borderWidth);
    }

    public int getColspan() {
        return pdfPCell.getColspan();
    }

    public void setColspan(int columnSpan) {
        pdfPCell.setColspan(columnSpan);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public float getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
    }
}
