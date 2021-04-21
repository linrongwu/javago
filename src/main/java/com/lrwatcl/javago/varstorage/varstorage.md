# 变量的存储大小与位置
PS:  
JVM参数：  
-XX:+UseSerialGC——使用SerialGC  
-XX:-UseCompressedOops——关闭压缩指针  
JDK版本：1.8  
虚拟机：HotSpot VM

| 变量类型 | 存储位置 |
| :--- | :--- |
| 实例变量 | JVM的方法区 |
| 静态变量 | JVM的堆内存 |
| 局部变量 | JVM的栈内存 |

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

    public static String staticString = null;
    public String instanceString = null;

    public void localInt() {
        boolean localBoolean = true;
        byte localByte = 3;
        short localShort = 3;
        char localChar = 3;
        int localInt = 3;
        float localFloat = 3;
        long localLong = 3;
        double localDouble = 3;
        String localString  = null;
        System.out.println(null==localString);
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
java.lang.Object object internals:

| OFF | SZ | TYPE | DESCRIPTION | VALUE |
| :--- | :--- |:--- | :--- |:--- |
| 0 | 8 |  | (object header: mark) | N/A|
| 8 | 8 |  | (object header: class) | N/A|

Instance size: 16 bytes

Space losses: 0 bytes internal + 0 bytes external = 0 bytes total

说明：Object对象 占16字节

com.lrwatcl.javago.varstorage.Variable object internals:

| OFF | SZ | TYPE | DESCRIPTION | VALUE |
| :--- | :--- |:--- | :--- |:--- |
| 0 | 8 |  | (object header: mark) | N/A|
| 8 | 8 |  | (object header: class) | N/A|
| 16 | 8 | long | Variable.instanceLong | N/A |
| 24 | 8 | double | Variable.instanceDouble | N/A |
| 32 | 4 | int | Variable.instanceInt | N/A |
| 36 | 4 | float | Variable.instanceFloat | N/A |
| 40 | 2 | short | Variable.instanceShort | N/A |
| 42 | 2 | char | Variable.instanceChar | N/A |
| 44 | 1 | boolean | Variable.instanceBoolean | N/A |
| 45 | 1 | byte | Variable.instanceByte | N/A |
| 46 | 2 |  | (alignment/padding gap) |  |
| 48 | 8 | java.lang.String | Variable.instanceString | N/A |

Instance size: 56 bytes

Space losses: 2 bytes internal + 0 bytes external = 2 bytes total

说明：本例的Variable对象 占56字节。  
静态变量不在对象里存储，故不在堆内存区域  
成员变量在对象存储，故在堆内存区域  
基本类型直接保存值，对象类型保存引用（指针），统一占8字节，非对象值（Object占16字节，而子类String占8字节，说明保存的不是值）  
分析过程
jps:Java Virtual Machine Process Status Tool,java提供的一个显示当前所有java进程pid的命令
9975 Variable
jhsdb hsdb --pid=9975 jhsdb jvm可视化工具


universe
Heap Parameters:
Gen 0:   eden [0x00007f6432000000,0x00007f6433762340,0x00007f6434110000) space capacity = 34668544, 70.72546225189036 used
  from [0x00007f6434110000,0x00007f6434110000,0x00007f6434530000) space capacity = 4325376, 0.0 used
  to   [0x00007f6434530000,0x00007f6434530000,0x00007f6434950000) space capacity = 4325376, 0.0 usedInvocations: 0

Gen 1:   old  [0x00007f645ae00000,0x00007f645ae00000,0x00007f64600b0000) space capacity = 86704128, 0.0 usedInvocations: 0

scanoops 0x00007f6432000000 0x00007f6434110000 com.lrwatcl.javago.varstorage.Variable
0x00007f6433738be0 com/lrwatcl/javago/varstorage/Variable

inspect 0x00007f6433738be0
instance of Oop for com/lrwatcl/javago/varstorage/Variable @ 0x00007f6433738be0 @ 0x00007f6433738be0 (size = 56)
_mark: 5
_metadata._klass: InstanceKlass for com/lrwatcl/javago/varstorage/Variable
instanceBoolean: true
instanceByte: 1
instanceShort: 1
instanceChar: ''
instanceInt: 1
instanceFloat: 1.0
instanceLong: 1
instanceDouble: 1.0
instanceString: null null

revptrs 0x00007f6433738be0
null
null
revptrs可根据对象地址查看引用该对象的活跃对象的地址，这里的引用是指通过类全局属性而非局部变量引用