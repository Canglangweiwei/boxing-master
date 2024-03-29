package com.bilibili.boxing_impl.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bilibili.boxing.AbsBoxingActivity;
import com.bilibili.boxing.AbsBoxingViewFragment;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing_impl.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Default UI Activity for simplest usage.
 * A simple subclass of {@link AbsBoxingActivity}. Holding a {@link AbsBoxingViewFragment} to display medias.
 */
public class BoxingActivity extends AbsBoxingActivity {

   private BoxingViewFragment mPickerFragment;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_boxing);
      createToolbar();
      setTitleTxt(getBoxingConfig());
   }

   @NonNull
   @Override
   public AbsBoxingViewFragment onCreateBoxingView(ArrayList<BaseMedia> medias) {
      mPickerFragment = (BoxingViewFragment) getSupportFragmentManager().findFragmentByTag(BoxingViewFragment.TAG);
      if (mPickerFragment == null) {
         mPickerFragment = (BoxingViewFragment) BoxingViewFragment.newInstance().setSelectedBundle(medias);
         getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, mPickerFragment, BoxingViewFragment.TAG).commit();
      }
      return mPickerFragment;
   }

   private void createToolbar() {
      Toolbar bar = findViewById(R.id.nav_top_bar);
      setSupportActionBar(bar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowTitleEnabled(false);
      bar.setNavigationOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            onBackPressed();
         }
      });
   }

   private void setTitleTxt(BoxingConfig config) {
      TextView titleTxt = findViewById(R.id.pick_album_txt);
      if (config.getMode() == BoxingConfig.Mode.VIDEO) {
         titleTxt.setText(R.string.boxing_video_title);
         titleTxt.setCompoundDrawables(null, null, null, null);
         return;
      }
      mPickerFragment.setTitleTxt(titleTxt);
   }

   @Override
   public void onBoxingFinish(Intent intent, @Nullable List<BaseMedia> medias) {
      setResult(Activity.RESULT_OK, intent);
      finish();
   }
}
