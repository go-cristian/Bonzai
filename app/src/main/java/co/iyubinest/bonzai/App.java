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

import android.app.Application;
import android.support.annotation.VisibleForTesting;
import com.facebook.stetho.Stetho;

public class App extends Application {

  private AppComponent component;

  @Override
  public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);
    component = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
  }

  public AppComponent component() {
    return component;
  }

  @VisibleForTesting
  public void setComponent(AppComponent component) {
    this.component = component;
  }
}
