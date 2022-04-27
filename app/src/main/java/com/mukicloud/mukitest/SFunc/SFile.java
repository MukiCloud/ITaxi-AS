package com.mukicloud.mukitest.SFunc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.mukicloud.mukitest.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SFile {
    private SMethods SM;
    private Activity Act;
    public static boolean RunDownload = false;

    public SFile(Activity act) {
        Act = act;
        SM = new SMethods(Act);
    }

    public void StartDownload(Context Con, JSONArray DownloadListJA){
        new SMethods(Con).SPSaveStringData("DownloadList",DownloadListJA.toString());
        DownloadHandler();
    }

    public void DownloadHandler(){
        try {
            String DownloadList = SM.SPReadStringData("DownloadList");
            if (DownloadList.length() > 0) {
                JSONArray DownloadListJA = new JSONArray(DownloadList);
                if(DownloadListJA.length() > 0){
                    StartDownloader(Act,DownloadListJA);
                }
            }
        }catch (Exception e){
            //
        }
    }

    private void StartDownloader(final Context Con, final JSONArray DownloadListJA){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    RunDownload = true;
                    SMethods CM = new SMethods(Con);
                    CM.SPSaveStringData("DownloadList",DownloadListJA.toString());
                    //Turn AL
                    ArrayList<String> DownloadListAL = new ArrayList<>();
                    for(int cnt=0;cnt<DownloadListJA.length();cnt++){
                        String URL = DownloadListJA.getString(cnt);
                        if(URL.contains("http")) {
                            DownloadListAL.add(URL);
                        }
                    }
                    onDownloadStatus("2");//Start Download
                    JSONArray FilePathJA = new JSONArray();
                    while(DownloadListAL.size() > 0 && RunDownload){
                        if(CM.isNetworkAvailable()) {
                            int SuccessSize = 0,TotalSize = DownloadListAL.size();
                            String FileUrl = DownloadListAL.get(0);
                            String FileName = FileUrl.substring(FileUrl.lastIndexOf("/")+1);
                            SProgressDialog(Con,"還有 "+DownloadListAL.size()+" 個檔案","正在下載檔案 "+FileName);
                            JSONObject ResJOB = SFileDownloader(Con, FileUrl);
                            String DownloadedSuccess = CM.JSONStrGetter(ResJOB, "DownloadedSuccess");
                            if(DownloadedSuccess.equals("True"))SuccessSize++;
                            DownloadListAL.remove(0);

                            FilePathJA.put(CM.JSONStrGetter(ResJOB,"FilePath"));
                            //Turn JA
                            JSONArray DownloadListTmpJA = new JSONArray();
                            for (String FileUrlTmp : DownloadListAL) {
                                DownloadListTmpJA.put(FileUrlTmp);
                            }
                            //確認沒有按下取消 才儲存
                            if(RunDownload)CM.SPSaveStringData("DownloadList", DownloadListTmpJA.toString());
                            int FailedNum = TotalSize - SuccessSize;
                            if(FailedNum > 0) CM.UIToast(FailedNum+" 個檔案下載失敗");
                            sleep(100);
                        }else{
                            sleep(10000);
                        }
                    }
                    onDownloadStatus("1");//All Finish
                    SM.SWToast(R.string.SF_Hint_DownloadFinish, SweetAlertDialog.SUCCESS_TYPE);
                    String DLResult = FilePathJA.toString();
                    Log.d("AL",DLResult);
                }catch (Exception e){
                    Toast.makeText(Con,"下載檔案Detect Error", Toast.LENGTH_SHORT).show();
                }finally {
                    SProgressDialog(Con);
                    RunDownload = false;
                }
            }
        }.start();
    }

    public JSONObject SFileDownloader(Context Con, String FileUrl){
        File FolderFile = Con.getExternalFilesDir("Cache");
        return SFileDownloader(Con,FileUrl,FolderFile,true,true);
    }

    public JSONObject SFileDownloader(Context Con, String FileUrl, File FolderFile, boolean Replace, boolean CreatePath){
        JSONObject ResJOB = new JSONObject();
        try {
            String DownloadedSuccess = "False";
            //CheckFolder
            boolean AvailableDownload = true;
            String FolderPath = null;
            if(FolderFile != null){
                FolderPath = FolderFile.getAbsolutePath();
                if(FolderPath.length() == 0){
                    AvailableDownload = false;
                }
            }else{
                AvailableDownload = false;
            }
            //Start Download
            if(AvailableDownload) {
                try {
                    if (FileUrl == null || FileUrl.length() == 0) {
                        return ResJOB;
                    }
                    URL Url = new URL(FileUrl);
                    HttpURLConnection httpConn = (HttpURLConnection) Url.openConnection();
                    int responseCode = httpConn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        String disposition = httpConn.getHeaderField("Content-Disposition");
                        String contentType = httpConn.getContentType();
                        int contentLength = httpConn.getContentLength();

                        System.out.println("Content-Type = " + contentType);
                        System.out.println("Content-Disposition = " + disposition);
                        System.out.println("Content-Length = " + contentLength);
                        //Auto Create Folder
                        String FilePath,FileFolderPath,FileName;
                        File FFolderFile;
                        if(CreatePath) {//透過網址產生路徑
                            FilePath = FolderPath + FileUrl.substring(FileUrl.indexOf("/", 10));
                            FileFolderPath = FilePath.substring(0, FilePath.lastIndexOf("/"));
                            FileName = FilePath.substring(FileFolderPath.length() + 1);
                            FFolderFile = new File(FileFolderPath);
                        }else{
                            FileName = FileUrl.substring(FileUrl.lastIndexOf("/")+1);
                            FilePath = FolderPath + File.separator + FileName;
                            FileFolderPath = FolderPath;
                            FFolderFile = new File(FileFolderPath);
                        }

                        if(FFolderFile.exists() || FFolderFile.mkdirs()) {
                            // opens input stream from the HTTP connection
                            File OriFile = new File(FilePath);
                            long OriFileSize = OriFile.length();
                            if (!OriFile.exists() || contentLength != OriFileSize || Replace) {
                                if(OriFile.exists()) //noinspection ResultOfMethodCallIgnored
                                    OriFile.delete();
                                SProgressDialog(Con, 0, contentLength / 1024);//Update file size info
                                InputStream inputStream = httpConn.getInputStream();
                                // opens an output stream to save into file
                                int BytesDownloaded = 0;
                                File NetTmpFile = new File(FileFolderPath, FileName + "_Tmp");
                                FileOutputStream outputStream = new FileOutputStream(NetTmpFile);
                                int bytesRead;
                                byte[] buffer = new byte[4096];
                                while (RunDownload && ((bytesRead = inputStream.read(buffer)) != -1)) {
                                    outputStream.write(buffer, 0, bytesRead);
                                    BytesDownloaded += bytesRead;
                                    SProgressDialog(Con, BytesDownloaded / 1024);
                                }
                                outputStream.close();
                                inputStream.close();
                                if (NetTmpFile.exists()) {//20190412 取消比較 因為下載html大小可能不一樣 NetTmpFile.length() == contentLength
                                    File NewFile = new File(FileFolderPath, FileName);
                                    if (NetTmpFile.renameTo(NewFile)) {
                                        ResJOB.put("FilePath", NewFile.getAbsolutePath());
                                        DownloadedSuccess = "True";
                                    }
                                }
                            } else {
                                DownloadedSuccess = "True";
                            }
                        }
                    } //No file to download.
                    httpConn.disconnect();
                } catch (Exception e) {
                    //DebugToast("StartImageDownloader\n" + e.getMessage());
                }
            }
            ResJOB.put("DownloadedSuccess",DownloadedSuccess);
            onDownloadStatus(DownloadedSuccess.equals("True")?"3":"4");//Download Success:Failed
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResJOB;
    }

    private static ProgressDialog PGD;
    private static long PreUpdateSPGDMillis;
    private void SProgressDialog(Context Con, String Title, String Msg, int Progress, int FileSize, boolean UpdateNow){
        if(System.currentTimeMillis() - PreUpdateSPGDMillis > 100 || UpdateNow) {
            PreUpdateSPGDMillis = System.currentTimeMillis();
            new Handler(Con.getMainLooper()).post(() -> {
                try {
                    if (PGD == null || !PGD.isShowing()) {
                        PGD = new ProgressDialog(Con,android.R.style.Theme_DeviceDefault_Light_Dialog);
                        PGD.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        PGD.setIndeterminate(false);
                        PGD.setCancelable(false);
                        PGD.setCanceledOnTouchOutside(false);
                        PGD.setProgressNumberFormat("%1d KB / %2d KB");
                        //Set Progress Dialog Cancel Function
                        PGD.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", (dialog, which) -> {
                            RunDownload = false;
                            new SMethods(Con).SPClearStringData("DownloadList");
                        });
                    }
                    if (PGD != null) {
                        if (Title.length() > 0) {
                            PGD.setTitle(Title);
                        }
                        if (Msg.length() > 0) {
                            PGD.setMessage(Msg);
                        }
                        if(FileSize > 0){
                            PGD.setMax(FileSize);
                        }
                        PGD.setProgress(Progress);
                        if (!PGD.isShowing()) {
                            PGD.show();
                        }
                    }
                }catch (Exception e){
                    Log.e("SProgressDialog",e.getMessage());
                }
            });
        }
    }

    private void SProgressDialog(Context Con, int Progress){//Update Progress
        SProgressDialog(Con,"","",Progress,0,false);
    }

    private void SProgressDialog(Context Con, int Progress, int FileSize){//Update FileSize Info
        SProgressDialog(Con,"","",Progress,FileSize,true);
    }

    private void SProgressDialog(Context Con, String Title, String Msg){//Update Message
        SProgressDialog(Con,Title,Msg,0,0,true);
    }

    public void SProgressDialog(Context Con){//Close PGD
        new Handler(Con.getMainLooper()).post(() -> {
            if(PGD != null && PGD.isShowing()){
                PGD.dismiss();
            }
        });
    }

    //Interface
    /*
    onDownloadStatus
    1 => All Success
    2 => Start Download
    3 => A File Download Finish
    4 => File Download Failed
     */
    private OnDownloadStatusListener ODSL;
    public void SetOnDownloadStatusListener(OnDownloadStatusListener ODSL){
        this.ODSL = ODSL;
    }

    public interface OnDownloadStatusListener{
        void onDownloadStatus(String Status);
    }

    private void onDownloadStatus(String Status){
        if(ODSL != null)ODSL.onDownloadStatus(Status);
    }
}
