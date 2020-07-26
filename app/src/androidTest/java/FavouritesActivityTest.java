import android.content.Intent;

import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.intent.Intents;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.hewgill.android.nzsldict.DictItem;
import com.hewgill.android.nzsldict.Dictionary;
import com.hewgill.android.nzsldict.FavouritesActivity;
import com.hewgill.android.nzsldict.FavouritesRepository;
import com.hewgill.android.nzsldict.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FavouritesActivityTest {
    private static FavouritesRepository repo;
    private static Dictionary dict;

    public static Matcher withDictItem(final Matcher itemMatcher) {
        return new TypeSafeMatcher<DictItem>() {
            @Override
            public boolean matchesSafely(DictItem item) {
                return itemMatcher.matches(item.gloss);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("");
            }
        };
    }

    @BeforeClass
    public static void initRepos() {
        repo = new FavouritesRepository(InstrumentationRegistry.getTargetContext());
        dict = new Dictionary(InstrumentationRegistry.getTargetContext());
    }


    @Rule
    public ActivityTestRule activityTestRule =
            new ActivityTestRule<>(FavouritesActivity.class, true, false);

    @Before
    public void initIntents() {
        Intents.init();
    }

    @After
    public void releaseIntents() { Intents.release(); }

    @Before
    public void resetFavourites() {
        for (DictItem item : repo.all()) repo.remove(item);

    }

    @Test
    public void activity__emptyDisplay() {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.empty_favourites_webview))
                .check(matches(withText("You haven't added anything to your favourites yet!")));
    }

    @Test
    public void activity__listsFavourites() {
        // Add some favourites
        DictItem word = dict.getWords("Hello").get(0);
        repo.add(word);
        activityTestRule.launchActivity(new Intent());
        onData(instanceOf(DictItem.class))
                .atPosition(0)
                .onChildView(withId(R.id.item_gloss))
                .check(matches(withText(word.gloss)));
    }

    @Test
    public void activity__downloadsFavourites() {
        // Add some favourites
        DictItem word = dict.getWords("Auckland").get(0);
        repo.add(word);
        activityTestRule.launchActivity(new Intent());

        onView(withId(R.id.action_favourites_download)).perform(click());

        // Accept the dialog prompt
        onView(withId(android.R.id.button1)).perform(click());

        try {
            Thread.sleep(3000); // Give the files time to download
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onData(instanceOf(DictItem.class))
                .atPosition(0)
                .onChildView(withId(R.id.action_favourite_item_download))
                .check(doesNotExist());

    }

    @Test
    public void activity__clearsFavourites() {
        // Add some favourites
        DictItem word = dict.getWords("Hello").get(0);
        repo.add(word);
        activityTestRule.launchActivity(new Intent());

        onView(withId(R.id.action_favourites_clear)).perform(click());

        // Accept the dialog prompt
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.empty_favourites_webview))
                .check(matches(withText("You haven't added anything to your favourites yet!")));
    }

    @Test
    public void activity__downloadSingleFavourite() {
        // Add some favourites
        DictItem word = dict.getWords("Hello").get(0);
        repo.add(word);
        activityTestRule.launchActivity(new Intent());
        onData(instanceOf(DictItem.class))
                .atPosition(0)
                .onChildView(withId(R.id.action_favourite_item_download))
                .perform(click());

        try {
            Thread.sleep(3000); // Give the files time to download
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onData(instanceOf(DictItem.class))
                .atPosition(0)
                .onChildView(withId(R.id.action_favourite_item_download))
                .check(doesNotExist());
    }

    @Test
    public void activity__favouriteListItemNavigation() {
        DictItem word = dict.getWords("Hello").get(0);
        repo.add(word);
        activityTestRule.launchActivity(new Intent());
        onData(instanceOf(DictItem.class))
                .atPosition(0)
                .perform(click());

        intending(allOf(
                hasComponent(hasShortClassName(".WordActivity")),
                hasExtra("item", word)));
    }
}
