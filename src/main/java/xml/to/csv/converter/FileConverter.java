/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.to.csv.converter;

/**
 *
 * @author Абс0лютный Н0ль
 */
public interface FileConverter {

    /**
     * Процесс конвертации из одного типа файла в другой
     *
     * @param outputFile поток для записи в файл
     * @return true, если процесс прошёл успешно, false в ином случае
     */
    boolean convert(Appendable outputFile);
}
