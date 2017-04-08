#include <iostream>
/*#include "MyString.h"
#include "MyString.cpp"
#include "Date.h"
#include "Date.cpp"*/
//#include "Car.h"
//#include "Car.cpp"
#include "CarPtr.h"
#include "CarPtr.cpp"
#include <fstream>
using namespace std;



int main(){
	char input;
	char ch;
	char *make = new char[100];
	char *model = new char[100];
	int id=1,year,dd,mm,yyyy,cost;
	CarPtr cArray[5];
	//CarPtr *cArray[3];
	int objInMemory[]={-1,-1}; //array tracks which object # is in memory
	//int idCache[]={-1,-1};//tracks the ids currently in the memory... 
	int objsInCache=0;
	int objCounter=0;
	while(1){
		system("cls");
	//Car c(1655,"Maruti","Swift",2015,Date(06,06,2014));
		cout<<"******************************************"<<endl;
		cout<<"		VCS"<<endl;
		cout<<"******************************************"<<endl;
		cout<<"(l) 		List cars"<<endl;
		cout<<"(c) 		Create a car"<<endl;
		cout<<"[1..5]	Edit a car"<<endl;
		cout<<"(s) 		Save Car Details"<<endl;
		cout<<"(q) 		Exit VCS"<<endl;
		cout<<"****************************************"<<endl;
		cout<<"enter an Option:"<<endl;

		cin>>input;

		if (input=='c' ){
			if (objCounter<5){
				cout<<" id:"<<id;
				cout<<"Enter year:";
				cin>>year;
				cout<<"Enter date of acquisition (mm/dd/yyyy)";
				cin>>mm>>dd>>yyyy;
				cout<<"Enter make:";
				cin>>make;
				cout<<"Enter Model:";
				cin>>model;
				cout<<"Enter cost: $";
				cin>>cost;

				cArray[objCounter]->setId(id);
				cArray[objCounter]->setYear(year);
				cArray[objCounter]->setMake(make);
				cArray[objCounter]->setModel(model);
				cArray[objCounter]->setCost(cost);
				cArray[objCounter]->setDate(Date(mm,dd,yyyy));
				cArray[objCounter].setId(id);	
				 if(id==1){
					ofstream outFile("1.txt");
					outFile<<*cArray[objCounter];
					outFile.close();
				}
				else if (id==2){
					ofstream outFile("2.txt");
				
				outFile<<*cArray[objCounter];
					outFile.close();
				}
				else if (id==3){
					ofstream outFile("3.txt");
				outFile<<*cArray[objCounter];
					outFile.close();
				}
				else if (id==4){
					ofstream outFile("4.txt");
					outFile<<*cArray[objCounter];
					outFile.close();
				}
				else if (id==5){
					ofstream outFile("5.txt");
					outFile<<*cArray[objCounter];
					outFile.close();
				}
				//if > 2 objects in memory, delete the object...
				if(objInMemory[0]==-1)
					objInMemory[0]=objCounter;
				else if (objInMemory[0]!=-1 && objInMemory[1]==-1)
					objInMemory[1]=objCounter;
				if (objsInCache>1)
					cArray[objCounter].delCar();
				objCounter++;
				objsInCache++;
				id++;

			}
			else
				cout<<"Inventory Limit Reached!"<<endl;
		}
		else if(input=='q')
			break;
		else if(input == '1' || input=='2' ||input =='3'||input=='4'||input=='5'){
				int objNum=input;
				//if the car id does not exist....
				if(!cArray[objNum-49].getLocId())
					cout<<"Car ID:"<<input<<" does not exist.";
				else{
					cout<<"Car details as follows:"<<endl;
					cout<<"ID:"<<cArray[objNum-49]->getId()<<endl;
					cout<<"Make:"<<cArray[objNum-49]->getMake()<<endl;
					cout<<"Model:"<<cArray[objNum-49]->getModel()<<endl;
					cout<<"Cost: $"<<cArray[objNum-49]->getCost()<<endl; 
					cout<<"Year:"<<cArray[objNum-49]->getYear()<<endl;
					cout<<"Date of acquisition:"<<cArray[objNum-49]->getDate().getMon()<<"/"<<cArray[objNum-49]->getDate().getDay()<<"/"<<cArray[objNum-49]->getDate().getYear()<<endl;
					if(objInMemory[0]==objNum-49 || objInMemory[1]==objNum-49)
						cout<<"Already in Memory";
					else if(objInMemory[0]!=-1 && objInMemory[0]!=(objNum-49)){
						cArray[objInMemory[0]].delCar(); //2 instances in memory commitment!!
						objInMemory[0]=objNum-49;
					}
					else if (objInMemory[1]!=-1 && objInMemory[1]!=(objNum-49))	{
						cArray[objInMemory[1]].delCar(); //2 instances in memory commitment!!
						objInMemory[1]=objNum-49;
					}
				}	
				cout<<"Inside Edit:";
				cin>>ch;
		}
	else if(input =='l'){
		
		if (objCounter!=0){

			for (int i=0;i<objCounter;i++){
				
				cout<<"ID:"<<cArray[i]->getId()<<endl;
				cout<<"Make:"<<cArray[i]->getMake()<<endl;
				cout<<"Model:"<<cArray[i]->getModel()<<endl;
				cout<<"Cost: $"<<cArray[i]->getCost()<<endl; 
				cout<<"Year:"<<cArray[i]->getYear()<<endl;
				cout<<"Date of acquisition:"<<cArray[i]->getDate().getMon()<<"/"<<cArray[i]->getDate().getDay()<<"/"<<cArray[i]->getDate().getYear()<<endl;
				
			}

		}
		else
			cout<<"No cars"<<endl;
		//delete the remaining objects in memory...
		for(int i=0;i<5;i++){
			if(i==objInMemory[0] || i==objInMemory[1])
				continue;
			else
			cArray[i].delCar();
		}

		cout<<"Press any Key to continue...."<<endl;
		cin>>ch;
	}
	
} //loop ends

delete make;
delete model;

return 0;
}



/*int main(){
	char *make = new char[100];
	char *model = new char[100];
	int id,year,dd,mm,yyyy;
	CarPtr c;
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

	c->setId(id);
	c->setMake(make);
	c->setModel(model);
	c->setYear(year);
	c->setDate(Date(mm,dd,yyyy));
	c.setId(id);
	
	cout<<"ID:"<<c.getLocId()<<endl;
	
	/*cout<<"Make:"<<c->getMake()<<endl;
	cout<<"Model:"<<c->getModel()<<endl;
	cout<<"Year:"<<c->getYear()<<endl;
	cout<<"Date of acquisition:"<<c->getDate().getMon()<<"/"<<c->getDate().getDay()<<"/"<<c->getDate().getYear()<<endl;*/
	
	//Car c(1,"Ford","ShelbyGT",1960,Date(10,29,1986));
	/*cout<<"About to write to a file"<<endl;
	ofstream outFile("1.txt");
	outFile<<*c;
	outFile.close();*/
	
	/*c.delCar();
	cout<<"ID from Car:"<<c->getId()<<endl;
	cout<<"Year:"<<c->getYear()<<endl;
	cout<<"Make:"<<c->getMake()<<endl;
	cout<<"Model:"<<c->getModel()<<endl;
	*/
	/*
cout<<"reading from file"<<endl;
	ifstream in("ObjData.txt");
	in>>id;
	in>>year;
	in>>make;
	in>>model;
	in.close();
	cout<<"id:"<<id<<endl;
	cout<<"year:"<<year<<endl;
	cout<<"make:"<<make<<" model:"<<model<<endl;*/

	/*return 0;
}*/
