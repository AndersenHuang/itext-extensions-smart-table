
import com.itextpdf.text.PageSize;
import fc.extensions.itext.Writer;
import fc.extensions.itext.smart.Cell;
import fc.extensions.itext.smart.Position;
import fc.extensions.itext.smart.SmartTable;
import fc.extensions.itext.smart.TableMediator;


public class HelloSmartTable {
    public static void main (String args[]) throws Exception {
        Writer writer = new Writer("HelloSmartTable.pdf", PageSize.A4, "C:\\WINDOWS\\Fonts\\msjh.ttf", "C:\\WINDOWS\\Fonts\\consola.ttf");

        createTable1(writer);

        writer.close();
    }

    // 1. single table
    // 2. two column table
    // 3. table cross page

    private static void createTable1(Writer w) throws Exception {
        Position p = new Position(10, 10, 200, 600);

        SmartTable t = new SmartTable(w, p, 1, 2, 1F);
        TableMediator tm = new TableMediator(t);
        tm.addCell(new Cell("abc"));
        tm.addCell(new Cell("def"));
        tm.flush();

    }
}
