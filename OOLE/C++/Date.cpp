#include "Date.h"
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