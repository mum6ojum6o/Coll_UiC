

#include"Date.h"
#include"MyString.h"
#include"Car.h"

using namespace std;
Car::Car(){
	id_=0;
	make_=NULL;
	model_=NULL;
	year_=0;
	cost_=0;
	picture_=0;
}
Car::Car(int id, char* make, char* model, int year, Date d, int cost, int pic)
	:id_(id),year_(year),date_(d),cost_(cost),picture_(pic)
	{
		make_=new MyString;
		model_=new MyString;
		make_->setData(make);
		model_->setData(model);
		picture_=0;
		

	}
Car::~Car(){
	delete make_;
	delete model_;
}
void Car::setId(int id){
	this->id_=id;
}
void Car::setYear(int year){
	year_=year;
}
void Car::setMake(char* make){
	if (make_==NULL)
		make_=new MyString;
	make_->setData(make);
}
void Car::setModel(char* model){
	if (model_==NULL)
		model_ =new MyString;
	model_->setData(model);
}
void Car::setDate(Date d){
date_ = d;
}

 ostream& operator<<(ostream& out, Car& aCar){
	out<<aCar.getId()<<endl;
	out<<aCar.getYear()<<endl;
	out<<aCar.getMake()<<" "<<aCar.getModel()<<endl;
	out<<aCar.getCost()<<endl;
	out<<aCar.getDate()<<endl;
	return out;
}

void Car::setCost(int cost){
	cost_=cost;
}
/*friend istream& operator>>(istream& in, Car& aCar){
	in>>aCar.id_;
	in>>aCar.year_;
	//in>>aCar.make_>>aCar.model_;
	return in;
}*/
