package com.mukicloud.mukitest.SFunc;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;

/**
 * Created by yuyang on 16/5/5.
 */
public class SFileUtils {
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        String column = "_data";
        String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isGoogleDriveDocument(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotos(Uri uri) {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    public static String getPath(Context context, Uri uri) {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }else if ("raw".equalsIgnoreCase(type)) {
                    return split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            } else if (isGoogleDriveDocument(uri)) {
                return ImportFromGoogleDrive(context, uri);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotos(uri)) {
                return ImportFromGooglePhotos(context, uri);
            } else {
                return getDataColumn(context, uri, null, null);
            }
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //Ori => uri.getPath() Because Chinese become 亂碼
            return uri.getPath();
        }// File
        else if ("raw".equalsIgnoreCase(uri.getScheme())) {
            //Ori => uri.getPath() Because Chinese become 亂碼
            return uri.getPath();
        }

        return null;
    }

    private static String ImportFromGooglePhotos(Context Con, Uri uri) {
        String Path = null;
        try {
            InputStream IS = Con.getContentResolver().openInputStream(uri);
            if (IS != null) {
                boolean SaveAble = true;
                File FolderFile = new File(Con.getFilesDir() + File.separator + "GooglePhotos");
                if (!FolderFile.exists()) {
                    if (!FolderFile.mkdir()) {
                        SaveAble = false;
                    }
                }
                if (SaveAble) {
                    DeleteRecursive(FolderFile);
                    String FileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
                    File PicFile = new File(FolderFile + File.separator + FileName);
                    FileOutputStream FOS = new FileOutputStream(PicFile);

                    byte[] buffer = new byte[8 * 1024];
                    int bytesRead;
                    while ((bytesRead = IS.read(buffer)) != -1) {
                        FOS.write(buffer, 0, bytesRead);
                    }
                    IS.close();
                    FOS.flush();
                    FOS.close();
                    if (PicFile.exists()) {
                        Path = PicFile.getPath();
                    }
                }
            }
        } catch (Exception e) {
            //
        }
        return Path;
    }

    private static String ImportFromGoogleDrive(Context Con, Uri uri) {
        String Path = null;
        try {
            InputStream IS = Con.getContentResolver().openInputStream(uri);
            if (IS != null) {
                File FolderFile = Con.getExternalFilesDir("ImportExcel");
                if (FolderFile != null) {
                    DeleteRecursive(FolderFile);
                    String FileName = String.valueOf(System.currentTimeMillis()) + ".xls";
                    File ExcelFile = new File(FolderFile + File.separator + FileName);
                    FileOutputStream FOS = new FileOutputStream(ExcelFile);

                    byte[] buffer = new byte[8 * 1024];
                    int bytesRead;
                    while ((bytesRead = IS.read(buffer)) != -1) {
                        FOS.write(buffer, 0, bytesRead);
                    }
                    IS.close();
                    FOS.flush();
                    FOS.close();
                    if (ExcelFile.exists()) {
                        Path = ExcelFile.getPath();
                    }
                }
            }
        } catch (Exception e) {
            //
        }
        return Path;
    }

    private static void DeleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory != null) {
            if (fileOrDirectory.isDirectory()) {
                for (File child : Objects.requireNonNull(fileOrDirectory.listFiles()))
                    DeleteRecursive(child);
            } else {
                //noinspection ResultOfMethodCallIgnored
                fileOrDirectory.delete();
            }
        }
    }
}
