package com.mycompany.sqliteinter;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ConexionDB {
    
    private ArrayList<String[]> datos;
    private ArrayList<String> nombre;
    private String[] columnName;
    private Connection connection;
    private Statement statement;
    private ResultSet tablas;
    private String tablaActual;
    
    public ConexionDB(String db) {
        init(db);
        ejecutarSentencia("SELECT * FROM "+nombre.get(0));
    }
    
    public ConexionDB(String db, String sql) {
        init(db);
        ejecutarSentencia(sql);
    }
    
    private void init(String db) {
        datos = new ArrayList<>();
        nombre = new ArrayList<>();
        try {
            connection(db);
            getNombreTablas();
            
            //tablas.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }  
    }
    
    /*
    *   Conecta con la base de datos
    */
    private void connection(String ruta) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:"+ruta);
            statement = connection.createStatement();
            tablas = statement.executeQuery("SELECT name FROM sqlite_master "+
                "WHERE type='table';");
            

        } catch(Exception e) {
                columnName = new String[] {"Fallo"};
                String a[] = new String[] {"Error al conectarse con la base de detos"};
                datos.add(a);
        }
    }
    
    /*
    *   Consige el nombre de las tablas y las mete en el arrayList nombre
    */
    private void getNombreTablas() {
        try {
            while(tablas.next()) {
                nombre.add(tablas.getString("name"));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /*
    *   Comprueba si una sentencia es de tipo Select o de otro y la ejecuta
    */
    private void ejecutarSentencia(String sentencia) {
        try {            
            String sql = sentencia+";";
            if(statement.execute(sql)) {
                ejecutar(sql);
            } else {
                tablas = statement.executeQuery("SELECT name FROM sqlite_master "+
                "WHERE type='table';");
                nombre = new ArrayList<>();
                getNombreTablas();
                ejecutar("SELECT * FROM " + nombre.get(0)+";");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /*
    *   Metodo que ejecuta una sentencia de tipo select.
    *   Mete los datos del selcet en el arrayList datos y el nombre de las columnas
    *   en el array columnName mas los Strings Borrar y Update.
    */
    private void ejecutar(String sql) {
        try {
            ResultSet resultado = statement.executeQuery(sql+";");  
            ResultSetMetaData meta = resultado.getMetaData();
            int num = meta.getColumnCount();
            boolean hayResultados = false;
            while(resultado.next()) {
                tablaActual = meta.getTableName(1);
                String a[] = new String[num+2];
                columnName = new String[num];
                for(int i=0; i<num; i++) {
                    a[i] = String.valueOf(resultado.getObject(i+1));
                    columnName[i] = meta.getColumnName(i+1);
                }
                a[a.length-2] = "Borrar";
                a[a.length-1] = "Update";
                datos.add(a);
                hayResultados = true;
            }
            if(!hayResultados) {
                columnName = new String[] {"Fallo"};
                String a[] = new String[] {"No hay resultados"};
                datos.add(a);
            }
            resultado.close();  
        } catch(Exception e) {
                columnName = new String[] {"Fallo"};
                String a[] = new String[] {"Error en la sentencia sql"};
                datos.add(a);
        }
    }
    
    /*
    *   Metodo que devueve un arrayList con las claves ajenas y las tablas a las que 
    *   pertenecen.
    */
    public ArrayList<String> clavesAjenas() {
        ArrayList<String> claves = new ArrayList<>();
        try {
            DatabaseMetaData data = connection.getMetaData();
            String tabString = data.getURL();
            ResultSet resultSet = data.getImportedKeys(null, tabString, tablaActual);
            
            while(resultSet.next()) {
                claves.add(resultSet.getString("PKTABLE_NAME")+"-"+resultSet.getString("PKCOLUMN_NAME")+"-"
                        +resultSet.getString("FKTABLE_NAME")+"-"+resultSet.getString("FKCOLUMN_NAME"));
            }            
           
        } catch (SQLException ex) {
            //Logger.getLogger(UpdateFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return claves;
    }
    
    /*
    *   Metodo que apartir de la url de la base de datos consigue su nombre
    */
    public String nombreDB() {
        String url = "";
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            url = metaData.getURL();
        } catch (SQLException ex) {
            Logger.getLogger(ConexionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        String nombre = "";
        String[] a = url.split(Pattern.quote("\\"));
        a = a[a.length-1].split(Pattern.quote("/"));
        nombre = a[a.length-1];
        System.out.println(a[a.length-1]);
        nombre = nombre.substring(0, nombre.length()-3);
        return nombre;
    }
    
    public void insert(String sql) {
        try {
            statement.execute(sql);
        } catch (SQLException ex) {
            
        }
    }
    
    public void update(String sql) {
        try {
            statement.execute(sql);
        } catch (SQLException ex) {
            
        }
    }

    public ArrayList<String[]> getDatos() {
        return datos;
    }

    public ArrayList<String> getNombre() {
        return nombre;
    }

    public String[] getColumnName() {
        return columnName;
    }

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }

    public ResultSet getTablas() {
        return tablas;
    }

    public String getTablaActual() {
        return tablaActual;
    }
}