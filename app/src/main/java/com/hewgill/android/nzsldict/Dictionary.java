package com.hewgill.android.nzsldict;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.security.MessageDigest;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Dictionary {

    private static final Integer EXACT_PRIMARY_MATCH_WEIGHTING = 100;
    private static final Integer CONTAINS_PRIMARY_MATCH_WEIGHTING = 80;
    private static final Integer EXACT_SECONDARY_MATCH_WEIGHTING = 70;
    private static final Integer CONTAINS_SECONDARY_MATCH_WEIGHTING = 60;

    public static class SignLocations {
        private static Map<String, String> locations = new HashMap<String, String>();
        static {
            locations.put("in front of body", "location_1_1_in_front_of_body");
            //"palm",
            locations.put("chest", "location_4_12_chest");
            locations.put("lower head", "location_3_9_lower_head");
            locations.put("fingers/thumb", "location_6_20_fingers_thumb");
            locations.put("in front of face", "location_2_2_in_front_of_face");
            locations.put("top of head", "location_3_4_top_of_head");
            locations.put("head", "location_3_3_head");
            locations.put("cheek", "location_3_8_cheek");
            locations.put("nose", "location_3_6_nose");
            locations.put("back of hand", "location_6_22_back_of_hand");
            locations.put("neck/throat", "location_4_10_neck_throat");
            locations.put("shoulders", "location_4_11_shoulders");
            //"blades",
            locations.put("abdomen", "location_4_13_abdomen");
            locations.put("eyes", "location_3_5_eyes");
            locations.put("ear", "location_3_7_ear");
            locations.put("hips/pelvis/groin", "location_4_14_hips_pelvis_groin");
            locations.put("wrist", "location_6_19_wrist");
            locations.put("lower arm", "location_5_18_lower_arm");
            locations.put("upper arm", "location_5_16_upper_arm");
            locations.put("elbow", "location_5_17_elbow");
            locations.put("upper leg", "location_4_15_upper_leg");
        }

        static Map<String, String> all() {
            return locations;
        }
    }

    public class DictItem implements Serializable, Comparable {
        public String gloss;
        public String minor;
        public String maori;
        public String image;
        public String video;
        public String handshape;
        public String location;

        public DictItem() {
            gloss = null;
            minor = null;
            maori = null;
            image = null;
            video = null;
            handshape = null;
            location = null;
        }

        public DictItem(String gloss, String minor, String maori, String image, String video, String handshape, String location) {
            this.gloss = gloss;
            this.minor = minor;
            this.maori = maori;
            this.image = image;
            this.video = video;
            this.handshape = handshape;
            this.location = location;
        }

        public String imagePath() {
            if (image.isEmpty()) return "";
            return "images/signs/" + image.toLowerCase();
        }

        public String handshapeImage() {
            return "handshape_" + handshape.replaceAll("[.]", "_");
        }

        public String locationImage() {
            String r = SignLocations.all().get(location);
            if (r == null) {
                r = "";
            }
            return r;
        }

        public String getVideo() {
            Context ctx = Dictionary.this.context;
            String deprecatedAssetServer = ctx.getString(R.string.deprecated_asset_server_origin);
            String assetServer = ctx.getString(R.string.asset_server_origin);
            return video.replaceFirst(deprecatedAssetServer, assetServer);
        }

        public String toString() {
            return gloss + "|" + minor + "|" + maori;
        }

        
        @Override
        public int compareTo(@NonNull Object o) {
            DictItem other = (DictItem) o;
            return this.image.compareTo(other.image);
        }
    }

    private ArrayList<DictItem> words = new ArrayList();
    protected Context context;

    public Dictionary(Context context) {
        this.context = context;
        InputStream db = null;
        try {
            db = context.getAssets().open("db/nzsl.dat");
            BufferedReader f = new BufferedReader(new InputStreamReader(db));
            while (true) {
                String s = f.readLine();
                if (s == null) {
                    break;
                }
                String[] a = s.split("\t");
                words.add(new DictItem(a[0], a[1], a[2], a[3], a[4], a[5], a[6]));
            }
        } catch (IOException x) {
            Log.d("dictionary", "exception reading from word list " + x.getMessage());
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (IOException x) {
                    // ignore
                }
            }
        }
        Collections.sort(words, new Comparator() {
            public int compare(Object o1, Object o2) {
                DictItem d1 = (DictItem) o1;
                DictItem d2 = (DictItem) o2;
                String s1 = skip_parens(d1.gloss);
                String s2 = skip_parens(d2.gloss);
                return s1.compareToIgnoreCase(s2);
            }
        });
    }

    public List<DictItem> getWords() {
        return words;
    }

    private static String normalise(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        StringBuilder r = new StringBuilder(s.length());
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c < 127) {
                r.append(c);
            }
        }
        return r.toString().toLowerCase();
    }

    public ArrayList<DictItem> getWords(String target) {
        // Create a sorted set for each type of match. This provides "buckets" to place results
        // in. Because it is a sorted set, uniqueness is guaranteed, and results should also be
        // naturally ordered.
        SortedSet<DictItem> exactPrimaryMatches = new TreeSet<>();
        SortedSet<DictItem> startsWithPrimaryMatches = new TreeSet<>();
        SortedSet<DictItem> containsPrimaryMatches = new TreeSet<>();
        SortedSet<DictItem> exactSecondaryMatches = new TreeSet<>();
        SortedSet<DictItem> containsSecondaryMatches = new TreeSet<>();

        String term = normalise(target);
        for (DictItem d : words) {
            String gloss = normalise(d.gloss);
            String minor = normalise(d.minor);
            String maori = normalise(d.maori);

            if (gloss.equals(term) || maori.equals(term)) exactPrimaryMatches.add(d);
            if (gloss.startsWith(term) || maori.startsWith(term)) startsWithPrimaryMatches.add(d);
            else if (gloss.contains(term) || maori.contains(term)) containsPrimaryMatches.add(d);
            else if (minor.equals(term)) exactSecondaryMatches.add(d);
            else if (minor.contains(term)) containsSecondaryMatches.add(d);
        }

        // Create an ArrayList of the size of all the results, and add each of the sets to it.
        // This returns a data structure ordered first by the priority of the result type, then
        // natural ordering within each set, e.g.:
        // Given: [exact: [e1, e2, e3], contains: [c1, c2, c2], exactSecondary: [es1, es2, es3]
        // Then: results = [e1, e2, e3, c1, c2, c3, es1, es2, es3]
        int resultCount = exactPrimaryMatches.size() +
                startsWithPrimaryMatches.size() +
                containsPrimaryMatches.size() +
                exactSecondaryMatches.size() +
                containsSecondaryMatches.size();

        LinkedHashSet<DictItem> results = new LinkedHashSet<>(resultCount);
        results.addAll(exactPrimaryMatches);
        results.addAll(startsWithPrimaryMatches);
        results.addAll(containsPrimaryMatches);
        results.addAll(exactSecondaryMatches);
        results.addAll(containsSecondaryMatches);

        return new ArrayList<>(results);
    }

    public List<DictItem> getWordsByHandshape(String handshape, String location) {
        List<DictItem> r = new ArrayList<DictItem>(words.size());
        for (DictItem d : words) {
            if ((handshape == null || handshape.length() == 0 || d.handshape.equals(handshape))
                    && (location == null || location.length() == 0 || d.location.equals(location))) {
                r.add(d);
            }
        }
        return r;
    }

    static Set taboo = new HashSet(Arrays.asList(new String[]{
            "(vaginal) discharge",
            "abortion",
            "abuse",
            "anus",
            "asshole",
            "attracted",
            "balls",
            "been to",
            "bisexual",
            "bitch",
            "blow job",
            "breech (birth)",
            "bugger",
            "bullshit",
            "cervical smear",
            "cervix",
            "circumcise",
            "cold (behaviour)",
            "come",
            "condom",
            "contraction (labour)",
            "cunnilingus",
            "cunt",
            "damn",
            "defecate, faeces",
            "dickhead",
            "dilate (cervix)",
            "ejaculate, sperm",
            "episiotomy",
            "erection",
            "fart",
            "foreplay",
            "gay",
            "gender",
            "get intimate",
            "get stuffed",
            "hard-on",
            "have sex",
            "heterosexual",
            "homo",
            "horny",
            "hysterectomy",
            "intercourse",
            "labour pains",
            "lesbian",
            "lose one's virginity",
            "love bite",
            "lust",
            "masturbate (female)",
            "masturbate, wanker",
            "miscarriage",
            "orgasm",
            "ovaries",
            "penis",
            "period",
            "period pains",
            "promiscuous",
            "prostitute",
            "rape",
            "sanitary pad",
            "sex",
            "sexual abuse",
            "shit",
            "smooch",
            "sperm",
            "strip",
            "suicide",
            "tampon",
            "testicles",
            "vagina",
            "virgin",
    }));

    public DictItem getWordOfTheDay() {
        String buf = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        try {
            byte[] digest = MessageDigest.getInstance("SHA-1").digest(buf.getBytes());
            int r = (((digest[0] & 0xff) << 8) | (digest[1] & 0xff)) % words.size();
            while (taboo.contains(words.get(r).gloss)) {
                r += 1;
            }
            return words.get(r);
        } catch (java.security.NoSuchAlgorithmException x) {
            // shouldn't happen, but return something sensible if it does
            return words.get(new java.util.Random().nextInt(words.size()));
        }
    }

    private String skip_parens(String s) {
        if (s.charAt(0) == '(') {
            int i = s.indexOf(") ");
            if (i > 0) {
                s = s.substring(i + 2);
            } else {
                Log.d("Dictionary", "expected to find closing parenthesis: " + s);
            }
        }
        return s;
    }
}
