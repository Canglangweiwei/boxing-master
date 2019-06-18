package com.bilibili.boxing;

import android.app.Application;

import com.bilibili.boxing.impl.BoxingGlideLoader;
import com.bilibili.boxing.impl.BoxingUcrop;
import com.bilibili.boxing.loader.IBoxingMediaLoader;

/**
 * aha, initial work.
 *
 * @author ChenSL
 */
public class BoxingApplication extends Application {

   @Override
   public void onCreate() {
      super.onCreate();

      IBoxingMediaLoader loader = new BoxingGlideLoader();
      BoxingMediaLoader.getInstance().init(loader);
      BoxingCrop.getInstance().init(new BoxingUcrop());
   }
}
