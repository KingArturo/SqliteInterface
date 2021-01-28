
package com.mycompany.sqliteinter;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.*;

/*
*   Clase que crea un frame con los la informacion de un registro de una tabla y permite actualizarlo
*/
public class UpdateFrame extends javax.swing.JFrame {

    private String tabla;
    private String[] tipoDatos;
    private Statement statement;
    private CampoPanel campos[];
    private String[] valores;
    private String[] columnName;
    private ConexionDB db;

    public UpdateFrame() {
        this.setUndecorated(true);
        initComponents();
        addButtonListener(jButton1);
    }
    
    public UpdateFrame(String tabla, String[] valores, ConexionDB db) {
        this.db = db;
        this.setUndecorated(true);
        initComponents();
        addButtonListener(jButton1);
        this.tabla = tabla;
        this.valores = valores;
    }
    
    /*
    *   Genera los campos necesarios para poder actualizar un registro de la base de datos
    */
    public void generarCampos(ConexionDB db) {
        try {
            statement = db.getStatement();
            jPanel2.removeAll();   
            ArrayList<String> claves = db.clavesAjenas();
            ResultSet resultado = statement.executeQuery("SELECT * FROM "+tabla);  
            ResultSetMetaData meta = resultado.getMetaData();
            //Numero de columnas de la tabla
            int num = meta.getColumnCount();
            tipoDatos = new String[num];
            campos = new CampoPanel[num];
            columnName = new String[num];
            jPanel2.setLayout(new GridLayout(1,num));
            for(int i=0; i<num; i++) { 
                columnName[i] = meta.getColumnName(i+1);
                tipoDatos[i] = meta.getColumnTypeName(i+1);
                formatoCampos(meta.getColumnName(i+1), i, statement, claves, num);
            }
            resultado.close();  
            jPanel2.revalidate();
            jPanel2.repaint();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    

    
    private void formatoCampos(String nombreCampo, int pos, Statement s, ArrayList<String> claves, int num) {
        boolean esClave = false;
        CampoPanel campo = null;
        String[] fkTable = null;
        for(String clave : claves) {
            String[] a = clave.split(Pattern.quote("-"));
            if(a[1].equals(nombreCampo)) {
                esClave = true;
                fkTable = a;
            }
        }
        if(!esClave) {
            campo = new CampoPanel((pos+1)+" - "+nombreCampo);
        } else {
            campo = new CampoPanel((pos+1)+" - "+nombreCampo,s,fkTable);
        }
        campo.setTexto(valores[pos]);
        campos[pos] = campo;
        campo.setSize(208,45);
        jPanel2.add(campo);
    }
    
    private void update() {
        String[] datos = new String[tipoDatos.length];
        String sent = "UPDATE "+tabla+" SET ";
        String condicion = "";
        String coma = "";
        String and = "";
        for(int i=0;i<tipoDatos.length;i++) {
            datos[i] = campos[i].getTexto();
            sent += coma+columnName[i]+"="+Tipo.tipoDato(tipoDatos[i], campos[i].getTexto());
            condicion += and+columnName[i]+"="+Tipo.tipoDato(tipoDatos[i], valores[i]);
            coma = ",";
            and = " AND ";
        }
        sent += " WHERE ";
        db.update(sent+condicion+";");
    }
    
    private void addButtonListener(JButton btn) {
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {      
                update();
                dispose();
            }
        });  
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 496, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 320, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanel2);

        jLabel1.setText("Update");

        jButton1.setText("Ejecutar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UpdateFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UpdateFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UpdateFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UpdateFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UpdateFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
