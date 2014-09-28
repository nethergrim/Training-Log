package com.nethergrim.combogymdiary;


import android.content.Context;

import com.loopj.android.image.SmartImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Constants {

    public static final String TYPEFACE_LIGHT = "fonts/Roboto-Light.ttf";
    public static final String TYPEFACE_THIN = "fonts/Roboto-Thin.ttf";
    public static final String PART_OF_BODY_LEGS = "legs";
    public static final String PART_OF_BODY_CHEST = "chest";
    public static final String PART_OF_BODY_BACK = "back";
    public static final String PART_OF_BODY_SHOULDERS = "shoulders";
    public static final String PART_OF_BODY_BICEPS = "biceps";
    public static final String PART_OF_BODY_TRICEPS = "triceps";
    public static final String PART_OF_BODY_ABS = "abs";
    public static final String PART_OF_BODY_NONE = "without_category";
    public static final String[] PARTS_OF_BODY = {PART_OF_BODY_LEGS, PART_OF_BODY_CHEST, PART_OF_BODY_BACK, PART_OF_BODY_SHOULDERS, PART_OF_BODY_BICEPS, PART_OF_BODY_TRICEPS, PART_OF_BODY_ABS, PART_OF_BODY_NONE};
    public static final String _ID = "_id";
    public static final String BUNDLE_EXERCISE = "exercise";
    public final static String TYPE_OF_DIALOG = "type_of_dialog";
    public static final String MARKET_LINK = "market://details?id=com.nethergrim.combogymdiary";
    public static final String MARKET_LINK_HTTP = "http://play.google.com/store/apps/details?id=com.nethergrim.combogymdiary";
    public static String[] partsOfBody;
    public static List<String> partsOfBodyURLs = new ArrayList<String>();

    public static String[] getPartsOfBodyRealNames(Context context) {
        String[] array = context.getResources().getStringArray(R.array.MuscleGroupsArray);
        String[] result = new String[array.length + 1];
        result[0] = array[1];
        result[1] = array[0];
        result[2] = array[2];
        result[3] = array[3];
        result[4] = array[4];
        result[5] = array[5];
        result[6] = array[6];
        result[7] = context.getResources().getString(R.string.without_category);
        partsOfBody = result;
        partsOfBodyURLs.add("http://www.criticalbench.com/exercises/pics/exercises-chest.gif");         //0
        partsOfBodyURLs.add("http://www.criticalbench.com/exercises/pics/exercises-biceps.gif");         //1
        partsOfBodyURLs.add("http://www.criticalbench.com/exercises/pics/exercises-triceps.gif");         //2
        partsOfBodyURLs.add("http://www.criticalbench.com/exercises/pics/exercises-shoulders.gif");         //3
        partsOfBodyURLs.add("http://www.criticalbench.com/exercises/pics/exercises-obliques.gif");         //4
        partsOfBodyURLs.add("http://www.criticalbench.com/exercises/pics/exercises-all-abs.gif");         //5
        partsOfBodyURLs.add("http://www.criticalbench.com/exercises/pics/exercises-quads.gif");         //6
        partsOfBodyURLs.add("http://www.criticalbench.com/exercises/pics/exercises-traps.gif");         //7
        partsOfBodyURLs.add("http://www.criticalbench.com/exercises/pics/exercises-arms.gif");         //8
        partsOfBodyURLs.add("http://www.criticalbench.com/exercises/pics/exercises-back-traps.gif");         //9
        partsOfBodyURLs.add("http://www.criticalbench.com/exercises/pics/exercises-lowerback.gif");         //10
        partsOfBodyURLs.add("http://www.criticalbench.com/exercises/pics/exercises-glutes.gif");         //11
        partsOfBodyURLs.add("http://www.criticalbench.com/exercises/pics/exercises-legs.gif");         //12
        partsOfBodyURLs.add("http://www.criticalbench.com/exercises/pics/exercises-hamstrings.gif");             //13
        partsOfBodyURLs.add("http://www.criticalbench.com/exercises/pics/exercises-calves.gif");                //14
        for (String partsOfBodyURL : partsOfBodyURLs) { // caching images from web!
            SmartImageView smartImageView = new SmartImageView(context);
            smartImageView.setImageUrl(partsOfBodyURL);
        }
        return result;
    }

    public static String getRealPartOfBodyName(String constantName){
        if (constantName.equals(PART_OF_BODY_LEGS)) return partsOfBody[0];
        else if (constantName.equals(PART_OF_BODY_CHEST)) return partsOfBody[1];
        else if (constantName.equals(PART_OF_BODY_BACK)) return partsOfBody[2];
        else if (constantName.equals(PART_OF_BODY_SHOULDERS)) return partsOfBody[3];
        else if (constantName.equals(PART_OF_BODY_BICEPS)) return partsOfBody[4];
        else if (constantName.equals(PART_OF_BODY_TRICEPS)) return partsOfBody[5];
        else if (constantName.equals(PART_OF_BODY_ABS)) return partsOfBody[6];
        else return partsOfBody[7];
    }

}
