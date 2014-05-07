/*
Write Training Data to CSV for Onset or Tartini Classes,

@filename = Name of the CSV file
@pathToWriteTo = The place where the CSV will be written
@trainingDatafolderPath = The folder of samples (Training Data) for information extraction
@mirCLass = Onsets or Tartini passed in for SCMIR extraction

Part of the AI Compressor Plugin Project.
© 2013-2014, Candidate Number 19329
Music Informatics Final Year Project, University of Sussex, Spring 2014
*/

WriteTrainingDataOnsetTartini {

	*new{arg fileName, pathToWriteTo, trainingDatafolderPath, mirClass;
		^super.newCopyArgs.write(fileName, pathToWriteTo, trainingDatafolderPath, mirClass);
	}

	write {arg fileName, pathToWriteTo, trainingDatafolderPath, mirClass;

		// Local variables
		var trainingData = List();
		var trainingDataString = "";
		var scMirTartini;

		"Extracting Information From Audio".postln;
		// Get All files within the folder
		trainingDatafolderPath = trainingDatafolderPath++"*";

		// Loop through every file in folder and extract audio information, store as training data
		trainingDatafolderPath.pathMatch.collect{arg i;
			scMirTartini = SCMIRAudioFile(i, [[mirClass]]);
			scMirTartini.extractFeatures();
			// change to .mean/.median etc for what suits best
			trainingData.add(scMirTartini.featuredata.mean);
		};

		// Organise the data into String for CSV File
		trainingData.size.do{arg i;
			trainingDataString = trainingDataString++","++trainingData[i];
		};

		// Write Data to CSV Files
		trainingDataString.writeArchive(pathToWriteTo +/+ fileName ++ ".csv");
		"Writing Training Data to CSV File Complete.".postln;
	}
}