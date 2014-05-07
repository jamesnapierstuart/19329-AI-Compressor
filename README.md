19329-AI-Compressor
===================

19329 AI Compressor Final Year Project: Towards Intelligent Autonomous Audio Plugins.                                       
Candidate No. 19329, Music Informatics Final Year Project, University of Sussex, Spring 2014.

The files included within this Git repository, are intended as submission for the University of Sussex, Music Informatics BA Final Year Project 2014. 

Two main programs were created for this project. First the AI Compressor Audio Unit Plugin; A DSP compressor plugin to be run within a DAW that can host AU plugins. It can be ran independently as a compressor plugin, or used alongside the analysis application. It listens for OSC messages from the analysis application and adapts its parameters according to the OSC messages receieved. 

The second program is the AI Compressor Analysis Application; used for instrument classification using machine listening approaches and compressor calculations using music information retieval processes. The application analyses audio inputted, classifies the instrument (currently training data for Kick, Snare, Eguitar and Vocal) and calculates the most suitable compressor parameters based upon info such as instrument classified, mean peak value, rms, average gain reduction etc. After analysis, these compressor values are sent over OSC to the corresponding AU plugin instance that adapts automatically.

How to install the programs can be found below, all include necessary read me's that further detail the functionality implemented.

Install:
===================

1. AI Compressor DMG file:
   For Standalone Mac Application and Audio Unit Plugin Component.

2. AI Compressor AU Component Xcode Project:
   For Xcode AI compressor Audio Unit Component Xcode files.
   To build and install the plugin component yourself.

3. AI Compressor Analysis Application SuperCollider Code:
   The SuperCollider files, to run the AI Compressor Application within SuperCollider.

Extras:
===================
Test samples have been uploaded, which can be used to test the AI Compressor application and Audio Unit Plugin. These were not used as training data examples and feel free to try out your own to understand and evaluate the limitations of the analysis techniques employed.
