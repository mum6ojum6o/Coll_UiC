/*
 * Array.cpp
 *
 *  Created on: Apr 28, 2017
 *      Author: Arjan
 */
#include<iostream>
#include "Collections.h"
#include "Array.h"
using namespace std;
Arrays::Arrays(int n) {
cout<<"Inside Constructor"<<endl;
	data_=new int[n];
	size_=n;
}
void Arrays::add(int n){
	cout<<"This method is available only to be used with BST!!"<<endl;
}
Arrays& Arrays::operator=(Arrays& a){
	cout<<"inside operator="<<endl;
	if(this==&a) return *this;
	else{

			delete []data_;
			size_=a.size_;
		for(int i=0;i<a.size_;i++)
			this->data_[i]=a.data_[i];
	}
	return *this;
}
Arrays::~Arrays() {
	cout<<"Inside Destructor"<<endl;
	if(data_!=nullptr){
		delete []data_;
	}
	// TODO Auto-generated destructor stub
}
int& Arrays::operator[](const int x){
	//cout<<"inside operator[]"<<endl;
	if(x<0||x>=size_){
		cout<<"Error:Array Out of Bounds"<<endl;
		return this->size_;
	}
	//cout<<"Inside []"<<endl;
	return data_[x];
}

Arrays* Arrays::copy(){
	cout<<"Inside Virtual Copy"<<endl;
	Arrays *a=new Arrays(*this);
	return a;
}
Arrays::Arrays(const Arrays& z){
cout<<"inside Copy Constructor:"<<endl;
this->size_=z.size_;
this->data_=new int[z.size_];
for (int i=0;i<z.size_;i++)
	data_[i]=z.data_[i];

}
