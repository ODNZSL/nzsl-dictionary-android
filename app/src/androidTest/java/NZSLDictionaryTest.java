import android.content.Intent;
import androidx.test.espresso.web.webdriver.Locator;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.hewgill.android.nzsldict.NZSLDictionary;
import com.hewgill.android.nzsldict.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static androidx.test.espresso.web.webdriver.DriverAtoms.findElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.getText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NZSLDictionaryTest {
    @Rule
    public ActivityTestRule<NZSLDictionary> mActivityRule = new ActivityTestRule<NZSLDictionary>(NZSLDictionary.class, false, false) {
        @Override
        protected void afterActivityLaunched() {
            // Enable JS!
            onWebView(withId(R.id.about_content)).forceJavascriptEnabled();
        }
    };

    @Before
    public void startActivity() {
        mActivityRule.launchActivity(new Intent());
    }

    @Test
    public void test_wordOfTheDay() {
        onView(withId(R.id.building_list_wotd_gloss)).check(matches(isDisplayed()));
    }

    @Test
    public void test_performingSearchShowsList() {
        onView(withId(R.id.building_list_search_box)).perform(typeText("Food"), closeSoftKeyboard());
        onView(withId(android.R.id.list)).check(matches(isDisplayed()));
        onView(withId(R.id.building_list_wotd_gloss)).check(matches(not(isDisplayed())));
    }

    @Test
    public void test_toggleHandshapeSearch() {
        onView(withId(R.id.action_search_mode_handshape)).perform(click());
        onView(withId(R.id.building_list_search_box))
                .check(matches(withText("(handshape search)")))
                .check(matches(not(isEnabled())));

        // TODO JM: Test should also assert that handshape view is present, but I cannot get
        // http://google.github.io/android-testing-support-library/docs/espresso/advanced/#matching-a-view-that-is-a-footerheader-in-a-listview
        // to work
    }


    @Test
    public void test_toggleNormalSearch() {
        onView(withId(R.id.action_search_mode_handshape))
                .perform(click());
        onView(withId(R.id.action_search_mode_keyword))
                .perform(click()); // Switch back
        onView(withId(R.id.building_list_search_box))
                .check(matches(withText("")))
                .check(matches(isEnabled()));

        // TODO JM: Test should also assert that handshape view is present, but I cannot get
        // http://google.github.io/android-testing-support-library/docs/espresso/advanced/#matching-a-view-that-is-a-footerheader-in-a-listview
        // to work
    }

    @Test
    public void test_aboutWebviewRenders() {
        onWebView(withId(R.id.about_content))
                .withElement(findElement(Locator.TAG_NAME, "body"))
                .check(webMatches(getText(), containsString("NZSL Dictionary Official App")));
    }

}
