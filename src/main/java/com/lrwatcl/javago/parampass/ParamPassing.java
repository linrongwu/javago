package com.lrwatcl.javago.parampass;

import com.alibaba.fastjson.JSON;
import lombok.Data;

@Data
public class ParamPassing {
    private int actualInt;
    private StringBuilder actualString;
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
    public ParamPassing(int actualInt, StringBuilder actualString) {
        this.actualInt = actualInt;
        this.actualString = actualString;
    }
    public static void passingAssignment(int formalInt, StringBuilder formalString){
        formalInt=0;
        formalString= new StringBuilder("formalString");
    }
    public static void passingModify(int formalInt, StringBuilder formalString){
        formalInt=2;
        formalString.append("formalString");
    }
    public static void main(String[] args) {
        ParamPassing paramPassing = new ParamPassing(1,new StringBuilder("actualString1"));
        System.out.println("变量："+paramPassing.toString());
        passingAssignment(paramPassing.getActualInt(),paramPassing.getActualString());
        System.out.println("变量passingAssignment后："+paramPassing.toString());
        passingModify(paramPassing.getActualInt(),paramPassing.getActualString());
        System.out.println("变量passingModify后："+paramPassing.toString());
    }
}
