/*
 * BST.h
 *
 *  Created on: Apr 26, 2017
 *      Author: Arjan
 */
#include "BNode.h"
#ifndef BST_H_
#define BST_H_

class BST:public Collections{

private:
	static BNode* temp1;
	static int counter;
	static bool found;
public:
	BST();
	virtual ~BST();
	virtual void add(int);
	void addToTree(int, BNode*);
	BNode* root_;
	void inOrder(BNode*);
	void delInOrder(BNode*);
	int doesContain(int n);
	int check(int,BNode*);
	virtual int& operator[](const int i);
	BNode* getNode(const int,BNode*);
	void proceedToLeft(BNode* );
	BST(const BST&);
	void BSTCopy(BNode*);
	virtual BST* copy();
	BST& operator=(const BST&);
};

#endif /* BST_H_ */
