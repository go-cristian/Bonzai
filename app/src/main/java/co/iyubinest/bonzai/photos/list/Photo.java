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

import android.os.Parcel;
import android.os.Parcelable;

class Photo implements Parcelable {
  private final String url;

  public Photo(String url) {
    this.url = url;
  }

  public String url() {
    return url;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Photo photo = (Photo) o;

    return url != null ? url.equals(photo.url) : photo.url == null;
  }

  @Override public int hashCode() {
    return url != null ? url.hashCode() : 0;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.url);
  }

  protected Photo(Parcel in) {
    this.url = in.readString();
  }

  public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
    @Override public Photo createFromParcel(Parcel source) {
      return new Photo(source);
    }

    @Override public Photo[] newArray(int size) {
      return new Photo[size];
    }
  };
}
