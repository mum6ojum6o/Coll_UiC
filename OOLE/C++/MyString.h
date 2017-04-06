#ifndef MYSTRING_H
#define MYSTRING_H
class MyString{

public:
	MyString();
	MyString(char *);
	MyString(const MyString &);
	char *getData(){ return data_;}
	MyString& setData(char*);
private:
	char *data_;
	int size_;
};
#endif