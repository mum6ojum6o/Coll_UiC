/* main.cpp
Apr 26, 2017
Arjan
*/
#include<iostream>
#include"Collections.h"
#include"BST.h"
using namespace std;
int someFunc(int x);
int main(){

	Collections *b= new BST();
	//x->add(100);
//	BST *b = new BST();
	b->add(100);
	b->add(50);
	b->add(75);
	b->add(65);
	b->add(150);
	b->add(125);
	b->add(110);
	b->add(140);
	b->add(180);
	b->add(160);
	b->add(200);
//	cout<<b[1]->getData()<<" "<<endl;
	cout<<"Elements added";
	b->map(&someFunc);
	for(int i=0;i<b->size_;i++){
		cout<<"element "<< (*b)[i]<<endl;
	}
	cout<< b->contains(100);

	delete b;
return 0;
}
int someFunc(int x){
	x+=2;
return x;
}
