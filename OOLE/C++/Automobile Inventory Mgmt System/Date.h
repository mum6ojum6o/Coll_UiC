#include<iostream>
#ifndef DATE_H
#define DATE_H
class Date{

public:
	Date();
	Date(int,int,int);
	int getDay(){return day_;}
	int getMon(){return month_;}
	int getYear(){return year_;}
	friend std::ostream& operator<<(std::ostream& , Date&);
	
private:
	int day_,month_,year_;
};

#endif
