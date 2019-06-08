#include <iostream>
#include <errno.h>
#include <unistd.h>
#include <string>
#include "AD9954.h"

using namespace std;

AD9954 DDS(0, 2);

int main(int argc, char *argv[])
{
    cout << "You have entered " << argc 
         << " arguments:" << "\n"; 
  
    for (int i = 0; i < argc; ++i) 
        cout << argv[i] << "\n"; 
	
	cout << "dds unit test" <<endl;
	DDS.initialize(500000000);
	unsigned long freq = 10000000L;
	

	
	
	if (argc == 2) {
		cout << "custom freq=" <<argv[1]<<endl;
		string::size_type sz; 
	    freq = stol(argv[1], &sz);
	} else {
		cout << "10MHz"<<endl;
	}
	cout << "freq=" <<freq<<endl;
	DDS.setFreq(freq);
}

