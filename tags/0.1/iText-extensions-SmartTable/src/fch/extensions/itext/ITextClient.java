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

package fch.extensions.itext;


import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfBorderDictionary;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import fch.extensions.itext.ptable.EasyCell;
import fch.extensions.itext.ptable.Position;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import org.apache.commons.lang.StringUtils;



/**
 * A wrapper class of iText Framework
 * 
 * @author Andersen.
 */
public final class ITextClient extends PdfPageEventHelper {

    private final static String CONTENT_OF_PAGE_COUNT = "          ";
    private boolean debugMode = false;
    private BaseColor color = null;
    private PdfReader reader = null;
    private Document document = null;
    private String tempFileName = null;
    private PdfWriter writer = null;
    private PdfContentByte writerContentByte = null;
    private PdfImportedPage page = null;
    private BaseFont baseFont = null;
    private BaseFont engBaseFont = null;
    private PdfTemplate pageHeadTemplate = null;
    private Page pageObject = null;
    private boolean isInited = false;
    private HashMap<Integer, Font> fontMap = new HashMap<Integer, Font>();
    private HashMap<Integer, Font> engFontMap = new HashMap<Integer, Font>();
    private String password = "";

    public ITextClient() {
    }

    private Font getFont(int fontSize) {
        if (!fontMap.containsKey(fontSize)) {
            fontMap.put(fontSize, new Font(baseFont, fontSize, Font.UNDEFINED, color));
        }
        return fontMap.get(fontSize);
    }

    private Font getEngFont(int fontSize) {
        if (!engFontMap.containsKey(fontSize)) {
            engFontMap.put(fontSize, new Font(engBaseFont, fontSize, Font.UNDEFINED, color));
        }
        return engFontMap.get(fontSize);
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        this.color = this.debugMode ? BaseColor.RED : BaseColor.BLACK;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void init(String tempFilePath, String pdfFileName, String fontFileName, String engFontFileName) throws Exception {
        try {
            reader = new PdfReader(pdfFileName);

            document = new Document(reader.getPageSize(1));
            File file = File.createTempFile(Long.toString(System.currentTimeMillis()), ".pdf", new File(tempFilePath));
            tempFileName = file.toString();

            writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            if (this.password.length() > 0 ) {
                if (this.debugMode ) {
                    writer.setEncryption(null, this.password.getBytes(), PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_MODIFY_ANNOTATIONS, PdfWriter.STANDARD_ENCRYPTION_128);
                } else {
                    writer.setViewerPreferences(PdfWriter.HideMenubar | PdfWriter.HideWindowUI | PdfWriter.HideToolbar);
                    writer.setEncryption(null, this.password.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.STANDARD_ENCRYPTION_128);
                }
            }
            writer.setPageEvent(this);
            document.open();

            writerContentByte = writer.getDirectContent();

            baseFont = BaseFont.createFont(fontFileName, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            engBaseFont = BaseFont.createFont(engFontFileName, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

        } finally {
            isInited = true;
        }
    }

    public void setPageObject(Page pageObject) throws Exception {
        if (!isInited) {
            throw new Exception("ITextClient: Cannot do setPageObject() before init() !!!");
        }
        if (writer.getCurrentPageNumber() > 1) {
            throw new Exception("ITextClient: Cannot do setPageObject() after first page !!!");
        }
        this.pageObject = pageObject;
        float width = baseFont.getWidthPoint(CONTENT_OF_PAGE_COUNT, pageObject.getHeader().getFontSize());
        float height = pageObject.getHeader().getPosition().getHeight();
        pageHeadTemplate = writer.getDirectContent().createTemplate(width, height);
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
            writerContentByte.saveState();
            writerContentByte.beginText();
            writerContentByte.setFontAndSize(baseFont, fontSize);
            writerContentByte.setTextMatrix(llx, ury);
            writerContentByte.setLeading(fontSize);
            for (int i = 0; i < headerBody.length; i++) {
                alingY = alingY - getLetterHeight(fontSize);
                if (i == headerBody.length - 1) {
                    writerContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT,
                            headerBody[i] + StringUtils.right(CONTENT_OF_PAGE_COUNT + writer.getCurrentPageNumber(), 6) + "/          ",
                            urx, alingY, 0);
                } else {
                    writerContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT,
                            headerBody[i], urx, alingY, 0);
                }
            }
            writerContentByte.endText();
            writerContentByte.addTemplate(pageHeadTemplate, urx - baseFont.getWidthPoint(CONTENT_OF_PAGE_COUNT, fontSize), alingY);
        } finally {
            writerContentByte.restoreState();
        }
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
    }

    public void insertPage(int pageNumberOfTemplate) {
        document.newPage();
        page = writer.getImportedPage(reader, pageNumberOfTemplate);
        writerContentByte.addTemplate(page, 0F, 0F);
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

            writerContentByte.saveState();
            writerContentByte.beginText();
            writerContentByte.setFontAndSize(baseFont, fontSize);
            writerContentByte.setColorFill(this.color);
            writerContentByte.setTextMatrix(leftX, getCenterViaTop(bottomY, topY, letterHeight * (float) arrayLength));
            writerContentByte.setLeading(letterHeight);
            for (String s : textArray) {
                if (s != null) {
                    writerContentByte.newlineShowText(s);
                }
            }
            writerContentByte.endText();
        } finally {
            writerContentByte.restoreState();
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
            writerContentByte.saveState();
            writerContentByte.beginText();
            writerContentByte.setFontAndSize(baseFont, fontSize);
            writerContentByte.setColorFill(this.color);
            writerContentByte.setTextMatrix(leftX, getCenterViaBottom(bottomY, topY, letterHeight));
            writerContentByte.showText(text);
            writerContentByte.endText();
        } finally {
            writerContentByte.restoreState();
        }
    }

    public void writeText(String text, int fontSize, Position position) {
        try {
            position.decreaseTop(fontSize);
            writerContentByte.saveState();
            writerContentByte.beginText();
            writerContentByte.setFontAndSize(baseFont, fontSize);
            writerContentByte.setColorFill(this.color);
            writerContentByte.setTextMatrix(position.getLeft(), position.getTop());
            writerContentByte.showText(text);
            writerContentByte.endText();
            position.decreaseTop(1);
        } finally {
            writerContentByte.restoreState();
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
            writerContentByte.saveState();
            writerContentByte.beginText();
            writerContentByte.setFontAndSize(baseFont, fontSize);
            writerContentByte.setColorFill(this.color);
            switch (alignment.charAt(0)) {
                case 'L':
                    writerContentByte.setTextMatrix(leftX, getCenterViaBottom(bottomY, topY, letterHeight));
                    writerContentByte.showText(text);
                    break;
                case 'C':
                    writerContentByte.showTextAligned(PdfContentByte.ALIGN_CENTER, text, leftX + ((rightX - leftX) / 2F), getCenterViaBottom(bottomY, topY, letterHeight), 0);
                    break;
                case 'R':
                    writerContentByte.showTextAligned(PdfContentByte.ALIGN_RIGHT, text, rightX, getCenterViaBottom(bottomY, topY, letterHeight), 0);
                    break;
                default:
                    throw new Exception("Invalid alignment, alignment: " + alignment);
            }
            writerContentByte.endText();
        } finally {
            writerContentByte.restoreState();
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
        Image image = code39.createImageWithBarcode(writerContentByte, this.color, this.color);
        image.setAbsolutePosition(leftX, bottomY);
        image.scalePercent(scalePercent);
        writerContentByte.addImage(image);
    }

    public void writeImage(String imagePath, float leftX, float bottomY, float scalePercent) throws Exception {
        Image image = Image.getInstance(imagePath);
        image.setAbsolutePosition(leftX, bottomY);
        if(scalePercent > 0F) {
            image.scalePercent(scalePercent);
        }
        writerContentByte.addImage(image);
    }

    public void writeImage(String imagePath, float leftX, float bottomY) throws Exception {
        writeImage(imagePath, leftX, bottomY, 0F);
    }
    
    /**
     * write PDF annotation for debugging.
     */
    public void writeAnnotation(String title, String content, float leftX, float bottomY, float rightX, float topY) throws Exception {
        try {
            writerContentByte.saveState();
            writerContentByte.setRGBColorStroke(255, 255, 0);
            writerContentByte.setLineWidth(1F);
            writerContentByte.rectangle(leftX, bottomY, rightX - leftX, topY - bottomY);
            writerContentByte.stroke();
            PdfFormField field = PdfFormField.createTextField(writer, false, false, 0);
            field.setWidget(new Rectangle(leftX, bottomY, rightX, topY), PdfAnnotation.HIGHLIGHT_INVERT);
            field.setFlags(PdfAnnotation.FLAGS_PRINT);
            field.setFieldName(title);
            field.setPage();
            field.setValueAsString(content);
            field.setBorderStyle(new PdfBorderDictionary(0.5F, PdfBorderDictionary.STYLE_DASHED));
            writer.addAnnotation(field);
        } finally {
            writerContentByte.restoreState();
        }
    }

    public void drawLine(float lineWidth, float fromX, float fromY, float toX, float toY) {
        try {
            writerContentByte.saveState();
            writerContentByte.setLineWidth(lineWidth);
            writerContentByte.setRGBColorStroke(0, 0, 0);
            writerContentByte.moveTo(fromX, fromY);
            writerContentByte.lineTo(toX, toY);
            writerContentByte.stroke();
        } finally {
            writerContentByte.restoreState();
        }
    }

    public void drawLine(float lineWidth, Position position) {
        try {
            position.decreaseTop(lineWidth);
            writerContentByte.saveState();
            writerContentByte.setLineWidth(lineWidth);
            writerContentByte.setRGBColorStroke(0, 0, 0);
            writerContentByte.moveTo(position.getLeft(), position.getTop());
            writerContentByte.lineTo(position.getRight(), position.getTop());
            writerContentByte.stroke();
            position.decreaseTop((lineWidth + 1));
        } finally {
            writerContentByte.restoreState();
        }
    }

    public void drawRectangle(Position position, float lineWidth) {
        try {
            writerContentByte.saveState();
            writerContentByte.setRGBColorStroke(0, 0, 0);
            writerContentByte.setLineWidth(lineWidth);
            writerContentByte.rectangle(position.getLeft(), position.getBottom(), position.getWidth(), position.getHeight());
            writerContentByte.stroke();
        } finally {
            writerContentByte.restoreState();
        }
    }

    public void drawRectangleWithVariableBorder(Position position, float lineWidth, int borderType) {
        try {
            writerContentByte.saveState();
            writerContentByte.setRGBColorStroke(0, 0, 0);
            Rectangle rectangle = new Rectangle(position.getLeft(), position.getBottom(), position.getRight(), position.getTop());
            rectangle.setBorderWidth(lineWidth);
            rectangle.setBorder(borderType);
            rectangle.setUseVariableBorders(true);
            writerContentByte.rectangle(position.getLeft(), position.getBottom(), position.getWidth(), position.getHeight());
            writerContentByte.stroke();
        } finally {
            writerContentByte.restoreState();
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
                writerContentByte.saveState();
                writerContentByte.setRGBColorStroke(0, 0, 0);
                writerContentByte.setLineWidth(lineWidth);
                writerContentByte.rectangle(left, position.getBottom(), width, height);
                writerContentByte.stroke();
            } finally {
                writerContentByte.restoreState();
            }
            left += width;
        }
    }

    public PdfPTable createTable(int columns, float width, int[] columnWidthScale) throws Exception {
        PdfPTable pdfTable = new PdfPTable(columns);
        pdfTable.setTotalWidth(width);
        pdfTable.setWidths(columnWidthScale);
        return pdfTable;
    }

    public void addCell(PdfPTable table, String content, int fontSize, float borderWidth, int columnSpan, float fixedHeight) {
        PdfPCell pdfCell = new PdfPCell();
        if (columnSpan > 1) {
            pdfCell.setColspan(columnSpan);
        }
        pdfCell.setFixedHeight(fixedHeight);
        pdfCell.setBorderWidth(borderWidth);
        pdfCell.setPhrase(new Phrase(content, getFont(fontSize)));
        pdfCell.setNoWrap(false);
        pdfCell.setHorizontalAlignment(PdfContentByte.ALIGN_LEFT);
        pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(pdfCell);
    }

    public void addCell(PdfPTable table, EasyCell cell) {
        PdfPCell pcell = cell.getPdfPCell();
        pcell.setPhrase(new Phrase(cell.getContent(), getFont(cell.getFontSize())));
        table.addCell(pcell);
    }

    public void addEmptyCell(PdfPTable table, float borderWidth) {
        table.getDefaultCell().setBorderWidth(borderWidth);
        table.addCell("");
    }

    public void addEngCell(PdfPTable table, EasyCell cell) {
        PdfPCell pcell = cell.getPdfPCell();
        pcell.setPhrase(new Phrase(cell.getContent(), getEngFont(cell.getFontSize())));
        table.addCell(pcell);
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
        return table.writeSelectedRows(0, -1, leftX, topY, writerContentByte);
    }

    public boolean isTampered() {
        return reader.isTampered();
    }

    public String flush() throws Exception {
        document.close();
        return tempFileName;
    }

    public int getCurrentPageNumber() {
        return writer.getCurrentPageNumber();
    }
}
