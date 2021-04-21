package com.lrwatcl.javago.scope.other;

import com.lrwatcl.javago.scope.main.ScopeObject;

/**
 *
 * @author String
 */
public class OtherPackageObj {

    /*不同包下非子类，只能访问public 的方法 or 属性*/

    public int publicInt = new ScopeObject().publicInt;
}
class OtherPackageSubObj extends ScopeObject {

    /*不同包下，子类可以访问父类的public protected 方法 or 属性*/

    public int publicInt = super.publicInt;
    public int protectedInt = super.protectedInt;
}
