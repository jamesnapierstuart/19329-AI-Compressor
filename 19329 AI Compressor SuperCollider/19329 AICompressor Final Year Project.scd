/*
Creates an Instance of the AI Compressor Analysis Application.

Part of the AI Compressor Plugin Project.
© 2013-2014, Candidate Number 19329
Music Informatics Final Year Project, University of Sussex, Spring 2014
*/


// To start, create a new AICompressor.
// Pass in the required machine learning CSV files as arguments (Found in the machine learning folder).
// (Replace the arguments to paths on your own local system).

//@periodicOnsetTraining = The machine learning CSV file path for periodic training data
//@nonPeriodicOnsetTraining = The machine learning CSV file path for non periodic training data
// @kickTartiniTraining = The machine learning CSV file path for kick tartini training data
// @snareTartiniTraining = The machine learning CSV file path for snare tartini training data
// @eguitarMfccTraining = The machine learning CSV file path for eguitar MFCC 13 training data
// @vocalMfccTraining = The machine learning CSV file path for vocal MFCC13 training data

AICompressor("/Users/jamie/Desktop/Machine Learning/NonPeriodic Periodic Binary Onset Training Data/PeriodicTrainingOnset.csv", "/Users/jamie/Desktop/Machine Learning/NonPeriodic Periodic Binary Onset Training Data/nonPeriodicTrainingOnset.csv", "/Users/jamie/Desktop/Machine Learning/Kick Snare Binary Tartini Training Data/kickTrainingTartini.csv", "/Users/jamie/Desktop/Machine Learning/Kick Snare Binary Tartini Training Data/snareTrainingTartini.csv", "/Users/jamie/Desktop/Machine Learning/Eguitar Vocal Binary MFFC 13 Training Data/EguitarMFCCTrainingData.csv", "/Users/jamie/Desktop/Machine Learning/Eguitar Vocal Binary MFFC 13 Training Data/VocalMFCCTrainingData.csv")
