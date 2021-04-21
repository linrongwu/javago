# 成员变量和方法作用域
| 作用域 | 说明 |
| :--- | :--- |
| public | 表明该成员变量或者方法是对所有类或者对象都是可见的,所有类或者对象都可以直接访问 |
| protected | 表明成员变量或者方法对类自身,与同在一个包中的其他类可见,其他包下的类不可访问,除非是他的子类 |
| default | 表明该成员变量或者方法只有自己和其位于同一个包的内可见,其他包内的类不能访问,即便是它的子类 |
| private | 表明该成员变量或者方法是私有的,只有当前类对其具有访问权限,除此之外其他类或者对象都没有访问权限,子类也没有访问权限 |

```Java
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
public class OtherPackageObj {

    /*不同包下非子类，只能访问public 的方法 or 属性*/

    public int publicInt = new ScopeObject().publicInt;
}
class OtherPackageSubObj extends ScopeObject {

    /*不同包下，子类可以访问父类的public protected 方法 or 属性*/

    public int publicInt = super.publicInt;
    public int protectedInt = super.protectedInt;
}
```