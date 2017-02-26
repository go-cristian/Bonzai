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

import io.reactivex.Flowable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class FlickrPhotos implements Photos {

  public static final String URL = "https://api.flickr.com/";
  private static final String API_KEY = "e4a73905b6b4a04be6a5c977d0005936";
  private static final String ENDPOINT_FORMAT =
    "services/rest/?method=flickr.photos.search&format=json&nojsoncallback=1";
  private FlickrService service;

  public FlickrPhotos(Retrofit retrofit) {
    service = retrofit.create(FlickrService.class);
  }

  @Override
  public Flowable<List<Photo>> queryBy(String tags) {
    return service.pictures(API_KEY, tags).flatMap(this::map);
  }

  private Flowable<List<Photo>> map(FlickrResponse pictures) {
    return Flowable.defer(() -> {
      List<Photo> all = new ArrayList<>(pictures.photos.photo.size());
      for (FlickrPictureResponse picture : pictures.photos.photo) {
        all.add(map(picture));
      }
      return Flowable.just(all);
    });
  }

  private Photo map(FlickrPictureResponse picture) {
    return new Photo(picture.url());
  }

  private interface FlickrService {

    @GET(ENDPOINT_FORMAT)
    Flowable<FlickrResponse> pictures(@Query("api_key") String apiKey, @Query("tags") String tag
    );
  }

  private static class FlickrResponse {

    FlickrPhotosResponse photos;
  }

  private static class FlickrPhotosResponse {

    List<FlickrPictureResponse> photo;
  }

  private static class FlickrPictureResponse {

    private static final String URL_FORMAT = "https://farm%s.staticflickr.com/%s/%s_%s_m.jpg";
    String farm;
    String server;
    String id;
    String secret;

    public String url() {
      return String.format(Locale.getDefault(), URL_FORMAT, farm, server, id, secret);
    }
  }
}
