/* Collections.cpp
Apr 26, 2017
Arjan
*/
#include<iostream>
#include"Collections.h"
using namespace std;

Collections::Collections(){
	size_=0;
}
Collections::Collections(const Collections& c){
	size_=c.size_;
}
Collections::~Collections(){

//cout<<"Nothing in Collections so far"<<endl;
}
bool Collections::contains(int x){
	for(int i=0;i<this->size_;i++){
		if((*this)[i]==x)
			return true;
	}

return false;
}
int Collections::someFunc(int x){
	x+=2;
return x;
}
Collections& Collections::map(int(*fn)(int x)){
//	fn=&someFunc;
	for (int i=0;i<this->size_;i++){
		(*this)[i]=(*fn)((*this)[i]);
	}
	return *this;
}

Collections& Collections::operator=(const Collections& c){
	return *this;
}
