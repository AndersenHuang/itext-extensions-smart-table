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

package fc.extensions.itext;


import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfBorderDictionary;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import fc.extensions.itext.smart.Cell;
import fc.extensions.itext.smart.Position;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import org.apache.commons.lang.StringUtils;



/**
 * A wrapper class of iText Framework
 * 
 * @author Andersen.
 */
public final class Writer extends PdfPageEventHelper {

    private final static String CONTENT_OF_PAGE_COUNT = "          ";

    private Document document = null;
    private BaseColor baseColor = null;
    private File pdfFile = null;
    private PdfWriter pdfWriter = null;
    private PdfContentByte pdfWriterCB = null;
    private BaseFont baseFont = null;
    private BaseFont engBaseFont = null;
    private PdfTemplate pageHeadTemplate = null;
    private Page pageObject = null;
    private HashMap<Integer, Font> fontMap = new HashMap<Integer, Font>();
    private HashMap<Integer, Font> engFontMap = new HashMap<Integer, Font>();

    public Writer(String pdfFile, Rectangle pageSize, String fontFile, String engFontFile) throws Exception {
        this.pdfFile = new File(pdfFile);

        document = new Document(pageSize);
        pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(this.pdfFile));
        pdfWriter.setPageEvent(this);
        document.open();
        pdfWriterCB = pdfWriter.getDirectContent();
        baseColor = BaseColor.BLACK;
        baseFont = BaseFont.createFont(fontFile, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        engBaseFont = BaseFont.createFont(engFontFile, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
    }

    private Font getFont(int fontSize) {
        if (!fontMap.containsKey(fontSize)) {
            fontMap.put(fontSize, new Font(baseFont, fontSize, Font.UNDEFINED, baseColor));
        }
        return fontMap.get(fontSize);
    }

    private Font getEngFont(int fontSize) {
        if (!engFontMap.containsKey(fontSize)) {
            engFontMap.put(fontSize, new Font(engBaseFont, fontSize, Font.UNDEFINED, baseColor));
        }
        return engFontMap.get(fontSize);
    }

    public void setDebug() {
        this.baseColor = BaseColor.RED;
    }

    public void setPage(Page page) throws Exception {
        if (pdfWriter.getCurrentPageNumber() > 1) {
            throw new Exception("ITextClient: Cannot do setPageObject() after first page !!!");
        }
        this.pageObject = page;
        float width = baseFont.getWidthPoint(CONTENT_OF_PAGE_COUNT, page.getHeader().getFontSize());
        float height = page.getHeader().getPosition().getHeight();
        pageHeadTemplate = pdfWriter.getDirectContent().createTemplate(width, height);
        pageHeadTemplate.setBoundingBox(new Rectangle(0F, 0F, width, height));
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        if (pageObject == null) {
            return;
        }
        pageHeadTemplate.beginText();
        pageHeadTemplate.setFontAndSize(baseFont, pageObject.getHeader().getFontSize());
        pageHeadTemplate.showTextAligned(PdfContentByte.ALIGN_RIGHT, StringUtils.right(CONTENT_OF_PAGE_COUNT + (writer.getCurrentPageNumber() - 1), 6), pageHeadTemplate.getWidth(), 0F, 0);
        pageHeadTemplate.endText();
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        if (pageObject == null) {
            return;
        }
        float llx = pageObject.getHeader().getPosition().getLeft();
        float lly = pageObject.getHeader().getPosition().getBottom();
        float urx = pageObject.getHeader().getPosition().getRight();
        float ury = pageObject.getHeader().getPosition().getTop();
        float alingY = pageObject.getHeader().getPosition().getTop();
        int fontSize = (int) pageObject.getHeader().getFontSize();

        String[] headerBody = pageObject.getHeader().getBody();
        try {
            pdfWriterCB.saveState();
            pdfWriterCB.beginText();
            pdfWriterCB.setFontAndSize(baseFont, fontSize);
            pdfWriterCB.setTextMatrix(llx, ury);
            pdfWriterCB.setLeading(fontSize);
            for (int i = 0; i < headerBody.length; i++) {
                alingY = alingY - getLetterHeight(fontSize);
                if (i == headerBody.length - 1) {
                    pdfWriterCB.showTextAligned(PdfContentByte.ALIGN_RIGHT,
                            headerBody[i] + StringUtils.right(CONTENT_OF_PAGE_COUNT + writer.getCurrentPageNumber(), 6) + "/          ",
                            urx, alingY, 0);
                } else {
                    pdfWriterCB.showTextAligned(PdfContentByte.ALIGN_RIGHT,
                            headerBody[i], urx, alingY, 0);
                }
            }
            pdfWriterCB.endText();
            pdfWriterCB.addTemplate(pageHeadTemplate, urx - baseFont.getWidthPoint(CONTENT_OF_PAGE_COUNT, fontSize), alingY);
        } finally {
            pdfWriterCB.restoreState();
        }
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
    }

    public void insertPage(int pageNumberOfTemplate) {
        document.newPage();
    }

    public float getLetterHeight(int fontSize) {
        return baseFont.getWidthPoint("中", fontSize);
    }

    public float getStringWidth(String content, int fontSize) {
        return baseFont.getWidthPoint(content, fontSize);
    }

    public void writeTextArray(String[] textArray, int fontSize, float leftX, float bottomY, float rightX, float topY) {
        try {
            float letterHeight = getLetterHeight(fontSize);

            int arrayLength = 0;
            for (String s : textArray) {
                if (s != null) {
                    arrayLength++;
                }
            }

            pdfWriterCB.saveState();
            pdfWriterCB.beginText();
            pdfWriterCB.setFontAndSize(baseFont, fontSize);
            pdfWriterCB.setColorFill(this.baseColor);
            pdfWriterCB.setTextMatrix(leftX, getCenterViaTop(bottomY, topY, letterHeight * (float) arrayLength));
            pdfWriterCB.setLeading(letterHeight);
            for (String s : textArray) {
                if (s != null) {
                    pdfWriterCB.newlineShowText(s);
                }
            }
            pdfWriterCB.endText();
        } finally {
            pdfWriterCB.restoreState();
        }
    }

    public void writeWrapText(String text, int fontSize, float leftX, float bottomY, float rightX, float topY) throws Exception {
        int realTextWidth = (int) baseFont.getWidthPoint(text, fontSize);
        int columnWidth = (int) (rightX - leftX);

        if (columnWidth >= realTextWidth) {
            writeText(text, fontSize, leftX, bottomY, topY);
        } else {
            String[] textArray;
            if (realTextWidth % columnWidth == 0) {
                textArray = new String[(realTextWidth / columnWidth) + 1];
            } else {
                textArray = new String[(realTextWidth / columnWidth) + 2];
            }
            int tmpWidth = 0;
            int start = 0;
            int index = 0;
            for (int i = 0; i < text.length(); i++) {
                tmpWidth += Math.round(baseFont.getWidthPoint(text.substring(i, i + 1), fontSize));
                if (tmpWidth >= (columnWidth - fontSize)) {
                    textArray[index] = text.substring(start, i);
                    start = i;
                    index++;
                    tmpWidth = 0;
                }
            }
            if (start < text.length()) {
                textArray[index] = text.substring(start);
            }

            writeTextArray(textArray, fontSize, leftX, bottomY, rightX, topY);
        }
    }

    public void writeText(String text, int fontSize, float leftX, float bottomY, float topY) {
        try {
            float letterHeight = getLetterHeight(fontSize);
            pdfWriterCB.saveState();
            pdfWriterCB.beginText();
            pdfWriterCB.setFontAndSize(baseFont, fontSize);
            pdfWriterCB.setColorFill(this.baseColor);
            pdfWriterCB.setTextMatrix(leftX, getCenterViaBottom(bottomY, topY, letterHeight));
            pdfWriterCB.showText(text);
            pdfWriterCB.endText();
        } finally {
            pdfWriterCB.restoreState();
        }
    }

    public void writeText(String text, int fontSize, Position position) {
        try {
            position.decreaseTop(fontSize);
            pdfWriterCB.saveState();
            pdfWriterCB.beginText();
            pdfWriterCB.setFontAndSize(baseFont, fontSize);
            pdfWriterCB.setColorFill(this.baseColor);
            pdfWriterCB.setTextMatrix(position.getLeft(), position.getTop());
            pdfWriterCB.showText(text);
            pdfWriterCB.endText();
            position.decreaseTop(1);
        } finally {
            pdfWriterCB.restoreState();
        }
    }

    private static float getCenterViaBottom(float bottomY, float topY, float letterHeight) {
        float tmpY = bottomY + ((topY - bottomY) / 2F) - letterHeight * 0.35F;
        return tmpY;
    }

    private static float getCenterViaTop(float bottomY, float topY, float letterHeight) {
        float tmpY = topY - ((topY - bottomY) / 2F) + letterHeight * 0.6F;
        return tmpY;
    }

    public void writeAlignedText(String text, int fontSize, String alignment, float leftX, float bottomY, float rightX, float topY) throws Exception {
        try {
            float letterHeight = getLetterHeight(fontSize);
            pdfWriterCB.saveState();
            pdfWriterCB.beginText();
            pdfWriterCB.setFontAndSize(baseFont, fontSize);
            pdfWriterCB.setColorFill(this.baseColor);
            switch (alignment.charAt(0)) {
                case 'L':
                    pdfWriterCB.setTextMatrix(leftX, getCenterViaBottom(bottomY, topY, letterHeight));
                    pdfWriterCB.showText(text);
                    break;
                case 'C':
                    pdfWriterCB.showTextAligned(PdfContentByte.ALIGN_CENTER, text, leftX + ((rightX - leftX) / 2F), getCenterViaBottom(bottomY, topY, letterHeight), 0);
                    break;
                case 'R':
                    pdfWriterCB.showTextAligned(PdfContentByte.ALIGN_RIGHT, text, rightX, getCenterViaBottom(bottomY, topY, letterHeight), 0);
                    break;
                default:
                    throw new Exception("Invalid alignment, alignment: " + alignment);
            }
            pdfWriterCB.endText();
        } finally {
            pdfWriterCB.restoreState();
        }
    }

    public void writeBarcode(String barcode, float leftX, float bottomY) throws Exception {
        writeBarcode(barcode, leftX, bottomY, 100F, true);
    }

    public void writeBarcode(String barcode, float leftX, float bottomY, float scalePercent, boolean isShowCode) throws Exception {
        Barcode39 code39 = new Barcode39();
        code39.setStartStopText(false);
        code39.setTextAlignment(PdfContentByte.ALIGN_LEFT);
        if (!isShowCode) {
            code39.setFont(null);
        }
        code39.setCode(barcode);
        Image image = code39.createImageWithBarcode(pdfWriterCB, this.baseColor, this.baseColor);
        image.setAbsolutePosition(leftX, bottomY);
        image.scalePercent(scalePercent);
        pdfWriterCB.addImage(image);
    }

    public void writeImage(String imagePath, float leftX, float bottomY, float scalePercent) throws Exception {
        Image image = Image.getInstance(imagePath);
        image.setAbsolutePosition(leftX, bottomY);
        if(scalePercent > 0F) {
            image.scalePercent(scalePercent);
        }
        pdfWriterCB.addImage(image);
    }

    public void writeImage(String imagePath, float leftX, float bottomY) throws Exception {
        writeImage(imagePath, leftX, bottomY, 0F);
    }
    
    /**
     * write PDF annotation for debugging.
     */
    public void writeAnnotation(String title, String content, float leftX, float bottomY, float rightX, float topY) throws Exception {
        try {
            pdfWriterCB.saveState();
            pdfWriterCB.setRGBColorStroke(255, 255, 0);
            pdfWriterCB.setLineWidth(1F);
            pdfWriterCB.rectangle(leftX, bottomY, rightX - leftX, topY - bottomY);
            pdfWriterCB.stroke();
            PdfFormField field = PdfFormField.createTextField(pdfWriter, false, false, 0);
            field.setWidget(new Rectangle(leftX, bottomY, rightX, topY), PdfAnnotation.HIGHLIGHT_INVERT);
            field.setFlags(PdfAnnotation.FLAGS_PRINT);
            field.setFieldName(title);
            field.setPage();
            field.setValueAsString(content);
            field.setBorderStyle(new PdfBorderDictionary(0.5F, PdfBorderDictionary.STYLE_DASHED));
            pdfWriter.addAnnotation(field);
        } finally {
            pdfWriterCB.restoreState();
        }
    }

    public void drawLine(float lineWidth, float fromX, float fromY, float toX, float toY) {
        try {
            pdfWriterCB.saveState();
            pdfWriterCB.setLineWidth(lineWidth);
            pdfWriterCB.setRGBColorStroke(0, 0, 0);
            pdfWriterCB.moveTo(fromX, fromY);
            pdfWriterCB.lineTo(toX, toY);
            pdfWriterCB.stroke();
        } finally {
            pdfWriterCB.restoreState();
        }
    }

    public void drawLine(float lineWidth, Position position) {
        try {
            position.decreaseTop(lineWidth);
            pdfWriterCB.saveState();
            pdfWriterCB.setLineWidth(lineWidth);
            pdfWriterCB.setRGBColorStroke(0, 0, 0);
            pdfWriterCB.moveTo(position.getLeft(), position.getTop());
            pdfWriterCB.lineTo(position.getRight(), position.getTop());
            pdfWriterCB.stroke();
            position.decreaseTop((lineWidth + 1));
        } finally {
            pdfWriterCB.restoreState();
        }
    }

    public void drawRectangle(Position position, float lineWidth) {
        try {
            pdfWriterCB.saveState();
            pdfWriterCB.setRGBColorStroke(0, 0, 0);
            pdfWriterCB.setLineWidth(lineWidth);
            pdfWriterCB.rectangle(position.getLeft(), position.getBottom(), position.getWidth(), position.getHeight());
            pdfWriterCB.stroke();
        } finally {
            pdfWriterCB.restoreState();
        }
    }

    public void drawRectangleWithVariableBorder(Position position, float lineWidth, int borderType) {
        try {
            pdfWriterCB.saveState();
            pdfWriterCB.setRGBColorStroke(0, 0, 0);
            Rectangle rectangle = new Rectangle(position.getLeft(), position.getBottom(), position.getRight(), position.getTop());
            rectangle.setBorderWidth(lineWidth);
            rectangle.setBorder(borderType);
            rectangle.setUseVariableBorders(true);
            pdfWriterCB.rectangle(position.getLeft(), position.getBottom(), position.getWidth(), position.getHeight());
            pdfWriterCB.stroke();
        } finally {
            pdfWriterCB.restoreState();
        }
    }

    /**
     * split the rectangle in vertical.
     */
    public void drawRectangle(Position position, float lineWidth, int splitSize) {
        float width = position.getWidth() / splitSize;
        float height = position.getHeight();
        float left = position.getLeft();
        for (int i = 1; i <= splitSize; i++) {
            try {
                pdfWriterCB.saveState();
                pdfWriterCB.setRGBColorStroke(0, 0, 0);
                pdfWriterCB.setLineWidth(lineWidth);
                pdfWriterCB.rectangle(left, position.getBottom(), width, height);
                pdfWriterCB.stroke();
            } finally {
                pdfWriterCB.restoreState();
            }
            left += width;
        }
    }

    public PdfPTable createTable(int columns, float width, int[] columnWidthScale) throws DocumentException {
        PdfPTable pdfTable = new PdfPTable(columns);
        //table.setTableEvent(null);
        pdfTable.setTotalWidth(width);
        pdfTable.setWidths(columnWidthScale);
        return pdfTable;
    }

    public void addEmptyCell(PdfPTable table, float borderWidth) {
        table.getDefaultCell().setBorderWidth(borderWidth);
        table.addCell("");
    }

    public void addCell(PdfPTable table, String content, int fontSize, float borderWidth, int columnSpan) {
        PdfPCell pCell = new PdfPCell();
        if (columnSpan > 1) {
            pCell.setColspan(columnSpan);
        }
        pCell.setBorderWidth(borderWidth);
        pCell.setPhrase(new Phrase(content, getFont(fontSize)));
        pCell.setNoWrap(false);
        pCell.setHorizontalAlignment(PdfContentByte.ALIGN_LEFT);
        pCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(pCell);
    }

    public void addEngCell(PdfPTable table, String content, int fontSize, float borderWidth, int columnSpan) {
        PdfPCell pCell = new PdfPCell();
        if (columnSpan > 1) {
            pCell.setColspan(columnSpan);
        }
        pCell.setBorderWidth(borderWidth);
        pCell.setPhrase(new Phrase(content, getEngFont(fontSize)));
        pCell.setNoWrap(false);
        pCell.setHorizontalAlignment(PdfContentByte.ALIGN_LEFT);
        pCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(pCell);
    }

    public void addCell(PdfPTable table, Cell cell) {
        PdfPCell pCell = cell.getPdfPCell();
        if (cell.getFontType() == Cell.FontType.DBCS) {
            pCell.setPhrase(new Phrase(cell.getContent(), getFont(cell.getFontSize())));
        } else {
            pCell.setPhrase(new Phrase(cell.getContent(), getEngFont(cell.getFontSize())));
        }
        table.addCell(pCell);
    }

    /**
     * Flush the table in memeory
     * 
     * @param table
     * @param leftX
     * @param topY
     * @return Y-coord of the bottom position of table
     */
    public float flushTable(PdfPTable table, float leftX, float topY) {
        // table.setTableEvent(null);
        return table.writeSelectedRows(0, -1, leftX, topY, pdfWriterCB);
    }

    public void close() throws Exception {
        document.close();
    }

    public int getCurrentPageNumber() {
        return pdfWriter.getCurrentPageNumber();
    }
}
