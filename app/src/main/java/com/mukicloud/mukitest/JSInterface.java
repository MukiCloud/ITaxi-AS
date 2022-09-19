package com.mukicloud.mukitest;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.mukicloud.mukitest.Activity.ActivityWeb;
import com.mukicloud.mukitest.SFunc.NFileUtils;
import com.mukicloud.mukitest.SFunc.SFile;
import com.mukicloud.mukitest.SFunc.SLocService;
import com.mukicloud.mukitest.SFunc.SMethods;
import com.mukicloud.mukitest.SFunc.SUploader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

public class JSInterface {
    private final Activity Act;
    public SMethods SM;
    private SFile SF;
    private WebView WBV;
    private final LocationManager LM;

    public JSInterface(Activity act) {
        Act = act;
        SM = new SMethods(Act);
        LM = (LocationManager) Act.getSystemService(LOCATION_SERVICE);
        InitJSBroadcastReceiver();
    }

    private void InitJSBroadcastReceiver() {
        if (Act instanceof ActivityWeb) {
            ActivityWeb ActS = ((ActivityWeb) Act);
            Act.registerReceiver(ActS.JBR, new IntentFilter("JSB"));
            WBV = ActS.WBV_Main;
            SF = ActS.SF;
        }
    }

    private void SendJSB(String Func, String... Value) {
        ArrayList<String> ValueAL = new ArrayList<>(Arrays.asList(Value));
        Intent intent = new Intent("JSB");
        intent.putExtra("Func", Func);
        intent.putStringArrayListExtra("ValueAL", ValueAL);
        Act.sendBroadcast(intent);
    }

    public void onDestroy() {
        if (Act != null && Act instanceof ActivityWeb) {
            ActivityWeb ActS = ((ActivityWeb) Act);
            ActS.unregisterReceiver(ActS.JBR);
        }
    }

    @SuppressLint("HardwareIds")
    @JavascriptInterface
    public void getAppInfo(String CallBackID) {
        JSONObject JOB = new JSONObject();
        try {
            String OSVersion = String.valueOf(Build.VERSION.SDK_INT); // OS version
            String Device = Build.DEVICE;          // Device
            String Model = Build.MODEL;            // Model
            String Product = Build.PRODUCT;        // Product
            String ANDROID_ID = Settings.Secure.getString(Act.getContentResolver(), Settings.Secure.ANDROID_ID);
            //Set JSONObject
            JOB.put("token", SM.SPReadStringData("token"));//推播Token
            JOB.put("PushyToken", SM.SPReadStringData("PushyToken"));//Pushy推播Token
            JOB.put("device", Device);//裝置型號資訊
            JOB.put("model", Model);//裝置型號資訊
            JOB.put("product", Product);//裝置型號資訊
            JOB.put("os_version", OSVersion);//系統版本
            JOB.put("device_id", ANDROID_ID);//ANDROID_ID
            JOB.put("application_version", BuildConfig.VERSION_CODE);//App版本號
            JOB.put("application_version_name", BuildConfig.VERSION_NAME);//App版本名
            SM.JSONValueAdder(JOB, "ResCode", "1");
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "getAppInfo", e);
            SM.JSONValueAdder(JOB, "ResCode", "-1");
            SM.JSONValueAdder(JOB, "Result", "Exception");
        }
        JSHandlerCallBack(CallBackID, JOB);
    }

    @JavascriptInterface
    public void setBadgeNum(String BadgeNum) {
        try {
            int BadgeNumVal = SM.StI(BadgeNum);
            if (BadgeNumVal > 0) {
                ShortcutBadger.applyCount(Act, BadgeNumVal); //for 1.1.4+
                SM.SPSaveStringData("BadgeNum", BadgeNum);
            } else {
                ShortcutBadger.removeCount(Act); //for 1.1.4+
                SM.SPSaveStringData("BadgeNum", "0");
            }
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "setBadgeNum", e);
        }
    }

    @JavascriptInterface
    public void shouldStartLoadWith(String GoToURL) {
        try {
            if (GoToURL.length() > 0) {
                Log.d("STest", "shouldStartLoadWith A " + GoToURL);
                SendJSB("shouldStartLoadWith", GoToURL, "True");
            } else {
                SM.UIToast(R.string.ERR_LostInfo);
            }
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "shouldStartLoadWith", e);
        }
    }

    @JavascriptInterface
    public void shouldStartLoadWith(String GoToURL, int UseBrowser) {
        try {
            if (GoToURL.length() > 0) {
                Log.d("STest", "shouldStartLoadWith B " + GoToURL);
                SendJSB("shouldStartLoadWith", GoToURL, UseBrowser == 1 ? "True" : "False");
            } else {
                SM.UIToast(R.string.ERR_LostInfo);
            }
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "shouldStartLoadWith", e);
        }
    }

    @JavascriptInterface
    public void openUrlByBrowser(String Url) {
        try {
            if (Url != null && Url.length() > 0) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Act.startActivity(intent);
            }
        } catch (Exception e) {
            SM.UIToast(R.string.JS_ERR_OpenLink);
        }
    }

    @JavascriptInterface
    public void alertDialog(String Title, String Content) {
        try {
            SM.SWToast(Title, Content, SweetAlertDialog.WARNING_TYPE, 3000);
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "alertDialog", e);
        }
    }

    @JavascriptInterface
    public void alertToast(String Content, String Position, String Time) {
        try {
            int ToastGravity = Gravity.NO_GRAVITY;
            switch (Position) {
                case "start":
                    ToastGravity = Gravity.START;
                    break;
                case "end":
                    ToastGravity = Gravity.END;
                    break;
                case "bottom":
                    ToastGravity = Gravity.BOTTOM;
                    break;
                case "top":
                    ToastGravity = Gravity.TOP;
                    break;
                case "center":
                    ToastGravity = Gravity.CENTER;
                    break;
            }
            Toast toast = Toast.makeText(Act, Content, SM.StI(Time));
            toast.setGravity(ToastGravity, 0, 0);
            toast.show();
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "alertToast", e);
        }
    }

    @JavascriptInterface
    public void startLoading() {
        try {
            SM.SWebProgress(true);
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "startLoading", e);
        }
    }

    @JavascriptInterface
    public void stopLoading() {
        try {
            SM.SWebProgress(false);
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "stopLoading", e);
        }
    }

    @JavascriptInterface
    public void dial(String Phone) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Phone));
            Act.startActivity(intent);
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "dial", e);
        }
    }

    @JavascriptInterface
    public void goTestSite(String ProjectID) {
        try {
            SendJSB("goTestSite", ProjectID);
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "dial", e);
        }
    }

    private String CallBackID_BAC;

    @JavascriptInterface
    public void backAction(String CallBackID, int BackType) {
        try {
            if (BackType == 1) CallBackID_BAC = CallBackID;//點一下執行callback
            else if (BackType == 2) CallBackID_BAC = null;//由裝置告知操作者點第2下將離開APP
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "backAction", e);
        }
    }

    public boolean CallBack() {
        if (CallBackID_BAC != null && CallBackID_BAC.length() > 0) {
            JSONObject ReturnJOB = new JSONObject();
            SM.JSONValueAdder(ReturnJOB, "res_code", "1");
            JSHandlerCallBack(CallBackID_BAC, ReturnJOB);
            return true;
        }
        return false;
    }

    /*
    @JavascriptInterface
    public void closeBackAction(String ProjectID){
        try {

        }catch (Exception e){
            SM.EXToast(R.string.CM_DetectError,"closeBackAction",e);
        }
    }

     */

    @JavascriptInterface
    public void goBackUrl(String BackUrl) {
        try {
            SendJSB("goBackUrl", BackUrl);
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "goBackUrl", e);
        }
    }

    @JavascriptInterface
    public void setBackUrl(String BackUrl) {
        try {
            SendJSB("setBackUrl", BackUrl);
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "goBackUrl", e);
        }
    }

    @JavascriptInterface
    public void getCoordinate(String CallBackID) {
        JSONObject JOB = new JSONObject();
        try {
            if (ActivityCompat.checkSelfPermission(Act, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(Act, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                JOB.put("resCode", "3");
                JOB.put("result", "No permission");
                JSHandlerCallBack(CallBackID, JOB);
            } else {
                Location Loc = LM.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (Loc != null) {
                    JOB.put("resCode", "1");
                    JOB.put("result", "Success");
                    JOB.put("latitude", Loc.getLatitude());
                    JOB.put("longitude", Loc.getLongitude());
                    JSHandlerCallBack(CallBackID, JOB);
                } else {
                    if (LM.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        LocationListener LL = new LocationListener() {
                            @Override
                            public void onLocationChanged(Location Loc) {
                                try {
                                    if (Loc != null) {
                                        JSONObject JOB = new JSONObject();
                                        JOB.put("resCode", "1");
                                        JOB.put("result", "Success");
                                        JOB.put("latitude", Loc.getLatitude());
                                        JOB.put("longitude", Loc.getLongitude());
                                        JSHandlerCallBack(CallBackID, JOB);
                                        LM.removeUpdates(this);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onStatusChanged(String s, int i, Bundle bundle) {
                            }

                            @Override
                            public void onProviderEnabled(String s) {
                            }

                            @Override
                            public void onProviderDisabled(String s) {
                            }
                        };
                        LM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 1, LL);
                    } else {
                        JOB.put("resCode", "2");
                        JOB.put("result", "Provider Not Available");
                        JSHandlerCallBack(CallBackID, JOB);
                    }
                }
            }
        } catch (Exception e) {
            try {
                JOB.put("resCode", "Exception");
                JSHandlerCallBack(CallBackID, JOB);
            } catch (Exception ee) {
                //
            }
        }
    }

    @JavascriptInterface
    public void openCoordinateByMap(String Lat, String Lng) {
        try {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Lat + "," + Lng);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (mapIntent.resolveActivity(Act.getPackageManager()) != null) {
                Act.startActivity(mapIntent);
            } else {
                SM.UIToast(R.string.JS_ERR_InstallGMap);
            }
        } catch (Exception e) {
            SM.UIToast(R.string.ERR_PrepareData);
        }
    }

    @JavascriptInterface
    public void openShare(String Content) {
        try {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Content);
            Act.startActivity(Intent.createChooser(sharingIntent, "Share"));
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "openShare", e);
        }
    }

    @JavascriptInterface
    public String shareByLine(String Text) {
        JSONObject JOB = new JSONObject();
        try {
            String PKGName = "jp.naver.line.android";
            if (isPackageExisted(PKGName)) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                //Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(Act.getContentResolver(), bitmap, null,null));
                //shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                //shareIntent.setType("image/jpeg"); //图片分享
                intent.setPackage(PKGName);
                intent.setType("text/plain"); // 纯文本
                intent.putExtra(Intent.EXTRA_TEXT, Text);
                //intent.putExtra(Intent.EXTRA_SUBJECT, "分享的标题");
                //intent.putExtra(Intent.EXTRA_TEXT, "分享的内容");
                Act.startActivity(intent);
                SM.JSONValueAdder(JOB, "ResCode", "Success");
            } else {
                Toast.makeText(Act, "請安裝Line", Toast.LENGTH_SHORT).show();
                SM.JSONValueAdder(JOB, "ResCode", "NoLine");
            }
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "shareByLine", e);
            SM.JSONValueAdder(JOB, "ResCode", "Exception");
        }
        return JOB.toString();
    }

    @JavascriptInterface
    public String shareByFacebook(String Text) {
        JSONObject JOB = new JSONObject();
        try {
            String PKGName = "com.facebook.katana";
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(PKGName);
            shareIntent.setType("text/plain"); // 纯文本
            shareIntent.putExtra(Intent.EXTRA_TEXT, Text);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (isPackageExisted(PKGName)) {
                Act.startActivity(shareIntent);
                SM.JSONValueAdder(JOB, "ResCode", "Success");
            } else {
                String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + Text;
                Intent URLIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
                URLIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Act.startActivity(URLIntent);
                SM.JSONValueAdder(JOB, "ResCode", "ByUrl");
            }
        } catch (ActivityNotFoundException e) {
            SM.UIToast("無法使用Facebook應用程式");
            SM.JSONValueAdder(JOB, "ResCode", "ActivityNotFoundException");
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "shareByFacebook", e);
            SM.JSONValueAdder(JOB, "ResCode", "Exception");
        }
        return JOB.toString();
    }

    //File==========================================================================================
    @JavascriptInterface
    public void getDownloadList(String CallBackID, String DownloadList) {
        try {
            JSONArray DownloadListJA = SM.JAGetter(SM.JAGetter(DownloadList), 0);
            if (DownloadListJA.length() > 0) {
                SF.SetOnDownloadStatusListener(Status -> {
                    JSONObject JOB = new JSONObject();
                    SM.JSONValueAdder(JOB, "res_code", "1");
                    JSHandlerCallBack(CallBackID, JOB);
                });
                SF.StartDownload(Act, DownloadListJA);
            }
        } catch (Exception e) {
            SM.EXToast(R.string.ERR_PrepareData, "getDownloadList", e);
        }
    }

    @JavascriptInterface
    public void getFilesSize(String CallBackID) {
        try {
            //getTotalInternalMemorySize
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long blockCount = stat.getBlockCountLong();
            float TotalInternalMemorySize = (float) ((double) (blockCount * blockSize) / (1024 * 1024));

            JSONObject JOB = new JSONObject();
            JOB.put("TotalInternalMemorySize", TotalInternalMemorySize);
            File FolderCache = Act.getExternalFilesDir("Cache");
            if (FolderCache != null) {
                float TotalFileSize = (float) ((double) SM.GetFolderSize(FolderCache) / (1024 * 1024));
                JOB.put("TotalFileSize", TotalFileSize);
            }
            JSHandlerCallBack(CallBackID, JOB);
        } catch (Exception e) {
            SM.EXToast(R.string.ERR_PrepareData, "getFilesSize", e);
        }
    }

    @JavascriptInterface
    public void isFile(String CallBackID, String FileName) {
        try {
            JSONObject JOB = new JSONObject();
            JOB.put("FileName", FileName);
            File FolderCache = Act.getExternalFilesDir("Cache");
            if (FolderCache != null) {
                File file = new File(FolderCache, FileName);
                JOB.put("Exist", file.exists());
            } else {
                JOB.put("Exist", false);
            }
            JSHandlerCallBack(CallBackID, JOB);
        } catch (Exception e) {
            SM.EXToast(R.string.ERR_PrepareData, "isFile", e);
        }
    }

    @JavascriptInterface
    public void openFile(String FileName) {
        try {
            File file = new File(Act.getExternalFilesDir("Cache"), FileName);
            if (file.exists()) {
                Uri uri = FileProvider.getUriForFile(Act, Act.getApplicationContext().getPackageName() + ".fileprovider", file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                String Mime = "*/*";
                MimeTypeMap MTM = MimeTypeMap.getSingleton();
                if (MTM.hasExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()))) {
                    Mime = MTM.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
                }
                intent.setDataAndType(uri, Mime);
                Act.startActivity(intent);
            } else {
                SM.UIToast(R.string.JS_ERR_NoOfflineFile);
            }
        } catch (ActivityNotFoundException e) {
            SM.UIToast("您的裝置沒有可以開啟此檔案的應用");
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "openFile", e);
        }
    }

    //下載到Download 資料夾
    @JavascriptInterface
    public void downloadUserFile(String FileUrl) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    if (Environment.getExternalStorageState() != null) {
                        File FolderFile = new File(Environment.getExternalStorageDirectory() + "/Download");
                        SFile.RunDownload = true;//允許開始下載
                        SF.SetOnDownloadStatusListener(Status -> {
                            if (Status.equals("3")) {
                                SM.SWToast(R.string.SF_Hint_DownloadFinish, SweetAlertDialog.SUCCESS_TYPE);
                            } else if (Status.equals("4")) {
                                SM.SWToast(R.string.SF_Hint_DownloadFailed, SweetAlertDialog.ERROR_TYPE);
                            }
                        });
                        SF.SFileDownloader(Act, FileUrl, FolderFile, true, false);
                    }
                } catch (Exception e) {
                    SM.EXToast(R.string.ERR_ProcessData, "downloadFile", e);
                } finally {
                    SFile.RunDownload = false;//關閉允許下載
                    SF.SProgressDialog(Act);//Close Progress
                }
            }
        }.start();
    }

    @JavascriptInterface
    public void downloadFile(String CallBackID, String FileUrl) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    if (Environment.getExternalStorageState() != null) {
                        SFile.RunDownload = true;//允許開始下載
                        SF.SetOnDownloadStatusListener(Status -> {
                            try {
                                if (Status.equals("3")) {
                                    SM.SWToast(R.string.SF_Hint_DownloadFinish, SweetAlertDialog.SUCCESS_TYPE);
                                    JSHandlerCallBack(CallBackID, new JSONObject().put("result_code", 1));//Success
                                } else if (Status.equals("4")) {
                                    SM.SWToast(R.string.SF_Hint_DownloadFailed, SweetAlertDialog.ERROR_TYPE);
                                    JSHandlerCallBack(CallBackID, new JSONObject().put("result_code", 2));//Failed
                                }
                            } catch (Exception e) {
                                SM.EXToast(R.string.ERR_ProcessData, "downloadFile", e);
                            }
                        });
                        File FolderFile = Act.getExternalFilesDir("Cache");
                        SF.SFileDownloader(Act, FileUrl, FolderFile, true, false);
                        SF.SProgressDialog(Act);//Close Progress Dialog
                    }
                } catch (Exception e) {
                    SM.EXToast(R.string.ERR_ProcessData, "downloadFile", e);
                } finally {
                    SFile.RunDownload = false;//關閉允許下載
                    SF.SProgressDialog(Act);//Close Progress
                }
            }
        }.start();
    }

    @JavascriptInterface
    public void deleteSingleFile(String FilePath) {
        try {
            File DeleteFile = new File(Act.getExternalFilesDir("Cache"), FilePath);
            if (DeleteFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                DeleteFile.delete();
            }
        } catch (Exception e) {
            SM.EXToast(R.string.ERR_PrepareData, "deleteSingleFile", e);
        }
    }

    @JavascriptInterface
    public void deleteAllFiles() {
        try {
            File FolderCache = Act.getExternalFilesDir("Cache");
            if (FolderCache != null) {
                SM.DeleteRecursive(FolderCache);
            }
        } catch (Exception e) {
            SM.EXToast(R.string.ERR_PrepareData, "deleteAllFiles", e);
        }
    }

    private String uploadFileCID, uploadFileUrl;
    private final String[] UploadMimeTypes = {"application/pdf", "image/*"};

    @JavascriptInterface
    public void uploadFile(String CallBackID, String UploadUrl) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    if (ActivityCompat.checkSelfPermission(Act, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Act, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, TD.RQC_Permission);
                        return;
                    }
                    uploadFileCID = CallBackID;
                    uploadFileUrl = UploadUrl;
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, false);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, UploadMimeTypes);
                    StringBuilder TypeDB = new StringBuilder();
                    for (int cnt = 0; cnt < UploadMimeTypes.length; cnt++) {
                        TypeDB.append(UploadMimeTypes[cnt]);
                        if (cnt < UploadMimeTypes.length - 1) TypeDB.append("|");
                    }
                    intent.setType(TypeDB.toString());
                    Intent destIntent = Intent.createChooser(intent, "選擇檔案");
                    //destIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Act.startActivityForResult(destIntent, TD.RQC_SelectFile);
                } catch (Exception e) {
                    SM.EXToast(R.string.ERR_ProcessData, "downloadFile", e);
                } finally {
                    SFile.RunDownload = false;//關閉允許下載
                    SF.SProgressDialog(Act);//Close Progress
                }
            }
        }.start();
    }

    private void uploadFileOnFileSelect(Intent data) {
        try {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null && uploadFileCID != null) {
                    String FilePath = NFileUtils.getPath(Act, uri);
                    if (FilePath.length() > 0) {
                        //Upload
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    SM.SProgressDialog(true, "檔案上傳中", 1000);
                                    SUploader SU = new SUploader();
                                    JSONObject ResJOB = SU.SUpload(uploadFileUrl, null, FilePath);
                                    boolean Success = SM.GetResultJOBAvailable(ResJOB);
                                    JSONObject JOB = new JSONObject();
                                    if (Success) {
                                        ResJOB.remove("SResult");
                                        JOB.put("result_code", 1);
                                        JOB.put("files", SM.JOBGetter(ResJOB, "file"));
                                    } else {
                                        JOB.put("result_code", 2);
                                    }
                                    JSHandlerCallBack(uploadFileCID, JOB);//Success:Failed
                                } catch (Exception e) {
                                    SM.EXToast(R.string.ERR_PrepareData, "transferWebDataUpload", e);
                                } finally {
                                    SM.SProgressDialog();
                                }
                            }
                        }.start();
                    } else {
                        SM.UIToast(R.string.ERR_LostInfo);
                    }
                } else {
                    SM.UIToast(R.string.ERR_LostInfo);
                }
            }
        } catch (SecurityException e) {
            SM.EXToast("錯誤的匯入方式", "transferWebData_Security", e);
        } catch (Exception e) {
            SM.EXToast(R.string.ERR_PrepareData, "transferWebData", e);
        }
    }

    //Offline=======================================================================================
    @JavascriptInterface
    public void transferWebData(String UrlLink, JSONArray PathListJA) {
        try {
            //JSONArray PathListJA = SM.JAGetter("");
            if (PathListJA.length() > 0) {
                JSONArray DownloadListJA = new JSONArray();
                for (int cnt = 0; cnt < PathListJA.length(); cnt++) {
                    DownloadListJA.put(UrlLink + PathListJA.getString(cnt));
                }
                //Delete Old Data First
                File FolderFile = Act.getExternalFilesDir("Cache");
                if (FolderFile != null) SM.DeleteRecursive(FolderFile);
                //StartDownload
                SF.StartDownload(Act, DownloadListJA);
                SM.SPSaveStringData("PreTransferWebTime", String.valueOf(System.currentTimeMillis()));
            }
        } catch (Exception e) {
            SM.EXToast(R.string.ERR_PrepareData, "transferWebData", e);
        }
    }

    @JavascriptInterface
    public void getTransferDatetime(String CallBackID) {
        JSONObject JOB = new JSONObject();
        try {
            String PreTransferWebTimeStr = SM.SPReadStringData("PreTransferWebTime");
            long PreTransferWebTime = SM.StL(PreTransferWebTimeStr);
            String res_code = PreTransferWebTime != 0 ? "1" : "0";
            JOB.put("res_code", res_code);
            if (res_code.equals("1")) {
                String Time = SM.MillisToTime(PreTransferWebTimeStr, "Both");
                JOB.put("res_content", Time);
            }
        } catch (Exception e) {
            SM.EXToast(R.string.ERR_PrepareData, "getTransferDatetime", e);
        }
        JSHandlerCallBack(CallBackID, JOB);
    }


    @JavascriptInterface
    public void cameraPermission(String CallBackID) {
        int resCode = -1;
        try {
            boolean PC = ActivityCompat.checkSelfPermission(Act, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
            boolean PW = ActivityCompat.checkSelfPermission(Act, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            resCode = (PC && PW) ? 1 : -1;
            if (resCode == -1) {
                ActivityCompat.requestPermissions(Act, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, TD.RQC_Permission_Camera);
            }
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "cameraPermission", e);
        }
        JSONObject JOB = new JSONObject();
        SM.JOBValueAdder(JOB, "res_code", String.valueOf(resCode));
        JSHandlerCallBack(CallBackID, JOB);
    }

    //Location======================================================================================
    @JavascriptInterface
    public void isGPS(String CallBackID) {
        String res_code, res_content;
        LocationManager LM = (LocationManager) Act.getSystemService(Context.LOCATION_SERVICE);
        if (LM != null) {
            if (LM.isProviderEnabled(LocationManager.GPS_PROVIDER) && LM.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                res_code = "1";
                res_content = "GPS功能正常";
            } else {
                res_code = "-1";
                res_content = "用戶未開啟定位功能";
            }
        } else {
            res_code = "-2";
            res_content = "無法取得 LocationManager";
        }

        JSONObject JOB = new JSONObject();
        SM.JOBValueAdder(JOB, "res_code", res_code);
        SM.JOBValueAdder(JOB, "res_content", res_content);
        JSHandlerCallBack(CallBackID, JOB);
    }

    @JavascriptInterface
    public void isGPSPermission(String CallBackID) {
        boolean FineLoc = ActivityCompat.checkSelfPermission(Act, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        JSONObject JOB = new JSONObject();
        SM.JOBValueAdder(JOB, "res_code", FineLoc ? "1" : "-1");
        SM.JOBValueAdder(JOB, "res_content", FineLoc ? "App已取得權限" : "App尚未取得權限");
        JSHandlerCallBack(CallBackID, JOB);
    }

    @JavascriptInterface
    public void requestGps() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Act.startActivity(intent);
    }

    private String CallBackID_RGP;

    @JavascriptInterface
    public void requestGpsPermission(String CallBackID) {
        CallBackID_RGP = CallBackID;
        ActivityCompat.requestPermissions(Act, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, TD.RQC_Permission_LocService);
    }

    @JavascriptInterface
    public void getCoordinateCond(String CallBackID, String Data) {
        try {
            String resCode, resContent;
            if (Act instanceof ActivityWeb) {
                JSONObject TaskJOB = SM.JOBGetter(Data);
                int StartLoc = SM.JSONIntGetter(TaskJOB, "coordinateCondKey", 0);
                if (StartLoc == 1) {
                    boolean FineLoc = ActivityCompat.checkSelfPermission(Act, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                    if (FineLoc) {
                        LocationManager LM = (LocationManager) Act.getSystemService(Context.LOCATION_SERVICE);
                        if (LM != null) {
                            if (LM.isProviderEnabled(LocationManager.GPS_PROVIDER) && LM.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                                SLocService.StartService(Act, TaskJOB);
                                resCode = "1";
                                resContent = "成功";
                            } else {
                                SM.SWToast(R.string.SLS_OpenLoc);
                                resCode = "-1";
                                resContent = "用戶未開啟定位功能";
                            }
                        } else {
                            resCode = "-2";
                            resContent = "無法取得 LocationManager";
                        }
                    } else { //Ask Permission
                        ActivityCompat.requestPermissions(Act, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, TD.RQC_Permission_LocService);
                        resCode = "-3";
                        resContent = "為戶尚未提供權限";
                    }
                } else {
                    SLocService.StopService(Act);
                    resCode = "2";
                    resContent = "關閉成功";
                }
            } else {
                resCode = "-4";
                resContent = "Activity不支援";
            }
            JSONObject ReturnJOB = new JSONObject();
            ReturnJOB.put("res_code", resCode);
            ReturnJOB.put("res_content", resContent);
            JSHandlerCallBack(CallBackID, ReturnJOB);
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "getCoordinateCond", e);
        }
    }

    @JavascriptInterface
    public void setFirstPage(String FirstPageUrl) {
        try {
            SendJSB("setFirstPage", FirstPageUrl);
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "setFirstPage", e);
        }
    }

    @JavascriptInterface
    public void openSound() {
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(Act, R.raw.opensound);
            mediaPlayer.start();
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "openSound", e);
        }
    }

    @JavascriptInterface
    public void openVibration() {
        try {
            Vibrator VB = (Vibrator) Act.getApplication().getSystemService(Service.VIBRATOR_SERVICE);
            if (VB != null) VB.vibrate(1000);
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "openVibration", e);
        }
    }

    //WiFi==========================================================================================
    @JavascriptInterface
    public void mukiWifiInfo(String CallBackID) {
        JSONObject ReturnJOB = new JSONObject();
        try {
            WifiManager WM = (WifiManager) Act.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (WM != null) {
                WifiInfo wifiInfo = WM.getConnectionInfo();
                SM.JOBValueAdder(ReturnJOB, "res_code", "0");
                SM.JOBValueAdder(ReturnJOB, "ssid", wifiInfo.getSSID().replace("\"", ""));
                SM.JOBValueAdder(ReturnJOB, "bssid", wifiInfo.getBSSID());
            } else {
                SM.JOBValueAdder(ReturnJOB, "res_code", "-1");
            }
        } catch (Exception e) {
            SM.JOBValueAdder(ReturnJOB, "res_code", "-2");
            SM.EXToast(R.string.CM_DetectError, "openVibration", e);
        }
        JSHandlerCallBack(CallBackID, ReturnJOB);
    }

    //開啟設定
    @JavascriptInterface
    public void openSet() {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", Act.getPackageName(), null);
            intent.setData(uri);
            Act.startActivity(intent);
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "openFile", e);
        }
    }

    //詢問麥克風權限
    private String CallBack_MIC;

    @JavascriptInterface
    public void microphonePermissions(String CallBackID) {
        try {
            CallBack_MIC = CallBackID;
            if (ActivityCompat.checkSelfPermission(Act, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                JSONObject ReturnJOB = new JSONObject();
                ReturnJOB.put("res_code", "1");
                JSHandlerCallBack(CallBack_MIC, ReturnJOB);
            } else {//請求權限
                ActivityCompat.requestPermissions(Act, new String[]{Manifest.permission.RECORD_AUDIO}, TD.RQC_Permission_Mic);
            }
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "microphonePermissions", e);
        }
    }

    //網頁端準備完成
    @JavascriptInterface
    public void appReady() {
        try {
            ((ActivityWeb) Act).AutoCallBackFCMValueJOB();
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "shouldStartLoadWith", e);
        }
    }

    //語音辨識========================================================================================
    private String CallBack_SPR;
    private boolean isRecognizing = false;

    @JavascriptInterface
    public void speechRecognition(String CallBackID) {
        if (!isRecognizing) {
            CallBack_SPR = CallBackID;
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    String language = "zh-TW";
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, language);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
                    intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
                    Act.startActivityForResult(intent, TD.RQC_Speech);
                    isRecognizing = true;
                } catch (Exception e) {
                    isRecognizing = false;
                    SM.EXToast(R.string.CM_DetectError, "speechRecognition", e);
                    JSHandlerCallBackF("onSpeechRecognitionClose");//回報語音識別結束
                }
            });
        }
    }

    private void speechRecognitionHandler(Intent data) {
        try {
            if (data != null) {
                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (results != null && results.size() > 0) {
                    JSONObject ReturnJOB = new JSONObject();
                    ReturnJOB.put("speechRecognizerStr", results.get(0));
                    JSHandlerCallBack(CallBack_SPR, ReturnJOB);
                }
            }
        } catch (Exception e) {
            SM.EXToast(R.string.CM_DetectError, "speechRecognitionHandler", e);
        } finally {
            isRecognizing = false;
            JSHandlerCallBackF("onSpeechRecognitionClose");//回報語音識別結束
        }
    }

    //onActivityResult==============================================================================
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                switch (requestCode) {
                    case TD.RQC_SelectFile:
                        uploadFileOnFileSelect(data);
                        break;
                    case TD.RQC_Speech:
                        speechRecognitionHandler(data);
                        break;
                }
            } catch (Exception e) {
                SM.UIToast(R.string.ERR_PrepareData);
            }
        });
    }

    //onRequestPermissionsResult
    public void onRequestPermissionsResult(int requestCode, int[] grantResults) {
        try {
            if (requestCode == TD.RQC_Permission_LocService) {
                if (Act instanceof ActivityWeb && CallBackID_RGP != null && CallBackID_RGP.length() > 0) {
                    String resCode, resContent;
                    if (((ActivityWeb) Act).CheckAllGrant(grantResults)) {
                        resCode = "1";
                        resContent = "成功取得GPS權限";
                    } else {
                        resCode = "-1";
                        resContent = "無法取得GPS權限";
                    }
                    JSONObject ReturnJOB = new JSONObject();
                    ReturnJOB.put("res_code", resCode);
                    ReturnJOB.put("res_content", resContent);
                    JSHandlerCallBack(CallBackID_RGP, ReturnJOB);
                    CallBackID_RGP = "";
                }
            } else if (requestCode == TD.RQC_Permission_Mic) {
                if (Act instanceof ActivityWeb && CallBack_MIC != null && CallBack_MIC.length() > 0) {
                    String resCode, resContent;
                    if (((ActivityWeb) Act).CheckAllGrant(grantResults)) {
                        resCode = "1";
                        resContent = "成功取得錄音權限";
                    } else {
                        resCode = "0";
                        resContent = "無法取得錄音權限";
                    }
                    JSONObject ReturnJOB = new JSONObject();
                    ReturnJOB.put("res_code", resCode);
                    ReturnJOB.put("res_content", resContent);
                    JSHandlerCallBack(CallBack_MIC, ReturnJOB);
                    CallBack_MIC = "";
                }
            }
        } catch (Exception e) {
            SM.EXToast(R.string.SC_Error_Process, "onRequestPermissionsResult", e);
        }
    }

    //Other=========================================================================================
    private boolean isPackageExisted(String targetPackage) {
        PackageManager pm = Act.getPackageManager();
        try {
            pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    /*
    public void JSCallBack(String JSName){
        new Handler(Looper.getMainLooper()).post(()-> WBV.evaluateJavascript("javascript:"+JSName+"()", value -> {
            //此处为 js 返回的结果
        }));
    }

    public void JSCallBack(String JSName,String Value){
        new Handler(Looper.getMainLooper()).post(()-> WBV.evaluateJavascript("javascript:"+JSName+"("+Value+")", value -> {
            //此处为 js 返回的结果
        }));
    }
     */

    public void JSHandlerCallBack(String CallBackID, JSONObject JOB) {
        JSHandlerCallBack(CallBackID, JOB.toString());
    }

    public void JSHandlerCallBack(String CallBackID, String Value) {
        //String Value = JOB.toString().replace("\\", "\\\\");//防止跳脫字元
        new Handler(Looper.getMainLooper()).post(() -> WBV.evaluateJavascript("javascript:jsHandlerFunc(" + Value + "," + CallBackID + ")", value -> {
            Log.d("JSHandlerCallBack", value);
        }));
    }

    public void JSHandlerCallBackF(String Func) {
        JSHandlerCallBackF(Func, "");
    }

    public void JSHandlerCallBackF(String Func, String Value) {
        try {
            String FValue = Uri.encode(Value);
            new Handler(Looper.getMainLooper()).post(() -> WBV.evaluateJavascript("javascript:" + Func + "('" + FValue + "');", value -> {
                Log.d("JSHandlerCallBack", value);
            }));
        } catch (Exception e) {
            SM.UIToast(e.getMessage());
        }
    }
}
