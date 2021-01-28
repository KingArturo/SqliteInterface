
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
import org.sqlite.core.DB;

/*
*   Clase que genera los tantos cuadros de texto como columnas hay en la tabla que esta seleccionada y
*   permite insertar un registro en la tabla.
*/
public class InsertPanel extends javax.swing.JPanel {

    private String tabla;
    private String tipoDatos[];
    private CampoPanel campos[];
    private Statement statement;
    private ConexionDB db;
    
    public InsertPanel() {
        initComponents();
        
    }
    
    public void setTable(String tabla) {
        this.tabla = tabla;
    }
    
    /*
    *   Metodo que apartir de un objeto ConexionDB genera los campos necesario
    *   para poder inseratar un registro en la tabla
    */
    public void generarCampos(ConexionDB db) {
        this.db = db;
        try {
            statement = db.getConnection().createStatement();
            this.removeAll();
            this.add(new JLabel("INSERT"));
            JButton boton = new JButton("Ejecutar");    
            // Genera los campos solo si existe la tabla
            if(tabla != null) {
                ResultSet resultado = statement.executeQuery("SELECT * FROM "+tabla);  
                ResultSetMetaData meta = resultado.getMetaData();
                //Numero de columnas de la tabla
                int num = meta.getColumnCount();
                // Array que guardara los tipos de tatos que contienen las columnas de la tabla
                tipoDatos = new String[num];
                campos = new CampoPanel[num];
                this.setLayout(new GridLayout((num+2/3),3));
                for(int i=0; i<num; i++) { 
                    tipoDatos[i] = meta.getColumnTypeName(i+1);
                    formatoCampos(meta.getColumnName(i+1), i,statement, db.clavesAjenas());
                }
                addButtonListener(boton);
                this.add(boton);
                resultado.close();  
                this.revalidate();
                this.repaint();
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /*
    *   Crea y añade un campo al JPanel
    *   El formato sera el numero de la columna, el nombe de la columna y un campo de texto o un desplegable
    *   para poder introducir un dato.
    */
    private void formatoCampos(String nombreCampo, int pos, Statement s, ArrayList<String> claves) {
        boolean esClave = false;
        CampoPanel campo = null;
        String[] fkTable = null;
        for(String clave : claves) {
            String[] a = clave.split(Pattern.quote("-"));
            // Entra en el if solo si la columna es una clave ajena
            if(a[1].equals(nombreCampo)) {
                esClave = true;
                fkTable = a;
            }
        }
        // Si es una clave ajena entra en el else y genera un campo con un desplegable con las opciones que se
        // podran usar
        if(!esClave) {
            campo = new CampoPanel((pos+1)+" - "+nombreCampo);
        } else {
            campo = new CampoPanel((pos+1)+" - "+nombreCampo,s,fkTable);
        }
        campos[pos] = campo;
        campo.setSize(208,45);
        this.add(campo);
    }
    /**
     * Ejecuta la sentencia insert
     */
    private void insert() {
        String[] datos = new String[tipoDatos.length];
        String sent = "INSERT INTO "+tabla+" VALUES (";
        String coma = "";
        for(int i=0;i<tipoDatos.length;i++) {
            datos[i] = campos[i].getTexto();
            sent += coma+Tipo.tipoDato(tipoDatos[i], campos[i].getTexto());
            coma = ",";
        }
        db.insert(sent+");");
    }
    
    /*
    *   Borra los campos existentes y los vuelve a crear
    */
    public void borrarCampos() {
         this.removeAll();
         generarCampos(db);
         this.repaint();
    }
    
    /*
    *   Inserta el nuevo registro y vuelve a crear los campos para que esten vacios
    */
    private void addButtonListener(JButton btn) {
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {      
                insert();
                borrarCampos() ;
            }
        });  
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 143, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
