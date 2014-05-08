/*
Creates an Instance of the AI Compressor Analysis Application.

@periodicOnsetTraining = The machine learning CSV file path for periodic training data
@nonPeriodicOnsetTraining = The machine learning CSV file path for non periodic training data
@kickTartiniTraining = The machine learning CSV file path for kick tartini training data
@snareTartiniTraining = The machine learning CSV file path for snare tartini training data
@eguitarMfccTraining = The machine learning CSV file path for eguitar MFCC 13 training data
@vocalMfccTraining = The machine learning CSV file path for vocal MFCC13 training data

Part of the AI Compressor Plugin Project.
© 2013-2014, Candidate Number 19329
Music Informatics Final Year Project, University of Sussex, Spring 2014
*/

AICompressor {

	var server;

	*new{arg periodicOnsetTraining, nonPeriodicOnsetTraining, kickTartiniTraining, snareTartiniTraining, eguitarMfccTraining, vocalMfccTraining;
		^super.newCopyArgs.createAICompressor(periodicOnsetTraining, nonPeriodicOnsetTraining, kickTartiniTraining, snareTartiniTraining, eguitarMfccTraining, vocalMfccTraining);
	}

	createAICompressor {arg periodicOnsetTraining, nonPeriodicOnsetTraining, kickTartiniTraining, snareTartiniTraining, eguitarMfccTraining, vocalMfccTraining;

		// GUI variables
		var analysisWindow;
		var halfcvs;
		var leftcvs;
		var leftcvs2;
		var audiocvs;
		var textcvs;
		var soundDrop;
		var soundPath, soundView, soundFile, soundFloatArray, pathDrop, pathPath;
		var font, font2;
		var leftText;
		var portText;
		var titleText;
		var resultsText;
		var parameterText;
		var analyseButton;
		var playStopButtons;
		var play = false;
		var stop = false;
		// Synth Variables
		var inputBuffer;
		var inSynth;
		// Classification Variables
		var classifiedPeriodicity;
		var classifiedPeriodicityLabel;
		var classifiedInstrument;
		var classifiedInstruemntLabel;
		// ananlysis routines
		var analyisTextRoutine;
		var analyisClassificationRoutine;
		var calculateCompressorParameters;
		var analyse;
		var isAnalysing = false;
		// OSC Variables
		var scPort;
		var auPort;
		// function variables
		var createGUI;
		var createSynth;
		var oscInit;
		var freeAnalyse;
		// compressor variables
		var sampleCompressorParameters;
		var threshold;
		var ratio;
		var attack;
		var release;
		var makeUpGain;
		var averageGainReduction;
		var dynamicVariability;
		var meanPeakValue;
		var rms;
		// Float constant (OSC in AU requires 0.f)
		var floatConstant = 0.1;

		// Init Buffer synth def
		createSynth = {
			"Initialising Buffer Synth...".postln;
			SynthDef("trackAnalyse", {arg out = 0, bufnum, num;
				var signal;
				signal = PlayBuf.ar(num.numChannels, bufnum);
				Out.ar(out, Pan2.ar(signal));
			}).store;
			"Audio Buffer Synth Intitialised".postln;
		};

		// Init GUI
		createGUI = {
			"Initialising AI Compressor Application...".postln;
			font = Font("Ariel", 30);
			font2 = Font("Ariel", 25);

			analysisWindow = Window.new("AI Compressor: Analysis Application", Rect(Window.availableBounds.width/4, Window.availableBounds.height/4, Window.availableBounds.width/2.2, Window.availableBounds.height/1.8), false);
			halfcvs = Array.fill(2, {arg i; CompositeView(analysisWindow, Rect(0, 0 + (i*analysisWindow.bounds.height/1.6), analysisWindow.bounds.width, analysisWindow.bounds.height/1.6))});

			leftcvs = Array.fill(3, {arg i; CompositeView(halfcvs[0], Rect(0, 0 + (i*halfcvs[0].bounds.height/3), halfcvs[0].bounds.width/4, halfcvs[0].bounds.height/3))});

			audiocvs = Array.fill(2, {arg i; CompositeView(halfcvs[0], Rect(halfcvs[0].bounds.width/4, 0 + (i*halfcvs[0].bounds.height/3)*2, (halfcvs[0].bounds.width/2)+(halfcvs[0].bounds.width/4), (halfcvs[0].bounds.height/3)*2 - (i*halfcvs[0].bounds.height/3)))});

			textcvs = Array.fill(2, {arg i; CompositeView(halfcvs[1], Rect(0 + (i*halfcvs[1].bounds.width/2), 0, halfcvs[1].bounds.width/2, halfcvs[1].bounds.height))});

			leftText = Array.fill(3, {arg i; StaticText(leftcvs[i], Rect(0, leftcvs[i].bounds.height/6.5, leftcvs[i].bounds.width, leftcvs[i].bounds.height/4)).align_(\center)});

			portText = StaticText(leftcvs[0], Rect(0, leftcvs[0].bounds.height/4, leftcvs[0].bounds.width, leftcvs[0].bounds.height/2)).align_(\center);

			titleText = Array.fill(2, {arg i; StaticText(textcvs[i], Rect(textcvs[i].bounds.width/12, textcvs[i].bounds.height/20, textcvs[i].bounds.width, textcvs[i].bounds.height/8))});

			resultsText = Array.fill(5, {arg i; StaticText(textcvs[0], Rect(textcvs[0].bounds.width/12, textcvs[0].bounds.height/6 + (i*textcvs[0].bounds.height/16), textcvs[0].bounds.width, textcvs[0].bounds.height/16))});

			parameterText = Array.fill(6, {arg i; StaticText(textcvs[1], Rect(textcvs[1].bounds.width/12, textcvs[1].bounds.height/6 + (i*textcvs[1].bounds.height/16), textcvs[1].bounds.width, textcvs[1].bounds.height/16))});

			analyseButton = Button(leftcvs[1], Rect(leftcvs[1].bounds.width/11, leftcvs[1].bounds.height/2.5, leftcvs[1].bounds.width/1.2, leftcvs[1].bounds.height/2));

			playStopButtons = Array.fill(2, {arg i; Button(leftcvs[2], Rect(leftcvs[2].bounds.width/12 + (i*(leftcvs[2].bounds.width/2.2)), leftcvs[2].bounds.height/2.5, leftcvs[2].bounds.width/2.5, leftcvs[2].bounds.height/3))});

			titleText.do{arg i; i.font_(font2)};
			titleText[0].string = "Analysis Results:";
			titleText[1].string = "Compressor Parameters:";
			titleText[0].stringColor = Color.white;
			titleText[1].stringColor = Color.white;

			resultsText.do{arg i; i.string = "fsaada"; i.stringColor = Color.white};
			resultsText[0].string = "Classified Periodicity: ";
			resultsText[1].string = "Classified Instrument: ";
			resultsText[2].string = "Dynamic Variability: ";
			resultsText[3].string = "Mean Peak Value: ";
			resultsText[4].string = "RMS: ";

			parameterText[0].string = "Threshold: ";
			parameterText[1].string = "Ratio: ";
			parameterText[2].string = "Attack: ";
			parameterText[3].string = "Release: ";
			parameterText[4].string = "Makeup Gain: ";
			parameterText[5].string = "Average Gain Reduction: ";

			parameterText.do{arg i; i.stringColor = Color.white};

			analyseButton.states_([["Analyse", Color.black, Color.white]]);
			playStopButtons[0].states_([["Play", Color.white, Color.grey]]);
			playStopButtons[1].states_([["Stop", Color.white, Color.grey]]);

			leftText[0].string = "AU PORT NO:".underlined($*);
			leftText[1].string = "ANALYSE AUDIO:".underlined($*);
			leftText[2].string = "PLAY/STOP AUDIO:".underlined($*);

			portText.string = "6000";
			portText.font_(font);

			halfcvs.do{arg i; i.background_(Color.rand)};
			halfcvs[1].background_(Color.gray(0.18));
			leftcvs.do{arg i; i.background_(Color.rand)};
			leftcvs[0].background_(Color.fromHexString("F3FFCF"));
			leftcvs[1].background_(Color.fromHexString("BAC9A9"));
			leftcvs[2].background_(Color.fromHexString("F3FFCF"));

			leftcvs[2].background_(Color.fromHexString("E0E4CC"));

			leftcvs[0].background_(Color.gray(0.6));
			leftcvs[1].background_(Color.gray(0.7));
			leftcvs[2].background_(Color.gray(0.6));

			audiocvs[1].background_(Color.gray(0.7));
			textcvs[0].background_(Color.gray(0.18));
			textcvs[1].background_(Color.gray(0.35));

			// Input Buffer stop and Starts
			playStopButtons[0].action_({arg b;
				case
				{soundFile != nil} {
					// inSynth.free;
					case
					{stop == true} {
						inSynth = Synth(\trackAnalyse, [\bufnum, inputBuffer, \num, soundFile]);
						stop = false;
					}
					{inSynth.free;
						inSynth = Synth(\trackAnalyse, [\bufnum, inputBuffer, \num, soundFile]);
						stop = false;
					}
				};
			});
			playStopButtons[1].action_({arg b;
				case
				{soundFile != nil && stop == false} {
					inSynth.free;
					stop = true;
				};
			});

			// Analyse Button
			analyseButton.action_({arg b;

				case
				{(soundFile != nil) && (isAnalysing == false)} {

					isAnalysing = true;

					resultsText[0].string = "Classified Periodicity: Analysing...";
					resultsText[1].string = "Classified Instrument: Analysing...";
					resultsText[2].string = "Dynamic Variability: Analysing...";
					resultsText[3].string = "Mean Peak Value: Analysing...";
					resultsText[4].string = "RMS: Analysing...";

					parameterText[0].string = "Threshold: Calculating...";
					parameterText[1].string = "Ratio: Calculating...";
					parameterText[2].string = "Attack: Calculating...";
					parameterText[3].string = "Release: Calculating...";
					parameterText[4].string = "Makeup Gain: Calculating...";
					parameterText[5].string = "Average Gain Reduction: Calculating...";

					analyisClassificationRoutine = {
						// Perform Periodicity Classification:
						classifiedPeriodicity = InstrumentClassificationPeriodicNonPeriodic(periodicOnsetTraining, nonPeriodicOnsetTraining, 25, soundPath, Onsets);

						// If nonPeriodic then perform analysis of Kick and Snare
						case
						// If Non periodic
						{classifiedPeriodicity.getClassifiedInstrument == "NonPeriodic"} {
							"Audio is Non Periodic, performing Tartini Analysis between kick and snare".postln;
							// Perform tartini analysis between kick and snare (Binary)
							classifiedInstrument = KickSnareClassificationOnsetTartini(kickTartiniTraining, snareTartiniTraining, 25, soundPath, Tartini);
						}
						// Else periodic
						{
							"Audio is Periodic, performing Tartini Analysis between Eguitar and Vocal".postln;
							// Perform MFCC analysis between eguitar and vocal (Binary)
							classifiedInstrument = EguitarVocalClassificationMFCC(eguitarMfccTraining, vocalMfccTraining, soundPath);
						};

						// Assign Analysis Labels
						classifiedPeriodicityLabel = classifiedPeriodicity.getClassifiedInstrument;
						classifiedInstruemntLabel = classifiedInstrument.getClassifiedInstrument;
					};

					// Perform Compressor Parameter Calculations
					calculateCompressorParameters = {

						// Calculate and Assign Compressor Parameters (Based upon classified Instrument)
						sampleCompressorParameters = CompressorParameterCalculations(classifiedInstruemntLabel, soundPath);
						threshold = sampleCompressorParameters.getThresholddB;
						ratio = sampleCompressorParameters.getRatio;
						attack = sampleCompressorParameters.getAttack;
						release = sampleCompressorParameters.getRelease;
						makeUpGain = sampleCompressorParameters.getMakeUpGaindB;
						averageGainReduction = sampleCompressorParameters.getAverageGainReductiondB;
						dynamicVariability = sampleCompressorParameters.getDynamicVariabilitydB;
						meanPeakValue = sampleCompressorParameters.getMeanPeakValuedB;
						rms = sampleCompressorParameters.getRmsdB;

						// Update the Analysis Results Text
						resultsText[0].string = "Classified Periodicity: " + classifiedPeriodicityLabel;
						resultsText[1].string = "Classified Instrument: " + classifiedInstruemntLabel;
						resultsText[2].string = "Dynamic Variability: " + dynamicVariability + "dB";
						resultsText[3].string = "Mean Peak Value: " + meanPeakValue + "dB";
						resultsText[4].string = "RMS: " + rms + "dB";

						// Set Compressor Parameter Strings
						parameterText[0].string = "Threshold: " + threshold.round + "dB";
						parameterText[1].string = "Ratio: " + ratio.round + ": 1";
						parameterText[2].string = "Attack: " + attack + "ms";
						parameterText[3].string = "Release: " + release + "ms";
						parameterText[4].string = "Makeup Gain: " + makeUpGain.round + "dB";
						parameterText[5].string = "Average Gain Reduction: " + averageGainReduction.round + "dB";

						// Send over OSC to the AU plugin the compressor parameters
						case
						{auPort != nil} {
							auPort.sendMsg("/threshold", threshold+floatConstant);
							auPort.sendMsg("/ratio", ratio+floatConstant);
							auPort.sendMsg("/attack", attack+floatConstant);
							auPort.sendMsg("/release", release+floatConstant);
							auPort.sendMsg("/makeupgain", makeUpGain+floatConstant);
						};

						freeAnalyse = {
						isAnalysing = false;
					};

											freeAnalyse.defer(0.2);

					};



					// Defer functions, to execute one after the other
					analyisClassificationRoutine.defer(0.1);
					calculateCompressorParameters.defer(0.1);

				};
			});

			// Drag and Drop Sound View
			soundDrop = DragSink(audiocvs[0], Rect(0-audiocvs[0].bounds.width/50, 0-audiocvs[0].bounds.height/50, audiocvs[0].bounds.width+audiocvs[0].bounds.width/1.8, audiocvs[0].bounds.height+audiocvs[0].bounds.height/1.8)).align_(\center);
			soundDrop.background = Color.grey;
			soundDrop.string = "Drag and Drop Audio Here";
			soundDrop.font_(font);
			soundDrop.stringColor = Color.white;
			soundDrop.canReceiveDragHandler = { View.currentDrag.isKindOf(Object) };
			soundDrop.receiveDragHandler = { arg v;

				case
				{soundFile != nil} { inSynth.free };

				soundDrop.string = "Loading In Audio...";

				// Store Audio and Display
				analyse = {

					// Reset all the analysis and compressor texts
					resultsText[0].string = "Classified Periodicity: ";
					resultsText[1].string = "Classified Instrument: ";
					resultsText[2].string = "Dynamic Variability: ";
					resultsText[3].string = "Mean Peak Value: ";
					resultsText[4].string = "RMS: ";

					parameterText[0].string = "Threshold: ";
					parameterText[1].string = "Ratio: ";
					parameterText[2].string = "Attack: ";
					parameterText[3].string = "Release: ";
					parameterText[4].string = "Makeup Gain: ";
					parameterText[5].string = "Average Gain Reduction: ";

					"Loading in Audio...".postln;
					// Store path name of audio
					soundPath = View.currentDrag.value.asString;
					soundFile = SoundFile();
					soundFile.openRead( soundPath,); // a stereo or multi channel file
					soundFloatArray = FloatArray.fill((soundFile.numFrames) * (soundFile.numChannels),0);
					soundFile.readData(soundFloatArray);
					soundFloatArray[14];

					// Dispaly Audio Waveform
					soundView = SoundFileView.new(audiocvs[1], Rect(0,0, audiocvs[1].bounds.width, audiocvs[1].bounds.height));
					soundView.soundfile = soundFile;
					soundView.read(0, soundFile.numFrames);
					soundView.elasticMode = true;
					soundView.timeCursorOn = true;
					soundView.timeCursorColor = Color.blue;
					soundView.drawsWaveForm = true;
					soundView.gridOn = false;
					soundView.gridResolution = 0.2;
					soundView.setSelectionColor(0, Color.blue);

					// Set audio buffer
					inputBuffer = Buffer.loadCollection(server, soundFloatArray, soundFile.numChannels);
					"Audio Loaded In".postln;

					soundDrop.string = "Drag and Drop Audio Here";

				};
				analyse.defer(0.001);
			};

			// Display GUI, on close free synth
			analysisWindow.front;
			analysisWindow.onClose_({
				case
				{soundFile != nil}
				{
					case
					{stop == true} {}
					{stop == false} {inSynth.free;}
				}
			});
			"AI Compressor Application Initialised".postln;
		};

		// Set up OSC receiver
		oscInit = {
			"Initialising OSC Receiver Port...".postln;
			scPort.free; scPort = OSCFunc({ |msg|
				msg.postln; {portText.string = msg[1];}.defer(0.01);
				// Set the AU Port to send Messages to the Port the AU sends to this AI App
				auPort = NetAddr("127.0.0.1", msg[1]); // loopback|
			}, '/AUPort', nil, 6880,);
			"OSC Receiver Port Initialised".postln;
		};

		// Init functions when server booted
		server = Server.local;
		server.boot.doWhenBooted{
			createSynth.defer(0.001);
			oscInit.defer(0.01);
			createGUI.defer(0.01);
		};
	}
}