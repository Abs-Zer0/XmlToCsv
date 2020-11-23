/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.to.csv.csv;

import java.util.Vector;

/**
 *
 * @author Абс0лютный Н0ль
 */
public final class CSVTable {

    private final Vector<CSVRow> rows = new Vector<>();
    private final Vector<String> colsName = new Vector<>();

    public CSVTable() {

    }

    /**
     * Добавляет строку в таблицу. Если такая же строка уже существует в
     * таблице, то вставка не происходит
     *
     * @param row строка с заполненными полями
     * @return номер вставленной строки
     */
    public int tryAddRow(CSVRow row) {
        row.getColsName().stream().filter(colName -> (!colsName.contains(colName))).forEachOrdered(colName -> {
            colsName.add(colName);
        });

        if (!rows.contains(row)) {
            rows.add(row);
        }

        return rows.indexOf(row);
    }

    /**
     * Конвертирует все данные таблицы в матрицу строк. В первую строку
     * записываются названия столбцов Удобно для записи в файл
     *
     * @return матричное представление всех данных таблицы
     */
    public String[][] toCSV() {
        int colsCount = colsName.size();
        int rowsCount = rows.size();
        final String[][] result = new String[rowsCount + 1][colsCount];

        for (int i = 0; i < colsCount; i++) {
            final String colName = colsName.get(i);
            result[0][i] = colName;
            for (int j = 0; j < rowsCount; j++) {
                result[j + 1][i] = rows.get(j).getValue(colName);
            }
        }

        return result;
    }
}
