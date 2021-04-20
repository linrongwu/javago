package com.lrwatcl.javago.scope.main;

/**
 *
 * @author String
 */
public class PackageObj {
    public int publicInt = new ScopeObject().publicInt;
    public int defaultInt = new ScopeObject().defaultInt;
    public int protectedInt = new ScopeObject().protectedInt;
}
class PackageSubObj extends ScopeObject {
    public int publicInt = super.publicInt;
    public int protectedInt = super.protectedInt;
}
