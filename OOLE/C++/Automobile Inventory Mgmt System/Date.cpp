#include "Date.h"
using namespace std;
Date::Date(){
	day_=1;
	month_=1;
	year_=1900;
}
Date::Date(int mm,int dd, int yy){
	month_=mm;
	day_=dd;
	year_=yy;
}
ostream& operator<<(ostream& out, Date& d){
	out<<d.month_<<endl;
	out<<d.day_<<endl;
	out<<d.year_<<endl;
	return out;
}
