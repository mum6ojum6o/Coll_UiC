#ifndef CARPTR_H
#define CARPTR_H
class Car; //forward declaration

class CarPtr{

public:
	CarPtr();
	CarPtr(int);
	~CarPtr();
	Car* operator->() ;
	Car& operator*() ;
	void delCar();
	void setId(int id);
	int getLocId();
	void loadObj();
protected:
	Car *aCar;
	int id_;
	CarPtr(const CarPtr&); //No Copy Constructor
	CarPtr& operator=(const CarPtr&);//No Assignment operator

};
 #endif
