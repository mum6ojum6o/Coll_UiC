/*
 * Collections.h
 *
 *  Created on: Apr 26, 2017
 *      Author: Arjan
 */

#ifndef COLLECTIONS_H_
#define COLLECTIONS_H_
class Collections{
	public:
	int size_;
	Collections();
	virtual ~Collections();
	virtual void add(int)=0;
	bool contains(int);
	Collections(const Collections&);
	virtual int& operator[](int)=0;
	int someFunc(int);
	Collections& map(int (*fn)(int));
};




#endif /* COLLECTIONS_H_ */
