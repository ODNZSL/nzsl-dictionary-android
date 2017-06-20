import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.hewgill.android.nzsldict.Dictionary;
import com.hewgill.android.nzsldict.Dictionary.DictItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class DictionaryAndroidUnitTest {
    private Dictionary mDictionary;
    private List<DictItem> mResults;

    @Before
    public void createDictionary() {
        mDictionary = new Dictionary(getInstrumentation().getTargetContext());
    }

    @After
    public void clearResults() {
        mResults = null;
    }

    @Test
    public void dictionary_getWordsContainsWords() {
        assertNotEquals(mDictionary.getWords().size(), 0);
    }

    @Test
    public void dictionary_getWordsExactMatchMainGloss() {
        mResults = mDictionary.getWords("Book");
        assertEquals(mResults.get(0).gloss, "book");
    }

    @Test
    public void dictionary_getWordsStartsWithMatchMainGlass() {
        mResults = mDictionary.getWords("sunglasse");
        assertEquals(mResults.get(0).gloss, "sunglasses");
    }


    @Test
    public void dictionary_getWordsContainsMatchMainGlass() {
        mResults = mDictionary.getWords("las");
        assertEquals(mResults.get(0).gloss, "lasagna");
    }

    @Test
    public void dictionary_getWordsExactMatchMaoriGloss() {
        mResults = mDictionary.getWords("ora");
        assertEquals(mResults.get(0).gloss, "alive, live, survive");
    }

    @Test
    public void dictionary_getWordsContainsMatchMaoriGloss() {
        mResults = mDictionary.getWords("oranga tina");
        assertEquals(mResults.get(0).gloss, "medical");
    }

    @Test
    public void dictionary_getWordsStartsWithMatchMaoriGloss() {
        mResults = mDictionary.getWords("Aorang");
        assertEquals(mResults.get(0).gloss, "Feilding");
    }

    @Test
    public void dictionary_getWordsExactSecondaryGloss() {
        mResults = mDictionary.getWords("nought");
        assertEquals(mResults.get(0).gloss, "zero");
    }

    @Test
    public void dictionary_getWordsContainsSecondaryGloss() {
        mResults = mDictionary.getWords("avoid, keep");
        assertEquals(mResults.get(0).gloss, "want nothing to do with");
    }

    @Test
    public void dictionary_getWordsRemovesDuplicatesFromMatchGroups() {
        mResults = mDictionary.getWords("Auckland");
        List<DictItem> resultsThatAreAuckland = new ArrayList<>();

        for (DictItem di : mResults) {
            if (di.gloss.equals("Auckland")) resultsThatAreAuckland.add(di);
        }

        // The term 'Auckland' matches both an exact match and has few enough results it appears
        // as a 'starts with'. If duplicate detection is working, the sign should appear only once per sign,
        // with three unique signs for Auckland
        assertEquals(resultsThatAreAuckland.size(), 3);
    }
}
