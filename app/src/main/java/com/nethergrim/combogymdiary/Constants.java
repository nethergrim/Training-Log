package com.nethergrim.combogymdiary;


import android.content.Context;

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
    public static String[] partsOfBody;



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
