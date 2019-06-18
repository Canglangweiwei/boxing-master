package com.bilibili.boxing.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * A file helper to make thing easier.
 *
 * @author ChenSL
 */
@SuppressWarnings("ALL")
public class BoxingFileHelper {

   public static final String DEFAULT_SUB_DIR = "/bili/boxing";

   public static boolean createFile(String path) throws ExecutionException, InterruptedException {
      if (TextUtils.isEmpty(path)) {
         return false;
      }
      final File file = new File(path);
      return file.exists() || file.mkdirs();
   }

   @Nullable
   public static String getCacheDir(@NonNull Context context) {
      context = context.getApplicationContext();
      File cacheDir = context.getCacheDir();
      if (cacheDir == null) {
         BoxingLog.d("cache dir do not exist.");
         return null;
      }
      String result = cacheDir.getAbsolutePath() + "/boxing";
      try {
         BoxingFileHelper.createFile(result);
      } catch (ExecutionException | InterruptedException e) {
         BoxingLog.d("cache dir " + result + " not exist");
         return null;
      }
      BoxingLog.d("cache dir is: " + result);
      return result;
   }

   @Nullable
   public static String getBoxingPathInDCIM() {
      return getExternalDCIM(null);
   }

   @Nullable
   public static String getExternalDCIM(String subDir) {
      if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
         File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
         if (file == null) {
            return null;
         }
         String dir = "/bili/boxing";
         if (!TextUtils.isEmpty(subDir)) {
            dir = subDir;
         }
         String result = file.getAbsolutePath() + dir;
         BoxingLog.d("external DCIM is: " + result);
         return result;
      }
      BoxingLog.d("external DCIM do not exist.");
      return null;
   }

   public static boolean isFileValid(String path) {
      if (TextUtils.isEmpty(path)) {
         return false;
      }
      File file = new File(path);
      return isFileValid(file);
   }

   static boolean isFileValid(File file) {
      return file != null && file.exists() && file.isFile() && file.length() > 0 && file.canRead();
   }
}
