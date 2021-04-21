package com.lrwatcl.javago.scope.main;
public class PackageObj {
    public int publicInt = new ScopeObject().publicInt;
    public int protectedInt = new ScopeObject().protectedInt;
    public int defaultInt = new ScopeObject().defaultInt;
}
