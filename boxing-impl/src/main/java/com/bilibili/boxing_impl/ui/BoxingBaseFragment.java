package com.bilibili.boxing_impl.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by ChenSL on 2017/4/5.
 */
public class BoxingBaseFragment extends Fragment {

   private boolean mNeedPendingUserVisibileHint;
   private boolean mLastUserVisibileHint;

   @Override
   public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      if (mNeedPendingUserVisibileHint) {
         setUserVisibleCompat(mLastUserVisibileHint);
      }
   }

   @Override
   public void setUserVisibleHint(boolean isVisibleToUser) {
      super.setUserVisibleHint(isVisibleToUser);
      if (getActivity() == null) {
         mNeedPendingUserVisibileHint = true;
         mLastUserVisibileHint = isVisibleToUser;
      } else {
         setUserVisibleCompat(isVisibleToUser);
      }
   }

   void setUserVisibleCompat(boolean userVisibleCompat) {
   }
}
