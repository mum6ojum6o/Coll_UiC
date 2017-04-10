#include <iostream>
/*#include "MyString.h"
#include "MyString.cpp"
#include "Date.h"
#include "Date.cpp"*/
//#include "Car.h"
//#include "Car.cpp"
#include "Car.h"
#include "CarPtr.h"
#include "Date.h"
#include <fstream>
using namespace std;

const char* returnFileName(int);
void loadSetup(CarPtr[]);
int objInMemory[]={-1,-1}; //keeps track of objects in the memory
int objCounter=0;//keeps tracks of the objects created.
int objsInCache=0;//keeps track of objects in Cache
int main(){
	char input;
	char *make = new char[100];
	char *model = new char[100];
	int id,year,dd,mm,yyyy,cost;
	CarPtr cArray[5];
	loadSetup(cArray);
	id=objCounter+1;
	//objInCache=objCounter;
	 //array tracks which object # is in memory

	int objsInCache=0;

	int currCar=-1;
	while(true){
		
	//loop keeps prompting options till the user enters q
		cout<<"******************************************"<<endl;
		cout<<"		VCS"<<endl;
		cout<<"******************************************"<<endl;
		cout<<"(l) 		List cars"<<endl;
		cout<<"(c) 		Create a car"<<endl;
		cout<<"[1..5]		Edit a car"<<endl;
		cout<<"(s) 		Save Car Details"<<endl;
		cout<<"(p)		Update Price of the Current Car"<<endl;
		cout<<"(q) 		Exit VCS"<<endl;
		cout<<"****************************************"<<endl;
		cout<<"enter an Option:"<<endl;

		cin>>input;

		if (input=='c' ){
			//entering car details...
			if (objCounter<5){
				cout<<" id:"<<id<<endl;
				cout<<"Enter year:";
				cin>>year;
				cout<<"Enter date of acquisition (mm dd yyyy)";
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
				 ofstream outFile(returnFileName(id));
				 outFile<< *cArray[objCounter];
					outFile.close();
				 //if > 2 objects in memory, delete the object...
				if(objInMemory[0]==-1)
					objInMemory[0]=objCounter;
				else if (objInMemory[0]!=-1 && objInMemory[1]==-1)
					objInMemory[1]=objCounter;
				//if (objsInCache>1)
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
		//edit option...
		else if(input == '1' || input=='2' ||input =='3'||input=='4'||input=='5'){
				int objNum=input;
				currCar=objNum-49; //variable to keep track of the current car selected.
				//cout<<"Current car is:"<<currCar;
				//if the car id does not exist....
				if(!cArray[objNum-49].getLocId())
					cout<<"Car ID:"<<input<<" does not exist.";
				else{
					cout<<"Current Car details as follows:"<<endl;
					cout<<"ID:"<<cArray[objNum-49]->getId()<<endl;
					cout<<"Make:"<<cArray[objNum-49]->getMake()<<endl;
					cout<<"Model:"<<cArray[objNum-49]->getModel()<<endl;
					cout<<"Cost: $"<<cArray[objNum-49]->getCost()<<endl; 
					cout<<"Year:"<<cArray[objNum-49]->getYear()<<endl;
					cout<<"Date of acquisition:"<<cArray[objNum-49]->getDate().getMon()<<"/"<<cArray[objNum-49]->getDate().getDay()<<"/"<<cArray[objNum-49]->getDate().getYear()<<endl;
					//deleting an object in memory
					if(objInMemory[0]==objNum-49 || objInMemory[1]==objNum-49)
						cout<<"Already in Memory";
					else if(objInMemory[0]!=-1 && objInMemory[0]!=(objNum-49)){
						cArray[objInMemory[0]].delCar(); //ensuring 2 instances in memory commitment!!
						objInMemory[0]=objNum-49;
					}
					else if (objInMemory[1]!=-1 && objInMemory[1]!=(objNum-49))	{
						cArray[objInMemory[1]].delCar(); //ensuring 2 instances in memory commitment!!
						objInMemory[1]=objNum-49;
					}
				}	
			}
		else if(input =='l'){
		//listing all cars...
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
				//check if object was in cache, then skip
				if(i==objInMemory[0] || i==objInMemory[1])
					continue;
				else //if object was not in memory then delete car...
					cArray[i].delCar();
			}

			
	}
	else if(input=='s'){
		//saving the
		//cout<<"objcache value:"<<objInMemory[0]<<endl;
		if(objInMemory[0]!=-1){
			remove(returnFileName(objInMemory[0]+1));
			ofstream out(returnFileName(objInMemory[0]+1));
			out<<*(cArray[objInMemory[0]]);
			out.close();
		}
		if(objInMemory[1]!=-1){
			remove(returnFileName(objInMemory[1]+1));
			ofstream o(returnFileName(objInMemory[1]+1));
			o<< *(cArray[objInMemory[1]]);
			o.close();
		}
	}
	else if(input=='p'){
		int cost;


		if (currCar==-1){
			cout<<"No car selected"<<endl;
			cin>>cost;
		}
		else{
			cout<<"Current car is:"<<currCar;
			cout<<"ID:"<<cArray[currCar]->getId()<<endl;
			cout<<"Make:"<<cArray[currCar]->getMake()<<endl;
			cout<<"Model:"<<cArray[currCar]->getModel()<<endl;
			cout<<"Cost: $"<<cArray[currCar]->getCost()<<endl; 
			cout<<"Year:"<<cArray[currCar]->getYear()<<endl;
			cout<<"Date of acquisition:"<<cArray[currCar]->getDate().getMon()<<"/"<<cArray[currCar]->getDate().getDay()<<"/"<<cArray[currCar]->getDate().getYear()<<endl;
			cout<<"Enter new Price: $";
			cin>>cost;
			cArray[currCar]->setCost(cost);
		}
	}
} //loop ends

	//avoiding leaks..
delete make;
delete model;

return 0;
}

//function that returns a filename on the basis of ID...
const char* returnFileName(int i){
	if (i==1)
		return "1.txt";
	else if(i==2)
		return "2.txt";
	else if(i==3)
		return "3.txt";
	else if(i==4)
		return "4.txt";
	else if(i==5)
		return "5.txt";

	return "";
}


// this function loads object details based on the objects created in the past.
void loadSetup(CarPtr x[]){
	int fileCounter=0;
	for (int i=0;i<5;i++){
		ifstream in;
		in.open(returnFileName(i+1));
		int id;
		if(in.is_open()){
			//cout<<"load Setup: in if"<<endl;
			objCounter++;
			fileCounter++;
			in>>id;
			x[i].setId(id);
			if (i<2){
				objInMemory[i]=i;
				objsInCache++;
			}
		}
		else{
			break;
		}

		in.close();
	}
	//
	if(fileCounter==0)
			cout<<"No files found!!Its a new Beginning..."<<endl;
	else
			cout<<fileCounter<<" files loaded!"<<endl;

}
