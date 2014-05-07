/*
Kick Snare Binary Classification Class via Onsets or Tartini using a KNN ML Algorithm,

@kickCsvFile = Kick Training Data
@snareCsvFile = Snare Training Data
@kNNAmount = Nearest Neghbour Amount (K)
@testDataFile = Audio Sample the user passes in
@mirClass = Either Onsets or Tartini to classify the Audio
(Currently Hard Coded for Kick, Snare)

Part of the AI Compressor Plugin Project.
© 2013-2014, Candidate Number 19329
Music Informatics Final Year Project, University of Sussex, Spring 2014
*/

KickSnareClassificationOnsetTartini {

	classvar <>sampleClassifiedAs = "Sample Not Classified Yet (Please Call the Classify Method)";

	*new { arg kickCsvFile, snareCsvFile, kNNAmount, testDataFile, mirClass;
		^super.newCopyArgs.classify(kickCsvFile, snareCsvFile, kNNAmount, testDataFile, mirClass);
	}

	classify { arg kickCsvFile, snareCsvFile, kNNAmount, testDataFile, mirClass;

		// Testing Data scMirSample
		var scMirSample;

		// testing medians
		var inputSample;

		// Training Data Arrays
		var kickArray = List();
		var snareArray = List();

		// Testing classification arrays
		var kickClassifiedAmount = List();
		var snareClassifiedAmount = List();

		// KNN variables
		var k = kNNAmount;
		var removeArrays = Array.fill(2);
		var knn = Array.fill(k, {0});
		var knn2 = Array.fill(k, {0});
		var weights = Array.fill(k, {0});
		var weights2 = Array.fill(k, {0});
		var weightTotal = 0;
		var weightTotal2 = 0;
		var meanWeight;
		var meanWeight2;
		var neighbourAverage;
		var neighbourAverage2;

		// The temp remve Arrays
		var tempArrays = Array.fill(2);

		// CSV Variables
		var kickString = "";
		var snareString = "";

		// Read in CSV Training Data
		"Reading in Training Data".postln;

		// Kick training data CSV
		kickArray = CSVFileReader.readInterpret(kickCsvFile);
		kickArray  = kickArray.flatten(1);
		kickArray.removeAt(0);

		// Snare training data CSV
		snareArray = CSVFileReader.readInterpret(snareCsvFile);
		snareArray  = snareArray.flatten(1);
		snareArray.removeAt(0);

		"Training Data Read".postln;

		// Perform Analysis and Classification of Audio Sample
		testDataFile.pathMatch.collect{arg i;

			// Reset Weights
			weightTotal = 0;
			weightTotal2 = 0;

			// Reset training data arrays
			removeArrays[0] =  kickArray.as(FloatArray);
			removeArrays[1] =  snareArray.as(FloatArray);

			// Extract Data
			scMirSample = SCMIRAudioFile(i, [[mirClass]]);
			scMirSample.extractFeatures();
			inputSample = scMirSample.featuredata.median;

			("Classifying Audio Sample...").postln;

			// Get the K nearest neighbours
			k.do{arg i;
				knn[i] = removeArrays[0].minItem { |item| item absdif: inputSample};
				removeArrays[0].remove(knn[i]);
				knn2[i] = removeArrays[1].minItem { |item| item absdif: inputSample};
				removeArrays[1].remove(knn2[i]);
			};

			// Calculate Weights
			k.do{arg i;
				case
				{inputSample-knn[i] == 0} {weights[i] = 1/1;}
				{
					weights[i] = 1/(absdif(inputSample, knn[i]));
				};
				case
				{inputSample-knn2[i] == 0} {weights2[i] = 1/1}
				{
					weights2[i] = 1/(absdif(inputSample, knn2[i]));
				};
			};

			// Get sum weights
			k.do{arg i;
				weightTotal = weightTotal+weights[i];
				weightTotal2 = weightTotal2+weights2[i];
			};

			// Get mean weights
			meanWeight = weightTotal/k;
			meanWeight2 = weightTotal2/k;

			// Compare the mean weights, classify accordingly
			case
			// classify as kick
			{[meanWeight, meanWeight2].maxItem == meanWeight} {
				kickClassifiedAmount.add(inputSample);
			}
			// classify as snare
			{[meanWeight, meanWeight2].maxItem == meanWeight2} {
				snareClassifiedAmount.add(inputSample);
			}
		};

		// Inform which instrument the sample was classified as
		case
		{kickClassifiedAmount.size > 0} {"[Sample Classified as Kick]".postln; sampleClassifiedAs = "Kick"}
		{snareClassifiedAmount.size > 0} {"[Sample Classified as Snare]".postln; sampleClassifiedAs = "Snare"}
	}

	getClassifiedInstrument {
		^sampleClassifiedAs;
	}

}