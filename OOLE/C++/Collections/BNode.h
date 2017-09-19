/*
 * BNode.h
 *
 *  Created on: Apr 26, 2017
 *      Author: Arjan
 */

#ifndef BNODE_H_
#define BNODE_H_

class BNode{
public:
	int data_;
	BNode *left_,*right_,*parent_;
	BNode();
	BNode(int);
	~BNode();
	void setData(int);
	int& getData();
};



#endif /* BNODE_H_ */
