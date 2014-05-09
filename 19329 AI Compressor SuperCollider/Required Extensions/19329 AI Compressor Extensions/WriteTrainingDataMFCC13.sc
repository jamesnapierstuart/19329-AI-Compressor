/*
Write Training Data to CSV for MFCC data with 13 coefficients.

@filename = Name of the CSV file
@pathToWriteTo = The place where the CSV will be written
@trainingDatafolderPath = The folder of samples (Training Data) for information extraction
@mirCLass = Onsets or Tartini passed in for SCMIR extraction

Part of the AI Compressor Plugin Project.
© 2013-2014, Candidate Number 19329
Music Informatics Final Year Project, University of Sussex, Spring 2014
*/

WriteTrainingDataMFCC13 {

	*new{arg fileName, pathToWriteTo, trainingDatafolderPath;
		^super.newCopyArgs.write(fileName, pathToWriteTo, trainingDatafolderPath);
	}

	write {arg fileName, pathToWriteTo, trainingDatafolderPath;

		var trainingData = List();
		var scMirTartini;
		var eachTrackCoefficients = List();
		var scMirMfcc;
		var mfcc = 13;
		var trainingDataString = "";

		"Extracting Information From Audio".postln;
		// Get All files within the folder
		trainingDatafolderPath = trainingDatafolderPath++"*";

		// Loop through every file in the folder and extract audio information, store as training data
		trainingDatafolderPath.pathMatch.collect{arg track, num;

			// Local variables
			var coArrayTotal = List();
			var coArrayVarianceTotal = List();
			var coArrayMeanTotal = List();
			var coArrayVarianceMeanTotal = List();
			var coArrayStandardDevTotal = List();
			var coArrayOverallCoefficients = List();
			var loopAmount;

			// Coefficients
			13.do{coArrayTotal.add(List())};
			// Means
			13.do{coArrayMeanTotal.add(List())};
			// Variance
			13.do{coArrayVarianceTotal.add(List())};
			// Variance Means
			13.do{coArrayVarianceMeanTotal.add(List())};
			// Standard Deviations
			13.do{coArrayStandardDevTotal.add(List())};

			scMirMfcc = SCMIRAudioFile(track, [[MFCC, mfcc]]);
			scMirMfcc.extractFeatures();
			loopAmount = scMirMfcc.numframes*scMirMfcc.numfeatures;

			// Loop through Audio (num frames * num features)
			loopAmount.do{arg i;
				case
				{i%13 == 0} {coArrayTotal[i%13].add(scMirMfcc.featuredata[i])}
				{i%13 == 1} {coArrayTotal[i%13].add(scMirMfcc.featuredata[i])}
				{i%13 == 2} {coArrayTotal[i%13].add(scMirMfcc.featuredata[i])}
				{i%13 == 3} {coArrayTotal[i%13].add(scMirMfcc.featuredata[i])}
				{i%13 == 4} {coArrayTotal[i%13].add(scMirMfcc.featuredata[i])}
				{i%13 == 5} {coArrayTotal[i%13].add(scMirMfcc.featuredata[i])}
				{i%13 == 6} {coArrayTotal[i%13].add(scMirMfcc.featuredata[i])}
				{i%13 == 7} {coArrayTotal[i%13].add(scMirMfcc.featuredata[i])}
				{i%13 == 8} {coArrayTotal[i%13].add(scMirMfcc.featuredata[i])}
				{i%13 == 9} {coArrayTotal[i%13].add(scMirMfcc.featuredata[i])}
				{i%13 == 10} {coArrayTotal[i%13].add(scMirMfcc.featuredata[i])}
				{i%13 == 11} {coArrayTotal[i%13].add(scMirMfcc.featuredata[i])}
				{i%13 == 12} {coArrayTotal[i%13].add(scMirMfcc.featuredata[i])}
			};

			// Coefficients
			13.do{arg i; coArrayTotal[i].postln};

			// Means
			13.do{arg i;
				coArrayMeanTotal[i] = coArrayTotal[i].mean};

			// Variance
			13.do{arg j;
				coArrayTotal[j].size.do{arg i;
					coArrayVarianceTotal[j].add((coArrayTotal[j][i]-coArrayMeanTotal[j])*(coArrayTotal[j][i]-coArrayMeanTotal[j]));
				};
			};

			// Variance Means
			13.do{arg i;
				coArrayVarianceMeanTotal[i] = coArrayVarianceTotal[i].mean};

			// Standard Deviations
			13.do{arg i; coArrayStandardDevTotal[i] = coArrayVarianceMeanTotal[i].sqrt};

			// Store the 13 coefficient standard deviations in List
			13.do{arg i; coArrayOverallCoefficients.add(coArrayStandardDevTotal[i])};

			// Standard Deviation 13 coefficient values
			coArrayOverallCoefficients.postln;

			// Create new space in the eachTrackCoefficients list and store deviation values of each track
			eachTrackCoefficients.add(List());
			eachTrackCoefficients[num].add(coArrayOverallCoefficients);
		};

		// Convert all Lists to arrays
		eachTrackCoefficients.size.do{arg i;
			eachTrackCoefficients[i][0] = eachTrackCoefficients[i][0].asArray
		};

		eachTrackCoefficients = eachTrackCoefficients.flat;

		"Finished here".postln;
		eachTrackCoefficients.postln;

		eachTrackCoefficients.size.do{arg i;
			trainingDataString = trainingDataString++","++eachTrackCoefficients[i]
		};

		trainingDataString.writeArchive(pathToWriteTo +/+ fileName ++ ".csv");
		"Writing Training Data to CSV File Complete.".postln;
	}
}