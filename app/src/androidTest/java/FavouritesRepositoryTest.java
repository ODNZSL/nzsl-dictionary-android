import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.hewgill.android.nzsldict.Dictionary;
import com.hewgill.android.nzsldict.DictItem;
import com.hewgill.android.nzsldict.FavouritesRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class FavouritesRepositoryTest {
    private FavouritesRepository repo;
    static private Dictionary dict;

    @BeforeClass
    public static void buildDictionary() {
        dict = new Dictionary(getInstrumentation().getTargetContext());
    }

    @Before
    public void createRepository() {
        repo = new FavouritesRepository(getInstrumentation().getTargetContext());
        for (DictItem di : repo.all()) { repo.remove(di); }
    }

    @After
    public void clearResults() {
        repo = null;
    }


    @Test
    public void add__adds_entry() {
        DictItem di = dict.getWords("hello").get(0);
        repo.add(di);
        assert(repo.contains(di));
        assertEquals(1, repo.all().size());
    }

    @Test
    public void add__does_not_add_duplicates() {
        DictItem di = dict.getWords("hello").get(0);
        repo.add(di);
        repo.add(di);
        assertEquals(1, repo.all().size());
    }

    @Test
    public void remove__removes_entry() {
        DictItem di = dict.getWords("hello").get(0);
        repo.add(di);
        repo.remove(di);
        assert(! repo.contains(di));
    }

    @Test
    public void all__lists_entries() {
        DictItem di1 = dict.getWords("hello").get(0);
        DictItem di2 = dict.getWords("world").get(0);
        repo.add(di1);
        repo.add(di2);

        assert(repo.all().get(1).equals(di1));
        assert(repo.all().get(0).equals(di2));
        assertEquals(2, repo.all().size());
    }

    @Test
    public void all__ignores_unknown_entries() {
        DictItem di1 = dict.getWords("hello").get(0);
        DictItem di2 = new DictItem();
        repo.add(di1);
        repo.add(di2);

        assert(repo.all().get(0).equals(di1));
        assertEquals(1, repo.all().size());
    }

    @Test
    public void contains__matches_existing_item() {
        DictItem di1 = dict.getWords("Hello").get(0);
        repo.add(di1);
        assert(repo.contains(di1));
    }

    @Test
    public void contains__ignores_non_existing_item() {
        DictItem di1 = dict.getWords("Hello").get(0);
        assert(! repo.contains(di1));
    }
}
