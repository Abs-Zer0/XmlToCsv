/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.to.csv.csv;

import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;

/**
 *
 * @author Абс0лютный Н0ль
 */
public final class CSVRow {

    private final HashMap<String, String> vals = new HashMap<>();

    public CSVRow() {

    }

    /**
     * Возвращает названия столбцов, в которых есть данные
     *
     * @return массив названий столбцов
     */
    public Vector<String> getColsName() {
        return new Vector<>(vals.keySet());
    }

    /**
     * Возвращает данные по названию столбца. Если данные отсутсвуют, то
     * возвращается пустая строка
     *
     * @param colName название столбца
     * @return данные в заданом столбце, пустая строка, если данных нет
     */
    public String getValue(String colName) {
        return vals.getOrDefault(colName, "");
    }

    /**
     * Добавляет данные в столбец
     *
     * @param colName название столбца
     * @param value данные
     */
    public void addValue(String colName, String value) {
        vals.put(colName, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null && !(obj instanceof CSVRow)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        CSVRow row = (CSVRow) obj;
        if (row.hashCode() != this.hashCode()) {
            return false;
        }

        return this.vals.equals(row.vals);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.vals);
        return hash;
    }

}
