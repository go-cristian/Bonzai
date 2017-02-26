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
package co.iyubinest.bonzai;

import co.iyubinest.bonzai.photos.list.FlickrPhotos;
import co.iyubinest.bonzai.retrofit.AppRetrofit;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit2.Retrofit;

@Module public class AppModule {
  private final App app;

  public AppModule(App app) {
    this.app = app;
  }

  @Provides @Singleton public Retrofit retrofit() {
    return AppRetrofit.build(FlickrPhotos.URL);
  }
}
