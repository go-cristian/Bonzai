/**
 * Copyright (C) 2017 Cristian Gomez Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.iyubinest.bonzai.photos.detail;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.iyubinest.bonzai.BaseActivity;
import co.iyubinest.bonzai.R;
import co.iyubinest.bonzai.photos.Photo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.Timer;
import java.util.TimerTask;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoDetailActivity extends BaseActivity {

  public static final String ORIENTATION = "orientation";
  public static final String LEFT = "left";
  public static final String TOP = "top";
  public static final String WIDTH = "width";
  public static final String HEIGHT = "height";
  private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
  private static final int ANIM_DURATION = 500;
  private static final String PHOTO_KEY = "entry";
  private static float sAnimatorScale = 1;
  @BindView(R.id.preview_root)
  View rootView;
  @BindView(R.id.photo_detail_image)
  ImageView previewView;
  @BindView(R.id.photo_detail_toolbar)
  Toolbar toolbarView;
  private int originalOrientation;
  private int leftDelta;
  private int topDelta;
  private float widthScale;
  private float heightScale;

  public static Intent getIntent(Context context, Photo photo, View view) {
    int orientation = context.getResources().getConfiguration().orientation;
    Intent intent = new Intent(context, PhotoDetailActivity.class);
    Bundle bundle = new Bundle();
    bundle.putParcelable(PHOTO_KEY, photo);
    int[] screenLocation = new int[2];
    view.getLocationOnScreen(screenLocation);
    intent.
      putExtra(ORIENTATION, orientation).
      putExtra(LEFT, screenLocation[0]).
      putExtra(TOP, screenLocation[1]).
      putExtra(WIDTH, view.getWidth()).
      putExtra(HEIGHT, view.getHeight()).putExtras(bundle);
    return intent;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.photo_detail_activity);
    ButterKnife.bind(this);
    configureActionBar();
    showImage(photo().url());
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  private void configureActionBar() {
    setSupportActionBar(toolbarView);
    setTitle(null);
    toolbarView.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
    toolbarView.setNavigationOnClickListener(v -> onBackPressed());
  }

  public void showImage(String imageUrl) {
    Picasso.with(this).load(imageUrl).into(previewView, new Callback() {
      @Override
      public void onSuccess() {
        prepareAnimation();
      }

      @Override
      public void onError() {
      }
    });
  }

  private Photo photo() {
    return getIntent().getExtras().getParcelable(PHOTO_KEY);
  }

  @Override
  public void onBackPressed() {
    runExitAnimation(this::finish);
  }

  private void prepareAnimation() {
    Bundle bundle = getIntent().getExtras();
    final int thumbnailTop = bundle.getInt(TOP);
    final int thumbnailLeft = bundle.getInt(LEFT);
    final int thumbnailWidth = bundle.getInt(WIDTH);
    final int thumbnailHeight = bundle.getInt(HEIGHT);
    originalOrientation = bundle.getInt(ORIENTATION);
    new PhotoViewAttacher(previewView);
    ViewTreeObserver observer = previewView.getViewTreeObserver();
    observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      @Override
      public boolean onPreDraw() {
        previewView.getViewTreeObserver().removeOnPreDrawListener(this);
        int[] screenLocation = new int[2];
        previewView.getLocationOnScreen(screenLocation);
        leftDelta = thumbnailLeft - screenLocation[0];
        topDelta = thumbnailTop - screenLocation[1];
        widthScale = (float) thumbnailWidth / previewView.getWidth();
        heightScale = (float) thumbnailHeight / previewView.getHeight();
        runEnterAnimation();
        return true;
      }
    });
  }

  public void runExitAnimation(final Runnable endAction) {
    final long duration = (long) (ANIM_DURATION * sAnimatorScale);
    final boolean fadeOut;
    if (getResources().getConfiguration().orientation != originalOrientation) {
      previewView.setPivotX(previewView.getWidth() / 2);
      previewView.setPivotY(previewView.getHeight() / 2);
      leftDelta = 0;
      topDelta = 0;
      fadeOut = true;
    } else {
      fadeOut = false;
    }
    previewView.animate().setDuration(duration).
      scaleX(widthScale).scaleY(heightScale).
      translationX(leftDelta).translationY(topDelta);
    if (fadeOut) {
      previewView.animate().alpha(0);
    }
    ObjectAnimator bgAnim = ObjectAnimator.ofInt(rootView, "alpha", 0);
    bgAnim.setDuration(duration);
    bgAnim.start();
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        endAction.run();
      }
    }, duration);
  }

  public void runEnterAnimation() {
    final long duration = (long) (ANIM_DURATION * sAnimatorScale);
    previewView.setPivotX(0);
    previewView.setPivotY(0);
    previewView.setScaleX(widthScale);
    previewView.setScaleY(heightScale);
    previewView.setTranslationX(leftDelta);
    previewView.setTranslationY(topDelta);
    previewView.animate().setDuration(duration).
      scaleX(1).scaleY(1).
      translationX(0).translationY(0).
      setInterpolator(sDecelerator);
    ObjectAnimator bgAnim = ObjectAnimator.ofInt(rootView, "alpha", 0, 255);
    bgAnim.setDuration(duration);
    bgAnim.start();
  }

  @Override
  public void finish() {
    super.finish();
    overridePendingTransition(0, 0);
  }
}
