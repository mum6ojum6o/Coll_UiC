/*
 * Array.h
 *
 *  Created on: Apr 28, 2017
 *      Author: Arjan
 */

#ifndef ARRAY_H_
#define ARRAY_H_

class Arrays: public Collections{

public:
	int *data_;
	Arrays(int n);
	Arrays(const Arrays&);
	void add(int);
	virtual int& operator[](const int);
	virtual ~Arrays();
	Arrays& operator=(Arrays&);
	virtual Arrays* copy();

};

#endif /* ARRAY_H_ */
