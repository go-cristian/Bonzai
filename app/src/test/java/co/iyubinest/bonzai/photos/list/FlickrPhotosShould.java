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

import co.iyubinest.bonzai.photos.Photo;
import co.iyubinest.bonzai.retrofit.AppRetrofit;
import io.reactivex.subscribers.TestSubscriber;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Retrofit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FlickrPhotosShould {

  private FlickrPhotos photos;
  private MockWebServer server;
  private Retrofit retrofit;
  private MockResponse error = new MockResponse().setHttp2ErrorCode(500);
  private TestSubscriber subscriber = new TestSubscriber();
  private MockResponse response = new MockResponse().setBody(fromFile("page.json"));
  private Photo firstPhoto =
    new Photo("https://farm1.staticflickr.com/770/33116416105_0563b72b0e_m.jpg");
  private String tag = "panda";

  private static String fromFile(String name) {
    try {
      InputStream resource = FlickrPhotosShould.class.getClassLoader().getResourceAsStream(name);
      BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
      StringBuilder result = new StringBuilder();
      String partial = reader.readLine();
      while (partial != null) {
        result.append(partial);
        partial = reader.readLine();
      }
      return result.toString();
    } catch (Exception ignored) {
      throw new IllegalArgumentException("File not found");
    }
  }

  @Before
  public void setup() throws Exception {
    server = new MockWebServer();
    retrofit = AppRetrofit.build(server.url("/").toString());
    photos = new FlickrPhotos(retrofit);
  }

  @After
  public void tearDown() throws Exception {
    server.shutdown();
  }

  @Test
  public void failOnError() throws Exception {
    server.enqueue(error);
    photos.queryBy(tag).subscribe(subscriber);
    subscriber.assertError(Exception.class);
  }

  @Test
  public void success() throws Exception {
    server.enqueue(response);
    photos.queryBy(tag).subscribe(subscriber);
    List<Photo> events = (List<Photo>) ((ArrayList) subscriber.getEvents().get(0)).get(0);
    assertThat(events.size(), is(100));
    assertThat(events.get(0), is(firstPhoto));
  }
}
