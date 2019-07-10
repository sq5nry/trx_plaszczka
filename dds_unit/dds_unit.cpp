#include <iostream>
#include <errno.h>
#include <unistd.h>
#include <string>
#include "AD9954.h"

using namespace std;

AD9954 DDS(0, 2);
const long IF = 9000000L;
const int PITCH = 250;

int main(int argc, char *argv[]) {
    cout << "You have entered " << argc  << " arguments:" << "\n"; 
  
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
		cout << "fixed freq 10MHz"<<endl;
	}
	cout << "freq=" <<freq<<endl;

	cout << "adjusting pitch = " << PITCH << "Hz" << endl;
	if (freq<IF) freq += PITCH;
	if (freq>IF) freq -= PITCH;
	cout << "adjusted pitch=" << freq << endl;
	
	cout << "adjusting IF = " << IF << "Hz" << endl;
	if (freq<IF) freq = IF-freq;
	if (freq>IF) freq = IF+freq;
	cout << "adjusted freq=" << freq << "Hz" << endl;

	DDS.setFreq(freq);
}

