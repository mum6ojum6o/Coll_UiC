#include "MyString.h"
#include <iostream>
using namespace std;
MyString::MyString(){
	cout<<"inside default constructor"<<endl;
	data_ = NULL;
	size_ =0;
}
MyString::MyString(char *string){  
	cout<<"inside convenience constructor"<<endl;
	int index=0;
	while(string[index]!='\0')
		index++;
	data_ = new char[index]; //creating space for the String
	for (int i=0;i<index;i++)
		data_[i]=string[i];
	size_=index;
	cout<<"The String is:"<<data_<<"and the size is:"<<size_<<endl;
}
MyString::MyString(const MyString& s){ 
	this->data_=s.data_;
	this->size_=s.size_;
}
MyString& MyString::setData(char* string){
cout<<"inside set data;data passed:"<<string<<endl;
	int index=0;
	while(string[index]!='\0'){
		index++;
	}
	
	data_ = new char[index]; //creating space for the String
	cout<<"CP 2:"<<endl;
	for (int i=0;i<index;i++){
		data_[i]=string[i];
	}
	cout<<"CP 3:"<<endl;
	size_=index;
	cout<<"The String is:"<<data_<<"and the size is:"<<size_<<endl;
	return *this;	
}