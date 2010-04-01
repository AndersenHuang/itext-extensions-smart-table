
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

    /**
     * 2x3 table
     *
     * @param w
     * @throws Exception
     */
    private static void createTable1(Writer w) throws Exception {
        Position p = new Position(10, 10, 200, 600);

        SmartTable t = new SmartTable(w, p, 2, 2, 9, .5f);
        TableMediator tm = new TableMediator(t);
        tm.addCell("小格1");
        tm.addCell("小格2");
        tm.addEngCell("Cell3");
        tm.addEngCell("Cell4");
        tm.addEngCell("Cell5");
        tm.flush();
    }
    

}
