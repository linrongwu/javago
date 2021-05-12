# 参数传递分析
关键概念解释

| 概念 | 说明 |
| :--- | :--- |
| 引用 | 变量的别名，对引用的所有操作等于直接对变量的操作 |  
| 求值策略 | 确定编程语言中表达式的求值的一组（通常确定性的）规则。求值策略分为两大基本类，严格的和非严格的。 |
| 严格求值 | 给函数的实际参数总是在应用这个函数之前求值 |
| 非严格求值 | 不求值给函数的实际参数，除非它们在函数体内实际上被用到了 |
| 传值调用 | 传值调用中实际参数被求值，其值被绑定到函数中对应的变量上（通常是把值复制到新内存区域）。如果函数或过程能把值赋给它的形式参数，则被赋值的只是局部拷贝 -- 就是说，在函数返回后调用者作用域里的曾传给函数的任何东西都不会变 |
| 传引用调用 | 传递给函数的是它的实际参数的隐式引用而不是实参的拷贝。通常函数能够修改这些参数（比如赋值），而且改变对于调用者是可见的 |  
| 传共享对象调用 | 与传引用调用不同，对于调用者而言在被调用函数里修改参数（赋值：赋值是给变量绑定一个新对象，而不是改变对象）是没有影响的，但修改了对象，调用者就可以看到变化（因为对象是共享的，没有拷贝） |  

```C++
#include <iostream>
using namespace std;
void passByReference(int& refVar);
void passByValue(int var);
int main(){
    int value = 4;
    cout << "In main, value is " << value << endl;
    passByReference(value);
    cout << "Now calling passByReference......value is "<< value << endl;
    passByValue(value);
    cout << "Now calling passByValue......value is "<< value << endl;
    return 0;
}
void passByReference(int& refVar){
    refVar *= 2;
}
void passByValue(int var){
    var *= 2;
}
```
```Java
@Data
public class ParamPassing {
    private int actualInt;
    private StringBuilder actualString;
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
    public ParamPassing(int actualInt, StringBuilder actualString) {
        this.actualInt = actualInt;
        this.actualString = actualString;
    }
    public static void passingAssignment(int formalInt, StringBuilder formalString){
        formalInt=0;
        formalString= new StringBuilder("formalString");
    }
    public static void passingModify(int formalInt, StringBuilder formalString){
        formalInt=2;
        formalString.append("formalString");
    }
    public static void main(String[] args) {
        ParamPassing paramPassing = new ParamPassing(1,new StringBuilder("actualString1"));
        System.out.println("变量："+paramPassing.toString());
        passingAssignment(paramPassing.getActualInt(),paramPassing.getActualString());
        System.out.println("变量passingAssignment后："+paramPassing.toString());
        passingModify(paramPassing.getActualInt(),paramPassing.getActualString());
        System.out.println("变量passingModify后："+paramPassing.toString());
    }
}
```
程序运行结果如下：
```
C++
In main, value is 4
Now calling passByReference......value is 8
Now calling passByValue......value is 8

Java
变量：{"actualInt":1,"actualString":"actualString1"}
变量passingAssignment后：{"actualInt":1,"actualString":"actualString1"}
变量passingModify后：{"actualInt":1,"actualString":"actualString1formalString"}
```
说明：  
传引用调用，被调函数内修改，会改变调用函数内的值  
传值调用，被调函数内修改，不会改变调用函数内的值  
Java中基本类型是传值调用，对象类型是传共享对象调用（虚拟机实现是传对象的地址）
