package com.mycompany.sqliteinter;

import com.sun.tools.javac.Main;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import javax.swing.tree.*;

public class Aplicacion extends javax.swing.JFrame {

    private JEditorPane EditorPane1;
    private ModeloDB db;
    private File dataBase;

    public Aplicacion() {
        dataBase = new File("Other Sources/empresa.db");
        db = new ModeloDB();
        this.setUndecorated(true);
        this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        initComponents(); 
        // Cambio el nombre de las columnas de la tabla
        cambiarColumnName();
        // Establece los elementos del JTree
        addMenu();
        // Establece la tabla en en la que te encuentras en un JLabel
        tableSelected();
        // Añade los campos que se usaran para insertar datos
        addInsertCampo();
        
        EditorPane1 = new JEditorPane();
        jMenuItem2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        jMenuItem3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cogerDB();
                } catch (Exception ex) {
                    Logger.getLogger(Aplicacion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });    
        addListenerMenu();
        addButtonListener();
        addTreeListener();
        addTableListener();
        addTableHover();
    }
    
    /*
    *   Establece la imagen de icona de la aplicacion
    */
    @Override
    public Image getIconImage() {
        URL imageResource = Main.class.getClassLoader().getResource("dato.png");
        Image retValue = Toolkit.getDefaultToolkit().getImage(imageResource);
        return retValue;
    }
    
    /*
    * Abre un explorador de archivos para coger una nueva DB y cambiarla por la anterior
    */
    public void cogerDB() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("DataBase", "db");
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(filter);
        int seleccion = fc.showOpenDialog(this.EditorPane1);
        if (seleccion == JFileChooser.APPROVE_OPTION){
            dataBase = fc.getSelectedFile();
            db = new ModeloDB(dataBase.getPath());
            tableSelected();
            jTable1.setModel(db);
        }

        addMenu();
        cambiarColumnName();
        addInsertCampo();
    }
    
    /*
    *   Cambia la tabla que se esta vilualizando por la que ha sido seleccionada
    */
    public void cambiarTabla(String base, String nombreTabla) {
        db = new ModeloDB(base, nombreTabla);
        tableSelected();
        jTable1.setModel(db);
    }
    
    /*
    * Añade el nombre de la base de datos y de las tablas al JTree para que se visualicen en forma de arbol
    */
    public void addMenu() {        
        String[] tablas = db.getTablas();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(db.getConexionDB().nombreDB());  
        for (int i=0; i<tablas.length; i++) {    
            DefaultMutableTreeNode v = new DefaultMutableTreeNode(tablas[i]); 
            root.add(v);     
        }
        jTree1 = new JTree(root);
        jScrollPane2.setViewportView(jTree1);
        addTreeListener();
    }
    
    /**
     *  Cambia el nombre de las columnas de la tabla por el nombre de las columnas de la tabla que esta seleccionada
     *  actualmente
     */
    public void cambiarColumnName() {
        JTableHeader head = jTable1.getTableHeader();
        TableColumnModel tcm = head.getColumnModel();
        String[] name = db.getColumnName();
        for(int i=0; i<name.length;i++) {
            TableColumn tabCM = tcm.getColumn(i);
            tabCM.setHeaderValue(name[i]);
        }
        if(!"Fallo".equals(tcm.getColumn(0).getHeaderValue())) {
            TableColumn tabCM = tcm.getColumn(name.length);
            tabCM.setHeaderValue("");
            tabCM = tcm.getColumn(name.length+1);
            tabCM.setHeaderValue("");
        }
        jTable1.repaint();
    }
    
    /*
    * Inserta en la variable jLabel1 el nombre de la tabla que esta seleccionada actualmente
    */
    private void tableSelected() {
        jLabel1.setText("Tabla seleccionada: "+db.getTable());
    }
    
    /*
    *   Crea los campos necesarios para que se pueda insertar una fila en la tabla actual
    */
    private void addInsertCampo() {
        //insertPanel1 = new InsertPanel();
        insertPanel1.setTable(db.getTable());
        insertPanel1.generarCampos(db.getConexionDB());
        this.revalidate();
        this.repaint();
    }
    
    /*
    * Coge los valores de una fila junto con el nombre de la columna y los va concatenando en un String 
    *   separado por un AND y lo devuelve: 'NombreColumna=ValorDeLaFila AND NombreColumna=ValorDeLaFila'
    */
    private String cogerValoresFila(int fila) {
        String aDevolver = "";
        String separador = "";
        int columnas =  db.getColumnName().length;
        for(int i=0; i<columnas;i++) {
            String clave = String.valueOf(jTable1.getModel().getValueAt(fila, i));
            String columClave = db.getColumnName()[i];
            aDevolver +=separador+columClave+"='"+clave+"'";
            separador = " AND ";
        }
        return aDevolver;
    }
    
    /*
    *   Devuelve un Array de String con los valores de una fila
    */
    private String[] valorFila(int fila) {
        String[] aDevolver = new String[db.getColumnName().length];
        for(int i=0;i<aDevolver.length;i++) {
            aDevolver[i] = String.valueOf(jTable1.getModel().getValueAt(fila, i));
        }
        return aDevolver;
    }
    
    /*
    * Permite que al pulsar el desplegable de relaciones se muestren las relaciones de esa tabla
    */
    private void addListenerMenu() {
        jMenuItem4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {      
                RelacionesFrame relaciones = new RelacionesFrame(db.getConexionDB());
                relaciones.setVisible(true);
            }
        });  
    }
    
    /*
    *   Ejecuta la sentencia que se haya escrito en el cuador de texto
    */
    private void addButtonListener() {
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {      
                cambiarTabla(dataBase.getPath(), jTextField1.getText());
                jTable1.setModel(db);
                addMenu();
                cambiarColumnName();
                addInsertCampo();
            }
        });  
    }
    
    /*
    *   Cambia la tabla selecionada cuando se pulsa sobre uno de sus elementos
    */
    private void addTreeListener() {
        jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                String tabla = evt.getNewLeadSelectionPath().getLastPathComponent().toString();
                if(!tabla.contains(db.getConexionDB().nombreDB())) {
                    cambiarTabla(dataBase.getPath(), ("SELECT * FROM " + tabla));
                    cambiarColumnName();
                    addInsertCampo();
                }
            }
        });
    }
    
    /*
    *   Detecta si has pulsado sobre borrar o update y borra la fila o crea la ventana de update.
    */
    private void addTableListener() {
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JTable table =(JTable) me.getSource();
                int fila = table.getSelectedRow();
                int columna = table.getSelectedColumn();
                String valorCelda = String.valueOf(table.getModel().getValueAt(fila, columna));
                String filaABorrar = cogerValoresFila(fila);
                if(valorCelda.equals("Borrar")) {
                    cambiarTabla(dataBase.getPath(), ("DELETE FROM "+db.getTable()+" WHERE "+filaABorrar+";"));
                    cambiarColumnName();
                    addInsertCampo();
                } else if(valorCelda.equals("Update")) {
                    UpdateFrame update = new UpdateFrame(db.getTable(),valorFila(fila),db.getConexionDB());
                    update.generarCampos(db.getConexionDB());
                    update.setVisible(true);
                }
            }
        });
    }
    
    
    /*
    *   Crea un efecto hover sobre las filas de la tabla
    */
    private void addTableHover() {        
        jTable1.addMouseMotionListener(new MouseMotionListener() {
            int hoveredRow = -1, hoveredColumn = -1;
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                hoveredRow = jTable1.rowAtPoint(p);
                hoveredColumn = jTable1.columnAtPoint(p);
                jTable1.setRowSelectionInterval(hoveredRow, hoveredRow);
                jTable1.repaint();    
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                hoveredRow = hoveredColumn = -1;
                jTable1.repaint();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        insertPanel1 = new com.mycompany.sqliteinter.InsertPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImage(getIconImage());
        setMinimumSize(new java.awt.Dimension(670, 522));

        jTable1.setModel(db);
        recolocarCeldas();
        jScrollPane1.setViewportView(jTable1);

        jTextField1.setToolTipText("");

        jButton1.setText("Ejecutar");

        Icon icon = new ImageIcon("tabla-de-datos.png");
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer)jTree1.getCellRenderer();
        renderer.setLeafIcon(icon);
        jScrollPane2.setViewportView(jTree1);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout insertPanel1Layout = new javax.swing.GroupLayout(insertPanel1);
        insertPanel1.setLayout(insertPanel1Layout);
        insertPanel1Layout.setHorizontalGroup(
            insertPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 634, Short.MAX_VALUE)
        );
        insertPanel1Layout.setVerticalGroup(
            insertPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jScrollPane3.setViewportView(insertPanel1);

        jMenu1.setText("Archivo");

        jMenuItem3.setText("DB");
        jMenu1.add(jMenuItem3);
        jMenu1.add(jSeparator1);

        jMenuItem2.setText("Salir");
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Relaciones");

        jMenuItem4.setText("Ver...");
        jMenu2.add(jMenuItem4);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField1)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void recolocarCeldas() {
        DefaultTableCellRenderer modelocentrar = new DefaultTableCellRenderer();
        modelocentrar.setHorizontalAlignment(SwingConstants.LEFT);
        int num = jTable1.getColumnCount();
        for(int i=0;i<num;i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(modelocentrar); 
        }
    }
    
    /* Tuve que quitarlo porque por alguna razon saltaba una escepcion si y solo si lo primero que hacia nada mas ejecutar 
    la aplicacion era cambiar el tema y darle al boton de Update.
    
    private void cambiarTema() {
        if(!tema) {
            try {
                UIManager.setLookAndFeel(new com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterContrastIJTheme());
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        } else {
            try {
                UIManager.setLookAndFeel(new com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialOceanicContrastIJTheme());
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        SwingUtilities.updateComponentTreeUI(this);
        tema = !tema;
    }*/
    
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
            UIManager.setLookAndFeel(new com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialOceanicContrastIJTheme());
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Aplicacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Aplicacion().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.mycompany.sqliteinter.InsertPanel insertPanel1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables
}