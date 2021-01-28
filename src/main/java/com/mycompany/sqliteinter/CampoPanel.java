package com.mycompany.sqliteinter;

import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/*
*   Clase que creara un campo con un JLabel con el nombre de una columna y 
*   un JTextField vacio o un desplegable con la informacion correspondiente de la columna con la que
*   esta relacionada esa colimna.
*/
public class CampoPanel extends javax.swing.JPanel {

    private JTextField jTextField1;
    private JComboBox<String> jComboBox1;

    /*
    *   Constructor que genera un panel con un label y un campo de texto
    */
    public CampoPanel(String tabla) {
        initComponents();
        jPanel1.setLayout(new GridLayout(1,1));
        jTextField1 = new JTextField();
        jPanel1.add(jTextField1);
        jLabel1.setText(tabla+":");
    }
    
    
    /*
    *   Constructor que se usa cuando la columna de una tabla es una clave ajena.
    *   Genera un label con el nombre de la columna y un desplegable con la informacion de la 
    *   columna con la que esta relacionada.
    */
    public CampoPanel(String tabla,Statement sentencia, String[] clave) {
        initComponents();
        jTextField1 = null;
        jComboBox1 = new JComboBox<>();
        try {
            ResultSet result = sentencia.executeQuery("SELECT "+clave[1]+" FROM "+clave[0]);
            while(result.next()) {
                int cont = 0;
                jComboBox1.addItem(String.valueOf(result.getObject(clave[1])));
                cont++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CampoPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        jPanel1.setLayout(new GridLayout(1,1));
        jPanel1.add(jComboBox1);
        jLabel1.setText(tabla+":");
    }
    
    
    /*
    *   Devuelve un String con el texto del campo
    */
    public String getTexto() {
        String devolver = "";
        if(jTextField1 == null) {
            devolver = jComboBox1.getSelectedItem().toString();
        } else {
            devolver = jTextField1.getText();
        }
        return devolver;
    }
    
    
    /*
    *   Establece el valor del campo
    */
    public void setTexto(String text) {
       if(jTextField1 == null) {
            jComboBox1.setSelectedItem(text);
        } else {
            jTextField1.setText(text);
        }        
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(208, 45));

        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 121, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 33, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
