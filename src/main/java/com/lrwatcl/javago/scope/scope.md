# 作用域
## 结论
| 作用域 | 说明 |
| :--- | :--- |
| public | 表明该变量或者方法是对所有类或者对象都是可见的,所有类或者对象都可以直接访问 |
| protected | 表明变量或者方法对类自身,与同在一个包中的其他类可见,其他包下的类不可访问,除非是他的子类 |
| default | 表明该变量或者方法只有自己和其位于同一个包的内可见,其他包内的类不能访问,即便是它的子类 |
| private | 表明该变量或者方法是私有的,只有当前类对其具有访问权限,除此之外其他类或者对象都没有访问权限,子类也没有访问权限 |

## 程序
```Java
public class ScopeObject {
    private int privateInt;
    int defaultInt;
    protected int protectedInt;
    public int publicInt;
}

public class PackageObj {
    public int publicInt = new ScopeObject().publicInt;
    public int protectedInt = new ScopeObject().protectedInt;
    public int defaultInt = new ScopeObject().defaultInt;
}

public class OtherPackageObj {
    public int publicInt = new ScopeObject().publicInt;
}
class OtherPackageSubObj extends ScopeObject {
    public int publicInt = super.publicInt;
    public int protectedInt = super.protectedInt;
}
```