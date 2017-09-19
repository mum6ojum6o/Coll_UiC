#include "CarPtr.h"
#include "Car.h"
#include "Date.h"
#include <fstream>
 CarPtr::CarPtr() {
 	//cout<<"inside default constructor"<<endl;
	 id_=0;
	 aCar=nullptr;
}
CarPtr::CarPtr(int i):id_(i),aCar(nullptr){}

 Car* CarPtr::operator->()  {
	//cout<<"CarPtr id value:"<<this->id_<<endl;
	if (this->aCar == nullptr && this->id_==0){
		//cout<<"fresh Car object being created";
		aCar = new Car;
	}
	else if(this->aCar == nullptr && this->id_!=0){
		//cout<<"Car object to be fetched from file.";
		aCar = new Car;
		this->loadObj();
	}

	return aCar; 
}
  Car& CarPtr::operator*() {
 	//cout<<"in the other overloaded operator.."<<endl;
	if (aCar==nullptr)
		aCar = new Car;
	return *aCar;
}
 CarPtr::~CarPtr(){
	delete aCar;
}
void CarPtr::delCar(){
	//cout<<"deleting object:"<<endl;
	delete aCar;
		aCar=nullptr;
}
void CarPtr::setId(int id){
	//cout<<"inside carptr setId():"<<endl; 
	id_=id;
}
int CarPtr::getLocId(){ return id_;}

void CarPtr::loadObj(){
	std::cout<<"Loaded from disk:"<<std::endl;
	char *make = new char[100];
	char *model = new char[100];
	std::ifstream in;
	int id,year,dd,mm,yyyy,cost;
		if(id_==1)
			 in.open("1.txt");
		else if(id_==2)
			in.open("2.txt");
		else if(id_==3)
			in.open("3.txt");
		else if(id_==4)
			in.open("4.txt");
		else if(id_==5)
			in.open("5.txt");

		
		in>>id;
		//cout<<"CarPtr:ID from file:"<<id<<endl;
		in>>year;
		in>>make;
		in>>model;
		in>>cost;
		//cout<<"cost from file:"<<cost<<endl;
		in>>mm>>dd>>yyyy;
		in.close();
		aCar->setId(id);
		aCar->setMake(make);
		aCar->setModel(model);
		aCar->setCost(cost);
		aCar->setYear(year);
		aCar->setDate(Date(mm,dd,yyyy));
		in.close();
		delete make;
		delete model;
		//c->setDate(Date(mm,dd,yyyy));

}
