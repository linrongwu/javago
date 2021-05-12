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