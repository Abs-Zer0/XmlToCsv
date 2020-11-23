/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.to.csv.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import xml.to.csv.string.StringNormalizer;

/**
 *
 * @author Абс0лютный Н0ль
 */
public abstract class XmlFileConverter implements FileConverter {

    protected Document xmlDocument;

    /**
     * Создаёт экземпляр класса XmlFileConverter для парсинга XML файлов
     *
     * @param xmlFile ресурс, из которого извлекается данные XML файла
     */
    public XmlFileConverter(InputSource xmlFile) {
        try {
            this.xmlDocument = createDocumentBuilder().parse(xmlFile);
            normalizeNode(xmlDocument);
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            Logger.getLogger(XmlFileConverter.class.getName()).log(Level.SEVERE, null, ex);
            this.xmlDocument = null;
        }
    }

    /**
     * Создаёт экземпляр класса XmlFileConverter для парсинга XML файлов
     *
     * @param xmlFile файл, из которого считываются данные
     */
    public XmlFileConverter(File xmlFile) throws FileNotFoundException {
        this(new FileInputStream(xmlFile));
    }

    /**
     * Создаёт экземпляр класса XmlFileConverter для парсинга XML файлов
     *
     * @param xmlFile поток, из которого считываются данные XML файла
     */
    public XmlFileConverter(InputStream xmlFile) {
        this(new InputSource(xmlFile));
    }

    /**
     * Возвращает DOM структуру из XML файла
     *
     * @return DOM структура, если парсинг XML файла выполнен успешно, null в
     * ином случае
     */
    public Document getDom() {
        return this.xmlDocument;
    }

    /**
     * Проверяет содержит ли элемент только текстовую информацию
     *
     * @param xmlElement элемент для проверки
     * @return true, если данные элемента только текстовые, false в ином случае
     */
    protected boolean isValueNode(Node xmlElement) {
        if (xmlElement.getChildNodes().getLength() != 1) {
            return false;
        }

        final Node child = xmlElement.getFirstChild();
        return child.getNodeName().equalsIgnoreCase("#text") && StringUtils.isNotBlank(child.getTextContent());
    }

    /**
     * Инициализация экземпляра класса для построения DOM структуры из XML файла
     *
     * @return
     * @throws ParserConfigurationException
     */
    private DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setNamespaceAware(true);
        final DocumentBuilder builder = factory.newDocumentBuilder();

        return builder;
    }

    /**
     * Нормализация DOM структуры элемента. Удаление лишних элементов, сжатие и
     * удаление пробельных символов
     *
     * @param node элемент для нормализации
     */
    private void normalizeNode(Node node) {
        if (node.getChildNodes().getLength() == 1) {
            final Node child = node.getFirstChild();
            if (child.getNodeName().equalsIgnoreCase("#text") && StringUtils.isNotBlank(child.getTextContent())) {
                child.setNodeValue(StringNormalizer.normalizeSpace(child.getNodeValue()));
            }
        }

        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength();) {
            final Node child = children.item(i);
            if (child.getNodeName().equalsIgnoreCase("#text") && StringUtils.isBlank(child.getTextContent())) {
                node.removeChild(child);
            } else {
                normalizeNode(child);
                i++;
            }
        }
    }

}
