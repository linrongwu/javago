package com.lrwatcl.javago.parampass;
public class ParamPassing {
    private int actualInt;
    private StringBuilder actualString;

    public ParamPassing(int actualInt, StringBuilder actualString) {
        this.actualInt = actualInt;
        this.actualString = actualString;
    }

    public int getActualInt() {
        return actualInt;
    }

    public StringBuilder getActualString() {
        return actualString;
    }

    public void setActualInt(int actualInt) {
        this.actualInt = actualInt;
    }

    public void setActualString(StringBuilder actualString) {
        this.actualString = actualString;
    }

    public static void testPassing(int formalInt, StringBuilder formalString){
        formalInt=0;
        formalString= new StringBuilder("formalString");
    }

    public static void testPassing2(int formalInt, StringBuilder formalString){
        formalInt=0;
        formalString.append("formalString");
    }

    public static void testPassing(ParamPassing paramPassing){
        paramPassing = new ParamPassing(2,new StringBuilder("actualString2"));
    }

    public static void testPassing2(ParamPassing paramPassing){
        paramPassing.setActualInt(3);
        paramPassing.setActualString(new StringBuilder("actualString3"));
    }

    public static void main(String[] args) {
        ParamPassing paramPassing = new ParamPassing(1,new StringBuilder("actualString1"));

        testPassing(paramPassing.getActualInt(),paramPassing.getActualString());
        System.out.println(paramPassing.getActualInt());
        System.out.println(paramPassing.getActualString());

        testPassing2(paramPassing.getActualInt(),paramPassing.getActualString());
        System.out.println(paramPassing.getActualInt());
        System.out.println(paramPassing.getActualString());

        testPassing(paramPassing);
        System.out.println(paramPassing.getActualInt());
        System.out.println(paramPassing.getActualString());

        testPassing2(paramPassing);
        System.out.println(paramPassing.getActualInt());
        System.out.println(paramPassing.getActualString());


        StringBuilder a = new StringBuilder("a");
        StringBuilder b=a;
        b.append("b");
        System.out.println(a);
        System.out.println(b);
    }
}
