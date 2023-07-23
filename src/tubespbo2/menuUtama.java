package tubespbo2;

import java.awt.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author ACER
 */
public class menuUtama extends javax.swing.JFrame {

    /**
     * Creates new form menuUtama
     */
    
    SQLDatabaseConnection mainSqlConnection;
    SQLDatabaseConnection sqlConnectionGetRowCount;
    SQLDatabaseConnection sqlConnectionModifyData;
    SQLDatabaseConnection sqlConnectionModifyData_1;
    SQLDatabaseConnection sqlConnectionModifyData_2;
    SQLDatabaseConnection sqlConnectionModifyData_3;
    SQLDatabaseConnection sqlConnectionModifyData_4;
    SQLDatabaseConnection sqlGetIdOfRowToModify;
    SQLDatabaseConnection sqlGetIdOfRowToModify_1;
    SQLDatabaseConnection sqlGetIdOfRowToModify_2;
    SQLDatabaseConnection sqlGetIdOfRowToModify_3;
    SQLDatabaseConnection sqlGetPlatList;
    
    String currentTable;
    String[] dataToInsert = new String[7];
    String[] idHolder = new String[4];
    int itemsToDisplayPerPage;
    int totalRowCount;
    int currentPageNumber;
    int maxPageCount;
    boolean previousPageButtonEnabled;
    boolean nextPageButtonEnabled;
    boolean submitDataButtonEnabled;
    
    private static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
    
    private static boolean isValidDate(String inDate) {
        if(inDate.length() != 10) {
            return false;
        }
        if(inDate.charAt(6) == '-') {
            return false;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }
    
    private static boolean checkIfDataIsInvalid(String str, String validationType) {
        if(isInteger(str) == false && "integer".equals(validationType)) {
            return true;
        }
        else return (isValidDate(str) == false && "date".equals(validationType));
    }
    
    private static boolean checkIfDataIsInvalid(String str, int maxLength) {
        return (str.length() > maxLength);
    }
    
    private boolean checkIfPlatExistsInDatabase(String plat) throws SQLException {
        sqlGetPlatList = new SQLDatabaseConnection("SELECT plat FROM datamotor WHERE plat=\""+plat+"\"", "select", 1);
        return (sqlGetPlatList.returnedRows[0][0] == null || sqlGetPlatList.returnedRows[0][0] == "(Tidak ada data)");
    }
    
    private String convertColumnSelectIntoInternalName(String str) {
        switch(str) {
            case "Merk":
                return "merk";
            case "Keterangan Surat":
                return "ketsurat";
            case "Plat":
                return "plat";
            case "Nama":
                return "nama";
            case "Nomor Telepon":
                return "nomortelp";
            case "Tanggal Pembelian":
                return "tglpembelian";
            case "Harga Pembelian":
                return "hargapembelian";
            case "Tanggal Penjualan":
                return "tglpenjualan";
            case "Harga Penjualan":
                return "hargapenjualan";
            default:
                return "ups";
        }
    }
    
    private void getModifyQueryToUse(String tableName, int columnNum, int rowNum, String dataToValidate) throws SQLException {
        errorText.setText("");
        if("datamotor".equals(tableName)) {
            sqlGetIdOfRowToModify = new SQLDatabaseConnection("SELECT datamotor.idmotor, datapembelian.tglpembelian, datapenjualan.tglpenjualan FROM datamotor INNER JOIN datapembelian ON datamotor.idtransaksipembelian = datapembelian.idtransaksipembelian INNER JOIN datapenjualan ON datamotor.idtransaksipenjualan = datapenjualan.idtransaksipenjualan ORDER BY tglpembelian DESC LIMIT 1 OFFSET "+(((currentPageNumber-1)*10)+rowNum), "select", 3);
            
            //System.out.println("ID: "+sqlGetIdOfRowToModify.returnedRows[0][0]+" | Tgl pembelian: "+sqlGetIdOfRowToModify.returnedRows[0][1]+" | New data: "+dataToValidate);
           
            //Validasi data untuk kolom-kolom tertentu
            switch (columnNum) {
                case 0:
                    if(checkIfDataIsInvalid(dataToValidate, 15)) {
                        errorText.setText("Error: Merk tidak boleh lebih dari 15 karakter");
                        refreshData();
                    }
                    else {
                        sqlConnectionModifyData = new SQLDatabaseConnection("UPDATE datamotor SET merk=\""+dataToValidate+"\" WHERE idmotor="+sqlGetIdOfRowToModify.returnedRows[0][0], "update", 1);
                        refreshData();
                    }
                    break;
                case 1:
                    sqlConnectionModifyData = new SQLDatabaseConnection("UPDATE datamotor SET ketsurat=\""+dataToValidate+"\" WHERE idmotor="+sqlGetIdOfRowToModify.returnedRows[0][0], "update", 1);
                    refreshData();
                    break;
                case 2:
                    if(checkIfDataIsInvalid(dataToValidate, 15)) {
                        errorText.setText("Error: Plat tidak boleh lebih dari 15 karakter");
                        refreshData();
                    }
                    else if(checkIfPlatExistsInDatabase(dataToValidate) == false) {
                        errorText.setText("Error: Plat harus unik; Tidak boleh sama");
                        refreshData();
                    }
                    else {
                        sqlConnectionModifyData = new SQLDatabaseConnection("UPDATE datamotor SET plat=\""+dataToValidate+"\" WHERE idmotor="+sqlGetIdOfRowToModify.returnedRows[0][0], "update", 1);
                        refreshData();
                    }
                    break;
                case 3:
                    if(checkIfDataIsInvalid(dataToValidate, "integer")) {
                        errorText.setText("Error: Harga beli harus bilangan bulat");
                        refreshData();
                    }
                    else {
                        sqlConnectionModifyData = new SQLDatabaseConnection("UPDATE datapembelian SET hargapembelian=\""+dataToValidate+"\" WHERE idmotor="+sqlGetIdOfRowToModify.returnedRows[0][0], "update", 1);
                        refreshData();
                    }
                    break;
                case 4:
                    if(checkIfDataIsInvalid(dataToValidate, "date")) {
                        errorText.setText("Error: Tanggal beli harus berformat yyyy-mm-dd");
                        refreshData();
                    }
                    else {
                        sqlConnectionModifyData = new SQLDatabaseConnection("UPDATE datapembelian SET tglpembelian=\""+dataToValidate+"\" WHERE idmotor="+sqlGetIdOfRowToModify.returnedRows[0][0], "update", 1);
                        refreshData();
                    }
                    break;
                case 5:
                    if(checkIfDataIsInvalid(dataToValidate, "integer")) {
                        errorText.setText("Error: Harga jual harus bilangan bulat");
                        refreshData();
                    }
                    else {
                        sqlConnectionModifyData = new SQLDatabaseConnection("UPDATE datapenjualan SET hargapenjualan=\""+dataToValidate+"\" WHERE idmotor="+sqlGetIdOfRowToModify.returnedRows[0][0], "update", 1);
                        refreshData();
                    }
                    break;
                case 6:
                    if(checkIfDataIsInvalid(dataToValidate, "date")) {
                        errorText.setText("Error: Tanggal jual harus berformat yyyy-mm-dd");
                        refreshData();
                    }
                    else {
                        sqlConnectionModifyData = new SQLDatabaseConnection("UPDATE datapenjualan SET tglpenjualan=\""+dataToValidate+"\" WHERE idmotor="+sqlGetIdOfRowToModify.returnedRows[0][0], "update", 1);
                        refreshData();
                    }
                    break;
                default:
                    break;
            }
        }
        else if("datapengguna".equals(tableName)) {
            sqlGetIdOfRowToModify = new SQLDatabaseConnection("SELECT idpengguna FROM datapengguna ORDER BY idpengguna DESC LIMIT 1 OFFSET "+(((currentPageNumber-1)*10)+rowNum), "select", 1);
            //Validasi data untuk kolom-kolom tertentu
            switch (columnNum) {
                case 0:
                    if(checkIfDataIsInvalid(dataToValidate, 64)) {
                        errorText.setText("Error: Nama tidak boleh lebih dari 64 karakter");
                        refreshData();
                    }
                    else {
                        sqlConnectionModifyData = new SQLDatabaseConnection("UPDATE datapengguna SET nama=\""+dataToValidate+"\" WHERE idpengguna="+sqlGetIdOfRowToModify.returnedRows[0][0], "update", 1);
                        refreshData();
                    }
                    break;
                case 1:
                    if(checkIfDataIsInvalid(dataToValidate, 12)) {
                        errorText.setText("Error: Nomor telepon tidak boleh lebih dari 12 karakter");
                        refreshData();
                    }
                    else {
                        sqlConnectionModifyData = new SQLDatabaseConnection("UPDATE datapengguna SET nomortelp=\""+dataToValidate+"\" WHERE idpengguna="+sqlGetIdOfRowToModify.returnedRows[0][0], "update", 1);
                        refreshData();
                    }
                    break;
                default:
                    break;
            }
        }
        else if("datapembelian".equals(tableName)) {
            //Validasi data untuk kolom-kolom tertentu
            switch (columnNum) {
                case 0:
                    if(checkIfDataIsInvalid(dataToValidate,"date")) {
                        errorText.setText("Error: Tanggal beli harus berformat yyyy-mm-dd");
                        refreshData();
                    }
                    else {
                        sqlGetIdOfRowToModify = new SQLDatabaseConnection("SELECT idmotor,tglpembelian FROM datapembelian ORDER BY tglpembelian DESC LIMIT 1 OFFSET "+(((currentPageNumber-1)*10)+rowNum), "select", 2);
                        sqlConnectionModifyData = new SQLDatabaseConnection("UPDATE datapembelian SET tglpembelian=\""+dataToValidate+"\" WHERE idmotor="+sqlGetIdOfRowToModify.returnedRows[0][0], "update", 1);
                        refreshData();
                    }
                    break;
                case 1:
                    if(checkIfDataIsInvalid(dataToValidate,"integer")) {
                        errorText.setText("Error: Harga beli harus bilangan bulat");
                        refreshData();
                    }
                    else {
                        sqlGetIdOfRowToModify = new SQLDatabaseConnection("SELECT idmotor,tglpembelian FROM datapembelian ORDER BY tglpembelian DESC LIMIT 1 OFFSET "+(((currentPageNumber-1)*10)+rowNum), "select", 2);
                        sqlConnectionModifyData = new SQLDatabaseConnection("UPDATE datapembelian SET hargapembelian=\""+dataToValidate+"\" WHERE idmotor="+sqlGetIdOfRowToModify.returnedRows[0][0], "update", 1);
                        refreshData();
                    }
                    break;
                case 2:
                    if(checkIfDataIsInvalid(dataToValidate,15)) {
                        errorText.setText("Error: Merk tidak boleh lebih dari 15 karakter");
                        refreshData();
                    }
                    else {
                        sqlGetIdOfRowToModify = new SQLDatabaseConnection("SELECT idmotor,tglpembelian FROM datapembelian ORDER BY tglpembelian DESC LIMIT 1 OFFSET "+(((currentPageNumber-1)*10)+rowNum), "select", 2);
                        sqlConnectionModifyData = new SQLDatabaseConnection("UPDATE datamotor SET merk=\""+dataToValidate+"\" WHERE idmotor="+sqlGetIdOfRowToModify.returnedRows[0][0], "update", 1);
                        refreshData();
                    }
                    break;
                case 3:
                    if(checkIfDataIsInvalid(dataToValidate,64)) {
                        errorText.setText("Error: Nama tidak boleh lebih dari 64 karakter");
                        refreshData();
                    }
                    else {
                        sqlGetIdOfRowToModify = new SQLDatabaseConnection("SELECT idpengguna,tglpembelian FROM datapembelian ORDER BY tglpembelian DESC LIMIT 1 OFFSET "+(((currentPageNumber-1)*10)+rowNum), "select", 2);
                        sqlConnectionModifyData = new SQLDatabaseConnection("UPDATE datapengguna SET nama=\""+dataToValidate+"\" WHERE idpengguna="+sqlGetIdOfRowToModify.returnedRows[0][0], "update", 1);
                        refreshData();
                    }
                    break;
                case 4:
                    if(checkIfDataIsInvalid(dataToValidate,12)) {
                        errorText.setText("Error: Nomor telepon tidak boleh lebih dari 12 karakter");
                        refreshData();
                    }
                    else {
                        sqlGetIdOfRowToModify = new SQLDatabaseConnection("SELECT idpengguna,tglpembelian FROM datapembelian ORDER BY tglpembelian DESC LIMIT 1 OFFSET "+(((currentPageNumber-1)*10)+rowNum), "select", 2);
                        sqlConnectionModifyData = new SQLDatabaseConnection("UPDATE datapengguna SET nomortelp=\""+dataToValidate+"\" WHERE idpengguna="+sqlGetIdOfRowToModify.returnedRows[0][0], "update", 1);
                        refreshData();
                    }
                    break;
                default:
                    break;
            }
        }
        else if("datapenjualan".equals(tableName)) {
            //Validasi data untuk kolom-kolom tertentu
            switch (columnNum) {
                case 0:
                    if(checkIfDataIsInvalid(dataToValidate,"date")) {
                        errorText.setText("Error: Tanggal jual harus berformat yyyy-mm-dd");
                        refreshData();
                    }
                    else {
                        sqlGetIdOfRowToModify = new SQLDatabaseConnection("SELECT idmotor,tglpenjualan FROM datapenjualan ORDER BY tglpenjualan DESC LIMIT 1 OFFSET "+(((currentPageNumber-1)*10)+rowNum), "select", 2);
                        sqlConnectionModifyData = new SQLDatabaseConnection("UPDATE datapenjualan SET tglpenjualan=\""+dataToValidate+"\" WHERE idmotor="+sqlGetIdOfRowToModify.returnedRows[0][0], "update", 1);
                        refreshData();
                    }
                    break;
                case 1:
                    if(checkIfDataIsInvalid(dataToValidate,"integer")) {
                        errorText.setText("Error: Harga jual harus bilangan bulat");
                        refreshData();
                    }
                    else {
                        sqlGetIdOfRowToModify = new SQLDatabaseConnection("SELECT idmotor,tglpenjualan FROM datapenjualan ORDER BY tglpenjualan DESC LIMIT 1 OFFSET "+(((currentPageNumber-1)*10)+rowNum), "select", 2);
                        sqlConnectionModifyData = new SQLDatabaseConnection("UPDATE datapenjualan SET hargapenjualan=\""+dataToValidate+"\" WHERE idmotor="+sqlGetIdOfRowToModify.returnedRows[0][0], "update", 1);
                        refreshData();
                    }
                    break;
                case 2:
                    if(checkIfDataIsInvalid(dataToValidate,15)) {
                        errorText.setText("Error: Merk tidak boleh lebih dari 15 karakter");
                        refreshData();
                    }
                    else {
                        sqlGetIdOfRowToModify = new SQLDatabaseConnection("SELECT idmotor,tglpenjualan FROM datapenjualan ORDER BY tglpenjualan DESC LIMIT 1 OFFSET "+(((currentPageNumber-1)*10)+rowNum), "select", 2);
                        sqlConnectionModifyData = new SQLDatabaseConnection("UPDATE datamotor SET merk=\""+dataToValidate+"\" WHERE idmotor="+sqlGetIdOfRowToModify.returnedRows[0][0], "update", 1);
                        refreshData();
                    }
                    break;
                case 3:
                    if(checkIfDataIsInvalid(dataToValidate,64)) {
                        errorText.setText("Error: Nama tidak boleh lebih dari 64 karakter");
                        refreshData();
                    }
                    else {
                        sqlGetIdOfRowToModify = new SQLDatabaseConnection("SELECT idpengguna,tglpenjualan FROM datapenjualan ORDER BY tglpenjualan DESC LIMIT 1 OFFSET "+(((currentPageNumber-1)*10)+rowNum), "select", 2);
                        sqlConnectionModifyData = new SQLDatabaseConnection("UPDATE datapengguna SET nama=\""+dataToValidate+"\" WHERE idpengguna="+sqlGetIdOfRowToModify.returnedRows[0][0], "update", 1);
                        refreshData();
                    }
                    break;
                case 4:
                    if(checkIfDataIsInvalid(dataToValidate,12)) {
                        errorText.setText("Error: Nomor telepon tidak boleh lebih dari 12 karakter");
                        refreshData();
                    }
                    else {
                        sqlGetIdOfRowToModify = new SQLDatabaseConnection("SELECT idpengguna,tglpenjualan FROM datapenjualan ORDER BY tglpenjualan DESC LIMIT 1 OFFSET "+(((currentPageNumber-1)*10)+rowNum), "select", 2);
                        sqlConnectionModifyData = new SQLDatabaseConnection("UPDATE datapengguna SET nomortelp=\""+dataToValidate+"\" WHERE idpengguna="+sqlGetIdOfRowToModify.returnedRows[0][0], "update", 1);
                        refreshData();
                    }
                    break;
                default:
                    break;
            }
        }
    }
    
    private void updateMaxPageNumber(String queryToUse) throws SQLException {
        sqlConnectionGetRowCount = new SQLDatabaseConnection(queryToUse, "select", 1);
        totalRowCount = Integer.parseInt((String) sqlConnectionGetRowCount.returnedRows[0][0]);
        maxPageCount = ((totalRowCount % 10 == 0) ? totalRowCount / 10 : (totalRowCount / 10) + 1 );
        
        //Refresh tombol-tombol pengatur halaman
        currentPageNumber = 1;
        pageNumber.setText("1");
        previousPageButtonEnabled = false;
        nextPageButtonEnabled = true;
        changeBackground(nextPagePanel, new Color(15,39,84));
        changeBackground(previousPagePanel, new Color(153,153,153));
        
        refreshData();
    }
    
    private void refreshData() throws SQLException {
        if(null != currentTable) switch (currentTable) {
            case "datamotor":
                mainSqlConnection = new SQLDatabaseConnection("SELECT datamotor.merk, datamotor.ketsurat, datamotor.plat, datapembelian.hargapembelian, datapembelian.tglpembelian, datapenjualan.hargapenjualan, datapenjualan.tglpenjualan FROM datamotor LEFT JOIN datapembelian ON datamotor.idtransaksipembelian = datapembelian.idtransaksipembelian LEFT JOIN datapenjualan ON datamotor.idtransaksipenjualan = datapenjualan.idtransaksipenjualan ORDER BY tglpembelian DESC LIMIT 10 OFFSET "+((currentPageNumber-1)*10), "select", 7);
                TableModel tempTableModelVarDatamotor = new javax.swing.table.DefaultTableModel(
                        mainSqlConnection.returnedRows,
                        new String [] {
                            "Merek", "Ket. Surat", "Plat", "Harga beli", "Tanggal beli", "Harga jual", "Tanggal jual"
                        }
                );
                tempTableModelVarDatamotor.addTableModelListener(new TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent e) {
                        int type = e.getType();
                        switch (type) {
                            case TableModelEvent.UPDATE:
                                if (e.getFirstRow() - e.getLastRow() == 0) {
                                    TableModel model = (TableModel) e.getSource();
                                    int row = e.getFirstRow();
                                    int col = e.getColumn();
                                    //System.out.println("Update " + row + "x" + col + " = " + model.getValueAt(row, col));
                                    try {
                                        getModifyQueryToUse("datamotor",col,row,(String) model.getValueAt(row, col));
                                    } catch (SQLException ex) {
                                        Logger.getLogger(menuUtama.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                break;
                        }
                    }
                });
                tabelData.setModel(tempTableModelVarDatamotor);
                tabelData.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(comboBoxKetSurat));
                break;
            case "datapengguna":
                mainSqlConnection = new SQLDatabaseConnection("SELECT nama, nomortelp FROM datapengguna ORDER BY idpengguna DESC LIMIT 10 OFFSET "+((currentPageNumber-1)*10), "select", 2);
                TableModel tempTableModelVarDatapengguna = new javax.swing.table.DefaultTableModel(
                        mainSqlConnection.returnedRows,
                        new String [] {
                            "Nama pengguna", "Nomor telepon"
                        }
                );
                tempTableModelVarDatapengguna.addTableModelListener(new TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent e) {
                        int type = e.getType();
                        switch (type) {
                            case TableModelEvent.UPDATE:
                                if (e.getFirstRow() - e.getLastRow() == 0) {
                                    TableModel model = (TableModel) e.getSource();
                                    int row = e.getFirstRow();
                                    int col = e.getColumn();
                                    //System.out.println("Update DATAPENGGUNA " + row + "x" + col + " = " + model.getValueAt(row, col));
                                    try {
                                        getModifyQueryToUse("datapengguna",col,row,(String) model.getValueAt(row, col));
                                    } catch (SQLException ex) {
                                        Logger.getLogger(menuUtama.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                break;
                        }
                    }
                });
                tabelData.setModel(tempTableModelVarDatapengguna);
                break;
            case "datapembelian":
                mainSqlConnection = new SQLDatabaseConnection("SELECT datapembelian.tglpembelian, datapembelian.hargapembelian, datamotor.merk, datapengguna.nama, datapengguna.nomortelp FROM datapembelian INNER JOIN datamotor ON datapembelian.idmotor = datamotor.idmotor INNER JOIN datapengguna ON datapembelian.idpengguna = datapengguna.idpengguna ORDER BY tglpembelian DESC LIMIT 10 OFFSET "+((currentPageNumber-1)*10), "select", 5);
                TableModel tempTableModelVarDatapembelian = new javax.swing.table.DefaultTableModel(
                        mainSqlConnection.returnedRows,
                        new String [] {
                            "Tanggal pembelian", "Harga pembelian", "Merek motor", "Nama penjual", "Nomor telepon"
                        }
                );
                tempTableModelVarDatapembelian.addTableModelListener(new TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent e) {
                        int type = e.getType();
                        switch (type) {
                            case TableModelEvent.UPDATE:
                                if (e.getFirstRow() - e.getLastRow() == 0) {
                                    TableModel model = (TableModel) e.getSource();
                                    int row = e.getFirstRow();
                                    int col = e.getColumn();
                                    //System.out.println("Update DATAPEMBELIAN " + row + "x" + col + " = " + model.getValueAt(row, col));
                                    try {
                                        getModifyQueryToUse("datapembelian",col,row,(String) model.getValueAt(row, col));
                                    } catch (SQLException ex) {
                                        Logger.getLogger(menuUtama.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                break;
                        }
                    }
                });
                tabelData.setModel(tempTableModelVarDatapembelian);
                break;
            case "datapenjualan":
                mainSqlConnection = new SQLDatabaseConnection("SELECT datapenjualan.tglpenjualan, datapenjualan.hargapenjualan, datamotor.merk, datapengguna.nama, datapengguna.nomortelp FROM datapenjualan INNER JOIN datamotor ON datapenjualan.idmotor = datamotor.idmotor INNER JOIN datapengguna ON datapenjualan.idpengguna = datapengguna.idpengguna ORDER BY tglpenjualan DESC LIMIT 10 OFFSET "+((currentPageNumber-1)*10), "select", 5);
                TableModel tempTableModelVarDatapenjualan = new javax.swing.table.DefaultTableModel(
                        mainSqlConnection.returnedRows,
                        new String [] {
                            "Tanggal penjualan", "Harga penjualan", "Merek motor", "Nama pembeli", "Nomor telepon"
                        }
                );
                tempTableModelVarDatapenjualan.addTableModelListener(new TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent e) {
                        int type = e.getType();
                        switch (type) {
                            case TableModelEvent.UPDATE:
                                if (e.getFirstRow() - e.getLastRow() == 0) {
                                    TableModel model = (TableModel) e.getSource();
                                    int row = e.getFirstRow();
                                    int col = e.getColumn();
                                    //System.out.println("Update DATAPENJUALAN " + row + "x" + col + " = " + model.getValueAt(row, col));
                                    try {
                                        getModifyQueryToUse("datapenjualan",col,row,(String) model.getValueAt(row, col));
                                    } catch (SQLException ex) {
                                        Logger.getLogger(menuUtama.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                break;
                        }
                    }
                });
                tabelData.setModel(tempTableModelVarDatapenjualan);
                break;
            default:
                break;
        }
    }
    
    private void setAddDataTableModel(String modelToUse) {
        if("pembelian".equals(modelToUse)) {
            dataToInsert = new String[7];
            TableModel tempTableModelVarAddDataPembelian = new javax.swing.table.DefaultTableModel(
                new Object [][] {
                    {null, null, null, null, null, null, null}
                },
                new String [] {
                    "Merk", "Ket. Surat", "Plat", "Harga beli", "Tanggal beli", "Nama penjual", "No. Telp. Penjual"
                }
            );
            tempTableModelVarAddDataPembelian.addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    int type = e.getType();
                    switch (type) {
                        case TableModelEvent.UPDATE:
                            if (e.getFirstRow() - e.getLastRow() == 0) {
                                TableModel model = (TableModel) e.getSource();
                                int row = e.getFirstRow();
                                int col = e.getColumn();
                                //System.out.println("Update ADDDATATABLE" + row + "x" + col + " = " + model.getValueAt(row, col));
                                
                                //Validasi data
                                String dataToValidate = (String) model.getValueAt(row, col);
                                //System.out.println(dataToValidate);
                                if(dataToValidate != null) {
                                    switch(col) {
                                        case 0:
                                            if(checkIfDataIsInvalid(dataToValidate, 15)) {
                                                errorTextForAddData.setText("Merk tidak boleh lebih dari 15 karakter");
                                                model.setValueAt(null,row,col);
                                            }
                                            else {
                                                dataToInsert[col] = (String) model.getValueAt(row,col);
                                            }
                                        break;
                                        case 1:
                                            dataToInsert[col] = (String) model.getValueAt(row,col);
                                        break;
                                        case 2:
                                            try {
                                                if(checkIfDataIsInvalid(dataToValidate, 15)) {
                                                    errorTextForAddData.setText("Plat tidak boleh lebih dari 15 karakter");
                                                    model.setValueAt(null,row,col);
                                                }
                                                else if (checkIfPlatExistsInDatabase(dataToValidate) == false) {
                                                    errorTextForAddData.setText("Plat harus unik; tidak boleh sama");
                                                    model.setValueAt(null,row,col);
                                                }
                                                else {
                                                    dataToInsert[col] = (String) model.getValueAt(row,col);
                                                }
                                            } catch (SQLException ex) {
                                                Logger.getLogger(menuUtama.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        break;
                                        case 3:
                                            if(checkIfDataIsInvalid(dataToValidate, "integer")) {
                                                errorTextForAddData.setText("Harga beli harus bilangan bulat");
                                                model.setValueAt(null,row,col);
                                            }
                                            else {
                                                dataToInsert[col] = (String) model.getValueAt(row,col);
                                            }
                                        break;
                                        case 4:
                                            if(checkIfDataIsInvalid(dataToValidate, "date")) {
                                                errorTextForAddData.setText("Tanggal beli harus berformat yyyy-mm-dd");
                                                model.setValueAt(null,row,col);
                                            }
                                            else {
                                                dataToInsert[col] = (String) model.getValueAt(row,col);
                                            }
                                        break;
                                        case 5:
                                            if(checkIfDataIsInvalid(dataToValidate, 64)) {
                                                errorTextForAddData.setText("Nama tidak boleh lebih dari 64 karakter");
                                                model.setValueAt(null,row,col);
                                            }
                                            else {
                                                dataToInsert[col] = (String) model.getValueAt(row,col);
                                            }
                                        break;
                                        case 6:
                                            if(checkIfDataIsInvalid(dataToValidate, 12)) {
                                                errorTextForAddData.setText("Nomor telepon tidak boleh lebih dari 12 karakter");
                                                model.setValueAt(null,row,col);
                                            }
                                            else {
                                                dataToInsert[col] = (String) model.getValueAt(row,col);
                                            }
                                        break;
                                        default:
                                            errorTextForAddData.setText("");
                                        break;
                                    }
                                }
                                //Semua data sangat penting - tidak boleh melewatkan satupun
                                if(model.getValueAt(0,0) != null
                                        && model.getValueAt(0,1) != null 
                                        && model.getValueAt(0,2) != null 
                                        && model.getValueAt(0,3) != null 
                                        && model.getValueAt(0,4) != null 
                                        && model.getValueAt(0,5) != null
                                        && model.getValueAt(0,6) != null) {
                                    changeBackground(panelSubmitData, new Color(15,39,84));
                                    submitDataButtonEnabled = true;
                                }
                                else {
                                    changeBackground(panelSubmitData, new Color(153,153,153));
                                    submitDataButtonEnabled = false;
                                }
                            }
                            break;
                    }
                }
            });
            addDataTable.setModel(tempTableModelVarAddDataPembelian);
            addDataTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(comboBoxKetSurat));
        }
        else if("penjualan".equals(modelToUse)) {
            dataToInsert = new String[5];
            TableModel tempTableModelVarAddDataPenjualan = new javax.swing.table.DefaultTableModel(
                new Object [][] {
                    {null, null, null, null, null}
                },
                new String [] {
                    "Plat", "Harga jual", "Tanggal jual", "Nama pembeli", "No. Telp. Pembeli"
                }
            );
            tempTableModelVarAddDataPenjualan.addTableModelListener(new TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent e) {
                        int type = e.getType();
                        switch (type) {
                            case TableModelEvent.UPDATE:
                                if (e.getFirstRow() - e.getLastRow() == 0) {
                                    TableModel model = (TableModel) e.getSource();
                                    int row = e.getFirstRow();
                                    int col = e.getColumn();
                                    //System.out.println("Update DATAPENJUALAN " + row + "x" + col + " = " + model.getValueAt(row, col));
                                    //Validasi data
                                    String dataToValidate = (String) model.getValueAt(row, col);
                                    //System.out.println(dataToValidate);
                                    if(dataToValidate != null) {
                                        switch(col) {
                                            case 0:
                                            {
                                                try {
                                                    if(checkIfPlatExistsInDatabase(dataToValidate) == false) {
                                                        dataToInsert[col] = (String) model.getValueAt(row,col);
                                                    }
                                                    else {
                                                        errorTextForAddData.setText("Plat harus terdaftar di Data Motor");
                                                        model.setValueAt(null,row,col);
                                                    }
                                                } catch (SQLException ex) {
                                                    Logger.getLogger(menuUtama.class.getName()).log(Level.SEVERE, null, ex);
                                                }
                                            }
                                                
                                            break;

                                            case 1:
                                                if(checkIfDataIsInvalid(dataToValidate, "integer")) {
                                                    errorTextForAddData.setText("Harga jual harus bilangan bulat");
                                                    model.setValueAt(null,row,col);
                                                }
                                                else {
                                                    dataToInsert[col] = (String) model.getValueAt(row,col);
                                                }
                                            break;
                                            case 2:
                                                if(checkIfDataIsInvalid(dataToValidate, "date")) {
                                                    errorTextForAddData.setText("Tanggal jual harus berformat yyyy-mm-dd");
                                                    model.setValueAt(null,row,col);
                                                }
                                                else {
                                                    dataToInsert[col] = (String) model.getValueAt(row,col);
                                                }
                                            break;
                                            case 3:
                                                if(checkIfDataIsInvalid(dataToValidate, 64)) {
                                                    errorTextForAddData.setText("Nama tidak boleh lebih dari 64 karakter");
                                                    model.setValueAt(null,row,col);
                                                }
                                                else {
                                                    dataToInsert[col] = (String) model.getValueAt(row,col);
                                                }
                                            break;
                                            case 4:
                                                if(checkIfDataIsInvalid(dataToValidate, 12)) {
                                                    errorTextForAddData.setText("Nomor telepon tidak boleh lebih dari 12 karakter");
                                                    model.setValueAt(null,row,col);
                                                }
                                                else {
                                                    dataToInsert[col] = (String) model.getValueAt(row,col);
                                                }
                                            break;
                                            default:
                                                errorTextForAddData.setText("");
                                            break;
                                        }
                                    }
                                    //Semua data sangat penting - tidak boleh melewatkan satupun
                                    if(model.getValueAt(0,0) != null
                                            && model.getValueAt(0,1) != null 
                                            && model.getValueAt(0,2) != null 
                                            && model.getValueAt(0,3) != null 
                                            && model.getValueAt(0,4) != null ) {
                                        changeBackground(panelSubmitData, new Color(15,39,84));
                                        submitDataButtonEnabled = true;
                                    }
                                    else {
                                        changeBackground(panelSubmitData, new Color(153,153,153));
                                        submitDataButtonEnabled = false;
                                    }
                                }
                                break;
                        }
                    }
                });
            addDataTable.setModel(tempTableModelVarAddDataPenjualan);
        }
    }
    
    public menuUtama() throws SQLException {
        itemsToDisplayPerPage = 10;
        currentPageNumber = 1;
        previousPageButtonEnabled = false;
        nextPageButtonEnabled = true;
        submitDataButtonEnabled = false;
        currentTable = "datamotor";
        
        sqlConnectionGetRowCount = new SQLDatabaseConnection("SELECT COUNT(*) as something FROM datamotor", "select", 1);
        totalRowCount = Integer.parseInt((String) sqlConnectionGetRowCount.returnedRows[0][0]);
        maxPageCount = ((totalRowCount % 10 == 0) ? totalRowCount / 10 : (totalRowCount / 10) + 1 );
        
        mainSqlConnection = new SQLDatabaseConnection("SELECT datamotor.merk, datamotor.ketsurat, datamotor.plat, datapembelian.hargapembelian, datapembelian.tglpembelian, datapenjualan.hargapenjualan, datapenjualan.tglpenjualan FROM datamotor LEFT JOIN datapembelian ON datamotor.idtransaksipembelian = datapembelian.idtransaksipembelian LEFT JOIN datapenjualan ON datamotor.idtransaksipenjualan = datapenjualan.idtransaksipenjualan ORDER BY tglpembelian DESC LIMIT 10", "select", 7);
        initComponents();   
        refreshData();
        errorText.setText("");
        errorTextForAddData.setText("");
        errorTextForSearchData.setText("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        iconPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        panelDataMotor = new javax.swing.JPanel();
        labelDataMotor = new javax.swing.JLabel();
        panelDataPengguna = new javax.swing.JPanel();
        labelDataPengguna = new javax.swing.JLabel();
        panelDataPembelian = new javax.swing.JPanel();
        labelDataPembelian = new javax.swing.JLabel();
        panelDataPenjualan = new javax.swing.JPanel();
        labelDataPenjualan = new javax.swing.JLabel();
        panelCariData = new javax.swing.JPanel();
        labelCariData = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        minimizePanel = new javax.swing.JPanel();
        windowSizePanel = new javax.swing.JPanel();
        closePanel = new javax.swing.JPanel();
        closeLabel = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        mainTabPanel = new javax.swing.JTabbedPane();
        tableDataPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelData = new javax.swing.JTable();
        panelTambahData = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        pageSwitcherPanel = new javax.swing.JPanel();
        previousPagePanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        nextPagePanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        pageNumber = new javax.swing.JLabel();
        errorText = new javax.swing.JLabel();
        addDataPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        addDataTable = new javax.swing.JTable();
        addDataTypeSelection = new javax.swing.JComboBox<>();
        panelSubmitData = new javax.swing.JPanel();
        labelSubmitData = new javax.swing.JLabel();
        errorTextForAddData = new javax.swing.JLabel();
        hiddenMaintenancePanel = new javax.swing.JPanel();
        comboBoxKetSurat = new javax.swing.JComboBox<>();
        comboBoxDaftarPlat = new javax.swing.JComboBox<>();
        searchDataPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        searchDataTextInput = new javax.swing.JTextField();
        searchDataTableSelect = new javax.swing.JComboBox<>();
        searchDataColumnSelect = new javax.swing.JComboBox<>();
        searchButtonPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        searchedDataTable = new javax.swing.JTable();
        errorTextForSearchData = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(960, 540));

        jPanel1.setBackground(new java.awt.Color(224, 224, 224));
        jPanel1.setPreferredSize(new java.awt.Dimension(120, 360));
        jPanel1.setLayout(new java.awt.BorderLayout());

        iconPanel.setBackground(new java.awt.Color(25, 62, 130));
        iconPanel.setPreferredSize(new java.awt.Dimension(120, 120));
        iconPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                iconPanelMouseEntered(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/logo.png"))); // NOI18N

        javax.swing.GroupLayout iconPanelLayout = new javax.swing.GroupLayout(iconPanel);
        iconPanel.setLayout(iconPanelLayout);
        iconPanelLayout.setHorizontalGroup(
            iconPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        iconPanelLayout.setVerticalGroup(
            iconPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(iconPanelLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel1.add(iconPanel, java.awt.BorderLayout.PAGE_START);

        jPanel4.setBackground(new java.awt.Color(224, 224, 224));

        panelDataMotor.setBackground(new java.awt.Color(160, 160, 160));
        panelDataMotor.setToolTipText("");
        panelDataMotor.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelDataMotor.setName(""); // NOI18N
        panelDataMotor.setPreferredSize(new java.awt.Dimension(120, 36));
        panelDataMotor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelDataMotorMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panelDataMotorMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panelDataMotorMouseExited(evt);
            }
        });

        labelDataMotor.setForeground(new java.awt.Color(255, 255, 255));
        labelDataMotor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/motor.png"))); // NOI18N
        labelDataMotor.setText("Motor");

        javax.swing.GroupLayout panelDataMotorLayout = new javax.swing.GroupLayout(panelDataMotor);
        panelDataMotor.setLayout(panelDataMotorLayout);
        panelDataMotorLayout.setHorizontalGroup(
            panelDataMotorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDataMotorLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(labelDataMotor)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelDataMotorLayout.setVerticalGroup(
            panelDataMotorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDataMotorLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(labelDataMotor)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelDataPengguna.setBackground(new java.awt.Color(160, 160, 160));
        panelDataPengguna.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelDataPengguna.setPreferredSize(new java.awt.Dimension(120, 36));
        panelDataPengguna.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelDataPenggunaMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panelDataPenggunaMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panelDataPenggunaMouseExited(evt);
            }
        });

        labelDataPengguna.setForeground(new java.awt.Color(255, 255, 255));
        labelDataPengguna.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/pengguna.png"))); // NOI18N
        labelDataPengguna.setText("Pengguna");

        javax.swing.GroupLayout panelDataPenggunaLayout = new javax.swing.GroupLayout(panelDataPengguna);
        panelDataPengguna.setLayout(panelDataPenggunaLayout);
        panelDataPenggunaLayout.setHorizontalGroup(
            panelDataPenggunaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDataPenggunaLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(labelDataPengguna)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelDataPenggunaLayout.setVerticalGroup(
            panelDataPenggunaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDataPenggunaLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(labelDataPengguna)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelDataPembelian.setBackground(new java.awt.Color(160, 160, 160));
        panelDataPembelian.setForeground(new java.awt.Color(255, 255, 255));
        panelDataPembelian.setToolTipText("");
        panelDataPembelian.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelDataPembelian.setPreferredSize(new java.awt.Dimension(120, 36));
        panelDataPembelian.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelDataPembelianMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panelDataPembelianMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panelDataPembelianMouseExited(evt);
            }
        });

        labelDataPembelian.setForeground(new java.awt.Color(255, 255, 255));
        labelDataPembelian.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/pembelian.png"))); // NOI18N
        labelDataPembelian.setText("Pembelian");

        javax.swing.GroupLayout panelDataPembelianLayout = new javax.swing.GroupLayout(panelDataPembelian);
        panelDataPembelian.setLayout(panelDataPembelianLayout);
        panelDataPembelianLayout.setHorizontalGroup(
            panelDataPembelianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDataPembelianLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(labelDataPembelian, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );
        panelDataPembelianLayout.setVerticalGroup(
            panelDataPembelianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDataPembelianLayout.createSequentialGroup()
                .addComponent(labelDataPembelian)
                .addGap(0, 4, Short.MAX_VALUE))
        );

        panelDataPenjualan.setBackground(new java.awt.Color(160, 160, 160));
        panelDataPenjualan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelDataPenjualan.setPreferredSize(new java.awt.Dimension(120, 36));
        panelDataPenjualan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelDataPenjualanMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panelDataPenjualanMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panelDataPenjualanMouseExited(evt);
            }
        });

        labelDataPenjualan.setForeground(new java.awt.Color(255, 255, 255));
        labelDataPenjualan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/penjualan.png"))); // NOI18N
        labelDataPenjualan.setText("Penjualan");

        javax.swing.GroupLayout panelDataPenjualanLayout = new javax.swing.GroupLayout(panelDataPenjualan);
        panelDataPenjualan.setLayout(panelDataPenjualanLayout);
        panelDataPenjualanLayout.setHorizontalGroup(
            panelDataPenjualanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDataPenjualanLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(labelDataPenjualan, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        panelDataPenjualanLayout.setVerticalGroup(
            panelDataPenjualanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDataPenjualanLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(labelDataPenjualan)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelCariData.setBackground(new java.awt.Color(160, 160, 160));
        panelCariData.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelCariData.setPreferredSize(new java.awt.Dimension(120, 36));
        panelCariData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelCariDataMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panelCariDataMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panelCariDataMouseExited(evt);
            }
        });

        labelCariData.setForeground(new java.awt.Color(255, 255, 255));
        labelCariData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/search.png"))); // NOI18N
        labelCariData.setText("Cari data");

        javax.swing.GroupLayout panelCariDataLayout = new javax.swing.GroupLayout(panelCariData);
        panelCariData.setLayout(panelCariDataLayout);
        panelCariDataLayout.setHorizontalGroup(
            panelCariDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCariDataLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(labelCariData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelCariDataLayout.setVerticalGroup(
            panelCariDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCariDataLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(labelCariData)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelDataMotor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelDataPengguna, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelDataPembelian, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelDataPenjualan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelCariData, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(panelDataMotor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(panelDataPengguna, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(panelDataPembelian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(panelDataPenjualan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelCariData, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(236, Short.MAX_VALUE))
        );

        panelDataMotor.getAccessibleContext().setAccessibleName("");

        jPanel1.add(jPanel4, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.LINE_START);

        jPanel2.setBackground(new java.awt.Color(170, 170, 170));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel5.setBackground(new java.awt.Color(15, 39, 84));
        jPanel5.setMinimumSize(new java.awt.Dimension(300, 40));
        jPanel5.setPreferredSize(new java.awt.Dimension(520, 40));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel7.setBackground(new java.awt.Color(15, 39, 84));
        jPanel7.setMinimumSize(new java.awt.Dimension(300, 40));
        jPanel7.setPreferredSize(new java.awt.Dimension(120, 40));
        jPanel7.setLayout(new java.awt.GridLayout(1, 3));

        minimizePanel.setBackground(new java.awt.Color(15, 39, 84));
        minimizePanel.setPreferredSize(new java.awt.Dimension(40, 40));
        minimizePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                minimizePanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                minimizePanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                minimizePanelMouseExited(evt);
            }
        });

        javax.swing.GroupLayout minimizePanelLayout = new javax.swing.GroupLayout(minimizePanel);
        minimizePanel.setLayout(minimizePanelLayout);
        minimizePanelLayout.setHorizontalGroup(
            minimizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        minimizePanelLayout.setVerticalGroup(
            minimizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel7.add(minimizePanel);

        windowSizePanel.setBackground(new java.awt.Color(15, 39, 84));
        windowSizePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                windowSizePanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                windowSizePanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                windowSizePanelMouseExited(evt);
            }
        });

        javax.swing.GroupLayout windowSizePanelLayout = new javax.swing.GroupLayout(windowSizePanel);
        windowSizePanel.setLayout(windowSizePanelLayout);
        windowSizePanelLayout.setHorizontalGroup(
            windowSizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        windowSizePanelLayout.setVerticalGroup(
            windowSizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel7.add(windowSizePanel);

        closePanel.setBackground(new java.awt.Color(15, 39, 84));
        closePanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        closePanel.setPreferredSize(new java.awt.Dimension(40, 40));
        closePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                closePanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closePanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                closePanelMouseExited(evt);
            }
        });

        closeLabel.setForeground(new java.awt.Color(255, 255, 255));
        closeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        closeLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/Close.png"))); // NOI18N

        javax.swing.GroupLayout closePanelLayout = new javax.swing.GroupLayout(closePanel);
        closePanel.setLayout(closePanelLayout);
        closePanelLayout.setHorizontalGroup(
            closePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, closePanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(closeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        closePanelLayout.setVerticalGroup(
            closePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, closePanelLayout.createSequentialGroup()
                .addComponent(closeLabel)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel7.add(closePanel);

        jPanel5.add(jPanel7, java.awt.BorderLayout.LINE_END);

        jPanel2.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        jPanel6.setBackground(new java.awt.Color(170, 170, 170));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        mainTabPanel.setBackground(new java.awt.Color(170, 170, 170));

        tableDataPanel.setBackground(new java.awt.Color(170, 170, 170));
        tableDataPanel.setPreferredSize(new java.awt.Dimension(770, 450));

        tabelData.setModel(new javax.swing.table.DefaultTableModel(
            mainSqlConnection.returnedRows,
            new String [] {
                "Merek", "Ket. Surat", "Plat", "Harga beli", "Tanggal beli", "Harga jual", "Tanggal jual"
            }
        ));
        tabelData.setColumnSelectionAllowed(true);
        tabelData.setRowSelectionAllowed(false);
        tabelData.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabelData);
        tabelData.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        panelTambahData.setBackground(new java.awt.Color(15, 39, 84));
        panelTambahData.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelTambahData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelTambahDataMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panelTambahDataMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panelTambahDataMouseExited(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Tambah data");

        javax.swing.GroupLayout panelTambahDataLayout = new javax.swing.GroupLayout(panelTambahData);
        panelTambahData.setLayout(panelTambahDataLayout);
        panelTambahDataLayout.setHorizontalGroup(
            panelTambahDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTambahDataLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelTambahDataLayout.setVerticalGroup(
            panelTambahDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTambahDataLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                .addContainerGap())
        );

        pageSwitcherPanel.setBackground(new java.awt.Color(140, 140, 140));
        pageSwitcherPanel.setPreferredSize(new java.awt.Dimension(192, 64));
        pageSwitcherPanel.setLayout(new java.awt.BorderLayout());

        previousPagePanel.setBackground(new java.awt.Color(153, 153, 153));
        previousPagePanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        previousPagePanel.setPreferredSize(new java.awt.Dimension(64, 64));
        previousPagePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                previousPagePanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                previousPagePanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                previousPagePanelMouseExited(evt);
            }
        });
        previousPagePanel.setLayout(new java.awt.BorderLayout());

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/leftarrow.png"))); // NOI18N
        previousPagePanel.add(jLabel5, java.awt.BorderLayout.CENTER);

        pageSwitcherPanel.add(previousPagePanel, java.awt.BorderLayout.WEST);

        nextPagePanel.setBackground(new java.awt.Color(15, 39, 84));
        nextPagePanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        nextPagePanel.setPreferredSize(new java.awt.Dimension(64, 64));
        nextPagePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextPagePanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                nextPagePanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                nextPagePanelMouseExited(evt);
            }
        });
        nextPagePanel.setLayout(new java.awt.BorderLayout());

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/rightarrow.png"))); // NOI18N
        nextPagePanel.add(jLabel4, java.awt.BorderLayout.CENTER);

        pageSwitcherPanel.add(nextPagePanel, java.awt.BorderLayout.EAST);

        pageNumber.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        pageNumber.setForeground(new java.awt.Color(255, 255, 255));
        pageNumber.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pageNumber.setText("1");
        pageSwitcherPanel.add(pageNumber, java.awt.BorderLayout.CENTER);

        errorText.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        errorText.setForeground(new java.awt.Color(200, 0, 0));
        errorText.setText("...");

        javax.swing.GroupLayout tableDataPanelLayout = new javax.swing.GroupLayout(tableDataPanel);
        tableDataPanel.setLayout(tableDataPanelLayout);
        tableDataPanelLayout.setHorizontalGroup(
            tableDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tableDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tableDataPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 828, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(tableDataPanelLayout.createSequentialGroup()
                        .addGroup(tableDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(tableDataPanelLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(panelTambahData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(tableDataPanelLayout.createSequentialGroup()
                                .addComponent(errorText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(pageSwitcherPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(17, 17, 17))))
        );
        tableDataPanelLayout.setVerticalGroup(
            tableDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tableDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pageSwitcherPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(errorText))
                .addGap(60, 60, 60)
                .addComponent(panelTambahData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(84, Short.MAX_VALUE))
        );

        mainTabPanel.addTab("tab1", tableDataPanel);

        addDataPanel.setBackground(new java.awt.Color(170, 170, 170));
        addDataPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Tambah data");
        addDataPanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 260, -1));

        addDataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Merk", "Ket. Surat", "Plat", "Harga beli", "Tanggal beli", "Nama pembeli", "No. Telp. Pembeli"
            }
        ));
        addDataTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane2.setViewportView(addDataTable);

        addDataPanel.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 780, 105));

        addDataTypeSelection.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pembelian Motor", "Penjualan Motor" }));
        addDataTypeSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDataTypeSelectionActionPerformed(evt);
            }
        });
        addDataPanel.add(addDataTypeSelection, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 30, 160, 30));

        panelSubmitData.setBackground(new java.awt.Color(153, 153, 153));
        panelSubmitData.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelSubmitData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelSubmitDataMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panelSubmitDataMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panelSubmitDataMouseExited(evt);
            }
        });

        labelSubmitData.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        labelSubmitData.setForeground(new java.awt.Color(255, 255, 255));
        labelSubmitData.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelSubmitData.setText("Masukkan");

        javax.swing.GroupLayout panelSubmitDataLayout = new javax.swing.GroupLayout(panelSubmitData);
        panelSubmitData.setLayout(panelSubmitDataLayout);
        panelSubmitDataLayout.setHorizontalGroup(
            panelSubmitDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSubmitDataLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelSubmitData, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelSubmitDataLayout.setVerticalGroup(
            panelSubmitDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSubmitDataLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelSubmitData, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                .addContainerGap())
        );

        addDataPanel.add(panelSubmitData, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 200, 280, -1));

        errorTextForAddData.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        errorTextForAddData.setForeground(new java.awt.Color(200, 0, 0));
        errorTextForAddData.setText("...");
        errorTextForAddData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                errorTextForAddDataMouseEntered(evt);
            }
        });
        addDataPanel.add(errorTextForAddData, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, -1, -1));

        mainTabPanel.addTab("tab2", addDataPanel);

        comboBoxKetSurat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "LENGKAP", "TIDAK LENGKAP" }));

        comboBoxDaftarPlat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout hiddenMaintenancePanelLayout = new javax.swing.GroupLayout(hiddenMaintenancePanel);
        hiddenMaintenancePanel.setLayout(hiddenMaintenancePanelLayout);
        hiddenMaintenancePanelLayout.setHorizontalGroup(
            hiddenMaintenancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hiddenMaintenancePanelLayout.createSequentialGroup()
                .addGap(350, 350, 350)
                .addGroup(hiddenMaintenancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboBoxKetSurat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBoxDaftarPlat, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(317, Short.MAX_VALUE))
        );
        hiddenMaintenancePanelLayout.setVerticalGroup(
            hiddenMaintenancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hiddenMaintenancePanelLayout.createSequentialGroup()
                .addGap(154, 154, 154)
                .addComponent(comboBoxKetSurat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56)
                .addComponent(comboBoxDaftarPlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(275, Short.MAX_VALUE))
        );

        mainTabPanel.addTab("tab3", hiddenMaintenancePanel);

        searchDataPanel.setBackground(new java.awt.Color(170, 170, 170));
        searchDataPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Cari data");
        searchDataPanel.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 17, 260, -1));

        searchDataTextInput.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        searchDataTextInput.setText("Teks yang akan dicari");
        searchDataPanel.add(searchDataTextInput, new org.netbeans.lib.awtextra.AbsoluteConstraints(205, 30, 230, -1));

        searchDataTableSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Data Motor", "Data Pengguna", "Data Pembelian", "Data Penjualan" }));
        searchDataTableSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchDataTableSelectActionPerformed(evt);
            }
        });
        searchDataPanel.add(searchDataTableSelect, new org.netbeans.lib.awtextra.AbsoluteConstraints(604, 29, 160, 31));

        searchDataColumnSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Merk", "Keterangan Surat", "Plat" }));
        searchDataColumnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchDataColumnSelectActionPerformed(evt);
            }
        });
        searchDataPanel.add(searchDataColumnSelect, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 29, 160, 31));

        searchButtonPanel.setBackground(new java.awt.Color(15, 39, 84));
        searchButtonPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        searchButtonPanel.setPreferredSize(new java.awt.Dimension(40, 40));
        searchButtonPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchButtonPanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                searchButtonPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                searchButtonPanelMouseExited(evt);
            }
        });

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/search.png"))); // NOI18N

        javax.swing.GroupLayout searchButtonPanelLayout = new javax.swing.GroupLayout(searchButtonPanel);
        searchButtonPanel.setLayout(searchButtonPanelLayout);
        searchButtonPanelLayout.setHorizontalGroup(
            searchButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchButtonPanelLayout.createSequentialGroup()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 3, Short.MAX_VALUE))
        );
        searchButtonPanelLayout.setVerticalGroup(
            searchButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchButtonPanelLayout.createSequentialGroup()
                .addComponent(jLabel7)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        searchDataPanel.add(searchButtonPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 24, -1, -1));

        searchedDataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Menunggu pencarian..."
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(searchedDataTable);

        searchDataPanel.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 790, 310));

        errorTextForSearchData.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        errorTextForSearchData.setForeground(new java.awt.Color(200, 0, 0));
        errorTextForSearchData.setText("...");
        errorTextForSearchData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                errorTextForSearchDataMouseEntered(evt);
            }
        });
        searchDataPanel.add(errorTextForSearchData, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 75, 720, -1));

        mainTabPanel.addTab("tab4", searchDataPanel);

        jPanel6.add(mainTabPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -29, 840, 560));

        jPanel2.add(jPanel6, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void changeBackground(JPanel panel, Color colorToChangeTo) {
        panel.setBackground(colorToChangeTo);
    }
    
    public void changeForeground(JLabel label, Color colorToChangeTo) {
        label.setForeground(colorToChangeTo);
    }
    
    private void panelDataMotorMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelDataMotorMouseEntered
        changeBackground(panelDataMotor, new Color(186, 186, 186));
    }//GEN-LAST:event_panelDataMotorMouseEntered

    private void panelDataMotorMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelDataMotorMouseExited
        changeBackground(panelDataMotor, new Color(160, 160, 160));
    }//GEN-LAST:event_panelDataMotorMouseExited

    private void panelDataPenggunaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelDataPenggunaMouseEntered
        changeBackground(panelDataPengguna, new Color(186, 186, 186));
    }//GEN-LAST:event_panelDataPenggunaMouseEntered

    private void panelDataPenggunaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelDataPenggunaMouseExited
        changeBackground(panelDataPengguna, new Color(160, 160, 160));
    }//GEN-LAST:event_panelDataPenggunaMouseExited

    private void panelDataPembelianMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelDataPembelianMouseEntered
        changeBackground(panelDataPembelian, new Color(186, 186, 186));
    }//GEN-LAST:event_panelDataPembelianMouseEntered

    private void panelDataPembelianMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelDataPembelianMouseExited
        changeBackground(panelDataPembelian, new Color(160, 160, 160));
    }//GEN-LAST:event_panelDataPembelianMouseExited

    private void panelDataPenjualanMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelDataPenjualanMouseEntered
        changeBackground(panelDataPenjualan, new Color(186, 186, 186));
    }//GEN-LAST:event_panelDataPenjualanMouseEntered

    private void panelDataPenjualanMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelDataPenjualanMouseExited
        changeBackground(panelDataPenjualan, new Color(160, 160, 160));
    }//GEN-LAST:event_panelDataPenjualanMouseExited

    private void iconPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_iconPanelMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_iconPanelMouseEntered

    private void minimizePanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minimizePanelMouseExited
//        changeBackground(minimizePanel, new Color(15, 39, 84));;
    }//GEN-LAST:event_minimizePanelMouseExited

    private void minimizePanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minimizePanelMouseEntered

//        changeBackground(minimizePanel, new Color(25, 62, 132));
    }//GEN-LAST:event_minimizePanelMouseEntered

    private void minimizePanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minimizePanelMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_minimizePanelMouseClicked

    private void closePanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closePanelMouseClicked
        System.exit(0);
    }//GEN-LAST:event_closePanelMouseClicked

    private void closePanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closePanelMouseEntered
        changeBackground(closePanel, new Color(25, 62, 132));
    }//GEN-LAST:event_closePanelMouseEntered

    private void closePanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closePanelMouseExited

        changeBackground(closePanel, new Color(15, 39, 84));
    }//GEN-LAST:event_closePanelMouseExited

    private void windowSizePanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_windowSizePanelMouseClicked
//        if(this.getExtendedState() != menuUtama.MAXIMIZED_BOTH) {
//            this.setExtendedState(menuUtama.MAXIMIZED_BOTH);
//        }
//        else {
//            this.setExtendedState(menuUtama.NORMAL);
//        }
    }//GEN-LAST:event_windowSizePanelMouseClicked

    private void windowSizePanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_windowSizePanelMouseEntered
//        changeBackground(windowSizePanel, new Color(25, 62, 132));
    }//GEN-LAST:event_windowSizePanelMouseEntered

    private void windowSizePanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_windowSizePanelMouseExited
//        changeBackground(windowSizePanel, new Color(15, 39, 84));
    }//GEN-LAST:event_windowSizePanelMouseExited

    private void panelDataMotorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelDataMotorMouseClicked
        submitDataButtonEnabled = false;
        changeBackground(panelSubmitData, new Color(153,153,153));
        currentTable = "datamotor";
        errorText.setText("");
        try {
            updateMaxPageNumber("SELECT COUNT(*) as something FROM datamotor");
        } catch (SQLException ex) {
            Logger.getLogger(menuUtama.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        mainTabPanel.setSelectedIndex(0);
    }//GEN-LAST:event_panelDataMotorMouseClicked

    private void panelDataPenggunaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelDataPenggunaMouseClicked
        submitDataButtonEnabled = false;
        changeBackground(panelSubmitData, new Color(153,153,153));
        currentTable = "datapengguna";
        errorText.setText("");
        try {
            updateMaxPageNumber("SELECT COUNT(*) as something FROM datapengguna");
        } catch (SQLException ex) {
            Logger.getLogger(menuUtama.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        mainTabPanel.setSelectedIndex(0);
    }//GEN-LAST:event_panelDataPenggunaMouseClicked

    private void panelDataPembelianMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelDataPembelianMouseClicked
        submitDataButtonEnabled = false;
        changeBackground(panelSubmitData, new Color(153,153,153));
        currentTable = "datapembelian";
        errorText.setText("");
        try {
            updateMaxPageNumber("SELECT COUNT(*) as something FROM datapembelian");
        } catch (SQLException ex) {
            Logger.getLogger(menuUtama.class.getName()).log(Level.SEVERE, null, ex);
        }
        mainTabPanel.setSelectedIndex(0);
    }//GEN-LAST:event_panelDataPembelianMouseClicked

    private void panelDataPenjualanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelDataPenjualanMouseClicked
        submitDataButtonEnabled = false;
        changeBackground(panelSubmitData, new Color(153,153,153));
        currentTable = "datapenjualan";
        errorText.setText("");
        try {
            updateMaxPageNumber("SELECT COUNT(*) as something FROM datapenjualan");
        } catch (SQLException ex) {
            Logger.getLogger(menuUtama.class.getName()).log(Level.SEVERE, null, ex);
        }
        mainTabPanel.setSelectedIndex(0);
    }//GEN-LAST:event_panelDataPenjualanMouseClicked

    private void panelTambahDataMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelTambahDataMouseEntered
        changeBackground(panelTambahData, new Color(25, 62, 132));
    }//GEN-LAST:event_panelTambahDataMouseEntered

    private void panelTambahDataMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelTambahDataMouseExited
        changeBackground(panelTambahData, new Color(15,39,84));
    }//GEN-LAST:event_panelTambahDataMouseExited

    private void panelTambahDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelTambahDataMouseClicked
        setAddDataTableModel("pembelian");
        addDataTypeSelection.setSelectedIndex(0);
        errorTextForAddData.setText("");
        mainTabPanel.setSelectedIndex(1);
    }//GEN-LAST:event_panelTambahDataMouseClicked

    private void addDataTypeSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDataTypeSelectionActionPerformed
        JComboBox cb = (JComboBox)evt.getSource();
        String chosenValue = (String)cb.getSelectedItem();
        submitDataButtonEnabled = false;
        changeBackground(panelSubmitData, new Color(153,153,153));
        if("Pembelian Motor".equals(chosenValue)) {
            setAddDataTableModel("pembelian");
        }
        else {
            setAddDataTableModel("penjualan");
        }
        
    }//GEN-LAST:event_addDataTypeSelectionActionPerformed

    private void nextPagePanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextPagePanelMouseEntered
        if(nextPageButtonEnabled == true) {
            changeBackground(nextPagePanel, new Color(25, 62, 132));
        }
    }//GEN-LAST:event_nextPagePanelMouseEntered

    private void nextPagePanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextPagePanelMouseExited
        if(nextPageButtonEnabled == true) {
            changeBackground(nextPagePanel, new Color(15,39,84));
        }
    }//GEN-LAST:event_nextPagePanelMouseExited

    private void previousPagePanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_previousPagePanelMouseEntered
        if(previousPageButtonEnabled == true) {
            changeBackground(previousPagePanel, new Color(25, 62, 132));
        }
    }//GEN-LAST:event_previousPagePanelMouseEntered

    private void previousPagePanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_previousPagePanelMouseExited
        if(previousPageButtonEnabled == true) {
            changeBackground(previousPagePanel, new Color(15,39,84));
        }
    }//GEN-LAST:event_previousPagePanelMouseExited

    private void previousPagePanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_previousPagePanelMouseClicked
        if(previousPageButtonEnabled == true) {
            currentPageNumber--;
            pageNumber.setText(Integer.toString(currentPageNumber));
            
            nextPageButtonEnabled = true;
            changeBackground(nextPagePanel, new Color(15,39,84));
            
            if(currentPageNumber <= 1) {
                previousPageButtonEnabled = false;
                changeBackground(previousPagePanel, new Color(153,153,153));
            }
            
            try {
                refreshData();
            } catch (SQLException ex) {
                Logger.getLogger(menuUtama.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_previousPagePanelMouseClicked

    private void nextPagePanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextPagePanelMouseClicked
        if(nextPageButtonEnabled == true) {
            currentPageNumber++;
            pageNumber.setText(Integer.toString(currentPageNumber));
            
            previousPageButtonEnabled = true;
            changeBackground(previousPagePanel, new Color(25, 62, 132));
            
            if(currentPageNumber >= maxPageCount) {
                nextPageButtonEnabled = false;
                changeBackground(nextPagePanel, new Color(153,153,153));
            }
            
            try {
                refreshData();
            } catch (SQLException ex) {
                Logger.getLogger(menuUtama.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_nextPagePanelMouseClicked

    private void panelSubmitDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelSubmitDataMouseClicked
        if(submitDataButtonEnabled == true) {
            String addDataType = (String)addDataTypeSelection.getSelectedItem();
            try {
                if("Pembelian Motor".equals(addDataType)) {
                    sqlConnectionModifyData_1 = new SQLDatabaseConnection("INSERT INTO datamotor (`merk`, `ketsurat`, `plat`) VALUES ('"+dataToInsert[0]+"','"+dataToInsert[1]+"','"+dataToInsert[2]+"')", "update", 3);
                    sqlGetIdOfRowToModify_1 = new SQLDatabaseConnection("SELECT idmotor FROM datamotor WHERE plat='"+dataToInsert[2]+"'", "select", 1);
                    idHolder[0] = (String) sqlGetIdOfRowToModify_1.returnedRows[0][0];
                    sqlConnectionModifyData_2 = new SQLDatabaseConnection("INSERT INTO datapengguna (`nama`, `nomortelp`) VALUES ('"+dataToInsert[5]+"','"+dataToInsert[6]+"')", "update", 2);
                    sqlGetIdOfRowToModify_2 = new SQLDatabaseConnection("SELECT idpengguna FROM datapengguna WHERE nomortelp='"+dataToInsert[6]+"'", "select", 1);
                    idHolder[1] = (String) sqlGetIdOfRowToModify_2.returnedRows[0][0];
                    sqlConnectionModifyData_3 = new SQLDatabaseConnection("INSERT INTO datapembelian (`hargapembelian`, `tglpembelian`, `idmotor`, `idpengguna`) VALUES ('"+dataToInsert[3]+"','"+dataToInsert[4]+"','"+idHolder[0]+"','"+idHolder[1]+"')", "update", 3);
                    sqlGetIdOfRowToModify_3 = new SQLDatabaseConnection("SELECT idtransaksipembelian FROM datapembelian WHERE idmotor='"+idHolder[0]+"'", "select", 1);
                    idHolder[2] = (String) sqlGetIdOfRowToModify_3.returnedRows[0][0];
                    sqlConnectionModifyData_4 = new SQLDatabaseConnection("UPDATE `datamotor` SET idtransaksipembelian="+idHolder[2]+" WHERE idmotor="+idHolder[0],"update",1);
                }
                else if("Penjualan Motor".equals(addDataType)) {
                    sqlConnectionModifyData_1 = new SQLDatabaseConnection("INSERT INTO datapengguna (`nama`, `nomortelp`) VALUES ('"+dataToInsert[3]+"','"+dataToInsert[4]+"')", "update", 2);
                    sqlGetIdOfRowToModify_1 = new SQLDatabaseConnection("SELECT idpengguna FROM datapengguna WHERE nomortelp='"+dataToInsert[4]+"'", "select", 1);
                    idHolder[1] = (String) sqlGetIdOfRowToModify_1.returnedRows[0][0];
                    sqlGetIdOfRowToModify_2 = new SQLDatabaseConnection("SELECT idmotor FROM datamotor WHERE plat='"+dataToInsert[0]+"'", "select", 1);
                    idHolder[0] = (String) sqlGetIdOfRowToModify_2.returnedRows[0][0];
                    sqlConnectionModifyData_3 = new SQLDatabaseConnection("INSERT INTO datapenjualan (`hargapenjualan`, `tglpenjualan`, `idmotor`, `idpengguna`) VALUES ('"+dataToInsert[1]+"','"+dataToInsert[2]+"','"+idHolder[0]+"','"+idHolder[1]+"')", "update", 3);
                    sqlGetIdOfRowToModify_3 = new SQLDatabaseConnection("SELECT idtransaksipenjualan FROM datapenjualan WHERE idmotor='"+idHolder[0]+"'", "select", 1);
                    idHolder[2] = (String) sqlGetIdOfRowToModify_3.returnedRows[0][0];
                    sqlConnectionModifyData_4 = new SQLDatabaseConnection("UPDATE `datamotor` SET idtransaksipenjualan="+idHolder[2]+" WHERE idmotor="+idHolder[0],"update",1);
                }
                refreshData();
                mainTabPanel.setSelectedIndex(0);
            }
            catch (SQLException ex) {
                Logger.getLogger(menuUtama.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_panelSubmitDataMouseClicked

    private void panelSubmitDataMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelSubmitDataMouseEntered
        if(submitDataButtonEnabled == true) {
            changeBackground(panelSubmitData, new Color(25,62,132));
        }
    }//GEN-LAST:event_panelSubmitDataMouseEntered

    private void panelSubmitDataMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelSubmitDataMouseExited
        if(submitDataButtonEnabled == true) {
            changeBackground(panelSubmitData, new Color(15,39,84));
        }
    }//GEN-LAST:event_panelSubmitDataMouseExited

    private void errorTextForAddDataMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_errorTextForAddDataMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_errorTextForAddDataMouseEntered

    private void panelCariDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelCariDataMouseClicked
        mainTabPanel.setSelectedIndex(3);
    }//GEN-LAST:event_panelCariDataMouseClicked

    private void panelCariDataMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelCariDataMouseEntered
        changeBackground(panelCariData, new Color(186,186,186));
    }//GEN-LAST:event_panelCariDataMouseEntered

    private void panelCariDataMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelCariDataMouseExited
        changeBackground(panelCariData, new Color(160,160,160));
    }//GEN-LAST:event_panelCariDataMouseExited

    private void searchDataColumnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchDataColumnSelectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchDataColumnSelectActionPerformed

    private void searchDataTableSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchDataTableSelectActionPerformed
        int tableToSearch = searchDataTableSelect.getSelectedIndex();
        switch (tableToSearch) {
            case 0:
                searchDataColumnSelect.setSelectedIndex(0);
                searchDataColumnSelect.removeAllItems();
                searchDataColumnSelect.addItem("Merk");
                searchDataColumnSelect.addItem("Keterangan Surat");
                searchDataColumnSelect.addItem("Plat");
                break;
            case 1:
                searchDataColumnSelect.setSelectedIndex(0);
                searchDataColumnSelect.removeAllItems();
                searchDataColumnSelect.addItem("Nama");
                searchDataColumnSelect.addItem("Nomor Telepon");
                break;
            case 2:
                searchDataColumnSelect.setSelectedIndex(0);
                searchDataColumnSelect.removeAllItems();
                searchDataColumnSelect.addItem("Harga Pembelian");
                searchDataColumnSelect.addItem("Tanggal Pembelian");
                break;
            case 3:
                searchDataColumnSelect.setSelectedIndex(0);
                searchDataColumnSelect.removeAllItems();
                searchDataColumnSelect.addItem("Harga Penjualan");
                searchDataColumnSelect.addItem("Tanggal Penjualan");
                break;
            default:
                break;
        }
    }//GEN-LAST:event_searchDataTableSelectActionPerformed

    private void searchButtonPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchButtonPanelMouseEntered
        changeBackground(searchButtonPanel, new Color(25,62,132));
    }//GEN-LAST:event_searchButtonPanelMouseEntered

    private void searchButtonPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchButtonPanelMouseExited
        changeBackground(searchButtonPanel, new Color(15,39,84));
    }//GEN-LAST:event_searchButtonPanelMouseExited

    private void searchButtonPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchButtonPanelMouseClicked
        TableModel tempTableModelVarCarian;
        String columnToSearch = (String) searchDataColumnSelect.getSelectedItem();
        String tableToSearch = (String) searchDataTableSelect.getSelectedItem();
        String extraQuotes = "'";
        columnToSearch = convertColumnSelectIntoInternalName(columnToSearch);
        String searchQuery = searchDataTextInput.getText();
        
        if("hargapembelian".equals(columnToSearch) || "hargapenjualan".equals(columnToSearch)) {
            extraQuotes = "";
        }
        else {
            extraQuotes = "'";
        }
        
        if(("tglpembelian".equals(columnToSearch) || "tglpenjualan".equals(columnToSearch))
                && checkIfDataIsInvalid(searchQuery, "date")) {
            searchDataTextInput.setText(null);
            errorTextForSearchData.setText("Teks harus dalam format tanggal yyyy-mm-dd");
        }
        else if(("hargapembelian".equals(columnToSearch) || "hargapenjualan".equals(columnToSearch))
                && checkIfDataIsInvalid(searchQuery, "integer")) {
            searchDataTextInput.setText(null);
            errorTextForSearchData.setText("Teks harus berupa bilangan bulat");
        }
        else {
            //Data yang lain tidak perlu divalidasi karena tidak akan menhasilkan error
            errorTextForSearchData.setText("");
            try {
                switch(tableToSearch) {
                    case "Data Motor":
                        mainSqlConnection = new SQLDatabaseConnection("SELECT datamotor.merk, datamotor.ketsurat, datamotor.plat, datapembelian.hargapembelian, datapembelian.tglpembelian, datapenjualan.hargapenjualan, datapenjualan.tglpenjualan FROM datamotor LEFT JOIN datapembelian ON datamotor.idtransaksipembelian = datapembelian.idtransaksipembelian LEFT JOIN datapenjualan ON datamotor.idtransaksipenjualan = datapenjualan.idtransaksipenjualan WHERE "+columnToSearch+"='"+searchQuery+"' ORDER BY tglpembelian DESC LIMIT 10", "select", 7);
                        tempTableModelVarCarian = new javax.swing.table.DefaultTableModel(
                            mainSqlConnection.returnedRows,
                            new String [] {
                                "Merek", "Ket. Surat", "Plat", "Harga beli", "Tanggal beli", "Harga jual", "Tanggal jual"
                            }
                        );
                        break;
                    case "Data Pengguna":

                        mainSqlConnection = new SQLDatabaseConnection("SELECT nama, nomortelp FROM datapengguna WHERE "+columnToSearch+"='"+searchQuery+"' LIMIT 10", "select", 2);
                        tempTableModelVarCarian = new javax.swing.table.DefaultTableModel(
                            mainSqlConnection.returnedRows,
                            new String [] {
                                "Nama", "Nomor telepon"
                            }
                        );
                        break;
                    case "Data Pembelian":
                        mainSqlConnection = new SQLDatabaseConnection("SELECT datapembelian.tglpembelian, datapembelian.hargapembelian, datamotor.merk, datapengguna.nama, datapengguna.nomortelp FROM datapembelian LEFT JOIN datamotor ON datapembelian.idmotor = datamotor.idmotor LEFT JOIN datapengguna ON datapembelian.idpengguna = datapengguna.idpengguna WHERE "+columnToSearch+"="+extraQuotes+searchQuery+extraQuotes+" ORDER BY tglpembelian DESC LIMIT 10", "select", 5);
                        tempTableModelVarCarian = new javax.swing.table.DefaultTableModel(
                            mainSqlConnection.returnedRows,
                            new String [] {
                                "Tanggal Pembelian", "Harga Pembelian", "Merek", "Nama Penjual", "Nomor Telepon"
                            }
                        );
                        break;
                    case "Data Penjualan":
                        mainSqlConnection = new SQLDatabaseConnection("SELECT datapenjualan.tglpenjualan, datapenjualan.hargapenjualan, datamotor.merk, datapengguna.nama, datapengguna.nomortelp FROM datapenjualan LEFT JOIN datamotor ON datapenjualan.idmotor = datamotor.idmotor LEFT JOIN datapengguna ON datapenjualan.idpengguna = datapengguna.idpengguna WHERE "+columnToSearch+"="+extraQuotes+searchQuery+extraQuotes+" ORDER BY tglpenjualan DESC LIMIT 10", "select", 5);
                        tempTableModelVarCarian = new javax.swing.table.DefaultTableModel(
                            mainSqlConnection.returnedRows,
                            new String [] {
                                "Tanggal Penjualan", "Harga Penjualan", "Merek", "Nama Pembeli", "Nomor Telepon"
                            }
                        );
                        break;
                    default:
                        tempTableModelVarCarian = new javax.swing.table.DefaultTableModel(
                            mainSqlConnection.returnedRows,
                            new String [] {
                                "Merek", "Ket. Surat", "Plat", "Harga beli", "Tanggal beli", "Harga jual", "Tanggal jual"
                            }
                        );
                        break;
                }

                searchedDataTable.setModel(tempTableModelVarCarian);
            }

            catch (SQLException ex) {
                Logger.getLogger(menuUtama.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_searchButtonPanelMouseClicked

    private void errorTextForSearchDataMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_errorTextForSearchDataMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_errorTextForSearchDataMouseEntered

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
            java.util.logging.Logger.getLogger(menuUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(menuUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(menuUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(menuUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new menuUtama().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(menuUtama.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel addDataPanel;
    private javax.swing.JTable addDataTable;
    private javax.swing.JComboBox<String> addDataTypeSelection;
    private javax.swing.JLabel closeLabel;
    private javax.swing.JPanel closePanel;
    private javax.swing.JComboBox<String> comboBoxDaftarPlat;
    private javax.swing.JComboBox<String> comboBoxKetSurat;
    private javax.swing.JLabel errorText;
    private javax.swing.JLabel errorTextForAddData;
    private javax.swing.JLabel errorTextForSearchData;
    private javax.swing.JPanel hiddenMaintenancePanel;
    private javax.swing.JPanel iconPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel labelCariData;
    private javax.swing.JLabel labelDataMotor;
    private javax.swing.JLabel labelDataPembelian;
    private javax.swing.JLabel labelDataPengguna;
    private javax.swing.JLabel labelDataPenjualan;
    private javax.swing.JLabel labelSubmitData;
    private javax.swing.JTabbedPane mainTabPanel;
    private javax.swing.JPanel minimizePanel;
    private javax.swing.JPanel nextPagePanel;
    private javax.swing.JLabel pageNumber;
    private javax.swing.JPanel pageSwitcherPanel;
    private javax.swing.JPanel panelCariData;
    private javax.swing.JPanel panelDataMotor;
    private javax.swing.JPanel panelDataPembelian;
    private javax.swing.JPanel panelDataPengguna;
    private javax.swing.JPanel panelDataPenjualan;
    private javax.swing.JPanel panelSubmitData;
    private javax.swing.JPanel panelTambahData;
    private javax.swing.JPanel previousPagePanel;
    private javax.swing.JPanel searchButtonPanel;
    private javax.swing.JComboBox<String> searchDataColumnSelect;
    private javax.swing.JPanel searchDataPanel;
    private javax.swing.JComboBox<String> searchDataTableSelect;
    private javax.swing.JTextField searchDataTextInput;
    private javax.swing.JTable searchedDataTable;
    private javax.swing.JTable tabelData;
    private javax.swing.JPanel tableDataPanel;
    private javax.swing.JPanel windowSizePanel;
    // End of variables declaration//GEN-END:variables
}
