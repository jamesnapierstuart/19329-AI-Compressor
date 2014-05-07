/*
Instrument Multi Classification Class via Onsets or Tartini using a KNN ML Algorithm,

@kickCsvFile = Kick Training Data
@snareCsvFile = Snare Training Data
@eguitarCsvFile = Electric Guitar Training Data
@vocalCsvFile = Vocal Training Data
@kNNAmount = Nearest Neghbour Amount (K)
@testDataFile = Audio Sample the user passes in
@mirClass = Either Onsets or Tartini to classify the Audio
(Currently Hard Coded for Kick, Snare, EGuitar and Vocals)

Part of the AI Compressor Plugin Project.
© 2013-2014, Candidate Number 19329
Music Informatics Final Year Project, University of Sussex, Spring 2014
*/

InstrumentClassificationOnsetTartini {

	classvar <>sampleClassifiedAs = "Sample Not Classified Yet (Please Call the Classify Method)";

	*new { arg kickCsvFile, snareCsvFile, eguitarCsvFile, vocalCsvFile, kNNAmount, testDataFile, mirClass;
		^super.newCopyArgs.classify(kickCsvFile, snareCsvFile, eguitarCsvFile, vocalCsvFile, kNNAmount, testDataFile, mirClass);
	}

	classify { arg kickCsvFile, snareCsvFile, eguitarCsvFile, vocalCsvFile, kNNAmount, testDataFile, mirClass;

		// Testing Data scMirSample
		var scMirSample;

		// testing medians
		var inputSample;

		// Training Data Arrays
		var kickArray = List();
		var snareArray = List();
		var eguitarArray = List();
		var vocalArray = List();

		// Testing classification arrays
		var kickClassifiedAmount = List();
		var snareClassifiedAmount = List();
		var eguitarClassifiedAmount = List();
		var vocalClassifiedAmount = List();

		// KNN variables
		var k = kNNAmount;
		var removeArrays = Array.fill(4);
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

		// The temp remve Arrays
		var tempArrays = Array.fill(4);

		// CSV Variables
		var kickString = "";
		var snareString = "";
		var eguitarString = "";
		var vocalString = "";

		// Read in CSV File as Training Data
		"Reading in Training Data".postln;

		// Kick training data CSV
		kickArray = CSVFileReader.readInterpret(kickCsvFile);
		kickArray  = kickArray.flatten(1);
		kickArray.removeAt(0);

		// Snare training data CSV
		snareArray = CSVFileReader.readInterpret(snareCsvFile);
		snareArray  = snareArray.flatten(1);
		snareArray.removeAt(0);

		// Eguitar training data CSV
		eguitarArray = CSVFileReader.readInterpret(eguitarCsvFile);
		eguitarArray  = eguitarArray.flatten(1);
		eguitarArray.removeAt(0);

		// Vocal training data CSV
		vocalArray = CSVFileReader.readInterpret(vocalCsvFile);
		vocalArray  = vocalArray.flatten(1);
		vocalArray.removeAt(0);

		"Training Data Read".postln;

		// Perform Analysis and Classification of Audio Sample
		testDataFile.pathMatch.collect{arg i;

			// Reset Weights
			weightTotal = 0;
			weightTotal2 = 0;
			weightTotal3 = 0;
			weightTotal4 = 0;

			// Reset training data arrays
			removeArrays[0] =  kickArray.as(FloatArray);
			removeArrays[1] =  snareArray.as(FloatArray);
			removeArrays[2] =  eguitarArray.as(FloatArray);
			removeArrays[3] =  vocalArray.as(FloatArray);

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
				knn3[i] = removeArrays[2].minItem { |item| item absdif: inputSample};
				removeArrays[2].remove(knn3[i]);
				knn4[i] = removeArrays[3].minItem { |item| item absdif: inputSample};
				removeArrays[3].remove(knn4[i]);
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
				case
				{inputSample-knn3[i] == 0} {weights3[i] = 1/1}
				{
					weights3[i] = 1/(absdif(inputSample, knn3[i]));
				};
				case
				{inputSample-knn4[i] == 0} {weights4[i] = 1/1}
				{
					weights4[i] = 1/(absdif(inputSample, knn4[i]));
				};
			};

			// Get sum weights
			k.do{arg i;
				weightTotal = weightTotal+weights[i];
				weightTotal2 = weightTotal2+weights2[i];
				weightTotal3 = weightTotal3+weights3[i];
				weightTotal4 = weightTotal4+weights4[i];
			};

			// Get mean weights
			meanWeight = weightTotal/k;
			meanWeight2 = weightTotal2/k;
			meanWeight3 = weightTotal3/k;
			meanWeight4 = weightTotal4/k;


			// Compare the mean weights, classify accordingly
			case
			// classify as kick
			{[meanWeight, meanWeight2, meanWeight3, meanWeight4].maxItem == meanWeight} {
				kickClassifiedAmount.add(inputSample);
			}
			// classify as snare
			{[meanWeight, meanWeight2, meanWeight3, meanWeight4].maxItem == meanWeight2} {
				snareClassifiedAmount.add(inputSample);
			}
			// classify as eguitar
			{[meanWeight, meanWeight2, meanWeight3, meanWeight4].maxItem == meanWeight3} {
				eguitarClassifiedAmount.add(inputSample);
			}
			// classify as vocal
			{[meanWeight, meanWeight2, meanWeight3, meanWeight4].maxItem == meanWeight4} {
				vocalClassifiedAmount.add(inputSample);
			}
		};

		// Inform which instrument the sample was classified as
		case
		{kickClassifiedAmount.size > 0} {"[Sample Classified as Kick]".postln; sampleClassifiedAs = "Kick"}
		{snareClassifiedAmount.size > 0} {"[Sample Classified as Snare]".postln; sampleClassifiedAs = "Snare"}
		{eguitarClassifiedAmount.size > 0} {"[Sample Classified as ELectric Guitar]".postln; sampleClassifiedAs = "Eguitar"}
		{vocalClassifiedAmount.size > 0} {"[Sample Classified as Vocal]".postln; sampleClassifiedAs = "Vocal"}
	}

	getClassifiedInstrument {
		^sampleClassifiedAs;
	}

}