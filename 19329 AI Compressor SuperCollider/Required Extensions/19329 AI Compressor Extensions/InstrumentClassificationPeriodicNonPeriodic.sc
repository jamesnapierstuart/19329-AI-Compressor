/*
Periodic/Non Periodic Binary Classification Class via Onsets or Tartini using a KNN ML Algorithm,

@periodicTrainingDataCsvFile = Periodic Training Data
@nonPeriodicTrainingDataCsvFile = Non Periodic Training Data
@kNNAmount = Nearest Neghbour Amount (K)
@testDataFile = Audio Sample the user passes in
@mirClass = Either Onsets or Tartini to classify the Audio

Part of the AI Compressor Plugin Project.
© 2013-2014, Candidate Number 19329
Music Informatics Final Year Project, University of Sussex, Spring 2014
*/

InstrumentClassificationPeriodicNonPeriodic {

	classvar <>sampleClassifiedAs = "Sample Not Classified Yet (Error Has Occured), pass valid arguments";

	*new { arg periodicTrainingDataCsvFile, nonPeriodicTrainingDataCsvFile, kNNAmount, testDataFile, mirClass;
		^super.newCopyArgs.classify(periodicTrainingDataCsvFile, nonPeriodicTrainingDataCsvFile, kNNAmount, testDataFile, mirClass);
	}

	classify { arg periodicTrainingDataCsvFile, nonPeriodicTrainingDataCsvFile, kNNAmount, testDataFile, mirClass;

		// Extraction variables
		var scMirTartini;

		// Training Data Arrays
		var periodicArray = List();
		var nonPeriodicArray = List();
		var testSampleMean2;
		var periodicClosest2;
		var nonPeriodicClosest2;
		var testAmountPeriodic2 = List();
		var testAmountNonPeriodic2 = List();

		// KNN variables
		var k= kNNAmount;
		var removeArrays = Array.fill(2);
		var knn = Array.fill(k, {0});
		var knn2 = Array.fill(k, {0});
		var knn3 = Array.fill(k, {0});
		var knn4 = Array.fill(k, {0});
		var weights = Array.fill(k, {0});
		var weights2 = Array.fill(k, {0});
		var weights3 = Array.fill(k, {0});
		var weights4 = Array.fill(k, {0});
		var weightTotal = 0;
		var weightTotal2 = 0;
		var weightTotal3 = 0;
		var weightTotal4 = 0;
		var meanWeight;
		var meanWeight2;
		var meanWeight3;
		var meanWeight4;
		var neighbourAverage;
		var neighbourAverage2;
		var neighbourAverage3;
		var neighbourAverage4;

		// CSV Variables
		var periodicString = "";
		var nonPeriodicString = "";

		// Read in the training Data
		"Reading in Training Data".postln;

		// Periodic CSV
		periodicArray = CSVFileReader.readInterpret(periodicTrainingDataCsvFile);
		periodicArray  = periodicArray.flatten(1);
		periodicArray.removeAt(0);

		// Non Periodic CSV
		nonPeriodicArray = CSVFileReader.readInterpret(nonPeriodicTrainingDataCsvFile);
		nonPeriodicArray  = nonPeriodicArray.flatten(1);
		nonPeriodicArray.removeAt(0);

		"Training Data Read".postln;

		// Classify the Input Sample
		testDataFile.pathMatch.collect{arg i;

			// Reset weight totals
			weightTotal = 0;
			weightTotal2 = 0;

			// Reset training data arrays
			removeArrays[0] =  nonPeriodicArray.asArray;
			removeArrays[1] =  periodicArray.asArray;

			scMirTartini = SCMIRAudioFile(i, [[mirClass]]);
			scMirTartini.extractFeatures();
			testSampleMean2 = scMirTartini.featuredata.median;

			// Get the K nearest neighbours
			k.do{arg i;
				knn[i] = removeArrays[0].minItem { |item| item absdif: testSampleMean2};
				removeArrays[0].remove(knn[i]);
				knn2[i] = removeArrays[1].minItem { |item| item absdif: testSampleMean2};
				removeArrays[1].remove(knn2[i]);
			};

			// Calculate Weights
			k.do{arg i;
				case
				{testSampleMean2-knn[i] == 0} {weights[i] = 1/1}
				{
					weights[i] = 1/(absdif(testSampleMean2, knn[i]));
				};
				case
				{testSampleMean2-knn2[i] == 0} {weights2[i] = 1/1}
				{
					weights2[i] = 1/(absdif(testSampleMean2, knn2[i]));
				};
			};

			// Get sum weight
			k.do{arg i;
				weightTotal = weightTotal+weights[i];
				weightTotal2 = weightTotal2+weights2[i];
			};

			// get mean weight
			meanWeight = weightTotal/k;
			meanWeight2 = weightTotal2/k;

			// Compare the mean weights, classify accordingly
			case
			{[meanWeight, meanWeight2].maxItem == meanWeight} {
				testAmountNonPeriodic2.add(testSampleMean2);
			}
			{[meanWeight, meanWeight2].maxItem == meanWeight2} {
				testAmountPeriodic2.add(testSampleMean2);
			};
		};

		// Inform user if periodic or non periodic was classified
		case
		{testAmountNonPeriodic2.size > 0} {"[Sample Classified as Non Periodic]".postln; sampleClassifiedAs = "NonPeriodic"}
		{testAmountPeriodic2.size > 0} {"[Sample Classified as Periodic]".postln; sampleClassifiedAs = "Periodic"}
	}

	getClassifiedInstrument {
		^sampleClassifiedAs;
	}

}