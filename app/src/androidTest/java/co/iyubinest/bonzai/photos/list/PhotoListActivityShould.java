package co.iyubinest.bonzai.photos.list;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;
import co.iyubinest.bonzai.DaggerRule;
import co.iyubinest.bonzai.R;
import co.iyubinest.bonzai.assertions.RecyclerViewActions;
import co.iyubinest.bonzai.assertions.RecyclerViewAssertions;
import co.iyubinest.bonzai.photos.Photo;
import io.reactivex.Flowable;
import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class PhotoListActivityShould {

  private final static String url =
    "https://developer.android.com/images/brand/Android_Robot_100.png";
  private final static List<Photo> PHOTOS_PAGE_0 = new ArrayList<>();

  static {
    for (int i = 0; i < 100; i++) {
      PHOTOS_PAGE_0.add(new Photo(url));
    }
  }

  @Rule
  public DaggerRule daggerRule = new DaggerRule();
  @Rule
  public ActivityTestRule<PhotoListActivity> rule =
    new ActivityTestRule<>(PhotoListActivity.class, false, false);
  @Mock
  Photos photos;
  private Exception exception = new Exception();

  @Test
  public void showErrorOnPhotoFailure() throws Exception {
    Mockito.when(photos.queryBy(anyString())).thenReturn(Flowable.error(exception));
    rule.launchActivity(new Intent());
    onView(withText(R.string.photo_list_error)).check(matches(isDisplayed()));
    onView(withId(R.id.photo_list_content)).check(matches(not(isDisplayed())));
    onView(withId(R.id.retry_button)).check(matches(isDisplayed()));
  }

  @Test
  public void showListOnPhotoSuccess() throws Exception {
    Mockito.when(photos.queryBy(anyString())).thenReturn(Flowable.just(PHOTOS_PAGE_0));
    rule.launchActivity(new Intent());
    onView(withId(R.id.photo_list_content)).check(matches(isDisplayed()));
    onView(withId(R.id.photo_list_content)).check(new RecyclerViewAssertions(100));
    onView(withId(R.id.retry_button)).check(matches(not(isDisplayed())));
  }

  @Test
  public void showListOnSearch() throws Exception {
    Mockito.when(photos.queryBy(anyString())).thenReturn(Flowable.just(PHOTOS_PAGE_0));
    rule.launchActivity(new Intent());
    onView(withId(R.id.action_search)).perform(click());
    onView(isAssignableFrom(EditText.class)).perform(typeText("cars"), pressImeActionButton());
    onView(withId(R.id.photo_list_content)).check(new RecyclerViewAssertions(100));
  }

  @Test
  public void hideListOnSearchClicked() throws Exception {
    Mockito.when(photos.queryBy(anyString())).thenReturn(Flowable.just(PHOTOS_PAGE_0));
    rule.launchActivity(new Intent());
    onView(withId(R.id.action_search)).perform(click());
    onView(withId(R.id.photo_list_content)).check(matches(not(isDisplayed())));
  }

  @Test
  public void showListWhenBackPressedOnSearch() throws Exception {
    Mockito.when(photos.queryBy(anyString())).thenReturn(Flowable.just(PHOTOS_PAGE_0));
    rule.launchActivity(new Intent());
    onView(withId(R.id.action_search)).perform(click());
    Espresso.pressBack();
    onView(withId(R.id.photo_list_content)).check(matches(isDisplayed()));
  }

  @Test
  public void showListWhenNavigationBackClickedOnSearch() throws Exception {
    Mockito.when(photos.queryBy(anyString())).thenReturn(Flowable.just(PHOTOS_PAGE_0));
    rule.launchActivity(new Intent());
    onView(withId(R.id.action_search)).perform(click());
    ViewInteraction backButton = onView(allOf(
      withContentDescription("Collapse"),
      withParent(withId(R.id.photo_list_toolbar)),
      isDisplayed()
    ));
    backButton.perform(click());
    onView(withId(R.id.photo_list_content)).check(matches(isDisplayed()));
  }

  @Test
  public void showDetailOnItemClicked() throws Exception {
    Mockito.when(photos.queryBy(anyString())).thenReturn(Flowable.just(PHOTOS_PAGE_0));
    rule.launchActivity(new Intent());
    onView(withId(R.id.photo_list_content)).perform(RecyclerViewActions.clickAt(0));
    onView(withId(R.id.photo_detail_image)).check(matches(isDisplayed()));
  }
}
