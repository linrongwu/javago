# 变量的存储大小与位置
JVM参数：-XX:+UseSerialGC -XX:-UseCompressedOops  
JDK版本：1.8  
JVM虚拟机：HotSpot VM

| 变量类型 | 存储位置 |
| :--- | :--- |
| 静态变量 | JVM的堆内存（类的class对象） |
| 实例变量 | JVM的堆内存（类的对象） |
| 局部变量 | JVM的栈内存 （栈帧）|

| 变量类型 | 存储大小 |
| :--- | :--- |
| boolean | 1字节 |
| byte | 1字节 |
| short | 2字节 |
| char | 2字节 |
| int | 4字节 |
| float | 4字节 |
| long | 8字节 |
| double | 8字节 |
| 对象类型 | 8字节 |

```Java
public class Variable {
    public static boolean staticBoolean = true;
    public static byte staticByte = 0;
    public static short staticShort = 0;
    public static char staticChar = 0;
    public static int staticInt = 0;
    public static float staticFloat = 0;
    public static long staticLong = 0;
    public static double staticDouble = 0;

    public boolean instanceBoolean = true;
    public byte instanceByte = 1;
    public short instanceShort = 1;
    public char instanceChar = 1;
    public int instanceInt = 1;
    public float instanceFloat = 1;
    public long instanceLong = 1;
    public double instanceDouble = 1;

    public static String staticString = "staticString123";
    public String instanceString = "instanceString123";

    public void localInt() {
        boolean localBoolean = true;
        byte localByte = 3;
        short localShort = 3;
        char localChar = 3;
        int localInt = 3;
        float localFloat = 3;
        long localLong = 3;
        double localDouble = 3;
        String localString  = "localString123";
        System.out.println(localString);
        System.out.println(localBoolean);
        System.out.println(localByte);
        System.out.println(localShort);
        System.out.println(localChar);
        System.out.println(localInt);
        System.out.println(localFloat);
        System.out.println(localLong);
        System.out.println(localDouble);
    }

    public static void main(String[] args) {
        System.out.println(ClassLayout.parseClass(Object.class).toPrintable());
        System.out.println(ClassLayout.parseClass(Variable.class).toPrintable());
        Variable variable = new Variable();
        variable.localInt();
    }
}
```
程序输出如下：  
```
java.lang.Object object internals:
OFF  SZ   TYPE DESCRIPTION               VALUE
  0   8        (object header: mark)     N/A
  8   8        (object header: class)    N/A
Instance size: 16 bytes
Space losses: 0 bytes internal + 0 bytes external = 0 bytes total

com.lrwatcl.javago.varstorage.Variable object internals:
OFF  SZ               TYPE DESCRIPTION                VALUE
  0   8                    (object header: mark)      N/A
  8   8                    (object header: class)     N/A
 16   8               long Variable.instanceLong      N/A
 24   8             double Variable.instanceDouble    N/A
 32   4                int Variable.instanceInt       N/A
 36   4              float Variable.instanceFloat     N/A
 40   2              short Variable.instanceShort     N/A
 42   2               char Variable.instanceChar      N/A
 44   1            boolean Variable.instanceBoolean   N/A
 45   1               byte Variable.instanceByte      N/A
 46   2                    (alignment/padding gap)    
 48   8   java.lang.String Variable.instanceString    N/A
Instance size: 56 bytes
Space losses: 2 bytes internal + 0 bytes external = 2 bytes total
```
JVM分析过程
```
打印所有Java进城pid指令
jps
9975 Variable

查看上述pid值的JVM内存运行时数据内容
jhsdb hsdb --pid=9975
    
    查看GC堆的情况
    universe
    Heap Parameters:
    Gen 0:   eden [0x0000000080000000,0x0000000081348278,0x0000000084450000) space capacity = 71630848, 28.225950919916514 used
             from [0x0000000084450000,0x0000000084450000,0x0000000084cd0000) space capacity = 8912896, 0.0 used
             to   [0x0000000084cd0000,0x0000000084cd0000,0x0000000085550000) space capacity = 8912896, 0.0 usedInvocations: 0
    
    Gen 1:   old  [0x00000000d4e00000,0x00000000d4e00000,0x00000000df8b0000) space capacity = 178978816, 0.0 usedInvocations: 0
    
    上述只有eden有对象，查询本例Variable对象的堆情况
    scanoops 0x0000000080000000 0x0000000084450000 com.lrwatcl.javago.varstorage.Variable
    0x000000008120fe28 com/lrwatcl/javago/varstorage/Variable
    
    上述有结果，证明了Java对象是保存在堆中，查看对象具体情况
    inspect 0x000000008120fe28
    instance of Oop for com/lrwatcl/javago/varstorage/Variable @ 0x000000008120fe28 @ 0x000000008120fe28 (size = 56)
    _mark: 1
    _metadata._klass: InstanceKlass for com/lrwatcl/javago/varstorage/Variable
    instanceBoolean: true
    instanceByte: 1
    instanceShort: 1
    instanceChar: ''
    instanceInt: 1
    instanceFloat: 1.0
    instanceLong: 1
    instanceDouble: 1.0
    instanceString: "instanceString123" @ 0x000000008120fe60 Oop for java/lang/String @ 0x000000008120fe60

    上述情况与程序打印的对象大小一致56字节，且基本类型的成员变量直接保存值，保存在对象内部，对象类型的成员变量保存的是引用（指针），占固定字节，本例为8字节，值为对象的堆地址，反查Variable对象的引用情况，revptrs可根据对象地址查看引用该对象的活跃对象的地址，这里的引用是指通过类全局属性而非局部变量引用
    revptrs 0x000000008120fe28
    Computing reverse pointers...
    Done.
    null
    null
    null
    
    上述无结果，说明Variable对象的引用是局部变量，查询该对象的使用线程情况
    whatis 0x000000008120fe28
    Address 0x000000008120fe28: In thread-local allocation buffer for thread "main" (1)  [0x00000000811ea518,0x0000000081210418,0x0000000081348260,{0x0000000081348278})
    
    上述结果说明，Variable对象是在main线程中使用，与程序一致，且对象内instanceString字段保存的是堆中的另一个对象的地址0x000000008120fe60，查看该String对象
    inspect 0x000000008120fe60
    instance of "instanceString123" @ 0x000000008120fe60 @ 0x000000008120fe60 (size = 32)
    <<Reverse pointers>>: 
    _mark: 1
    _metadata._klass: InstanceKlass for java/lang/String
    value: [C @ 0x000000008120fe80 Oop for [C @ 0x000000008120fe80
    hash: 0
    
    上述结果说明：对象类型的成员变量所指向的对象保存在堆中，其引用（指针）也保存在（对象内）堆中，基本类型的成员变量保存在对象内（堆中），保存的就是值
    同时Variable对象的引用不在堆中，说明main方法的局部变量的variable引用不在堆中，
    打开stack memory for main 窗口，观察main线程的栈帧情况，同时main线程的栈帧中存在值为0x000000008120fe28 的Variable类型的对象引用 和0x000000008120fec0的String类型的对象引用
    反查0x000000008120fec0
    inspect 0x000000008120fec0
    instance of "localString123" @ 0x000000008120fec0 @ 0x000000008120fec0 (size = 32)
    <<Reverse pointers>>: 
    _mark: 1
    _metadata._klass: InstanceKlass for java/lang/String
    value: [C @ 0x000000008120fee0 Oop for [C @ 0x000000008120fee0
    hash: 0
    
    上述结果说明：对象类型的局部变量所指向的对象保存在堆中，其引用（指针）保存栈(局部变量表)中，
    用idea 安装jclasslib 查看Variable.java 的字节码文件，观察main和localInt方法内的LocalVariableTable
    main中存在两个变量一个是参数args,一个是varibale
    0	0	41	0	cp_info #87(args)       cp_info #88(Ljava/lang/String)
    1	36	5	1	cp_info #89(variable)   cp_info #75(Lcom/lrwatcl/javago/varstorage/V)
    localInt存在10个变量处理方法内的9个，还有一个this变量
    0	0	100	0	cp_info #74(this)           cp_info #75(Lcom/lrwatcl/javago/varstorage/V)
    1	2	98	1	cp_info #77(localBoolean)   cp_info #43(Z)
    2	4	96	2	cp_info #78(localByte)	    cp_info #45(B)
    3	6	94	3	cp_info #79(localShort)	    cp_info #47(S)
    4	9	91	4	cp_info #80(localChar)	    cp_info #49(C)
    5	12	88	5	cp_info #76(localInt)	    cp_info #51(I)
    6	16	84	6	cp_info #81(localFloat)	    cp_info #53(F)
    7	21	79	7	cp_info #82(localLong)	    cp_info #55(J)
    8	26	74	9	cp_info #83(localDouble)    cp_info #57(D)
    9	30	70	11	cp_info #84(localString)    cp_info #67(Ljava/lang/String)
    基本类型的成员变量的值就保存栈(局部变量表)中
    接下来，我们分析静态变量，获取Variable对象的instanceKlass地址
    mem 0x000000008120fe28 2
    0x000000008120fe28: 0x0000000000000001 
    0x000000008120fe30: 0x0000000013651a10
    
    分析Variable对象的instanceKlass地址，查看java mirror（也就是Class对应的java 对象）的地址
    inspect 0x0000000013651a10
    Type is InstanceKlass (size of 440)
    juint Klass::_super_check_offset: 48
    Klass* Klass::_secondary_super_cache: Klass @ null
    Array<Klass*>* Klass::_secondary_supers: Array<Klass*> @ 0x0000000013240f88
    Klass* Klass::_primary_supers[0]: Klass @ 0x0000000013241c00
    oop Klass::_java_mirror: Oop for java/lang/Class @ 0x00000000805101d8 Oop for java/lang/Class @ 0x00000000805101d8
    jint Klass::_modifier_flags: 1
    Klass* Klass::_super: Klass @ 0x0000000013241c00
    Klass* Klass::_subklass: Klass @ null
    jint Klass::_layout_helper: 56
    Symbol* Klass::_name: Symbol @ 0x00000000144cdbc0
    AccessFlags Klass::_access_flags: 2097185
    markOop Klass::_prototype_header: 5
    Klass* Klass::_next_sibling: Klass @ 0x00000000135cb458
    u8 Klass::_trace_id: 44433408
    Klass* InstanceKlass::_array_klasses: Klass @ null
    Array<Method*>* InstanceKlass::_methods: Array<Method*> @ 0x00000000136515f8
    Array<Method*>* InstanceKlass::_default_methods: Array<Method*> @ null
    Array<Klass*>* InstanceKlass::_local_interfaces: Array<Klass*> @ 0x0000000013240f88
    Array<Klass*>* InstanceKlass::_transitive_interfaces: Array<Klass*> @ 0x0000000013240f88
    Array<u2>* InstanceKlass::_fields: Array<u2> @ 0x0000000013651518
    u2 InstanceKlass::_java_fields_count: 18
    ConstantPool* InstanceKlass::_constants: ConstantPool @ 0x0000000013651028
    ClassLoaderData* InstanceKlass::_class_loader_data: ClassLoaderData @ 0x0000000013b345d0
    u2 InstanceKlass::_source_file_name_index: 92
    char* InstanceKlass::_source_debug_extension: char @ null
    Array<jushort>* InstanceKlass::_inner_classes: Array<jushort> @ 0x0000000013240f58
    int InstanceKlass::_nonstatic_field_size: 5
    int InstanceKlass::_static_field_size: 5
    u2 InstanceKlass::_static_oop_field_count: 1
    int InstanceKlass::_nonstatic_oop_map_size: 1
    bool InstanceKlass::_is_marked_dependent: 0
    u2 InstanceKlass::_minor_version: 0
    u2 InstanceKlass::_major_version: 52
    u1 InstanceKlass::_init_state: 4
    Thread* InstanceKlass::_init_thread: Thread @ 0x0000000003033800
    int InstanceKlass::_vtable_len: 6
    int InstanceKlass::_itable_len: 2
    u1 InstanceKlass::_reference_type: 0
    OopMapCache* InstanceKlass::_oop_map_cache: OopMapCache @ 0x00000000145e9b20
    JNIid* InstanceKlass::_jni_ids: JNIid @ 0x0000000014edebf0
    nmethod* InstanceKlass::_osr_nmethods_head: nmethod @ null
    BreakpointInfo* InstanceKlass::_breakpoints: BreakpointInfo @ 0x00000000144d3be0
    u2 InstanceKlass::_generic_signature_index: 0
    jmethodID* InstanceKlass::_methods_jmethod_ids: jmethodID @ 0x00000000145f0050
    u2 InstanceKlass::_idnum_allocated_count: 4
    Annotations* InstanceKlass::_annotations: Annotations @ null
    nmethodBucket* InstanceKlass::_dependencies: nmethodBucket @ null
    Array<int>* InstanceKlass::_method_ordering: Array<int> @ 0x0000000013651c10
    Array<int>* InstanceKlass::_default_vtable_indices: Array<int> @ null
 
    分析Variable类对应的Class对应的java 对象
    inspect 0x00000000805101d8
    instance of Oop for java/lang/Class @ 0x00000000805101d8 @ 0x00000000805101d8 (size = 200)
    <<Reverse pointers>>: 
    staticBoolean: true
    staticByte: 0
    staticShort: 0
    staticChar: '
    staticInt: 0
    staticFloat: 0.0
    staticLong: 0
    staticDouble: 0.0
    staticString: "staticString123" @ 0x0000000080514930 Oop for java/lang/String @ 0x0000000080514930
    
    上述结果表明：对象类型的静态变量的引用（指针）在类对应的Class对象的内，所指向的对象保存在堆中，基本类型的静态变量的值直接保存在类对应的Class对象的内
```