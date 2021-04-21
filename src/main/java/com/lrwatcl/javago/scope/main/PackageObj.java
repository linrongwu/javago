package com.lrwatcl.javago.scope.main;

/**
 *
 * @author String
 */
public class PackageObj {

    /*同包下非子类，可以访问public protected default 方法 or 属性*/

    public int publicInt = new ScopeObject().publicInt;
    public int protectedInt = new ScopeObject().protectedInt;
    public int defaultInt = new ScopeObject().defaultInt;
}
class PackageSubObj extends ScopeObject {

    /*同包下子类，可以访问父类的public protected default 方法 or 属性*/

    public int publicInt = super.publicInt;
    public int protectedInt = super.protectedInt;
    public int defaultInt = super.defaultInt;

}
