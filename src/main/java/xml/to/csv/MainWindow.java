/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.to.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import xml.to.csv.converter.XmlCsvFileConverter;

/**
 *
 * @author Абс0лютный Н0ль
 */
public class MainWindow extends javax.swing.JFrame {

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ChooseBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(800, 480));
        setSize(new java.awt.Dimension(800, 480));

        ChooseBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        ChooseBtn.setText("Выбрать файл");
        ChooseBtn.setMaximumSize(new java.awt.Dimension(800, 480));
        ChooseBtn.setPreferredSize(new java.awt.Dimension(800, 50));
        ChooseBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ChooseBtnMouseClicked(evt);
            }
        });
        getContentPane().add(ChooseBtn, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ChooseBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ChooseBtnMouseClicked
        // TODO add your handling code here:
        final JFileChooser opener = new JFileChooser();

        opener.setDialogTitle("Выбор файла");
        opener.setFileSelectionMode(JFileChooser.FILES_ONLY);

        final FileFilter all = opener.getAcceptAllFileFilter();
        opener.removeChoosableFileFilter(all);
        opener.addChoosableFileFilter(new FileNameExtensionFilter("XML files (*.xml)", "xml"));
        opener.addChoosableFileFilter(all);

        int openResult = opener.showOpenDialog(this);
        if (openResult == JFileChooser.APPROVE_OPTION) {
            final JFileChooser saver = new JFileChooser();

            saver.setDialogTitle("Сохранить в");
            saver.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int saveResult = saver.showSaveDialog(this);
            if (saveResult == JFileChooser.APPROVE_OPTION) {
                try {
                    final File xml = opener.getSelectedFile();
                    final String pathToCsv = saver.getSelectedFile().getAbsolutePath()
                            + File.separatorChar + xml.getName() + ".csv";
                    final FileWriter csv = new FileWriter(pathToCsv);

                    final XmlCsvFileConverter converter = new XmlCsvFileConverter(xml);
                    if (converter.convert(csv)) {
                        JOptionPane.showMessageDialog(this, "Файл сконвертирован успешно",
                                "Успех", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "При конвертации возникли ошибки",
                                "Неудача", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this, "При конвертации возникли ошибки",
                            "Неудача", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_ChooseBtnMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ChooseBtn;
    // End of variables declaration//GEN-END:variables
}
