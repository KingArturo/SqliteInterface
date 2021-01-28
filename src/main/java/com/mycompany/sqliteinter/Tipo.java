package com.mycompany.sqliteinter;

/*
*   Clase que apartir de un String dado lo devuelve entre comillas simples si el tipo es de tipo texto
*/
public class Tipo {

    private static String[] tipoDatos = new String[]{"int", "float", "decimal","null"};
    
    public static String tipoDato(String tipo, String dato) {
        String d = "'"+dato+"'";
        tipo = tipo.toLowerCase();
        for(int i=0;i<tipoDatos.length;i++) {
            if(tipo.contains(tipoDatos[i])) {
                d = dato;
            }
        }
        return d;
    }
    
}
