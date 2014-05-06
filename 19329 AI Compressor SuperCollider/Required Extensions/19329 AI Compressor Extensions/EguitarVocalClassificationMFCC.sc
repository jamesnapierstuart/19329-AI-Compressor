/*
Eguitar & Vocal Binary Classification Class via MFCC with 13 coefficients using Nearest Neighour algorithm,

@eguitarCsvFile = eguitar Training Data
@vocalCsvFile = vocal Training Data
@testDataFile = Audio Sample the user passes in
(Currently Hard Coded for eguitar, vocal)

Part of the AI Compressor Plugin Project.
© 2013-2014, Candidate Number 19329
Music Informatics Final Year Project, University of Sussex, Spring 2014
*/

EguitarVocalClassificationMFCC {

	classvar <>sampleClassifiedAs = "Sample Not Classified Yet (Please Call the Classify Method)";

	*new { arg eguitarCsvFile, vocalCsvFile, testDataFile;
		^super.newCopyArgs.classify(eguitarCsvFile, vocalCsvFile, testDataFile);
	}

	classify { arg eguitarCsvFile, vocalCsvFile, testDataFile;

		// Training Data Arrays
		var kickArray = List();
		var snareArray = List();
		var eguitarArray = List();
		var vocalArray = List();

		// Instrument Amount Classified as
		var testAmountKick = List();
		var testAmountSnare = List();
		var testAmountEguitar = List();
		var testAmountVocal = List();

		// MFCC Coefficient Variables
		var kickCoArrayTotal = List();
		var snareCoArrayTotal = List();
		var eguitarCoArrayTotal = List();
		var vocalCoArrayTotal = List();

		// MFCC Classification Comparison Coefficient Variables
		var kickCoefficientArray = List();
		var snareCoefficientArray = List();
		var eguitarCoefficientArray = List();
		var vocalCoefficientArray = List();

		// Testing classification arrays
		var kickClassifiedAmount = List();
		var snareClassifiedAmount = List();
		var eguitarClassifiedAmount = List();
		var vocalClassifiedAmount = List();

		// Tesing variables
		var removeArray = Array.fill(4); // Kick, snare, eguitar, vocal

		// Nearest neighbours variables
		var kickNN = Array.fill(13, {});
		var snareNN = Array.fill(13, {});
		var eguitarNN = Array.fill(13, {});
		var vocalNN = Array.fill(13, {});

		// Nearest neighbour comparison
		var coNNArray = Array.fill(13, {List()});
		// nearest coefficients
		var coNN = Array.fill(13);

		// Test data variables
		var scMirTartini;
		var scMirTartini2;
		var eachTrackCoefficients = List();
		var scMirMfcc;
		var mfcc = 13;
		var testArray;

		// The temp remvoe Arrays
		var tempArrays = Array.fill(4);

		// Eguitar training data CSV
		eguitarArray = CSVFileReader.readInterpret(eguitarCsvFile);
		eguitarArray  = eguitarArray.flatten(1);
		eguitarArray.removeAt(0);

		// Vocal training data CSV
		vocalArray = CSVFileReader.readInterpret(vocalCsvFile);
		vocalArray  = vocalArray.flatten(1);
		vocalArray.removeAt(0);

		// Kick Coefficients
		13.do{kickCoArrayTotal.add(List())};
		// Snare Coefficients
		13.do{snareCoArrayTotal.add(List())};
		// Eguitar Coefficients
		13.do{eguitarCoArrayTotal.add(List())};
		// Vocal Coefficients
		13.do{vocalCoArrayTotal.add(List())};

		// Read in Test Data file path
		testDataFile.pathMatch.collect{arg track, num;

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

			// Standard Deviation 13 coefficient values for each track added to eachTrackCo array
			eachTrackCoefficients.add(coArrayOverallCoefficients);

			// Store temporary remove arrays as Eguitar and Vocal arrays
			removeArray[2] = eguitarArray.as(FloatArray);
			removeArray[3] = vocalArray.as(FloatArray);

			13.do{eguitarCoArrayTotal.add(List())};
			// Vocal Coefficients
			13.do{vocalCoArrayTotal.add(List())};

			// Assign all eguitar coefficients to eguitar array
			removeArray[2].size.do{arg i;
				case
				{i%13 == 0} {eguitarCoArrayTotal[i%13].add(removeArray[2][i])}
				{i%13 == 1} {eguitarCoArrayTotal[i%13].add(removeArray[2][i])}
				{i%13 == 2} {eguitarCoArrayTotal[i%13].add(removeArray[2][i])}
				{i%13 == 3} {eguitarCoArrayTotal[i%13].add(removeArray[2][i])}
				{i%13 == 4} {eguitarCoArrayTotal[i%13].add(removeArray[2][i])}
				{i%13 == 5} {eguitarCoArrayTotal[i%13].add(removeArray[2][i])}
				{i%13 == 6} {eguitarCoArrayTotal[i%13].add(removeArray[2][i])}
				{i%13 == 7} {eguitarCoArrayTotal[i%13].add(removeArray[2][i])}
				{i%13 == 8} {eguitarCoArrayTotal[i%13].add(removeArray[2][i])}
				{i%13 == 9} {eguitarCoArrayTotal[i%13].add(removeArray[2][i])}
				{i%13 == 10} {eguitarCoArrayTotal[i%13].add(removeArray[2][i])}
				{i%13 == 11} {eguitarCoArrayTotal[i%13].add(removeArray[2][i])}
				{i%13 == 12} {eguitarCoArrayTotal[i%13].add(removeArray[2][i])}
			};

			// Assign all vocal coefficients to vocal array
			removeArray[3].size.do{arg i;
				case
				{i%13 == 0} {vocalCoArrayTotal[i%13].add(removeArray[3][i])}
				{i%13 == 1} {vocalCoArrayTotal[i%13].add(removeArray[3][i])}
				{i%13 == 2} {vocalCoArrayTotal[i%13].add(removeArray[3][i])}
				{i%13 == 3} {vocalCoArrayTotal[i%13].add(removeArray[3][i])}
				{i%13 == 4} {vocalCoArrayTotal[i%13].add(removeArray[3][i])}
				{i%13 == 5} {vocalCoArrayTotal[i%13].add(removeArray[3][i])}
				{i%13 == 6} {vocalCoArrayTotal[i%13].add(removeArray[3][i])}
				{i%13 == 7} {vocalCoArrayTotal[i%13].add(removeArray[3][i])}
				{i%13 == 8} {vocalCoArrayTotal[i%13].add(removeArray[3][i])}
				{i%13 == 9} {vocalCoArrayTotal[i%13].add(removeArray[3][i])}
				{i%13 == 10} {vocalCoArrayTotal[i%13].add(removeArray[3][i])}
				{i%13 == 11} {vocalCoArrayTotal[i%13].add(removeArray[3][i])}
				{i%13 == 12} {vocalCoArrayTotal[i%13].add(removeArray[3][i])}
			};

			// Find nearest neighbour in each instrument for each coefficient
			13.do{arg i;
				eguitarNN[i] = eguitarCoArrayTotal[i].minItem { |item| item absdif: eachTrackCoefficients[num][i]};
				vocalNN[i] = vocalCoArrayTotal[i].minItem { |item| item absdif: eachTrackCoefficients[num][i]};
			};

			// Add the Coefficients for each instrument to array
			13.do{arg i;
				coNNArray[i].add(eguitarNN[i]);
				coNNArray[i].add(vocalNN[i]);
			};

			// Cycle through array, comparing instrument neighbours, which ever is closest use
			13.do{arg i;
				coNN[i] = coNNArray[i].minItem { |item| item absdif: eachTrackCoefficients[num][i]};
			};

			// Add to the instrument coefficient total count
			13.do{arg i;
				case
				{coNN[i] == eguitarNN[i]} {
					eguitarCoefficientArray.add(eachTrackCoefficients[i]);
				}
				{coNN[i] == vocalNN[i]} {
					vocalCoefficientArray.add(eachTrackCoefficients[i]);
				};
			};

			// Compare the totals in each instrument coefficient amount, which ever has the most coefficients classes as instrument, then classify the sample overall as that instrument
			case
			{[eguitarCoefficientArray.size, vocalCoefficientArray.size].maxItem == eguitarCoefficientArray.size} {
				"Classify completely as Eguitar".postln;
				eguitarClassifiedAmount.add(1);
			}
			{[eguitarCoefficientArray.size, vocalCoefficientArray.size].maxItem == vocalCoefficientArray.size} {
				"Classify completely as Vocal".postln;
				vocalClassifiedAmount.add(1);
			};
		};

		// Inform which instrument the sample was classified as
		case
		{eguitarClassifiedAmount.size > 0} {"[Sample Classified as eguitar]".postln; sampleClassifiedAs = "Eguitar"}
		{vocalClassifiedAmount.size > 0} {"[Sample Classified as vocal]".postln; sampleClassifiedAs = "Vocal"}
	}

	getClassifiedInstrument {
		^sampleClassifiedAs;
	}

}