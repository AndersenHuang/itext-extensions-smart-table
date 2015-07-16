## Introduction ##
The table is an common element of business documents (ex. Statement / report). But building tables in iText is not an easy job, it's complex, so after the overall analysis, we summarize and simplified the needs of the most frequently requirement. I call it **Smart Table**.

## Examples ##
### one 2x3 table ###
```
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
w.close();
```


### two 2x2 tables ###
```
Writer w = new Writer("table2.pdf", PageSize.A4, "C:\\WINDOWS\\Fonts\\msjh.ttf", "C:\\WINDOWS\\Fonts\\consola.ttf");
Position p = new Position(10, 10, 200, 800);
SmartTable t = new SmartTable(w, p, 2, 2, 9, .5f);
TableMediator tm = new TableMediator(t, 2, 5f);
tm.addCellEx("小格1").addCellEx("小格2");
tm.addAnsiCellEx("Cell3").addAnsiCellEx("Cell4");
tm.addAnsiCell("Cell5");
tm.addAnsiCell("Cell6");
tm.addAnsiCell("Cell7");
tm.addAnsiCell("Cell8");
w.close();
```