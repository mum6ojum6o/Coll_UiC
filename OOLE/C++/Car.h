#include"MyString.h"
#include"Date.h"
#ifndef CAR_H
#define CAR_H
class Car{
	
	public:
		Car();
		Car(int,char *,char *,int,Date d);
		Car(const Car&);
		~Car();
		int getId(){return id_;}
		int getYear(){return year_;}
		char* getMake(){return make_->getData();}
		char* getModel(){return model_->getData();}
		Date& getDate(){return date_;}
		void setId(int);
		void setYear(int);
		void setMake(char*);
		void setModel(char*);
		void setDate(Date);
	protected:
	int id_;
	MyString *make_;
	MyString *model_;
	int year_;
	Date date_;
	int picture_;

};

#endif