package com.mukicloud.mukitest.NetDB;

import android.util.Log;

import com.mukicloud.mukitest.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class NetLogDBHlp {
    private Connection SQLConn;
    private Statement STM;
    //Constants
    private final static String Com = ", ";
    private final static String Sta = "(";
    private final static String End = ")";
    private final static String Qut = "'";
    private final static String EQU = " = ";
    //TABLE
    private static String TABLE_NAME = "Package";

    private static final String COL_ID = "ID";
    private static final String COL_Time = "Time";
    private static final String COL_UserID = "UserID";
    private static final String COL_Action = "Action";
    private static final String COL_Value = "Value";

    private String[] NetDBAry = new String[]{"remotemysql.com", "pvahOCHzz8", "pvahOCHzz8", "oPpLMn5VZA"};

    boolean ConnectSQL() throws Exception {
        if (STM == null) {
            if (NetDBConn.ConnectSQL(NetDBAry[0], NetDBAry[1], NetDBAry[2], NetDBAry[3])) {
                SQLConn = NetDBConn.getSQLConn();
                STM = NetDBConn.getSTM();
                AutoCreateTable();
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    private void AutoCreateTable() throws Exception {
        //Prepare Create Table
        TABLE_NAME = BuildConfig.APPLICATION_ID;
        TABLE_NAME = TABLE_NAME.replace(".", "_");
        String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " ( " +
                        " ID INT AUTO_INCREMENT," +
                        " Time TEXT, " +
                        " UserID TEXT, " +
                        " Action TEXT, " +
                        " Value TEXT, " +
                        " PRIMARY KEY (ID)); ";
        //Alert Create
        DatabaseMetaData DMD = SQLConn.getMetaData();
        ResultSet Tables = DMD.getTables(null, null, TABLE_NAME, null);
        if (!Tables.next()) {//Not Exist
            STM.execute(TABLE_CREATE);
        }
    }

    public boolean InsertDB(NetLogSite Site) {
        try {
            String SQLSyntax = "INSERT INTO " + TABLE_NAME + " ";
            SQLSyntax += AddColumns(COL_Time, COL_UserID, COL_Action, COL_Value); //Columns
            SQLSyntax += AddValues(Site.getTime(), Site.getUserID(), Site.getAction(), Site.getValue());//Values
            STM.execute(SQLSyntax);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean UpdateDB(NetLogSite Site) {
        try {
            String SQLSyntax = "UPDATE " + TABLE_NAME;
            //Columns
            String[] ColAry = new String[]{COL_Time, COL_UserID, COL_Action, COL_Value};
            String[] ValAry = new String[]{Site.getTime(), Site.getUserID(), Site.getAction(), Site.getValue()};
            SQLSyntax += AddUpdateValues(ColAry, ValAry);
            //Where
            SQLSyntax += " WHERE " + COL_ID + EQU + GetValSQLStr(Site.getID());
            STM.execute(SQLSyntax);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean DeleteID(String ID) {
        try {
            String SQLSyntax = "DELETE FROM " + TABLE_NAME + " WHERE " + COL_ID + " = '" + ID + "'";
            STM.execute(SQLSyntax);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean DeleteTable() {
        try {
            String SQLSyntax = "DROP TABLE " + TABLE_NAME;
            STM.execute(SQLSyntax);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean DeleteAll() {
        try {
            String DeleteSQL = "DELETE FROM " + TABLE_NAME;
            STM.execute(DeleteSQL);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getCount() {
        int Count = -1;
        try {
            ResultSet RS = STM.executeQuery("SELECT COUNT(*) AS count FROM " + TABLE_NAME);
            while (RS.next()) {
                Count = RS.getInt("count");
            }
        } catch (Exception e) {
            Log.e("getCount", "" + e.getMessage());
        }
        return Count;
    }

    NetLogSite findDBColumn(String Column, String Data) {
        NetLogSite Site = null;
        ArrayList<NetLogSite> SiteAL = findDBColumnMulti(Column, Data, 1);
        if (SiteAL.size() > 0) {
            Site = SiteAL.get(0);
        }
        return Site;
    }

    public ArrayList<NetLogSite> findDBColumnMulti(String Column, String Data) {
        return findDBColumnMulti(Column, Data, 0);
    }

    private ArrayList<NetLogSite> findDBColumnMulti(String Column, String Data, int Limit) {
        ArrayList<NetLogSite> SiteAL = new ArrayList<>();
        try {
            String FindSQL = "SELECT * FROM " + TABLE_NAME;
            //Where
            FindSQL += " WHERE " + Column + EQU + GetValSQLStr(Data);
            //Limit
            if (Limit > 0) FindSQL += " LIMIT " + Limit;
            //Query
            ResultSet RS = STM.executeQuery(FindSQL);
            while (RS.next()) {
                SiteAL.add(new NetLogSite(RS.getString(1), RS.getString(2), RS.getString(3),
                        RS.getString(4), RS.getString(5)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SiteAL;
    }

    public ArrayList<NetLogSite> findIDExist(String Column, ArrayList<String> Values) {
        ArrayList<NetLogSite> SiteAL = new ArrayList<>();
        try {
            String FindSQL = "SELECT * FROM " + TABLE_NAME;
            //Where
            FindSQL += AddFindDataWhere(Column, Values);
            //Query
            ResultSet RS = STM.executeQuery(FindSQL);
            if (RS.isBeforeFirst()) {
                while (RS.next()) {
                    SiteAL.add(new NetLogSite(RS.getString(1), RS.getString(2), RS.getString(3),
                            RS.getString(4), RS.getString(5)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SiteAL;
    }

    public JSONArray GetAllJA() {
        JSONArray JA = new JSONArray();
        try {
            String FindSQL = "SELECT * FROM " + TABLE_NAME;
            //Query
            ResultSet RS = STM.executeQuery(FindSQL);
            if (RS.isBeforeFirst()) {
                while (RS.next()) {
                    JSONObject JOB = new JSONObject();
                    JOB.put(COL_ID, RS.getString(1));
                    JOB.put(COL_Time, RS.getString(2));
                    JOB.put(COL_UserID, RS.getString(3));
                    JOB.put(COL_Action, RS.getString(4));
                    JOB.put(COL_Value, RS.getString(5));
                    JA.put(JOB);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JA;
    }

    //Insert============================================================================================================
    private String AddColumns(String... Columns) {
        StringBuilder SQLSyntaxSB = new StringBuilder();
        if (Columns.length > 0) {
            SQLSyntaxSB.append(Sta);
            for (int cnt = 0; cnt < Columns.length; cnt++) {
                SQLSyntaxSB.append(Columns[cnt]);
                SQLSyntaxSB.append(cnt < Columns.length - 1 ? Com : End);
            }
        }
        return SQLSyntaxSB.toString();
    }

    private String AddValues(String... Values) {
        StringBuilder SQLSyntaxSB = new StringBuilder();
        if (Values.length > 0) {
            SQLSyntaxSB.append(" VALUES ");
            SQLSyntaxSB.append(Sta);
            for (int cnt = 0; cnt < Values.length; cnt++) {
                SQLSyntaxSB.append(GetValSQLStr(Values[cnt]));
                SQLSyntaxSB.append(cnt < Values.length - 1 ? Com : End);
            }
        }
        return SQLSyntaxSB.toString();
    }

    //Update========================================================================================
    private String AddUpdateValues(String[] Col, String[] Val) {
        StringBuilder SQLSyntaxSB = new StringBuilder();
        if (Col.length > 0 && Col.length == Val.length) {
            SQLSyntaxSB.append(" SET ");
            for (int cnt = 0; cnt < Col.length; cnt++) {
                SQLSyntaxSB.append(Col[cnt]).append(EQU).append(GetValSQLStr(Val[cnt]));
                if (cnt < Col.length - 1) SQLSyntaxSB.append(Com);
            }
        }
        return SQLSyntaxSB.toString();
    }

    //findDataExist=================================================================================
    private String AddFindDataWhere(String Col, ArrayList<String> Val) {
        StringBuilder SQLSyntaxSB = new StringBuilder();
        if (Col.length() > 0 && Val.size() > 0) {
            SQLSyntaxSB.append(" WHERE ");
            for (int cnt = 0; cnt < Val.size(); cnt++) {
                SQLSyntaxSB.append(Col).append(EQU).append(GetValSQLStr(Val.get(cnt)));
                if (cnt < Val.size() - 1) SQLSyntaxSB.append(" OR ");
            }
        }
        return SQLSyntaxSB.toString();
    }

    //Other=========================================================================================
    private String GetValSQLStr(String Value) {
        return Qut + Value + Qut;
    }
}
