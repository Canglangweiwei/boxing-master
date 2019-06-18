package com.bilibili.boxing.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bilibili.boxing.AbsBoxingActivity;
import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.BoxingMediaLoader;
import com.bilibili.boxing.demo.R;
import com.bilibili.boxing.model.BoxingManager;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.config.BoxingCropOption;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing.model.entity.impl.ImageMedia;
import com.bilibili.boxing.utils.BoxingFileHelper;
import com.bilibili.boxing.utils.ImageCompressor;
import com.bilibili.boxing_impl.ui.BoxingActivity;
import com.bilibili.boxing_impl.ui.BoxingBottomSheetActivity;
import com.bilibili.boxing_impl.view.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A demo to show how to use {@link AbsBoxingActivity} and all the functions.
 *
 * @author ChenSL
 */
@SuppressWarnings("ALL")
public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

   private static final int REQUEST_CODE = 1024;
   private static final int COMPRESS_REQUEST_CODE = 2048;

   /// 最大选组数量
   private static final int MAX_SELECT_COUNT = 9;

   private RecyclerView mRecyclerView;
   private MediaResultAdapter mAdapter;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_first);
      createToolbar();

      findViewById(R.id.single_image_btn).setOnClickListener(this);
      findViewById(R.id.single_image_btn_crop_btn).setOnClickListener(this);
      findViewById(R.id.multi_image_btn).setOnClickListener(this);
      findViewById(R.id.video_btn).setOnClickListener(this);
      findViewById(R.id.outside_bs_btn).setOnClickListener(this);

      mRecyclerView = findViewById(R.id.media_recycle_view);
      mAdapter = new MediaResultAdapter();
      mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
      mRecyclerView.setAdapter(mAdapter);
      mRecyclerView.addItemDecoration(new SpacesItemDecoration(8));
      mRecyclerView.setOnClickListener(this);
   }

   private void createToolbar() {
      Toolbar bar = findViewById(R.id.nav_top_bar);
      setSupportActionBar(bar);
      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null) {
         actionBar.setTitle(R.string.first_demo_title);
      }
      bar.setNavigationOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            onBackPressed();
         }
      });
   }

   @Override
   public void onClick(View v) {
      int id = v.getId();
      switch (id) {
         case R.id.single_image_btn:// 图片单选
            BoxingConfig singleImgConfig = new BoxingConfig(BoxingConfig.Mode.SINGLE_IMG)
                .withMediaPlaceHolderRes(R.drawable.ic_boxing_default_image);

            Boxing.of(singleImgConfig)
                .withIntent(this, BoxingActivity.class)
                .start(this, COMPRESS_REQUEST_CODE);
            break;
         case R.id.single_image_btn_crop_btn:// 图片单选加裁剪
            String cachePath = BoxingFileHelper.getCacheDir(this);
            if (TextUtils.isEmpty(cachePath)) {
               Toast.makeText(getApplicationContext(), R.string.boxing_storage_deny, Toast.LENGTH_SHORT).show();
               return;
            }
            Uri destUri = new Uri.Builder()
                .scheme("file")
                .appendPath(cachePath)
                .appendPath(String.format(Locale.US, "%s.png", System.currentTimeMillis()))
                .build();

            BoxingConfig singleCropImgConfig = new BoxingConfig(BoxingConfig.Mode.SINGLE_IMG)
                .withCropOption(new BoxingCropOption(destUri))
                .withMediaPlaceHolderRes(R.drawable.ic_boxing_default_image);

            Boxing.of(singleCropImgConfig)
                .withIntent(this, BoxingActivity.class)
                .start(this, REQUEST_CODE);
            break;
         case R.id.multi_image_btn:// 图片多选
            BoxingConfig config = new BoxingConfig(BoxingConfig.Mode.MULTI_IMG)
                .needCamera(R.drawable.ic_boxing_camera_white)
                .withMaxCount(MAX_SELECT_COUNT)
                .needGif();

            Boxing.of(config)
                .withIntent(this, BoxingActivity.class)
                .start(this, REQUEST_CODE);
            break;
         case R.id.video_btn:// 视频单选
            BoxingConfig videoConfig = new BoxingConfig(BoxingConfig.Mode.VIDEO)
                .withVideoDurationRes(R.drawable.ic_boxing_play);

            Boxing.of(videoConfig)
                .withIntent(this, BoxingActivity.class)
                .start(this, REQUEST_CODE);
            break;
         case R.id.outside_bs_btn:// Bottom Sheet
            BoxingConfig bsConfig = new BoxingConfig(BoxingConfig.Mode.SINGLE_IMG);
            Boxing.of(bsConfig)
                .withIntent(this, BoxingBottomSheetActivity.class)
                .start(this, REQUEST_CODE);
            break;
         default:
            break;
      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (resultCode == RESULT_OK) {
         if (mRecyclerView == null || mAdapter == null) {
            return;
         }
         mRecyclerView.setVisibility(View.VISIBLE);
         final ArrayList<BaseMedia> medias = Boxing.getResult(data);
         if (medias == null || medias.size() == 0) {
            return;
         }
         switch (requestCode) {
            case REQUEST_CODE:            // 直接选择
            {
               mAdapter.setList(medias);
            }
            break;
            case COMPRESS_REQUEST_CODE:   // 选择并裁剪
            {
               final List<BaseMedia> imageMedias = new ArrayList<>(1);
               BaseMedia baseMedia = medias.get(0);
               if (!(baseMedia instanceof ImageMedia)) {
                  return;
               }
               final ImageMedia imageMedia = (ImageMedia) baseMedia;
               // the compress task may need time
               if (imageMedia.compress(new ImageCompressor(this))) {
                  imageMedia.removeExif();
                  imageMedias.add(imageMedia);
                  mAdapter.setList(imageMedias);
               }
            }
            break;
         }
      }
   }

   private class MediaResultAdapter extends RecyclerView.Adapter {

      private ArrayList<BaseMedia> mList;

      public MediaResultAdapter() {
         mList = new ArrayList<>();
      }

      public void setList(List<BaseMedia> list) {
         if (list == null) {
            return;
         }
         mList.clear();
         mList.addAll(list);
         notifyDataSetChanged();
      }

      public List<BaseMedia> getMedias() {
         if (mList == null || mList.size() <= 0 || !(mList.get(0) instanceof ImageMedia)) {
            return null;
         }
         return mList;
      }

      @Override
      public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_boxing_simple_media_item, parent, false);
         int height = parent.getMeasuredHeight() / 4;
         view.setMinimumHeight(height);
         return new MediaViewHolder(view);
      }

      @Override
      public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
         if (holder instanceof MediaViewHolder) {
            MediaViewHolder mediaViewHolder = (MediaViewHolder) holder;
            mediaViewHolder.mImageView.setImageResource(BoxingManager.getInstance().getBoxingConfig().getMediaPlaceHolderRes());
            BaseMedia media = mList.get(position);
            String path;
            if (media instanceof ImageMedia) {
               path = ((ImageMedia) media).getThumbnailPath();
            } else {
               path = media.getPath();
            }
            BoxingMediaLoader.getInstance()
                .displayThumbnail(mediaViewHolder.mImageView, path, 150, 150);
         }
      }

      @Override
      public int getItemCount() {
         return mList == null ? 0 : mList.size();
      }
   }

   private class MediaViewHolder extends RecyclerView.ViewHolder {

      private ImageView mImageView;

      public MediaViewHolder(View itemView) {
         super(itemView);
         mImageView = itemView.findViewById(R.id.media_item);
      }
   }
}
