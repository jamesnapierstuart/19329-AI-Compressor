AI COMPRESSOR: Analysis Application

CANDIDATE NUMBER: 19329
MUSIC INFORMATICS FINAL YEAR PROJECT
University of Sussex
MAY 2014

BUILT IN SuperCollider 3.6.6
MAC OSX VERSION 10.8.4
--------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------
AI Compressor Analysis About:

To be used alongside the AU plugin AI Compressor, this analysis application takes audio input and analyses in non real-time different features of the musical content. Machine listening approaches are employed with music information retrieval techniques to classify the instrument in the audio and calculate the most suitable compressor parameters.
Drag and Drop audio in and hit 'analyse' to perform analysis and compression calculations. If an AU plugin has been set up, then it will send an OSC Port Number which this app listens for. When analysis is complete, this analysis app will send over OSC the most suitable compression parameters to the AU plugin and will make the AU plugin's compressor parameters update immediately.

In addition, this application can be used individually for pure analysis and compressor setting calculation. Very useful, when using different compressor plugins rather than the AI compressor plugin provided. For educational purposes, please see the Manual Guide that informs how the compressor parameter values are calculated to enhance learning within the compression domain. 

--------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------
Install:

The Disk Image file will give the option of dragging and dropping the AI Compressor to the users application folder:

1. Drag the AI Compressor Analysis App into your Applications folder.
-------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------

Development of this SuperCollider Standalone application was made possible from building upon Dathinaios' sc_osx_standalone template (https://github.com/dathinaios/sc_osx_standalone, last accessed 24/04/14). And running the .sh script provided with Platypus Application (http://sveinbjorn.org/platypus, last accessed 24/04/14).

Modifications had to be made to change the Scope Method to look in the new scsynth directory, and similarly synthdef libraries were re-configured.
Notes from Dathinaios' original read me regarding the Linux standalone implementation it was based upon can be found below:
-------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------

# SuperCollider OSX Standalone

Modified from the linux version:
https://github.com/miguel-negrao/linuxStandalone

[SuperCollider ] (http://supercollider.sourceforge.net/) version `3.6.6`

## Usage

- Run the standalone with `run.sh` script.
- Modify `init.scd` to customize.
- [Platypus ](http://sveinbjorn.org/platypus) can be used to convert the script based structure into a native OSX application.

--------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------