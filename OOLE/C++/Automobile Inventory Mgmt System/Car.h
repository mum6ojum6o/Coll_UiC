#include"MyString.h"
#include"Date.h"
#include <iostream>
#ifndef CAR_H
#define CAR_H

class Car{
	
	public:
		Car();
		Car(int,char *,char *,int,Date ,int,int);
		Car(const Car&);
		~Car();
		int getId(){return id_;}
		int getYear(){return year_;}
		char* getMake(){return make_->getData();}
		char* getModel(){return model_->getData();}
		Date& getDate(){return date_;}
		int getCost(){return cost_;}
		void setId(int);
		void setYear(int);
		void setMake(char*);
		void setModel(char*);
		void setDate(Date);
		void setCost(int);
		friend std::ostream& operator<<(std::ostream& ,  Car&);
		friend std::istream& operator>>(std::istream& , Car&);
	protected:
	int id_;
	MyString *make_;
	MyString *model_;
	int year_;
	Date date_;
	int cost_;
	int picture_;

};

#endif
