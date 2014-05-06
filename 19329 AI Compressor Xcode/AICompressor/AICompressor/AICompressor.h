#ifndef __AICompressor__main__
#define __AICompressor__main__

#define ADDRESS "127.0.0.1"
#define PORT 57125

#define OUTPUT_BUFFER_SIZE 1024

#include <pthread.h>


#pragma mark ____AICompressor Parameter Constants

static CFStringRef kParamName_AICompressor_Ratio = CFSTR ("Ratio"); // 1
static const float kDefaultValue_AICompressor_Ratio = 4.0; // 2
static const float kMinimumValue_AICompressor_Ratio = 1.0; // 3
static const float kMaximumValue_AICompressor_Ratio = 100.0; // 4

static CFStringRef kParamName_AICompressor_Threshold = CFSTR ("Threshold"); // 5
static const float kDefaultValue_AICompressor_Threshold = -25.8;
static const float kMinimumValue_AICompressor_Threshold = -60.0;
static const float kMaximumValue_AICompressor_Threshold = -0.0;

static CFStringRef kParamName_AICompressor_MakeUpGain = CFSTR ("Makeup Gain"); // 5
static const float kDefaultValue_AICompressor_MakeUpGain = 0.0;
static const float kMinimumValue_AICompressor_MakeUpGain = 0.0;
static const float kMaximumValue_AICompressor_MakeUpGain = 40.0;

static CFStringRef kParamName_AICompressor_Attack = CFSTR ("Attack"); // 5
static const float kDefaultValue_AICompressor_Attack = 20.0;
static const float kMinimumValue_AICompressor_Attack = 0.5;
static const float kMaximumValue_AICompressor_Attack = 300.0;

static CFStringRef kParamName_AICompressor_Release = CFSTR ("Release"); // 5
static const float kDefaultValue_AICompressor_Release = 300.0;
static const float kMinimumValue_AICompressor_Release = 5.0;
static const float kMaximumValue_AICompressor_Release = 3900.0;

static CFStringRef kParamName_Tremolo_Waveform = CFSTR ("Waveform"); // 6
static const int kSineWave_Tremolo_Waveform = 1;
static const int kSquareWave_Tremolo_Waveform = 2;
static const int kDefaultValue_Tremolo_Waveform = kSineWave_Tremolo_Waveform;

// menu item names for the waveform parameter
static CFStringRef kMenuItem_Tremolo_Sine = CFSTR ("Sine"); // 7
static CFStringRef kMenuItem_Tremolo_Square = CFSTR ("Square"); // 8


#pragma mark ____AICompressor Factory Preset Constants
static const float kParameter_Preset_Ratio_Slow = 4.0; // 1
static const float kParameter_Preset_Ratio_Fast = 60.0; // 2
static const float kParameter_Preset_Threshold_Slow = -3.0; // 3
static const float kParameter_Preset_Threshold_Fast = -28.0; // 4
static const float kParameter_Preset_MakeUpGain_Slow = 0.0; // 3
static const float kParameter_Preset_MakeUpGain_Fast = 10.0; // 4
static const float kParameter_Preset_Attack_Slow = 20.0; // 3
static const float kParameter_Preset_Attack_Fast = 0.5; // 4
static const float kParameter_Preset_Release_Slow = 20.0; // 3
static const float kParameter_Preset_Release_Fast = 0.5; // 4

static const float kParameter_Preset_Waveform_Slow // 5
= kSineWave_Tremolo_Waveform;
static const float kParameter_Preset_Waveform_Fast // 6
= kSquareWave_Tremolo_Waveform;
enum {kPreset_Slow = 0, // 7
    kPreset_Fast = 1, // 8
    kNumberPresets = 2 // 9
};

static AUPreset kPresets [kNumberPresets] = { // 10
    {kPreset_Slow, CFSTR ("Slow & Gentle")},{kPreset_Fast, CFSTR ("Fast & Hard")}};
static const int kPreset_Default = kPreset_Slow; // 11


// parameter identifiers
enum { // 9
    kParameter_Ratio = 0,
    kParameter_Threshold = 1,
    kParameter_MakeUpGain = 2,
    kParameter_Attack = 3,
    kParameter_Release = 4,
//    kParameter_Waveform = 6,
    kNumberOfParameters = 5
};


class AICompressorKernel : public AUKernelBase
{
public:
	AICompressorKernel(AUEffectBase * inAudioUnit);
	virtual void Process(Float32 const* inSourceP,
                         Float32 * inDestP,
                         UInt32 inFramesToProcess,
                         UInt32 inNumChannels,
                         bool & ioSilence);
    // The reset method: Refresh all counters
    virtual void Reset();
    
private:
        
    // Threshold setting
    float threshold = 0.17782794100389;
    
    // Ratio settings
    float ratio = 60;
        
    // And more setting variables
    float difference;
    float ratioDivide;
    float gainReduction = 0;
    float everyAttackGainReduction;
    float everyReleaseGainReduction;
    float currentSampleGainReduction;

    // Release settings
    float releaseTime;
    float releaseDivide;
    float releaseSampleAmount = 147000;
    float releaseDifference;
    int releasePos = 0;
    float everyReleaseGainIncrease;
    float currentSampleGainIncrease;
    float currentGainIncrease;
    
    // Attack settings
    int attackPos = 0;
    float envAttackAmount;
    float envReleaseAmount;
    float currentGainReduction;    
    float attackDivide;
    float attackSampleAmount = 882;
    
    // Makeup Gain
    float makeUpGain = 0.0;
    
    // Output Settings
    float outSignal;
    float outSignaldB;
    float finalOutSignal;
    
    
    // test dsp code
    float gain = 1;
    float seekGain = 0;  
};


class AICompressor : public AUEffectBase
{
public:
	AICompressor(AudioUnit component);
    
    
    virtual ComponentResult GetPresets (       // 1
                                        CFArrayRef        *outData
                                        ) const;
    
    virtual OSStatus NewFactoryPresetSet (     // 2
                                          const AUPreset    &inNewFactoryPreset
                                          );
    
    virtual ComponentResult            GetParameterInfo(   AudioUnitScope          inScope,
                                                        AudioUnitParameterID    inParameterID,
                                                        AudioUnitParameterInfo  &outParameterInfo );
        
    virtual bool SupportsTail () {return true;}
    
	virtual OSStatus Version() { return 0xFFFFFF; }
	virtual OSStatus Initialize();
    
    
    OSStatus AudioUnitAddPropertyListener (AudioUnit inUnit,
                                           AudioUnitPropertyID inID,
                                           AudioUnitPropertyListenerProc inProc,
                                           void *inProcUserData
    );
    

virtual AUKernelBase *NewKernel() { return new AICompressorKernel(this); }
private:
};

AUDIOCOMPONENT_ENTRY(AUBaseFactory, AICompressor)

#endif // defined(__AICompressor__main__)
