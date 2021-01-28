package com.mycompany.sqliteinter;

import javax.swing.table.AbstractTableModel;

/*
*   Clase encargada de crear un modelo para la una tabla con la 
*   informacion de una base de datos
*/
public class ModeloDB extends AbstractTableModel{
    
    private ConexionDB conexionDB;
    
    public ModeloDB() {
        init("Other Sources/empresa.db");
    }
    
    public ModeloDB(String db) {
        init(db);
    }
    
    public ModeloDB(String db, String sql) {
        conexionDB = new ConexionDB(db, sql);
    }
    
    private void init(String ruta) {
        conexionDB = new ConexionDB(ruta);
    }
    
    public String[] getColumnName() {
        return conexionDB.getColumnName();
    }
    
    public String[] getTablas() {
        String[] n = new String[conexionDB.getNombre().size()];
        for(int i=0;i<n.length;i++) {
            n[i] = conexionDB.getNombre().get(i);
        }
        return  n;
    }
    
    public String getTable() {
        return conexionDB.getTablaActual();
    }

    public ConexionDB getConexionDB() {
        return conexionDB;
    }
    
    @Override
    public int getRowCount() {
        return conexionDB.getDatos().size();
    }

    @Override
    public int getColumnCount() {
        return conexionDB.getDatos().get(0).length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return conexionDB.getDatos().get(rowIndex)[columnIndex];
    }
}