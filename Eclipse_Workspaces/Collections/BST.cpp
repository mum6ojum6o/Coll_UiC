/* BST.cpp
Apr 26, 2017
Arjan
*/
#include<iostream>
#include "Collections.h"
#include "BST.h"
#include "BNode.h"
using namespace std;
int BST::counter = 0;
bool BST::found = false;
BNode* BST::temp1;
BST::BST():Collections(){ root_=nullptr;
	cout<<"Inside the Constructor"<<endl;
}
 void BST :: add(int x){
	 cout<<"You decided to enter "<<x<<endl;
	 	//cout<<"the size is="<<size_<<endl;
	 	this->addToTree(x,root_);

} //add ends


void BST::addToTree(int x, BNode* tempRoot){
	/*if(tempRoot!=nullptr)
		cout<<"at:"<<tempRoot->getData()<<endl;*/
	 if(root_==nullptr)
			 root_=new BNode(x);
	/* else if(tempRoot==nullptr){
		 	 cout<<"creating new Node"<<x<<endl;
		 	 tempRoot= new BNode(x);
	 }*/
		 else if (x<tempRoot->getData()){ //insert to the left sub-tree
			 cout<<"checking LST"<<endl;
			 if (tempRoot->left_==nullptr){
				 //cout<<tempRoot->getData()<<"'s LST is null"<<endl;
				 size_++;
				 BNode* newNode=new BNode(x);
				 tempRoot->left_=newNode;
				 newNode->parent_=tempRoot;
			 }
			 else{
				 //cout<<tempRoot->getData()<<"'s LST is not null"<<endl;
				 addToTree(x,tempRoot->left_);
			 }
		}//elseif ends
		 else if(x>tempRoot->getData()){//insert to the left sub-tree
			 //cout<<"Checking RST"<<endl;
			 if(tempRoot->right_==nullptr){
				 size_++;
				 //cout<<tempRoot->getData()<<"'s RST is null"<<endl;
				 BNode* newNode=new BNode(x);
				 tempRoot->right_=newNode;
				 newNode->parent_=tempRoot;
			 }
			 else{
				 //cout<<tempRoot->getData()<<"'s RST is not null"<<endl;
				 addToTree(x,tempRoot->right_);
			 }
		 }//else if ends

}//addToTree Ends.

void BST::inOrder(BNode* temp){
	if(temp==nullptr)
		return;
	inOrder(temp->left_);
	cout<<temp->getData()<<" ";
	inOrder(temp->right_);
}
void BST::delInOrder(BNode* temp){
	if(temp==nullptr)
			return;
	delInOrder(temp->left_);
	delete temp;
	delInOrder(temp->right_);
}
 BST::~BST(){
	 delInOrder(root_);
 }

 int BST::doesContain(int x){
	 int r = 0;
	 r = check(x,root_);
	 return r;
 }
 int BST::check(int x, BNode* temp){
	 int result=-1;
	 if(temp->getData()==x){
		 cout<<"equality "<<temp->getData()<<endl;
		 result=1;
		 return result;
	 }
	 else if(x<temp->getData()){
		 if(temp->left_==nullptr){
			return 0;
		 }
		else{
			cout<<"LST: "<<endl;
			result = check(x,temp->left_);
		}
	 }
	 else if(x>temp->getData()){
		 if(temp->left_==nullptr){
		 			return 0;
		 		 }
		 else{
			 	 cout<<"RST: "<<temp->getData()<<endl;
		 		result = check(x,temp->right_);
		 	}

	 }
	 return result;
 }

 int& BST::operator[](const int i){
	 BST::counter=0;
	// cout<<"Root"<<root_->getData()<<endl;
	 if(i<0 || i> size_)
		 cout<<"Array out of Bounds Exception!"<<endl;
	 else{
		(getNode(i,root_));
	 }

	 return temp1->getData();
 }



BNode* BST::getNode(const int i,BNode* temp){
	//cout<<"at "<<temp->getData()<<endl;
	//BNode* temp2;
	if(temp->left_!=nullptr){
		getNode(i,temp->left_);

	}

//	cout<<"getNode "<<temp2->getData()<<endl;
	if (BST::counter==i){
		//cout<<"returning "<<endl;
		temp1 = temp;
		//cout<<"Temp1 changed"<< temp1->getData();
	}
		BST::counter++;
	if(temp->right_!=nullptr){
		getNode(i,temp->right_);

	}
//	return temp2;
}
