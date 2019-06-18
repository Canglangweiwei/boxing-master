package com.bilibili.boxing.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.BoxingMediaLoader;
import com.bilibili.boxing.demo.R;
import com.bilibili.boxing.impl.BoxingFrescoLoader;
import com.bilibili.boxing.impl.BoxingGlideLoader;
import com.bilibili.boxing.impl.BoxingPicassoLoader;
import com.bilibili.boxing.loader.IBoxingMediaLoader;
import com.bilibili.boxing.model.config.BoxingConfig;

/**
 * @author ChenSL
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      createToolbar();
      findViewById(R.id.first_btn).setOnClickListener(this);
      findViewById(R.id.second_btn).setOnClickListener(this);
   }

   @Override
   public void onClick(View v) {
      int id = v.getId();
      switch (id) {
         case R.id.first_btn:
            Intent intent1 = new Intent(MainActivity.this, FirstActivity.class);
            startActivity(intent1);
            break;
         case R.id.second_btn:
            BoxingConfig singleImgConfig = new BoxingConfig(BoxingConfig.Mode.SINGLE_IMG);
            Boxing.of(singleImgConfig).withIntent(this, SecondActivity.class).start(this);
            break;
         default:
            break;
      }
   }

   private void createToolbar() {
      Toolbar bar = findViewById(R.id.nav_top_bar);
      setSupportActionBar(bar);
      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null) {
         actionBar.setTitle(R.string.boxing_app_name);
      }
      bar.setNavigationOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            onBackPressed();
         }
      });
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.activity_main, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      IBoxingMediaLoader loader;
      switch (id) {
         case R.id.menu_fresco:
            loader = new BoxingFrescoLoader(this);
            break;
         case R.id.menu_glide:
            loader = new BoxingGlideLoader();
            break;
         case R.id.menu_picasso:
            loader = new BoxingPicassoLoader();
            break;
         default:
            loader = new BoxingFrescoLoader(this);
            break;
      }
      BoxingMediaLoader.getInstance().init(loader);
      return super.onOptionsItemSelected(item);
   }
}
