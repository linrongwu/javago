package com.lrwatcl.javago.scope.main;

/**
 * 作用域
 * @author String
 */
public class ScopeObject {
    /**
     * 私有变量：只能自己访问
     */
    private int privateInt;
    /**
     * 默认变量：同包下访问
     */
    int defaultInt;
    /**
     * 保护变量：同包 or 非同包的子类 访问
     */
    protected int protectedInt;
    /**
     * 公共变量：全局访问
     */
    public int publicInt;
}
