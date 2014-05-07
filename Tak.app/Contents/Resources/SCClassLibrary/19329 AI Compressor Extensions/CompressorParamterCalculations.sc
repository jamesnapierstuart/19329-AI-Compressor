/*
Calculate the most suitable compression values for the instrument and audio content provided.

@instrument = Musical Instrument to be Analysed
@filePath = Path to the Audio file

Part of the AI Compressor Plugin Project.
© 2013-2014, Candidate Number 19329
Music Informatics Final Year Project, University of Sussex, Spring 2014
*/

CompressorParameterCalculations {

	classvar <>threshold, <>thresholddB, <>ratio, <>attack, <>release, <>makeUpGain, <>makeUpGaindB, <>meanPeakValue, <>meanPeakValuedB, <>rms, <>rmsdB, <>dynamicVariability, <>dynamicVariabilitydB;
	classvar sample, sampleArray, squaresArray, compressedArray;
	classvar <>averageGainReduction, <>averageGainReductiondB;
	classvar <>compressedMeanPeakValue, <>compressedMeanPeakValuedB;

	*new{arg instrument, filePath;
		^super.newCopyArgs.calculate(instrument, filePath);
	}

	calculate {arg instrument, filePath;

		squaresArray = List();

		// Load in Audio file
		"Loading in Sample...".postln;
		sample = SoundFile();
		sample.openRead(filePath,);
		"Sample Loaded".postln;
		"Loading sample to array...".postln;
		sampleArray = FloatArray.fill((sample.numFrames) * (sample.numChannels),0);
		sample.readData(sampleArray);
		"Sample array loaded".postln;

		// Calculate Mean Peak Value and RMS
		"Analysing sample for compressor parameter calculations...".postln;
		"Calculating mean peak value...".postln;
		meanPeakValue = sampleArray.abs.mean;
		meanPeakValuedB = 20*(log10(meanPeakValue/1));
		meanPeakValuedB.postln;
		"Calculating RMS...".postln;
		sampleArray.size.do{arg i;
			case
			{i%1024 == 0} {
				squaresArray.add((sampleArray[i].abs)*(sampleArray[i].abs));
			};
		};
		rms = squaresArray.mean.sqrt;
		rmsdB = 20*(log10(rms/1));
		rmsdB.postln;

		// Calculate Dynamic Variability (MeanPeakValue - RMS)
		"Calculating Dynamic Variability...".postln;
		dynamicVariability = (meanPeakValue absdif: rms);
		dynamicVariabilitydB = (meanPeakValuedB absdif: rmsdB);
		dynamicVariabilitydB.postln;

		// Perform Instrument Specific Calculations
		switch (instrument,
			"Kick", {this.kickCalculations},
			"Snare", {this.snareCalculations},
			"Eguitar", {this.eguitarCalculations},
			"Vocal", {this.vocalCalculations};
		);
	}

	// Kick Analysis
	kickCalculations {
		"Calculating Compressor Parameters for Kick...".postln;
		threshold = meanPeakValue;
		thresholddB = 20*(log10(threshold/1));
		ratio = 6.0;
		attack = 20;
		release = 0.2;
		// Compress and Analyse based upon kick parameter values
		this.compressAnalyse;
	}

	// Snare Analysis
	snareCalculations {
		"Calculating Compressor Parameters for Snare...".postln;
		threshold = meanPeakValue;
		thresholddB = 20*(log10(threshold/1));
		ratio = 5.0;
		attack = 20;
		release = 0.2;
		// Compress and Analyse based upon snare parameter values
		this.compressAnalyse;
	}

	// Eguitar Analysis
	eguitarCalculations {
		"Calculating Compressor Parameters for Eguitar...".postln;
		threshold = meanPeakValue;
		thresholddB = 20*(log10(threshold/1));
		ratio = 8.0;
		attack = 5;
		release = 500;
		// Compress and Analyse based upon eguitar parameter values
		this.compressAnalyse;
	}

	// Vocal Analysis
	vocalCalculations {
		"Calculating Compressor Parameters for Vocal...".postln;
		threshold = meanPeakValue;
		thresholddB = 20*(log10(threshold/1));
		ratio = 3.0;
		attack = 2;
		release = 300;
		// Compress and Analyse based upon vocal parameter values
		this.compressAnalyse;
	}

	// Offline compression and analysis of compression
	compressAnalyse {

		var difference;
		var ratioDivide;
		var attackSampleAmount, releaseSampleAmount;
		var attackPos, releasePos;
		compressedArray = List();

		// Attack/release amount per sample
		attackSampleAmount = 44100/(1000/attack);
		releaseSampleAmount = 44100/(1000/release);

		// AttackPos and releasePos
		attackPos = 0;
		releasePos = releaseSampleAmount;

		"Calculating Threshold...".postln;
		(20*(log10(threshold/1))).postln;

		"Calculating Ratio...".postln;
		ratio.round.postln;

		// Calculate offline compression for average gain reudction, and makeup gain
		"Offline compression calculation for average gain reductio and makeup gain...".postln;
		sampleArray.size.do{arg i;
			case
			{i%1024 == 0} {
				case
				// Above Threshold
				{sampleArray[i].abs > threshold} {
					difference = sampleArray[i].abs - threshold;
					ratioDivide = difference/ratio;
					case
					{sampleArray[i] < 0} { compressedArray.add((threshold+ratioDivide).neg)}
					{
						compressedArray.add(threshold+ratioDivide);
					}
				}
				// Else below threshold
				{
					compressedArray.add(sampleArray[i]);
				};
			}
		};

		"Completed Offline Compression".postln;

		"Calculate the Mean Amplitude Value of new Array...".postln;
		compressedMeanPeakValue = compressedArray.abs.mean;
		compressedMeanPeakValuedB = 20*(log10(compressedMeanPeakValue/1));
		compressedMeanPeakValuedB.postln;

		// Average Gain Reduction is the difference in gain from the original signal to the compressed signal
		"Calculating average gain reduction...".postln;
		averageGainReduction = (meanPeakValue absdif: compressedMeanPeakValue);
		averageGainReduction = (averageGainReduction*2).neg;
		"Average Gain Reduction again".postln;
		averageGainReduction.postln;

		// Average Gain Reduction is the difference in gain from the original signal to the compressed signal
		"Calculating average gain reduction...".postln;
		averageGainReductiondB = (meanPeakValuedB absdif: compressedMeanPeakValuedB);
		averageGainReductiondB = (averageGainReductiondB*2).neg;
		"Average Gain Reduction again".postln;
		averageGainReductiondB.postln;

		"Calculating Makeup Gain...".postln;
		makeUpGain = averageGainReduction/2;
		makeUpGain = makeUpGain.neg;
		makeUpGain.postln;

		"Calculating Makeup Gain...".postln;
		makeUpGaindB = averageGainReductiondB/2;
		makeUpGaindB = makeUpGaindB.neg;
		makeUpGaindB.postln;
	}

	// Getters for compression parameters and analysis features
	getThreshold {
		^threshold;
	}

	getThresholddB {
		^(thresholddB.round);
	}

	getRatio {
		^ratio;
	}

	getAttack {
		^attack;
	}

	getRelease {
		^release;
	}


	getMakeUpGain {
		^makeUpGain;
	}

	getMakeUpGaindB {
		^makeUpGaindB;
	}

	getMeanPeakValue {
		^meanPeakValue;
	}

	getMeanPeakValuedB {
		^(meanPeakValuedB.round);
	}

	getRms {
		^rms;
	}

	getRmsdB {
		^(rmsdB.round);
	}

	getDynamicVariability {
		^dynamicVariability;
	}

	getDynamicVariabilitydB {
		^(dynamicVariabilitydB.round);
	}

	getAverageGainReduction {
		^averageGainReduction;
	}

	getAverageGainReductiondB {
		^(averageGainReductiondB.round);
	}
}