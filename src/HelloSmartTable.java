
import com.itextpdf.text.PageSize;
import fc.extensions.itext.Writer;
import fc.extensions.itext.smart.Cell;
import fc.extensions.itext.smart.Position;
import fc.extensions.itext.smart.SmartTable;
import fc.extensions.itext.smart.TableMediator;


public class HelloSmartTable {

    public static void main (String args[]) throws Exception {
        createTable1();
        createTable2();
    }

    /**
     * 2x3 table
     *
     * @param w
     * @throws Exception
     */
    private static void createTable1() throws Exception {
        Writer w = new Writer("table1.pdf", PageSize.A4, "C:\\WINDOWS\\Fonts\\msjh.ttf", "C:\\WINDOWS\\Fonts\\consola.ttf");
        Position p = new Position(10, 10, 200, 800);

        SmartTable t = new SmartTable(w, p, 2, 3, 9, .5f);
        TableMediator tm = new TableMediator(t);
        tm.addCell("小格1");
        tm.addCell("小格2");
        tm.addAnsiCell("Cell3");
        tm.addAnsiCell("Cell4");
        tm.addAnsiCell("Cell5");
        tm.addAnsiCell("Cell6");
        boolean isCellAdded = tm.addAnsiCell("Cell7");
        System.out.println("isCellAdded:" + isCellAdded);
        
        w.close();
    }

    /**
     * mediate two 2x2 table
     *
     * @param w
     * @throws Exception
     */
    private static void createTable2() throws Exception {
        Writer w = new Writer("table2.pdf", PageSize.A4, "C:\\WINDOWS\\Fonts\\msjh.ttf", "C:\\WINDOWS\\Fonts\\consola.ttf");
        Position p = new Position(10, 10, 200, 800);

        SmartTable t = new SmartTable(w, p, 2, 2, 9, .5f);
        TableMediator tm = new TableMediator(t, 2, 5f);
        tm.addCellEx("小格1").addCellEx("小格2").addAnsiCellEx("Cell3").addAnsiCellEx("Cell4").addAnsiCell("Cell5");
        tm.addAnsiCell("Cell6");
        tm.addAnsiCell("Cell7");
        tm.addAnsiCell("Cell8");

        w.close();
    }

}
