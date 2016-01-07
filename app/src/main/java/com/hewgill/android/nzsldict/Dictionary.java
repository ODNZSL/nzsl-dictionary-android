package com.hewgill.android.nzsldict;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dictionary {

    public static class DictItem implements Serializable {
        public String gloss;
        public String minor;
        public String maori;
        public String image;
        public String video;
        public String handshape;
        public String location;

        static Map<String, String> Locations = new HashMap<String, String>();
        static {
            Locations.put("in front of body", "location_1_1_in_front_of_body");
            //"palm",
            Locations.put("chest", "location_4_12_chest");
            Locations.put("lower head", "location_3_9_lower_head");
            Locations.put("fingers/thumb", "location_6_20_fingers_thumb");
            Locations.put("in front of face", "location_2_2_in_front_of_face");
            Locations.put("top of head", "location_3_4_top_of_head");
            Locations.put("head", "location_3_3_head");
            Locations.put("cheek", "location_3_8_cheek");
            Locations.put("nose", "location_3_6_nose");
            Locations.put("back of hand", "location_6_22_back_of_hand");
            Locations.put("neck/throat", "location_4_10_neck_throat");
            Locations.put("shoulders", "location_4_11_shoulders");
            //"blades",
            Locations.put("abdomen", "location_4_13_abdomen");
            Locations.put("eyes", "location_3_5_eyes");
            Locations.put("ear", "location_3_7_ear");
            Locations.put("hips/pelvis/groin", "location_4_14_hips_pelvis_groin");
            Locations.put("wrist", "location_6_19_wrist");
            Locations.put("lower arm", "location_5_18_lower_arm");
            Locations.put("upper arm", "location_5_16_upper_arm");
            Locations.put("elbow", "location_5_17_elbow");
            Locations.put("upper leg", "location_4_15_upper_leg");
        };

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
            String base = image.substring(0, image.length() - 4);
            String name = "images/signs/" + base.toLowerCase().replaceAll("[-.]", "_") + ".png";
            return name;
        }

        public String handshapeImage() {
            return "handshape_" + handshape.replaceAll("[.]", "_");
        }

        public String locationImage() {
            String r = Locations.get(location);
            if (r == null) {
                r = "";
            }
            return r;
        }

        public String toString() {
            return gloss + "|" + minor + "|" + maori;
        }
    }

    private ArrayList<DictItem> words = new ArrayList();

    Dictionary(Context context)
    {
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
            Log.d("dictionary", "exception reading from word list");
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

    public List<DictItem> getWords()
    {
        return words;
    }

    private static String normalise(String s)
    {
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

    public List<DictItem> getWords(String target)
    {
        List<DictItem> r = new ArrayList<DictItem>(words.size());
        String t = normalise(target);
        for (DictItem d: words) {
            if (normalise(d.gloss).indexOf(t) >= 0
             || normalise(d.minor).indexOf(t) >= 0
             || normalise(d.maori).indexOf(t) >= 0) {
                r.add(d);
            }
        }
        return r;
    }

    public List<DictItem> getWordsByHandshape(String handshape, String location)
    {
        List<DictItem> r = new ArrayList<DictItem>(words.size());
        for (DictItem d: words) {
            if ((handshape == null || handshape.length() == 0 || d.handshape.equals(handshape))
             && (location == null || location.length() == 0 || d.location.equals(location))) {
                r.add(d);
            }
        }
        return r;
    }

    static Set taboo = new HashSet(Arrays.asList(new String[] {
        "(vaginal) discharge",
        "abortion",
        "anus",
        "asshole",
        "bitch",
        "blow job",
        "breech (birth)",
        "bugger",
        "bullshit",
        "cervical smear",
        "cervix",
        "circumcise",
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
        "get stuffed",
        "hard-on",
        "have sex",
        "hysterectomy",
        "intercourse",
        "labour pains",
        "lose one's virginity",
        "masturbate (female)",
        "masturbate, wanker",
        "miscarriage",
        "orgasm",
        "ovaries",
        "penis",
        "period",
        "period pains",
        "prostitute",
        "rape",
        "sanitary pad",
        "sex",
        "sexual abuse",
        "shit",
        "sperm",
        "suicide",
        "tampon",
        "testicles",
        "vagina",
    }));

    public DictItem getWordOfTheDay()
    {
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

    private String skip_parens(String s)
    {
        if (s.charAt(0) == '(') {
            int i = s.indexOf(") ");
            if (i > 0) {
                s = s.substring(i+2);
            } else {
                Log.d("Dictionary", "expected to find closing parenthesis: " + s);
            }
        }
        return s;
    }
}
