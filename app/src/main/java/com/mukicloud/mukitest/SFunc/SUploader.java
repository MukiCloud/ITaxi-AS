package com.mukicloud.mukitest.SFunc;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.FileNameMap;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class SUploader {

    public JSONObject SUpload(String Url, Map<String, String> Params, String FilePath) throws Exception {
        JSONObject ResultJOB = new JSONObject();
        HttpURLConnection Conn;
        ByteArrayOutputStream BAOS;
        DataOutputStream outputStream;

        String twoHyphens = "--";
        String boundary = "*****" + System.currentTimeMillis() + "*****";
        String lineEnd = "\r\n";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;

        try {
            File file = new File(FilePath);
            FileInputStream fileInputStream = new FileInputStream(file);

            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            String FileMimeType = fileNameMap.getContentTypeFor(file.getName());
            String FileName = file.getName();

            URL url = new URL(Url);
            Conn = (HttpURLConnection) url.openConnection();

            Conn.setDoInput(true);
            Conn.setDoOutput(true);
            Conn.setUseCaches(false);
            Conn.setConnectTimeout(20000);

            Conn.setRequestMethod("POST");
            Conn.setRequestProperty("Connection", "Keep-Alive");
            Conn.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            Conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            Conn.setRequestProperty("file", FileName);

            outputStream = new DataOutputStream(Conn.getOutputStream());
            outputStream.write(GB(twoHyphens + boundary + lineEnd));
            outputStream.write(GB("Content-Disposition: form-data; name=\"file\"; filename=\"" + FileName + "\"" + lineEnd));
            outputStream.write(GB("Content-Type: "+FileMimeType + lineEnd));
            outputStream.write(GB("Content-Transfer-Encoding: binary" + lineEnd));
            outputStream.write(GB(lineEnd));

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            outputStream.write(GB(lineEnd));
            // Upload POST Data
            if(Params != null) {
                for (String key : Params.keySet()) {
                    String value = Params.get(key);

                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                    outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(value);
                    outputStream.writeBytes(lineEnd);
                }
            }
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            int ResponseCode = Conn.getResponseCode();
            if (ResponseCode == HttpURLConnection.HTTP_OK) {
                InputStream IS = Conn.getInputStream();
                byte[] ISBuf = new byte[8192];
                int BytesRead;
                BAOS = new ByteArrayOutputStream();
                while((BytesRead = IS.read(ISBuf)) != -1){
                    BAOS.write(ISBuf, 0, BytesRead);
                }
                ResultJOB = new JSONObject(BAOS.toString("UTF-8"));
                ResultJOB.put("SResult","FetchSuccess");
            }else{
                ResultJOB.put("SResult","Failed");
                ResultJOB.put("ResponseCode",ResponseCode);
            }

            InputStream IS = Conn.getInputStream();
            ResultJOB.put("ReturnResult",convertStreamToString(IS));

            fileInputStream.close();
            IS.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            ResultJOB.put("SResult","Exception");
            ResultJOB.put("ExceptionReason",e.getMessage());
        }
        return ResultJOB;
    }

    private String convertStreamToString(InputStream IS) {
        BufferedReader BR = new BufferedReader(new InputStreamReader(IS));
        StringBuilder SB = new StringBuilder();

        String line;
        try {
            while ((line = BR.readLine()) != null) {
                SB.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                IS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return SB.toString();
    }

    private byte[] GB(String Value){
        return Value.getBytes();
    }
}
