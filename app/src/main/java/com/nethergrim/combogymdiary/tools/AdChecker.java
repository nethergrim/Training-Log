package com.nethergrim.combogymdiary.tools;

public class AdChecker {

    private static boolean isPaid = false;

    public static void setPaid(boolean paid) {
        isPaid = paid;
    }

    public static boolean IsPaid() {
        return isPaid;
    }


}
