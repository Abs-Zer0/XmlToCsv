/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.to.csv.string;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Абс0лютный Н0ль
 */
public final class StringNormalizer {

    /**
     * Сжимает все пробелельные символы до одного пробела и удаляет все
     * пробельные символы в начале и в конце строки
     *
     * @param str строка для нормализации
     * @return нормализованную строку
     */
    public static String normalizeSpace(String str) {
        return StringUtils.strip(str.replaceAll("\\s+", " "));
    }
}
