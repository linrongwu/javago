# 类加载
## 生命周期
| 概念 | 说明 |
| :--- | :--- |
| 加载 | 通过类的全限定名称获取定义此类的二进制字节流，将这个字节流代表的静态存储结构转化为方法区的运行时数据结构，同时在内存中生成一个java.lang.Class对象，作为方法区这个类的各种数据的访问入口 |  
| 验证 | 验证是连接的第一步，该阶段保证字节流符合虚拟机规范的全部约束 |
| 准备 |  |
| 解析 |  |
| 初始化 |  |
| 使用 |  |  
| 卸载 |  |

加载->验证->准备->解析->初始化->使用->卸载  
加载，验证，准备，初始化，卸载 这五个阶段顺序是固定的，解析阶段不一定，可能在初始化阶段后，为了支持Java的动态绑定（运行时绑定）特性  
Java虚拟机规范没有定义什么时机开始加载阶段，但严格规定了初始化阶段的时机：
* new,getstatic,putstatic,invokestatic 这四个字节码指令，如果类型没有初始化，则触发初始化阶段。四个字节码指令的典型Java代码场景： 
    - new 实例化对象
    - 读取or设置类型的静态字段（final修饰，在编译器把结果放入常量池的字段除外）
    - 调用类型的静态方法
* 使用Java.lang.reflect包的方法进行反射调用，如果类型没有初始化，则触发初始化阶段。
* 初始化类型时，如果父类没有初始化，则触发父类初始化阶段。
* 虚拟机启动时，用户需要指定一个主类（main()方法的类），虚拟机会先初始化这个类
* JDK7的动态语言支持特性，如果Java.lang.invoke.MethodHandle实例最后的解析结果为REF_getStatic,REF_putStatic,REF_invokeStatic,REF_newInvokeSpecial四种类型的方法句柄，且方法句柄对应的类型没有初始化，则进行初始化
* JDK8的默认方法（default修饰的接口方法），如果有接口的实现类发生了初始化，则接口需要在其之前初始化  
以上的场景称为主动引用，除此之外，所有引用类型的方法都不会触发初始化称为被动引用

```Java
public abstract class ClassLoader { 
    
    private final ClassLoader parent;
    
    private ClassLoader(Void unused, ClassLoader parent) {
        this.parent = parent;
        if (ParallelLoaders.isRegistered(this.getClass())) {
            parallelLockMap = new ConcurrentHashMap<>();
            package2certs = new ConcurrentHashMap<>();
            domains =
                Collections.synchronizedSet(new HashSet<ProtectionDomain>());
            assertionLock = new Object();
        } else {
            // no finer-grained lock; lock on the classloader instance
            parallelLockMap = null;
            package2certs = new Hashtable<>();
            domains = new HashSet<>();
            assertionLock = this;
        }
    }
    
    protected Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                long t0 = System.nanoTime();
                try {
                    if (parent != null) {
                        c = parent.loadClass(name, false);
                    } else {
                        c = findBootstrapClassOrNull(name);
                    }
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }

                if (c == null) {
                    // If still not found, then invoke findClass in order
                    // to find the class.
                    long t1 = System.nanoTime();
                    c = findClass(name);

                    // this is the defining class loader; record the stats
                    sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    sun.misc.PerfCounter.getFindClasses().increment();
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
    
    public URL getResource(String name) {
        URL url;
        if (parent != null) {
            url = parent.getResource(name);
        } else {
            url = getBootstrapResource(name);
        }
        if (url == null) {
            url = findResource(name);
        }
        return url;
    }
    
    public Enumeration<URL> getResources(String name) throws IOException {
        @SuppressWarnings("unchecked")
        Enumeration<URL>[] tmp = (Enumeration<URL>[]) new Enumeration<?>[2];
        if (parent != null) {
            tmp[0] = parent.getResources(name);
        } else {
            tmp[0] = getBootstrapResources(name);
        }
        tmp[1] = findResources(name);

        return new CompoundEnumeration<>(tmp);
    }
    
    @CallerSensitive
    public final ClassLoader getParent() {
        if (parent == null)
            return null;
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            // Check access to the parent class loader
            // If the caller's class loader is same as this class loader,
            // permission check is performed.
            checkClassLoaderPermission(parent, Reflection.getCallerClass());
        }
        return parent;
    }
    
    boolean isAncestor(ClassLoader cl) {
        ClassLoader acl = this;
        do {
            acl = acl.parent;
            if (cl == acl) {
                return true;
            }
        } while (acl != null);
        return false;
    }
    
    protected Package getPackage(String name) {
        Package pkg;
        synchronized (packages) {
            pkg = packages.get(name);
        }
        if (pkg == null) {
            if (parent != null) {
                pkg = parent.getPackage(name);
            } else {
                pkg = Package.getSystemPackage(name);
            }
            if (pkg != null) {
                synchronized (packages) {
                    Package pkg2 = packages.get(name);
                    if (pkg2 == null) {
                        packages.put(name, pkg);
                    } else {
                        pkg = pkg2;
                    }
                }
            }
        }
        return pkg;
    }
    
    protected Package[] getPackages() {
        Map<String, Package> map;
        synchronized (packages) {
            map = new HashMap<>(packages);
        }
        Package[] pkgs;
        if (parent != null) {
            pkgs = parent.getPackages();
        } else {
            pkgs = Package.getSystemPackages();
        }
        if (pkgs != null) {
            for (int i = 0; i < pkgs.length; i++) {
                String pkgName = pkgs[i].getName();
                if (map.get(pkgName) == null) {
                    map.put(pkgName, pkgs[i]);
                }
            }
        }
        return map.values().toArray(new Package[map.size()]);
    }
}
```
