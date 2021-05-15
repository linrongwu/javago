#类加载-加载
## 类加载器体系结构图
![avatar](ClassLoader.png)

## 类加载器架构
| 概念 | 说明 |
| :--- | :--- |
| BootstrapClassLoader | 启动类加载器|  
| ExtClassLoader | 扩展类加载器 |
| AppClassLoader | 应用类加载器 |

## 源码分析
```Java
public abstract class ClassLoader {
    private final ClassLoader parent;
    
    private ClassLoader(Void var1, ClassLoader var2) {
        this.classes = new Vector();
        this.defaultDomain = new ProtectionDomain(new CodeSource((URL)null, (Certificate[])null), (PermissionCollection)null, this, (Principal[])null);
        this.packages = new HashMap();
        this.nativeLibraries = new Vector();
        this.defaultAssertionStatus = false;
        this.packageAssertionStatus = null;
        this.classAssertionStatus = null;
        this.parent = var2;
        if (ClassLoader.ParallelLoaders.isRegistered(this.getClass())) {
            this.parallelLockMap = new ConcurrentHashMap();
            this.package2certs = new ConcurrentHashMap();
            this.domains = Collections.synchronizedSet(new HashSet());
            this.assertionLock = new Object();
        } else {
            this.parallelLockMap = null;
            this.package2certs = new Hashtable();
            this.domains = new HashSet();
            this.assertionLock = this;
        }

    }

    protected ClassLoader(ClassLoader var1) {
        this(checkCreateClassLoader(), var1);
    }

    protected ClassLoader() {
        this(checkCreateClassLoader(), getSystemClassLoader());
    }
    
    public Class<?> loadClass(String var1) throws ClassNotFoundException {
        return this.loadClass(var1, false);
    }
    
    protected Class<?> loadClass(String var1, boolean var2) throws ClassNotFoundException {
        synchronized(this.getClassLoadingLock(var1)) {
            Class var4 = this.findLoadedClass(var1);
            if (var4 == null) {
                long var5 = System.nanoTime();

                try {
                    if (this.parent != null) {
                        var4 = this.parent.loadClass(var1, false);
                    } else {
                        var4 = this.findBootstrapClassOrNull(var1);
                    }
                } catch (ClassNotFoundException var10) {
                }

                if (var4 == null) {
                    long var7 = System.nanoTime();
                    var4 = this.findClass(var1);
                    PerfCounter.getParentDelegationTime().addTime(var7 - var5);
                    PerfCounter.getFindClassTime().addElapsedTimeFrom(var7);
                    PerfCounter.getFindClasses().increment();
                }
            }

            if (var2) {
                this.resolveClass(var4);
            }

            return var4;
        }
    }
    
    private Class<?> findBootstrapClassOrNull(String var1) {
        return !this.checkName(var1) ? null : this.findBootstrapClass(var1);
    }

    private native Class<?> findBootstrapClass(String var1);
    
    protected final Class<?> findLoadedClass(String var1) {
        return !this.checkName(var1) ? null : this.findLoadedClass0(var1);
    }

    private final native Class<?> findLoadedClass0(String var1);
}
```
```C
/*
 * Returns NULL if class not found.
 */
JNIEXPORT jclass JNICALL
Java_java_lang_ClassLoader_findBootstrapClass(JNIEnv *env, jobject loader,
                                              jstring classname)
{
    char *clname;
    jclass cls = 0;
    char buf[128];

    if (classname == NULL) {
        return 0;
    }

    clname = getUTF(env, classname, buf, sizeof(buf));
    if (clname == NULL) {
        JNU_ThrowOutOfMemoryError(env, NULL);
        return NULL;
    }
    VerifyFixClassname(clname);

    if (!VerifyClassname(clname, JNI_TRUE)) {  /* expects slashed name */
        goto done;
    }

    cls = JVM_FindClassFromBootLoader(env, clname);

 done:
    if (clname != buf) {
        free(clname);
    }

    return cls;
}

JNIEXPORT jclass JNICALL
Java_java_lang_ClassLoader_findLoadedClass0(JNIEnv *env, jobject loader,
                                           jstring name)
{
    if (name == NULL) {
        return 0;
    } else {
        return JVM_FindLoadedClass(env, loader, name);
    }
}
```
```Java
public class Launcher {
    private static String bootClassPath = System.getProperty("sun.boot.class.path");
    
    public Launcher() {
        Launcher.ExtClassLoader var1;
        try {
            var1 = Launcher.ExtClassLoader.getExtClassLoader();
        } catch (IOException var10) {
            throw new InternalError("Could not create extension class loader", var10);
        }

        try {
            this.loader = Launcher.AppClassLoader.getAppClassLoader(var1);
        } catch (IOException var9) {
            throw new InternalError("Could not create application class loader", var9);
        }

        Thread.currentThread().setContextClassLoader(this.loader);
        String var2 = System.getProperty("java.security.manager");
        if (var2 != null) {
            SecurityManager var3 = null;
            if (!"".equals(var2) && !"default".equals(var2)) {
                try {
                    var3 = (SecurityManager)this.loader.loadClass(var2).newInstance();
                } catch (IllegalAccessException var5) {
                } catch (InstantiationException var6) {
                } catch (ClassNotFoundException var7) {
                } catch (ClassCastException var8) {
                }
            } else {
                var3 = new SecurityManager();
            }

            if (var3 == null) {
                throw new InternalError("Could not create SecurityManager: " + var2);
            }

            System.setSecurityManager(var3);
        }

    }
    
    static class ExtClassLoader extends URLClassLoader {
        private static volatile Launcher.ExtClassLoader instance;

        public static Launcher.ExtClassLoader getExtClassLoader() throws IOException {
            if (instance == null) {
                Class var0 = Launcher.ExtClassLoader.class;
                synchronized(Launcher.ExtClassLoader.class) {
                    if (instance == null) {
                        instance = createExtClassLoader();
                    }
                }
            }

            return instance;
        }

        private static Launcher.ExtClassLoader createExtClassLoader() throws IOException {
            try {
                return (Launcher.ExtClassLoader)AccessController.doPrivileged(new PrivilegedExceptionAction<Launcher.ExtClassLoader>() {
                    public Launcher.ExtClassLoader run() throws IOException {
                        File[] var1 = Launcher.ExtClassLoader.getExtDirs();
                        int var2 = var1.length;

                        for(int var3 = 0; var3 < var2; ++var3) {
                            MetaIndex.registerDirectory(var1[var3]);
                        }

                        return new Launcher.ExtClassLoader(var1);
                    }
                });
            } catch (PrivilegedActionException var1) {
                throw (IOException)var1.getException();
            }
        }

        void addExtURL(URL var1) {
            super.addURL(var1);
        }

        public ExtClassLoader(File[] var1) throws IOException {
            super(getExtURLs(var1), (ClassLoader)null, Launcher.factory);
            SharedSecrets.getJavaNetAccess().getURLClassPath(this).initLookupCache(this);
        }

        private static File[] getExtDirs() {
            String var0 = System.getProperty("java.ext.dirs");
            File[] var1;
            if (var0 != null) {
                StringTokenizer var2 = new StringTokenizer(var0, File.pathSeparator);
                int var3 = var2.countTokens();
                var1 = new File[var3];

                for(int var4 = 0; var4 < var3; ++var4) {
                    var1[var4] = new File(var2.nextToken());
                }
            } else {
                var1 = new File[0];
            }

            return var1;
        }

        private static URL[] getExtURLs(File[] var0) throws IOException {
            Vector var1 = new Vector();

            for(int var2 = 0; var2 < var0.length; ++var2) {
                String[] var3 = var0[var2].list();
                if (var3 != null) {
                    for(int var4 = 0; var4 < var3.length; ++var4) {
                        if (!var3[var4].equals("meta-index")) {
                            File var5 = new File(var0[var2], var3[var4]);
                            var1.add(Launcher.getFileURL(var5));
                        }
                    }
                }
            }

            URL[] var6 = new URL[var1.size()];
            var1.copyInto(var6);
            return var6;
        }

        public String findLibrary(String var1) {
            var1 = System.mapLibraryName(var1);
            URL[] var2 = super.getURLs();
            File var3 = null;

            for(int var4 = 0; var4 < var2.length; ++var4) {
                URI var5;
                try {
                    var5 = var2[var4].toURI();
                } catch (URISyntaxException var9) {
                    continue;
                }

                File var6 = Paths.get(var5).toFile().getParentFile();
                if (var6 != null && !var6.equals(var3)) {
                    String var7 = VM.getSavedProperty("os.arch");
                    File var8;
                    if (var7 != null) {
                        var8 = new File(new File(var6, var7), var1);
                        if (var8.exists()) {
                            return var8.getAbsolutePath();
                        }
                    }

                    var8 = new File(var6, var1);
                    if (var8.exists()) {
                        return var8.getAbsolutePath();
                    }
                }

                var3 = var6;
            }

            return null;
        }

        private static AccessControlContext getContext(File[] var0) throws IOException {
            PathPermissions var1 = new PathPermissions(var0);
            ProtectionDomain var2 = new ProtectionDomain(new CodeSource(var1.getCodeBase(), (Certificate[])null), var1);
            AccessControlContext var3 = new AccessControlContext(new ProtectionDomain[]{var2});
            return var3;
        }

        static {
            ClassLoader.registerAsParallelCapable();
            instance = null;
        }
    }
    
    static class AppClassLoader extends URLClassLoader {
        final URLClassPath ucp = SharedSecrets.getJavaNetAccess().getURLClassPath(this);

        public static ClassLoader getAppClassLoader(final ClassLoader var0) throws IOException {
            final String var1 = System.getProperty("java.class.path");
            final File[] var2 = var1 == null ? new File[0] : Launcher.getClassPath(var1);
            return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<Launcher.AppClassLoader>() {
                public Launcher.AppClassLoader run() {
                    URL[] var1x = var1 == null ? new URL[0] : Launcher.pathToURLs(var2);
                    return new Launcher.AppClassLoader(var1x, var0);
                }
            });
        }

        AppClassLoader(URL[] var1, ClassLoader var2) {
            super(var1, var2, Launcher.factory);
            this.ucp.initLookupCache(this);
        }

        public Class<?> loadClass(String var1, boolean var2) throws ClassNotFoundException {
            int var3 = var1.lastIndexOf(46);
            if (var3 != -1) {
                SecurityManager var4 = System.getSecurityManager();
                if (var4 != null) {
                    var4.checkPackageAccess(var1.substring(0, var3));
                }
            }

            if (this.ucp.knownToNotExist(var1)) {
                Class var5 = this.findLoadedClass(var1);
                if (var5 != null) {
                    if (var2) {
                        this.resolveClass(var5);
                    }

                    return var5;
                } else {
                    throw new ClassNotFoundException(var1);
                }
            } else {
                return super.loadClass(var1, var2);
            }
        }

        protected PermissionCollection getPermissions(CodeSource var1) {
            PermissionCollection var2 = super.getPermissions(var1);
            var2.add(new RuntimePermission("exitVM"));
            return var2;
        }

        private void appendToClassPathForInstrumentation(String var1) {
            assert Thread.holdsLock(this);

            super.addURL(Launcher.getFileURL(new File(var1)));
        }

        private static AccessControlContext getContext(File[] var0) throws MalformedURLException {
            PathPermissions var1 = new PathPermissions(var0);
            ProtectionDomain var2 = new ProtectionDomain(new CodeSource(var1.getCodeBase(), (Certificate[])null), var1);
            AccessControlContext var3 = new AccessControlContext(new ProtectionDomain[]{var2});
            return var3;
        }

        static {
            ClassLoader.registerAsParallelCapable();
        }
    }
}
```