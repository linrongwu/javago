package com.lrwatcl.javago.varstorage;
import org.openjdk.jol.info.ClassLayout;
public class Variable {
    public static boolean staticBoolean = true;
    public static byte staticByte = 0;
    public static short staticShort = 0;
    public static char staticChar = 0;
    public static int staticInt = 0;
    public static float staticFloat = 0;
    public static long staticLong = 0;
    public static double staticDouble = 0;

    public boolean instanceBoolean = true;
    public byte instanceByte = 1;
    public short instanceShort = 1;
    public char instanceChar = 1;
    public int instanceInt = 1;
    public float instanceFloat = 1;
    public long instanceLong = 1;
    public double instanceDouble = 1;

    public static String staticString = null;
    public String instanceString = null;

    public void localInt() {
        boolean localBoolean = true;
        byte localByte = 3;
        short localShort = 3;
        char localChar = 3;
        int localInt = 3;
        float localFloat = 3;
        long localLong = 3;
        double localDouble = 3;
        String localString  = null;
        System.out.println(null==localString);
        System.out.println(localBoolean);
        System.out.println(localByte);
        System.out.println(localShort);
        System.out.println(localChar);
        System.out.println(localInt);
        System.out.println(localFloat);
        System.out.println(localLong);
        System.out.println(localDouble);
    }

    public static void main(String[] args) {
        System.out.println(ClassLayout.parseClass(Object.class).toPrintable());
        System.out.println(ClassLayout.parseClass(Variable.class).toPrintable());
        Variable variable = new Variable();
        variable.localInt();
    }
}
