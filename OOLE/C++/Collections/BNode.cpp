/* BNode.cpp
Apr 26, 2017
Arjan
*/
#include<iostream>
#include "BNode.h"
using namespace std;


BNode::BNode(){
	data_=0;
	left_=nullptr;
	right_=nullptr;
}

BNode::BNode(int x){
	data_=x;
	left_=nullptr;
	right_=nullptr;
	parent_=nullptr;
}
void BNode::setData(int x){

	data_=x;
	left_=nullptr;
	right_=nullptr;
	parent_=nullptr;

}
int& BNode::getData(){
return data_;
}
BNode::~BNode(){}

