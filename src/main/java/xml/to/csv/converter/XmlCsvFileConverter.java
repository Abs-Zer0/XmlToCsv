/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.to.csv.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import xml.to.csv.csv.CSVRow;
import xml.to.csv.csv.CSVTable;

/**
 *
 * @author Абс0лютный Н0ль
 */
public final class XmlCsvFileConverter extends XmlFileConverter {

    private final HashMap<String, CSVTable> tables = new HashMap<>();

    /**
     * Создаёт экземпляр класса XmlCsvFileConverter для парсинга XML файлов в
     * CSV
     *
     * @param xmlFile ресурс, из которого извлекается данные XML файла
     */
    public XmlCsvFileConverter(InputSource xmlFile) {
        super(xmlFile);
    }

    /**
     * Создаёт экземпляр класса XmlCsvFileConverter для парсинга XML файлов в
     * CSV
     *
     * @param xmlFile поток, из которого считываются данные XML файла
     */
    public XmlCsvFileConverter(InputStream xmlFile) {
        super(xmlFile);
    }

    /**
     * Создаёт экземпляр класса XmlCsvFileConverter для парсинга XML файлов в
     * CSV
     *
     * @param xmlFile файл, из которого считываются данные
     */
    public XmlCsvFileConverter(File xmlFile) throws FileNotFoundException {
        super(xmlFile);
    }

    @Override
    public boolean convert(Appendable outputFile) {
		if (this.xmlDocument == null) {
			return false;
		}
		
        if (this.tables.isEmpty()) {
            fillTables();
        }

        if (this.tables.isEmpty()) {
            return false;
        }

        try (CSVPrinter printer = new CSVPrinter(outputFile, CSVFormat.ORACLE)) {
            for (String tableName : this.tables.keySet()) {
                final CSVTable table = this.tables.get(tableName);
                printer.printRecord(tableName + ":");
                printer.printRecords(table.toCSV());
                printer.println();
            }
            printer.flush();

            return true;
        } catch (IOException ex) {
            Logger.getLogger(XmlCsvFileConverter.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }
    }

    /**
     * Заполнение CSV таблиц данными из DOM структуры XML файла
     */
    private void fillTables() {
        final NodeList root = this.xmlDocument.getChildNodes();
        int elementsCount = root.getLength();

        for (int i = 0; i < elementsCount; i++) {
            final Node element = root.item(i);

            if (isCollection(element)) {
                final String tableName = element.getNodeName();
                final NodeList elementChildren = element.getChildNodes();
                int childrenCount = elementChildren.getLength();

                for (int j = 0; j < childrenCount; j++) {
                    addRowInTable(elementChildren.item(j), tableName);
                }
            } else {
                addRowInTable(element, element.getNodeName());
            }
        }
    }

    /**
     * Извлечение данных из элемента и занесение их в таблицу
     *
     * @param xmlElement элемент, из которого извлекаются данные
     * @param tableName название таблицы, в которую заносятся данные
     * @return номер строки, в которую были записаны данные
     */
    private int addRowInTable(Node xmlElement, String tableName) {
        CSVRow row = new CSVRow();

        row = addAttributes(xmlElement, row, "");
        row = addChildren(xmlElement, row, "");

        return getTable(tableName).tryAddRow(row);
    }

    /**
     * Добавление атрибутов элемента в качестве значений строки
     *
     * @param xmlElement элемент, из которго берутся атрибуты
     * @param output строка, в которую заносятся данные
     * @param prefix приставка к названию столбца, нужна если элемент имеет
     * сложную структуру во избежании конфликтов в названиях столбцов
     * @return заполненная строка
     */
    private CSVRow addAttributes(Node xmlElement, CSVRow output, String prefix) {
        final NamedNodeMap attrs = xmlElement.getAttributes();
        int attrsCount = attrs.getLength();
        for (int i = 0; i < attrsCount; i++) {
            final Node attribute = attrs.item(i);
            final String colName = StringUtils.isBlank(prefix)
                    ? attribute.getNodeName() : prefix + "/" + attribute.getNodeName();
            output.addValue(colName, attribute.getTextContent());
        }

        return output;
    }

    /**
     * Добавление потомков элемента в качестве значений строки
     *
     * @param xmlElement элемент, из которго берутся потомки
     * @param output строка, в которую заносятся данные
     * @param prefix приставка к названию столбца, нужна если элемент имеет
     * сложную структуру во избежании конфликтов в названиях столбцов
     * @return заполненная строка
     */
    private CSVRow addChildren(Node xmlElement, CSVRow output, String prefix) {
        final NodeList vals = xmlElement.getChildNodes();
        int valsCount = vals.getLength();
        for (int i = 0; i < valsCount; i++) {
            final Node val = vals.item(i);
            final String prefixVal = StringUtils.isBlank(prefix) ? "" : prefix + "/";

            if (isValueNode(val)) {
                output.addValue(prefixVal + val.getNodeName(), val.getTextContent());
            } else if (isCollection(val)) {
                final String refTable = val.getNodeName();
                String values = refTable + "->";
                for (int j = 0; j < val.getChildNodes().getLength(); j++) {
                    values += String.valueOf(addRowInTable(val.getChildNodes().item(j), refTable));
                    if (j < val.getChildNodes().getLength() - 1) {
                        values += ";";
                    }
                }
                output.addValue(prefixVal + refTable, values);
            } else {
                output = addAttributes(val, output, prefixVal + val.getNodeName());
                output = addChildren(val, output, prefixVal + val.getNodeName());
            }
        }

        return output;
    }

    /**
     * Находит CSV таблицу по названию. Если нужной таблицы ещё нет, то
     * создаётся новая с таким названием
     *
     * @param name название таблицы
     * @return экземпляр класса CSV таблицы
     */
    private CSVTable getTable(String name) {
        if (!tables.containsKey(name)) {
            tables.put(name, new CSVTable());
        }

        return tables.get(name);
    }

    /**
     * Проверяет является ли элемент хранилищем для одинаковых типов данных
     *
     * @param xmlElement элемент для проверки
     * @return true, если элемент коллекция, false в ином случае
     */
    private boolean isCollection(Node xmlElement) {
        if (xmlElement.getChildNodes().getLength() == 0) {
            return false;
        }

        if (xmlElement.getChildNodes().getLength() == 1
                && xmlElement.getFirstChild().getNodeName().equalsIgnoreCase("#text")) {
            return false;
        }

        final NodeList vals = xmlElement.getChildNodes();
        final String originName = vals.item(0).getNodeName();
        for (int i = 1; i < vals.getLength(); i++) {
            final String valName = vals.item(i).getNodeName();
            if (!originName.equalsIgnoreCase(valName)) {
                return false;
            }
        }

        return true;
    }

}
