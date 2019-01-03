package com.hewgill.android.nzsldict;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DictItem implements Serializable, Comparable {
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
    }

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
        String assetName = "images/signs/" + image.toLowerCase();
        return assetName;
    }

    /* Lacking a formal ID in the signs export, the image path is unqiue across
        the signs database.
     */
    public String uniqueKey() {
        return image;
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

    public String videoFilename() {
        return this.video.substring(this.video.lastIndexOf('/')+1, this.video.length());
    }


    @Override
    public int compareTo(@NonNull Object o) {
        DictItem other = (DictItem) o;
        return this.uniqueKey().compareTo(other.uniqueKey());
    }
}
