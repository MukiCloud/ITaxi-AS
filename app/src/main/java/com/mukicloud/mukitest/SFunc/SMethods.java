package com.mukicloud.mukitest.SFunc;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.mukicloud.mukitest.R;
import com.mukicloud.mukitest.TD;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;


/**
 * Created by SinyoTsai on 2018/2/6.
 */

public class SMethods {
    private Activity Act;
    private Context Con;

    public SMethods() {
    }

    public SMethods(Activity Actin) {
        Act = Actin;
        Con = Act;
    }

    public SMethods(Context Conin) {
        Con = Conin;
    }

    //JSON Long Getter
    public boolean JSONBoolGetter(JSONObject JOB, String Key) {
        boolean Value = false;
        try {
            Value = JOB.getBoolean(Key);
        } catch (Exception e) {
            //DebugToast("JSONStrGetter\n"+e.getMessage());
        }
        return Value;
    }

    //JSON Getter
    public String JSONStrGetter(JSONObject JOB, String Key) {
        return JSONStrGetter(JOB, Key, "");
    }

    public String JSONStrGetter(JSONObject JOB, String Key, String Init) {
        String Value = Init;
        try {
            if (JOB == null) {
                return "";
            }
            Value = JOB.getString(Key);
        } catch (Exception e) {
            //DebugToast("JSONStrGetter\n"+e.getMessage());
        }
        return Value.trim();
    }

    public String JSONStrGetter(JSONArray JA, int Pos) {
        String Value;
        try {
            if (JA == null) {
                return "";
            }
            Value = JA.getString(Pos);
        } catch (Exception e) {
            Value = "";
        }
        return Value.trim();
    }

    //JSON Integer Getter
    public int JSONIntGetter(JSONObject JOB, String Key) {
        return JSONIntGetter(JOB, Key, -1);
    }

    public int JSONIntGetter(JSONObject JOB, String Key, int InitValue) {
        int Value = InitValue;
        try {
            Value = JOB.getInt(Key);
        } catch (Exception e) {
            try {
                Value = Integer.parseInt(JOB.getString(Key));
            } catch (Exception ee) {
                //DebugToast("JSONStrGetter\n"+e.getMessage());
            }
        }
        return Value;
    }

    public int JAIntGetter(JSONArray JA, int Pos) {
        int Value = 0;
        try {
            Value = JA.getInt(Pos);
        } catch (Exception e) {
            //DebugToast("JSONStrGetter\n"+e.getMessage());
        }
        return Value;
    }

    //JSON Float Getter
    public float JSONFloatGetter(JSONObject JOB, String Key) {
        float Value = 0;
        try {
            Value = (float) JOB.getDouble(Key);
        } catch (Exception e) {
            try {
                Value = Float.parseFloat(JOB.getString(Key));
            } catch (Exception ee) {
                //DebugToast("JSONStrGetter\n"+e.getMessage());
            }
        }
        return Value;
    }

    //JSON Long Getter
    public long JSONLongGetter(JSONObject JOB, String Key) {
        long Value = 0;
        try {
            Value = Long.parseLong(JOB.getString(Key));
        } catch (Exception e) {
            //DebugToast("JSONStrGetter\n"+e.getMessage());
        }
        return Value;
    }

    //JSON Long Getter
    public double JSONDoubleGetter(JSONObject JOB, String Key) {
        double Value = 0;
        try {
            Value = Double.parseDouble(JOB.getString(Key));
        } catch (Exception e) {
            //DebugToast("JSONStrGetter\n"+e.getMessage());
        }
        return Value;
    }

    //JSON Array Getter
    public JSONArray JAGetter(JSONObject JOB, String Key) {
        JSONArray JA;
        try {
            JA = JOB.getJSONArray(Key);
        } catch (Exception e) {
            JA = new JSONArray();
        }
        return JA;
    }

    public JSONArray JAGetter(JSONArray JA, int Pos) {
        JSONArray DJA;
        try {
            DJA = JA.getJSONArray(Pos);
        } catch (Exception e) {
            DJA = new JSONArray();
        }
        return DJA;
    }

    public JSONArray JAGetter(String JAStr) {
        JSONArray JA;
        try {
            JA = new JSONArray(JAStr);
        } catch (Exception e) {
            JA = new JSONArray();
        }
        return JA;
    }

    public JSONObject JOBGetter(String JOBStr) {
        JSONObject JOB;
        try {
            JOB = new JSONObject(JOBStr);
        } catch (Exception e) {
            JOB = new JSONObject();
        }
        return JOB;
    }

    public JSONObject JOBGetter(JSONObject RJOB, String Key) {
        return JOBGetter(RJOB, Key, true);
    }

    public JSONObject JOBGetter(JSONObject RJOB, String Key, boolean ReturnNull) {
        JSONObject JOB;
        try {
            JOB = RJOB.getJSONObject(Key);
        } catch (Exception e) {
            JOB = ReturnNull ? null : new JSONObject();
        }
        return JOB;
    }

    public JSONObject JOBGetter(JSONArray JA, int Pos) {
        JSONObject JOB;
        try {
            JOB = JA.getJSONObject(Pos);
        } catch (Exception e) {
            JOB = new JSONObject();
        }
        return JOB;
    }

    //JOB Value Adder
    public void JOBValueAdder(JSONObject JOB, String Key, String Value) {
        try {
            JOB.put(Key, Value);
        } catch (Exception e) {
            //DebugToast("JSONStrGetter\n"+e.getMessage());
        }
    }

    public boolean JAContains(JSONArray DataJA, String... KeyWord) {
        String DataJAStr = DataJA.toString();
        boolean Contains = false;
        for (int cnt = 0; cnt < KeyWord.length; cnt++) {
            if (DataJAStr.contains("\"" + KeyWord[cnt] + "\"")) {
                Contains = true;
                break;
            }
        }
        return Contains;
    }

    //UIToast=================================================================
    private ArrayList<String> ToastBuffer = new ArrayList<>();

    public void UIToast(int StringID) {
        try {
            String Msg = Con.getResources().getString(StringID);
            UIToast(Msg);
        } catch (Exception e) {
            //DebugToast("UIToast\n"+e.getMessage());
        }
    }

    public void UIToast(final String ToastMsg) {
        try {
            if (ToastBuffer.size() < 4) {
                ToastBuffer.add(ToastMsg);
                ToastShower();
            }
        } catch (Exception e) {
            DebugToast("UIToast\n" + e.getMessage());
        }
    }

    private boolean ToastShowerRun = false;

    private void ToastShower() {
        if (!ToastShowerRun) {
            ToastShowerRun = true;
            new Thread() {
                public void run() {
                    try {
                        while (ToastBuffer.size() > 0) {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                try {
                                    if (ToastBuffer.size() > 0) {
                                        Toast.makeText(Con, ToastBuffer.get(0), Toast.LENGTH_SHORT).show();
                                        ToastBuffer.remove(0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                            sleep(1200);
                        }
                    } catch (Exception e) {
                        DebugToast("UIToast\n" + e.getMessage());
                    } finally {
                        ToastShowerRun = false;
                    }
                }
            }.start();
        }
    }

    //Debug UIToast=================================================================
    private static final ArrayList<String> DebugTosAL = new ArrayList<>();

    public void DebugToast(String FuncName, String Msg) {
        DebugToast(FuncName + "\n" + Msg);
    }

    public void DebugToast(String ToastMsg) {
        if (TD.ShowDebugTos && ToastMsg != null && ToastMsg.length() > 0) {
            DebugTosAL.add(ToastMsg);
            DebugToastShower();
        }
    }

    private boolean DebugToastShowerRun = false;

    private void DebugToastShower() {
        if (!DebugToastShowerRun) {
            DebugToastShowerRun = true;
            new Thread() {
                public void run() {
                    try {
                        while (DebugTosAL.size() > 0) {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                try {
                                    if (DebugTosAL.size() > 0) {
                                        Toast.makeText(Act, DebugTosAL.get(0), Toast.LENGTH_SHORT).show();
                                        DebugTosAL.remove(0);
                                    }
                                } catch (Exception e) {
                                    //
                                }
                            });
                            sleep(1100);
                        }
                    } catch (Exception e) {
                        //
                    } finally {
                        DebugToastShowerRun = false;
                    }
                }
            }.start();
        }
    }

    //Exception Toast===============================================================================
    public void EXToast(int ErrorMsgID, String Function, Exception Ex) {
        EXToast(IDStr(ErrorMsgID), Function, Ex);
    }

    public void EXToast(String ErrorMsg, String Function, Exception Ex) {
        try {
            UIToast(ErrorMsg);
            if (TD.ShowDebugTos)
                new Handler().postDelayed(() -> DebugToast(Function, Ex.getMessage()), 1200);
        } catch (Exception e) {
            //
        }
    }

    //SWToast
    public void SWToast(String Title) {
        SWToast(Title, "", SweetAlertDialog.WARNING_TYPE);
    }

    public void SWToast(int TitleID) {
        SWToast(TitleID, SweetAlertDialog.WARNING_TYPE);
    }

    public void SWToast(int TitleID, int Type) {
        try {
            String Title = Con.getResources().getString(TitleID);
            SWToast(Title, Type);
        } catch (Exception e) {
            //DebugToast("UIToast\n"+e.getMessage());
        }
    }

    public void SWToast(int TitleID, String Content) {
        try {
            String Title = Con.getResources().getString(TitleID);
            SWToast(Title, Content, SweetAlertDialog.WARNING_TYPE);
        } catch (Exception e) {
            //DebugToast("UIToast\n"+e.getMessage());
        }
    }

    public void SWToast(String Title, int Type) {
        SWToast(Title, "", Type);
    }

    public void SWToast(String Title, String Content, int Type) {
        SWToast(Title, Content, Type, 2500);
    }

    public void SWToast(int TitleID, String Content, int Type) {
        SWToast(IDStr(TitleID), Content, Type, 2500);
    }

    public void SWToast(int TitleID, int ContentID, int Type) {
        SWToast(IDStr(TitleID), IDStr(ContentID), Type, 2500);
    }

    public void SWToast(int Title, int Content, int Type, int ShowMillis) {
        SWToast(IDStr(Title), IDStr(Content), Type, ShowMillis);
    }

    public SweetAlertDialog SAD;
    private static boolean SADShowing = false;

    public void SWToast(String Title, String Content, int Type, int ShowMillis) {
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                if (SADShowing) return;//Don't show when other showing
                if (SAD != null && SAD.isShowing()) {
                    SAD.dismiss();
                    SAD = null;
                }
                SAD = new SweetAlertDialog(Con, Type);
                SAD.setTitleText(Title);
                if (Content.length() > 0) {
                    SAD.showContentText(true);
                    SAD.setContentText(Content);
                }
                SAD.setCancelable(true);
                SAD.setCanceledOnTouchOutside(true);
                SAD.showCancelButton(false);
                SAD.setConfirmText(IDStr(R.string.CM_OK));
                SAD.setOnDismissListener(dialog -> SADShowing = false);
                SAD.setOnKeyListener((dialog, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (SAD != null && SAD.isShowing()) {
                            SAD.dismiss();
                            SAD = null;
                        }
                        return true;
                    }
                    return false;
                });
                SAD.show();
                SADShowing = true;
                if (ShowMillis > 0) {
                    new Handler().postDelayed(SAD::dismissWithAnimation, ShowMillis);
                }
            } catch (Exception e) {
                DebugToast("SWToast\n" + e.getMessage());
            }
        });
    }

    public SweetAlertDialog SWToastCreator(JSONObject SettingJOB) {
        final SweetAlertDialog SAD = new SweetAlertDialog(Act, JSONIntGetter(SettingJOB, "type", SweetAlertDialog.WARNING_TYPE));
        String Title = JSONStrGetter(SettingJOB, "title");
        String Content = JSONStrGetter(SettingJOB, "content");
        long ShowMillis = JSONLongGetter(SettingJOB, "showMillis");
        SAD.setTitleText(Title);
        if (Content.length() > 0) {
            SAD.showContentText(true);
            SAD.setContentText(Content);
        } else {
            SAD.showContentText(false);
        }
        SAD.setCancelable(true);
        SAD.showCancelButton(JSONBoolGetter(SettingJOB, "cancelButton"));
        SAD.setConfirmText(JSONStrGetter(SettingJOB, "confirmText", "確認"));
        SAD.setCancelText("取消");
        SAD.show();
        if (ShowMillis > 0) {
            new Handler().postDelayed(SAD::dismissWithAnimation, ShowMillis);
        }
        return SAD;
    }

    public SweetAlertDialog NPGSDialog;

    public void SProgressDialog(boolean Show, int StringID) {
        try {
            String Msg = Act.getResources().getString(StringID);
            SProgressDialog(Show, Msg);
        } catch (Exception e) {
            DebugToast("SProgressDialog\n" + e.getMessage());
        }
    }

    public void SProgressDialog() {
        SProgressDialog(false, "");
    }

    public void SProgressDialog(int StringID) {
        String Msg = Act.getResources().getString(StringID);
        SProgressDialog(Msg);
    }

    public void SProgressDialog(String Msg) {
        SProgressDialog(true, Msg);
    }

    public void SProgressDialog(boolean Show, String Msg) {
        SProgressDialog(Show, Msg, 2000);
    }

    public void SProgressDialog(boolean Show, int StringID, int DelayShowMillis) {
        try {
            String Msg = Act.getResources().getString(StringID);
            SProgressDialog(Show, Msg, DelayShowMillis);
        } catch (Exception e) {
            DebugToast("SProgressDialog\n" + e.getMessage());
        }
    }

    public void SProgressDialog(boolean Show, String Msg, int DelayShowMillis) {
        SProgressDialog(Show, Msg, DelayShowMillis, false);
    }

    public void SProgressDialog(final boolean Show, final String Msg, final int DelayShowMillis, final boolean CancelAble) {
        try {
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    if (Show) {
                        if (!(NPGSDialog != null && NPGSDialog.isShowing())) {
                            NPGSDialog = new SweetAlertDialog(Act, SweetAlertDialog.PROGRESS_TYPE);
                            NPGSDialog.setTitleText(Msg);
                            NPGSDialog.setCancelable(CancelAble);
                            NPGSDialog.setCanceledOnTouchOutside(CancelAble);
                            if (NPGSDialog != null && !NPGSDialog.isShowing()) {
                                new Handler(Act.getMainLooper()).postDelayed(() -> {
                                    try {
                                        if (NPGSDialog != null && !NPGSDialog.isShowing()) {
                                            NPGSDialog.show();
                                        }
                                    } catch (Exception e) {
                                        //
                                    }
                                }, DelayShowMillis);
                            }
                        } else if (NPGSDialog != null && NPGSDialog.isShowing()) {
                            NPGSDialog.setTitleText(Msg);
                        }
                    } else {
                        if (NPGSDialog != null && !NPGSDialog.isShowing()) {
                            NPGSDialog = null;
                        } else {
                            new Handler(Act.getMainLooper()).postDelayed(() -> {
                                try {
                                    if (NPGSDialog != null && NPGSDialog.isShowing()) {
                                        NPGSDialog.dismissWithAnimation();
                                        NPGSDialog = null;
                                    }
                                } catch (Exception e) {
                                    //
                                }
                            }, 1000);
                        }
                    }
                } catch (Exception e) {
                    DebugToast("SProgressDialog\n" + e.getMessage());
                }
            });
        } catch (Exception e) {
            DebugToast("SProgressDialog\n" + e.getMessage());
        }
    }

    //Progress Dialog
    private static Dialog WPGSDialog;

    public void SWebProgress(boolean Show) {
        SWebProgress(Show, null);
    }

    public void SWebProgress(boolean Show, JSONObject SettingJOB) {
        try {
            if (Show) {
                if (!(WPGSDialog != null && WPGSDialog.isShowing())) {
                    WPGSDialog = new Dialog(Act, R.style.DialogSWebProgress);
                    WPGSDialog.setContentView(R.layout.dialog_progress);
                    WPGSDialog.setCancelable(false);
                    //Set Dialog
                    ProgressWheel PW_PGS = WPGSDialog.findViewById(R.id.PW_PGS);
                    //FBFilter
                    ImageView IMV_PGS = WPGSDialog.findViewById(R.id.IMV_PGS);
                    TextView TV_PGS = WPGSDialog.findViewById(R.id.TV_PGS);
                    //Background Color
                    int DefaultBackColor = Color.argb(128, 255, 255, 255);
                    Window window = WPGSDialog.getWindow();
                    if (window != null)
                        window.setBackgroundDrawable(new ColorDrawable(JSONIntGetter(SettingJOB, "BackColor", DefaultBackColor)));
                    //Progress
                    if (PW_PGS.getVisibility() == View.VISIBLE) {
                        int DefaultBarColor = Color.parseColor("#000000");
                        int DefaultRimColor = Color.parseColor("#DDDDDD");
                        PW_PGS.setBarColor(JSONIntGetter(SettingJOB, "BarColor", DefaultBarColor));
                        PW_PGS.setRimColor(JSONIntGetter(SettingJOB, "RimColor", DefaultRimColor));
                    } else {
                        IMV_PGS.setAnimation(AnimationUtils.loadAnimation(Act, R.anim.infinite_rotate));
                        TV_PGS.setText(R.string.CM_Loading);
                    }
                    WPGSDialog.show();
                }
            } else {
                if (WPGSDialog != null && !WPGSDialog.isShowing()) {
                    WPGSDialog = null;
                } else {
                    new Handler(Act.getMainLooper()).postDelayed(() -> {
                        try {
                            if (WPGSDialog != null && WPGSDialog.isShowing()) {
                                WPGSDialog.dismiss();
                                WPGSDialog = null;
                            }
                        } catch (Exception e) {
                            //
                        }
                    }, 500);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SWebProgress() {
        SWebProgress(false);
    }

    //SharedPreferences
    private SharedPreferences SPClient;
    private static final String SPKey = "Nextgentek";

    public String SPReadStringData(String Name) {
        SPClient = Con.getSharedPreferences(SPKey, Context.MODE_PRIVATE);
        return SPClient.getString(Name, "");
    }

    public boolean SPSaveStringData(String Name, String Data) {
        SPClient = Con.getSharedPreferences(SPKey, Context.MODE_PRIVATE);
        return SPClient.edit().putString(Name, Data).commit();
    }

    public void SPClearStringData(String Name) {
        SPClient = Con.getSharedPreferences(SPKey, Context.MODE_PRIVATE);
        SPClient.edit().remove(Name).apply();
    }

    //GetString
    public String IDStr(int StrID) {
        return Con.getResources().getString(StrID);
    }

    public String GetDateStr(int Year, int Month, int Day) {
        return Year + "/" + (Month + 1) + "/" + Day;
    }

    public long GetDateToMillis(String DateStr, boolean DayStart) {
        long DateMillis = 0;
        try {
            String[] DateAry = DateStr.split("/");
            int Year = Integer.parseInt(DateAry[0]);
            int Month = Integer.parseInt(DateAry[1]) - 1;
            int Day = Integer.parseInt(DateAry[2]);
            Calendar Cal = Calendar.getInstance();
            if (DayStart) {
                Cal.set(Year, Month, Day, 0, 0, 1);
            } else {
                Cal.set(Year, Month, Day, 23, 59, 59);
            }
            DateMillis = Cal.getTimeInMillis();
        } catch (Exception e) {
            //
        }
        return DateMillis;
    }

    //Net Data======================================================================================
    public JSONObject SFetch(String APIType, JSONObject JOB) {
        return SFetch(TD.MainURL, APIType, JOB);
    }

    public JSONObject SFetch(String MainUrl, String APIType, JSONObject JOB) {
        JSONObject ResultJOB = new JSONObject();
        HttpURLConnection Conn = null;
        ByteArrayOutputStream BAOS = null;
        String FUrl = MainUrl + APIType;
        String ReceiveResult;
        try {
            URL url = new URL(FUrl);
            Conn = (HttpURLConnection) url.openConnection();
            Conn.setRequestMethod(JOB != null ? "POST" : "GET");
            Conn.setReadTimeout(30000);
            Conn.setConnectTimeout(30000);
            //Add Values
            if (JOB != null) {
                ContentValues CValues = new ContentValues();
                Iterator<String> iter = JOB.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    CValues.put(key, JSONStrGetter(JOB, key));
                }

                OutputStream OS = Conn.getOutputStream();
                BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(OS, StandardCharsets.UTF_8));
                BW.write(getQuery(CValues));
                BW.flush();
            }
            int responseCode = Conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream IS = Conn.getInputStream();
                BAOS = new ByteArrayOutputStream();
                //Detect Memory
                boolean DataOverFlow = false;
                byte[] ISBuf = new byte[8192];
                long NowFreeMemory = (long) ((double) Runtime.getRuntime().freeMemory() * 0.99);
                long StartGCMemory = (long) ((double) Runtime.getRuntime().freeMemory() * 0.95);
                int BytesRead;
                boolean AlreadyGC = false;
                while ((BytesRead = IS.read(ISBuf)) != -1) {
                    BAOS.write(ISBuf, 0, BytesRead);
                    int BAOSSize = BAOS.size();
                    if (BAOSSize != 0) {
                        if (BAOSSize >= NowFreeMemory && NowFreeMemory != 0) {
                            DataOverFlow = true;
                            break;
                        } else if (!AlreadyGC && BAOSSize >= StartGCMemory && StartGCMemory != 0) {
                            Runtime.getRuntime().gc();
                            AlreadyGC = true;
                            //DebugToast("已清理記憶體A");
                            NowFreeMemory = (long) ((double) Runtime.getRuntime().freeMemory() * 0.95);
                        }
                    }
                }

                if (DataOverFlow) {
                    ResultJOB = new JSONObject();
                    AddResultJOBResult(ResultJOB, "OverFlow");
                    ResultJOB.put("SBLength", (int) ((double) BAOS.size()) / 1024);
                    ResultJOB.put("FreeMemory", (int) ((double) NowFreeMemory / 1024));
                    //response.setLength(0);
                    SWToast("已阻止了危險行為", "目前記憶體不足 為了避免影響您的使用 已中斷了本次的連線", SweetAlertDialog.ERROR_TYPE);
                    DebugToast("SBLength:" + JSONStrGetter(ResultJOB, "SBLength") + "\nFree:" + JSONStrGetter(ResultJOB, "FreeMemory"));
                } else {
                    long FreeMem = Runtime.getRuntime().freeMemory();
                    if (FreeMem < 128 * 1024 && FreeMem != 0) {
                        Runtime.getRuntime().gc();
                        //DebugToast("已清理記憶體B");
                    }
                    ReceiveResult = BAOS.toString("UTF-8");
                    ResultJOB = new JSONObject(ReceiveResult);
                    AddResultJOBResult(ResultJOB, "FetchSuccess");
                }
            } else {
                AddResultJOBResult(ResultJOB, "Exception");
                SWToast(IDStr(R.string.Fetch_ER_Fetching), "請稍後重新嘗試", SweetAlertDialog.ERROR_TYPE, 3000);
            }
        } catch (RuntimeException | JSONException e) {
            AddResultJOBResult(ResultJOB, "Exception");
            EXToast(R.string.ERR_ProcessData, "SFetch JSONException", e);
            ShowFetchErrorContent(BAOS);
        } catch (IOException e) {
            AddResultJOBResult(ResultJOB, "Exception");
            SWToast(IDStr(R.string.ERR_Connection), "建議您檢查網路品質後重新嘗試", SweetAlertDialog.ERROR_TYPE, 0);
            DebugToast("SFetch\n" + e.getMessage());
        } catch (Exception e) {
            AddResultJOBResult(ResultJOB, "Exception");
            SWToast(IDStr(R.string.ERR_ProcessData), "請稍後再重新嘗試", SweetAlertDialog.ERROR_TYPE, 5000);
            DebugToast("SFetch\n" + e.getMessage());
        } finally {
            try {
                if (Conn != null) Conn.disconnect();
                if (BAOS != null) BAOS.close();
            } catch (Exception e) {
                DebugToast("SFetch\n" + e.getMessage());
            }
        }
        return ResultJOB;
    }

    public boolean GetResultJOBAvailable(JSONObject ResultJOB) {
        boolean Available = false;
        if (ResultJOB != null) {
            String SResult = JSONStrGetter(ResultJOB, "SResult");
            if (SResult.equals("FetchSuccess")) {
                Available = true;
            }
        }
        return Available;
    }

    private void ShowFetchErrorContent(ByteArrayOutputStream BAOS) {
        try {
            SWToast("Error Content", BAOS.toString("UTF-8"), SweetAlertDialog.NORMAL_TYPE, 0);
        } catch (Exception ee) {
            //
        }
    }

    private void AddResultJOBResult(JSONObject ResultJOB, String SResult) {
        try {
            if (ResultJOB == null) {
                ResultJOB = new JSONObject();
            }
            ResultJOB.put("SResult", SResult);
        } catch (Exception e) {
            //
        }
    }

    private String getQuery(ContentValues values) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, Object> entry : values.valueSet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
        }

        //Log.i("Result",result.toString() +" "+ String.valueOf(response));
        return result.toString();
    }

    public String GetFileExtensionName(File file) {
        String ExtensionName = "";
        String FileName = file.getName();
        int LastDotPos = FileName.lastIndexOf(".");
        if (LastDotPos > 0) {
            ExtensionName = FileName.substring(LastDotPos, FileName.length());
        }
        return ExtensionName;
    }

    public String GetFolderPath(String FolderName) {
        return GetFolderPath(FolderName, true);
    }

    public String GetFolderPath(String FolderName, boolean Private) {
        String FolderPath = "";
        if (Private) {
            FolderPath = Con.getFilesDir() + File.separator;
        } else {
            File sdFile = android.os.Environment.getExternalStorageDirectory();
            File ACClientFile = new File(sdFile.getPath() + File.separator + "ACClient");
            if (ACClientFile.exists()) {
                FolderPath = ACClientFile.getPath() + File.separator;
            } else {
                if (ACClientFile.mkdir()) {
                    FolderPath = ACClientFile.getPath() + File.separator;
                }
            }
        }
        File FolderFile = new File(FolderPath + FolderName);
        if (!FolderFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            FolderFile.mkdir();
        }
        return FolderFile.getPath();
    }

    private boolean FileSender(final String FilePath, OutputStream OS) {
        boolean Success = false;
        try {
            //Send Photo
            FileInputStream FIS = new FileInputStream(FilePath);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = FIS.read(buffer)) > 0) {
                OS.write(buffer, 0, length);
            }
            OS.flush();
            FIS.close();
            Success = true;
        } catch (Exception e) {
            //
        }
        return Success;
    }

    //File Transfer
    private boolean FileReceiver(String FolderPath, InputStream IS) {
        boolean Success = false;
        try {
            //Receive FileInfo
            ObjectInputStream OIS = new ObjectInputStream(IS);
            JSONObject PhotoInfoJOB = new JSONObject((String) OIS.readObject());
            String NeedDownload = JSONStrGetter(PhotoInfoJOB, "NeedDownload");
            String FileName = JSONStrGetter(PhotoInfoJOB, "FileName");
            //long FileSize = JSONLongGetter(PhotoInfoJOB,"FileSize");
            if (NeedDownload.equals("True")) {
                //Receive File
                File NewFile = new File(FolderPath, FileName + "_tmp");
                FileOutputStream PhotoFOS = new FileOutputStream(NewFile);
                byte[] buffer = new byte[8192];
                int length;
                long TotalDownload = 0;
                while ((length = IS.read(buffer)) > 0) {
                    PhotoFOS.write(buffer, 0, length);
                    TotalDownload += length;
                }
                PhotoFOS.flush();
                PhotoFOS.close();
                if (TotalDownload > 1024) {
                    if (NewFile.renameTo(new File(FolderPath, FileName))) {
                        Success = true;
                    }
                } else {
                    //noinspection ResultOfMethodCallIgnored
                    NewFile.delete();
                }
            } else {
                Success = true;
            }
        } catch (Exception e) {
            Log.e("FileReceiver", e.getMessage());
        }
        return Success;
    }

    public void DeleteUselessFile(final String FolderName, final JSONArray FileJA) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //Delete Useless Data
                    String FolderPath = GetFolderPath(FolderName, true);
                    File FolderFile = new File(FolderPath);
                    for (File file : FolderFile.listFiles()) {
                        try {
                            String FileName = file.getName();
                            boolean FoundName = false;
                            for (int cnt = 0; cnt < FileJA.length(); cnt++) {
                                String ServerFileName = FileJA.getString(cnt);
                                if (FileName.equals(ServerFileName)) {
                                    FoundName = true;
                                    break;
                                }
                            }
                            if (!FoundName) {
                                //noinspection ResultOfMethodCallIgnored
                                file.delete();
                            }
                        } catch (Exception e) {
                            //
                        }
                    }
                } catch (Exception e) {
                    //
                }
            }
        }.start();
    }

    public void DeleteOverSize(String FolderName, boolean Private, int MaxSize) {
        try {
            String MainPath = GetFolderPath(FolderName, Private);
            File FolderFile = new File(MainPath);
            File[] FileList = FolderFile.listFiles();
            while (FileList.length > MaxSize) {
                //Find Oldest File
                File OldestFile = FileList[0];
                for (File file : FileList) {
                    if (OldestFile.lastModified() > file.lastModified()) {
                        OldestFile = file;
                    }
                }
                //Delete Oldest file
                //noinspection ResultOfMethodCallIgnored
                OldestFile.delete();
                //Refresh List
                FileList = FolderFile.listFiles();
            }
        } catch (Exception e) {
            //
        }
    }

    //Photos========================================================================================
    public Bitmap GetResizedBitmap(String PicPath, int NEW_SIZE) throws Exception {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(PicPath, option);
        int width = option.outWidth;
        int height = option.outHeight;
        int Scale = 1;
        while (true) {
            if (width <= NEW_SIZE || height <= NEW_SIZE) {
                break;
            }
            width /= 2;
            height /= 2;
            Scale *= 2;
        }

        option = new BitmapFactory.Options();
        option.inSampleSize = Scale;
        return rotateBitmapByDegree(BitmapFactory.decodeFile(PicPath, option), GetBitmapDegree(PicPath));
    }

    private Bitmap rotateBitmapByDegree(Bitmap BMP, int degree) {
        if (BMP != null) {
            try {
                // 根據旋轉角度，生成旋轉矩陣
                Matrix matrix = new Matrix();
                matrix.postRotate(degree);
                // 將原始圖片按照旋轉矩陣進行旋轉，並得到新的圖片
                if (degree > 0) {
                    BMP = Bitmap.createBitmap(BMP, 0, 0, BMP.getWidth(), BMP.getHeight(), matrix, true);
                }
            } catch (OutOfMemoryError e) {
                //
            }
        }
        return BMP;
    }

    //取得圖片角度
    public int GetBitmapDegree(String PicPath) throws Exception {
        ExifInterface exif = new ExifInterface(PicPath);
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public boolean SaveBitmap(String PicPath, Bitmap BMP) {
        File PicFile = new File(PicPath);
        try {
            boolean AvailableSave = true;
            if (PicFile.exists()) {
                if (!PicFile.delete()) {
                    AvailableSave = false;
                }
            }
            if (AvailableSave) {
                FileOutputStream FOS = new FileOutputStream(PicFile);
                BMP.compress(Bitmap.CompressFormat.JPEG, 100, FOS);
                FOS.flush();
                FOS.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
    public int convertDpToPixel(float dp){
        float px = dp * getDensity();
        return (int)px;
    }

    public int convertPixelToDp(float px){
        float dp = px / getDensity();
        return (int)dp;
    }

    private float getDensity(){
        DisplayMetrics metrics = Act.getResources().getDisplayMetrics();
        return metrics.density;
    }
    */

    public int DpToSp(int dp) {
        return (int) (DpToPx(dp) / Con.getResources().getDisplayMetrics().scaledDensity);
    }

    public int SpToPx(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Con.getResources().getDisplayMetrics());
    }

    public int DpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Con.getResources().getDisplayMetrics());
    }


    //ListView
    public void setListViewHeightBasedOnChildren(ListView listView, int AddHeight) throws Exception {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        //int desiredWidth = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        int DataCount = listAdapter.getCount();
        for (int i = 0; i < DataCount; i++) {
            view = listAdapter.getView(i, view, listView);
            //view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);//desiredWidth,View.MeasureSpec.UNSPECIFIED
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + AddHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public String TimeShower(int Hour, int Minute, boolean ShowHead) {
        return TimeShower(String.valueOf(Hour), String.valueOf(Minute), "", "", 1, ShowHead);
    }

    public String TimeShower(String Hour, String Minute, boolean ShowHead) {
        return TimeShower(Hour, Minute, "", "", 1, ShowHead);
    }

    public String TimeShower(String StartHour, String StartMinute, String EndHour, String EndMinute, int Mode, boolean ShowHead) {
        String Time = "";
        try {
            if (Mode == 0 || Mode == 1) {//Both || Start
                int StartHourInt = Integer.parseInt(StartHour);
                int StartMinuteInt = Integer.parseInt(StartMinute);
                if (ShowHead) {
                    if (StartHourInt >= 12) {
                        Time += "下午 ";
                        if (StartHourInt != 12) {
                            StartHourInt -= 12;
                        }
                    } else {
                        Time += "上午 ";
                    }
                }
                if (StartHourInt < 10) {
                    Time += "0";
                }
                Time += String.valueOf(StartHourInt) + ":";

                if (StartMinuteInt < 10) {
                    Time += "0";
                }
                Time += StartMinute;
                if (Mode == 0) {
                    Time += " ~ ";
                }
            }
            if (Mode == 0 || Mode == 2) {//Both || End
                int EndHourInt = Integer.parseInt(EndHour);
                int EndMinuteInt = Integer.parseInt(EndMinute);
                if (ShowHead) {
                    if (EndHourInt >= 12) {
                        Time += "下午 ";
                        if (EndHourInt != 12) {
                            EndHourInt -= 12;
                        }
                    } else {
                        Time += "上午 ";
                    }
                }
                if (EndHourInt < 10) {
                    Time += "0";
                }
                Time += String.valueOf(EndHourInt) + ":";

                if (EndMinuteInt < 10) {
                    Time += "0";
                }
                Time += EndMinute;
            }
        } catch (Exception e) {
            Time = StartHour + ":" + StartMinute + " ~ " + EndHour + ":" + EndMinute;
        }
        return Time;
    }

    public String GetRandNum(int Length) {
        StringBuilder RandNumSB = new StringBuilder();
        for (int cnt = 0; cnt < Length; cnt++) {
            RandNumSB.append(String.valueOf((int) (Math.random() * 10)));
        }
        return RandNumSB.toString();
    }

    public int GetRandNum() {
        return (int) (Math.random() * 10);
    }


    public int[] GetScreenSize() {
        Display display = Act.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = size.x;
        int height = size.y;
        return new int[]{width, height};
    }

    public int StI(String Val) {
        return StI(Val, 0);
    }

    public int StI(String Val, int InitVal) {
        int Result;
        try {
            Result = Integer.parseInt(Val);
        } catch (Exception e) {
            Result = InitVal;
        }
        return Result;
    }

    public String ItS(int Val) {
        String Result;
        try {
            Result = String.valueOf(Val);
        } catch (Exception e) {
            Result = "";
        }
        return Result;
    }


    public float StF(String Val) {
        return StF(Val, 0);
    }

    public float StF(String Val, float InitValue) {
        float Result;
        try {
            Result = Float.parseFloat(Val);
        } catch (Exception e) {
            Result = InitValue;
        }
        return Result;
    }

    public long StL(String Val) {
        long Result = 0;
        try {
            if (Val.length() > 0) {
                Result = Long.parseLong(Val);
            }
        } catch (Exception e) {
            //
        }
        return Result;
    }

    public boolean isInt(String Val) {
        for (int i = 0; i < Val.length(); i++) {
            if (!Character.isDigit(Val.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public int ValueType(String Val) {
        int Type;
        try {
            Integer.parseInt(Val);
            Type = 1;
        } catch (Exception e) {
            try {
                Float.parseFloat(Val);
                Type = 2;
            } catch (Exception ee) {
                Type = 3;//String
            }
        }
        return Type;
    }


    public void HideKeyBoard() {
        View view = Act.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) Act.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public Bitmap CropToSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width) ? height - (height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0) ? 0 : cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0) ? 0 : cropH;
        return Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);
    }

    public long DateToMillis(String DateIn) throws Exception {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = formatter.parse(DateIn);
        assert date != null;
        return date.getTime();
    }

    public String SecToTime(long Sec, String Mode) {
        return MillisToTime(String.valueOf(Sec * 1000), Mode);
    }

    public String MillisToSecStr(long Millis) {
        return String.valueOf(Millis / 1000);
    }

    public String MillisToTime(String Millis, String Mode) {
        return MillisToTime(Millis, Mode, false);
    }

    public String MillisToTime(String Millis, String Mode, boolean Wrap) {
        String ShowTime = "";
        try {
            Calendar Cal = Calendar.getInstance();
            Cal.setTimeInMillis(Long.parseLong(Millis));
            if (Mode.equals("Date") || Mode.equals("Both")) {
                int Year = Cal.get(Calendar.YEAR);
                int Month = Cal.get(Calendar.MONTH) + 1;
                int Day = Cal.get(Calendar.DAY_OF_MONTH);
                ShowTime += Year + "/" + AFN(Month) + "/" + AFN(Day);
                ShowTime += Wrap ? "\n" : (Mode.equals("Both") ? " " : "");
            }
            if (Mode.equals("Time") || Mode.equals("Both")) {
                int Hour = Cal.get(Calendar.HOUR_OF_DAY);
                int Minute = Cal.get(Calendar.MINUTE);
                int Second = Cal.get(Calendar.SECOND);
                ShowTime += AFN(Hour) + ":" + AFN(Minute) + ":" + AFN(Second);
            }
        } catch (Exception e) {
            //
        }
        return ShowTime;
    }

    //Auto Fill Num
    private String AFN(int Num) {
        String Result = ItS(Num);
        if (Num < 10) Result = "0" + Result;
        return Result;
    }

    public int[] CalToAry(Calendar Cal, String Mode) {
        int[] DataAry = new int[]{};
        try {
            int Year = Cal.get(Calendar.YEAR);
            int Month = Cal.get(Calendar.MONTH) + 1;
            int Day = Cal.get(Calendar.DAY_OF_MONTH);
            int Hour = Cal.get(Calendar.HOUR_OF_DAY);
            int Minute = Cal.get(Calendar.MINUTE);
            int Second = Cal.get(Calendar.SECOND);
            if (Mode.equals("Date")) {
                DataAry = new int[]{Year, Month, Day};
            } else if (Mode.equals("Time")) {
                DataAry = new int[]{Hour, Minute, Second};
            } else if (Mode.equals("Both")) {
                DataAry = new int[]{Year, Month, Day, Hour, Minute, Second};
            }
        } catch (Exception e) {
            //
        }
        return DataAry;
    }

    public boolean CheckFeature(String Feature) {
        PackageManager PM = Con.getPackageManager();
        return PM.hasSystemFeature(Feature);
    }

    public boolean isAppInstalled(String PackageName) {
        PackageManager PM = Con.getPackageManager();
        try {
            PM.getPackageInfo(PackageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void OOMHandler() {
        double FreeMemory = (double) Runtime.getRuntime().freeMemory();
        double TotalMemory = (double) Runtime.getRuntime().totalMemory();
        if (FreeMemory > (TotalMemory * 0.95)) {
            Runtime.getRuntime().gc();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager CNM = (ConnectivityManager) Con.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (CNM != null) {
            NetworkInfo activeNetworkInfo = CNM.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } else {
            return false;
        }
    }

    public boolean SContains(String Data, String... Keys) {
        return SContains(Data, 0, Keys);
    }

    public boolean SContains(String Data, int Mode, String... Keys) {
        boolean Contain = false;
        Data = Data.toLowerCase();
        for (String Key : Keys) {
            Key = Key.toLowerCase();
            if (Mode == 0) {
                if (Data.contains(Key) || Key.contains(Data)) {
                    Contain = true;
                    break;
                }
            } else if (Mode == 1) {
                if (Data.contains(Key)) {
                    Contain = true;
                    break;
                }
            } else if (Mode == 2) {
                if (Key.contains(Data)) {
                    Contain = true;
                    break;
                }
            }
        }
        return Contain;
    }

    public void SetDialogSize(Dialog TDialog, float WidthPercent, float HeightPercent, int Height) {
        Window WD = TDialog.getWindow();
        WindowManager.LayoutParams LP = new WindowManager.LayoutParams();
        if (WD != null) {
            //Get Screen Size
            DisplayMetrics metrics = new DisplayMetrics();
            Act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int HeightPixels = metrics.heightPixels;
            int WidthPixels = metrics.widthPixels;

            LP.copyFrom(WD.getAttributes());
            LP.width = (int) (WidthPixels * (WidthPercent / 100));
            if (HeightPercent > 0) {
                LP.height = (int) (HeightPixels * (HeightPercent / 100));
            } else {
                LP.height = Height;
            }
            WD.setAttributes(LP);
            WD.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//隱藏原有背景Dialog圓角才會出來
        }
    }

    public Bitmap LayoutCapture(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap BMP = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false); // clear drawing cache
        return BMP;
    }

    public String NumDF(float Num) {
        DecimalFormat DF = new DecimalFormat("#.##");
        return DF.format(Num);
    }

    public String GetMacAddress(String interfaceName) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (TextUtils.equals(networkInterface.getName(), interfaceName)) {
                    byte[] bytes = networkInterface.getHardwareAddress();
                    StringBuilder builder = new StringBuilder();
                    for (byte b : bytes) {
                        builder.append(String.format("%02X:", b));
                    }

                    if (builder.length() > 0) {
                        builder.deleteCharAt(builder.length() - 1);
                    }
                    return builder.toString();
                }
            }
            return "";
        } catch (SocketException e) {
            return "";
        }
    }

    public void JSONValueAdder(JSONObject JOB, String Key, String Value) {
        try {
            JOB.put(Key, Value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void DeleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);
            //noinspection ResultOfMethodCallIgnored
            fileOrDirectory.delete();
        } else {
            //noinspection ResultOfMethodCallIgnored
            fileOrDirectory.delete();
        }
    }

    //RecyclerView==================================================================================
    public void DefineRVHeight(ScrollView SCV, RecyclerView RV, View SView) {
        SCV.post(() -> {
            //SView.getHeight()扣去調整功能列的高度
            int SViewHeight = SView.getHeight();
            int TargetHeight = SCV.getBottom() - SCV.getTop() - SViewHeight;
            if (TargetHeight > 0) {
                RV.getLayoutParams().height = TargetHeight;
            }
            SCV.getViewTreeObserver().addOnScrollChangedListener(() -> RV.setNestedScrollingEnabled(SCV.getScrollY() >= RV.getTop() - SViewHeight));
        });
    }

    public int GetPicOrientation(String imagePath) {
        int rotate = 0;
        try {
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public int GetRandColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    public void KillApp(String packageName) {
        ActivityManager am = (ActivityManager) Con.getSystemService(Activity.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(packageName);
    }

    public boolean isAppExist(String PackageName) {
        boolean Exist = false;

        try {
            PackageManager PM = Act.getPackageManager();
            PM.getPackageInfo(PackageName, 0);
            Exist = PM.getApplicationInfo(PackageName, 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Exist;
    }

    public long GetFolderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += GetFolderSize(file);
        }
        return length;
    }

    //ImageView Url=================================================================================
    //讀取網路圖片，型態為Bitmap
    public void getBitmapFromURL(ImageView IMV, String imageUrl) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    URL url = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream IS = connection.getInputStream();
                    final Bitmap BMP = BitmapFactory.decodeStream(IS);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        IMV.setImageBitmap(BMP);
                    });
                } catch (Exception e) {
                    DebugToast("getBitmapFromURL\n" + e.getMessage());
                }
            }
        }.start();
    }
}