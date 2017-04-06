#include <iostream>
/*#include "MyString.h"
#include "MyString.cpp"
#include "Date.h"
#include "Date.cpp"*/
#include "Car.h"
#include "Car.cpp"
using namespace std;
int main(){
	char input;
	/*MyString s;
	MyString s2("Hello Arjan!!");
	MyString s3(s2);
	cout<<"S3's string:"<<s3.getData()<<endl;
	Date d(9,27,1988);
	cout<<"The entered date:"<<d.getDay()<<"/"<<d.getMon()<<"/"<<d.getYear()<<endl;
	Date *d1(&d);
	cout<<"Copied date:"<<d1->getDay()<<"/"<<d1->getMon()<<"/"<<d1->getYear()<<endl;*/
	char *make = new char[100];
	char *model = new char[100];
	int id,year,dd,mm,yyyy;
	Car cArray[2];
	int objCounter=0;
	while(1){
	//Car c(1655,"Maruti","Swift",2015,Date(06,06,2014));
		cout<<"******************************************"<<endl;
		cout<<"		VCS"<<endl;
		cout<<"******************************************"<<endl;
		cout<<"(l) List cars"<<endl;
		cout<<"(c) Create a car"<<endl;
		cout<<"(e) Edit a car"<<endl;
		cout<<"(q) Exit VCS"<<endl;
		cout<<"****************************************"<<endl;
		cout<<"enter an Option:"<<endl;

		cin>>input;

		if (input=='c' ){
			if (objCounter<2){
				cout<<"Enter id:";
				cin>>id;
				cout<<"Enter year:";
				cin>>year;
				cout<<"Enter date of acquisition (mm/dd/yyyy)";
				cin>>mm>>dd>>yyyy;
				cout<<"Enter make:";
				cin>>make;
				cout<<"Enter Model:";
				cin>>model;
				cArray[objCounter].setId(id);
				cArray[objCounter].setYear(year);
				cArray[objCounter].setMake(make);
				cArray[objCounter].setModel(model);
				cArray[objCounter].setDate(Date(mm,dd,yyyy));
				objCounter++;
			}
			else
				cout<<"Inventory Limit Reached!"<<endl;
		}
		else if(input=='q')
			break;

	else if(input =='l'){
		if (objCounter!=0){
			for (int i=0;i<objCounter;i++){
				cout<<"ID:"<<cArray[i].getId()<<endl;
				cout<<"Make:"<<cArray[i].getMake()<<endl;
				cout<<"Model:"<<cArray[i].getModel()<<endl;
				cout<<"Year:"<<cArray[i].getYear()<<endl;
				cout<<"Date of acquisition:"<<cArray[i].getDate().getMon()<<"/"<<cArray[i].getDate().getDay()<<"/"<<cArray[i].getDate().getYear()<<endl;
			}
		}
		else
			cout<<"No cars"<<endl;
	}
	
} //loop ends

delete make;
delete model;

return 0;
}
