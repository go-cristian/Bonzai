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
package co.iyubinest.bonzai.photos.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.iyubinest.bonzai.BaseActivity;
import co.iyubinest.bonzai.R;
import java.util.List;
import javax.inject.Inject;

public class PhotoListActivity extends BaseActivity {

  private static final String DEFAULT_TAG = "panda";
  @Inject Photos photos;
  @BindView(android.R.id.content) View contentView;
  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.retry_button) View retryView;
  @BindView(R.id.loading) View loadingView;
  @BindView(R.id.photo_list_content) PhotoListWidget photosView;
  private String tag;
  SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
    @Override public boolean onQueryTextSubmit(String query) {
      return false;
    }

    @Override public boolean onQueryTextChange(String tag) {
      queryBy(tag);
      return false;
    }
  };

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.photo_list_activity);
    ButterKnife.bind(this);
    setSupportActionBar(toolbar);
    appComponent().photoListComponent(new PhotoListModule()).inject(this);
    queryBy(DEFAULT_TAG);
    photosView.onPhotoSelected(this::showDetail);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.photo_list_search_menu, menu);
    MenuItem searchMenuItem = menu.findItem(R.id.action_search);
    SearchView mSearchView = (SearchView) searchMenuItem.getActionView();
    mSearchView.setOnQueryTextListener(listener);
    return true;
  }

  @OnClick(R.id.retry_button) public void retry() {
    queryBy(tag);
  }

  private void queryBy(String tag) {
    this.tag = tag;
    show(loadingView);
    photos.queryBy(tag).subscribe(this::showPhotos, this::showError);
  }

  private void showPhotos(List<Photo> photoList) {
    show(photosView);
    photosView.load(photoList);
  }

  private void showError(Throwable throwable) {
    show(retryView);
    Snackbar.make(contentView, R.string.photo_list_error, Snackbar.LENGTH_LONG).show();
  }

  private void show(View view) {
    retryView.setVisibility(View.INVISIBLE);
    photosView.setVisibility(View.INVISIBLE);
    loadingView.setVisibility(View.INVISIBLE);
    view.setVisibility(View.VISIBLE);
  }

  private void showDetail(Photo photo, View view) {
    startActivity(PhotoDetailActivity.getIntent(this, photo, view));
  }
}
