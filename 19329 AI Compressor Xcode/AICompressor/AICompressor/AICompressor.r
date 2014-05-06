#include <AudioUnit/AudioUnit.r>

#define RES_ID    1000
#define COMP_TYPE 'aufx'
#define COMP_SUBTYPE 'TEST'
#define COMP_MANUF 'TEST'
#define VERSION 0xFFFFFF

#define NAME "Jamie Stuart 19329 AUDIO: AI COMPRESSOR"
#define DESCRIPTION "AU Intelligent Compressor that sets Parameters Automaically by Communicating with External Machine Listening Application"
#define ENTRY_POINT "AICompressorEntry"

#include "AUResources.r"
