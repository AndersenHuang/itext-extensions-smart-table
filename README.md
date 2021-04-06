# itext-extensions-smart-table

This extensions can use iText to create PDF tables more easily, reduce efforts when building E-Statement Java program.
Here is an example.

```java
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
```

### TODO
need to be upgrade to latest library
