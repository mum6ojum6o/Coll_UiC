/* main.cpp
Apr 26, 2017
Arjan
*/
#include<iostream>
#include"Collections.h"
#include"BST.h"
#include"Array.h"
using namespace std;
int someFunc(int x);
int main(){

/*//	Collections *b= new BST();

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
	BST *c = (BST*)b->copy();
	BST x;

	b->map(&someFunc);

	cout<<"BST b"<<endl;
	for(int i=0;i<b->size_;i++){
		cout<<"element "<< (*b)[i]<<endl;
	}
	//cout<< b->contains(102);
	c->map(&someFunc);
	x=*c;
cout<<"C's elements"<<endl;
	for(int i=0;i<c->size_;i++){
			cout<<"element "<< (*c)[i]<<endl;
		}
	cout<<"X's elements"<<endl;
		for(int i=0;i<x.size_;i++){
				cout<<"element "<< x[i]<<endl;
			}

	cout<< b->contains(102);


	delete b;
	delete c;*/

/*Arrays a(3);
a[0]=100;
a[1]=20;
a[2]=40;
Arrays z(5);
for (int i=0;i<5;i++)
	z[i]=i;

a=z;
cout<<"Printing A"<<endl;
for(int i=0;i<a.size_;i++){
	cout<<a[i]<<" "<<endl;
}*/

	Collections *c=new Arrays(3);
	Arrays *a=new Arrays(5);
	for(int i=0; i<a->size_;i++)
		(*a)[i]=i++;
	(*c)[0]=100;
	(*c)[1]=101;
	(*c)[2]=102;
	a=dynamic_cast<Arrays*>(c)->copy();
	cout<<c->size_<<endl;
	c->map(&someFunc);
	//dynamic_cast<Arrays*>(c);

	cout<<(*c)[0]<<endl;
	cout<<"A's elements"<<endl;
	for(int i=0; i<a->size_;i++)
	cout<<(*a)[i]<<endl;
	cout<<c->contains(102)<<endl;
	//(Arrays)c[0];
	//c[0]=100;
	delete a;
	delete c;
	return 0;
}
int someFunc(int x){
	x+=2;
return x;
}
