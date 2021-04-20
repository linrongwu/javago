package com.lrwatcl.javago.scope.other;

import com.lrwatcl.javago.scope.main.ScopeObject;

/**
 *
 * @author String
 */
public class OtherPackageObj {
    public int publicInt = new ScopeObject().publicInt;
}
class OtherPackageSubObj extends ScopeObject {
    public int publicInt = super.publicInt;
    public int protectedInt = super.protectedInt;
}
