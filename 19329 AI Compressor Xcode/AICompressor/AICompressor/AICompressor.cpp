#include "AICompressor.h"
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <iostream>

// Include the OSCpkt header files
#include "oscpkt.hh"
#include "udp.hh"

using namespace oscpkt;
using namespace std;
using std::cout;

// Set Up OSC/UDP:
oscpkt::UdpSocket sock;
oscpkt::PacketReader pr;

oscpkt::UdpSocket sockSend;
oscpkt::PacketReader prSend;
oscpkt::PacketWriter pwSend;


AICompressorKernel::AICompressorKernel(AUEffectBase * inAudioUnit) : AUKernelBase(inAudioUnit)
{

}

void AICompressorKernel::Process(Float32 const* inSourceP, Float32 * inDestP, UInt32 inFramesToProcess, UInt32 inNumChannels, bool & ioSilence)
{
    
    // Our OSC Server: Recieves OSC messages from SuperCollider
    if (sock.isOk()) {
        
        if (sock.receiveNextPacket(0 /* timeout, in ms */)) {
            pr.init(sock.packetData(), sock.packetSize());
            oscpkt::Message *msg;
            
            float iarg;
            msg = pr.popMessage();
            
            // Receieve Ratio Message
            if(msg->match("/ratio").popFloat(iarg).isOkNoMoreArgs()) {
                mAudioUnit->SetParameter( kParameter_Ratio, iarg);
            }
            // Receieve Threshold Message
            if(msg->match("/threshold").popFloat(iarg).isOkNoMoreArgs()) {
                mAudioUnit->SetParameter( kParameter_Threshold, iarg);
            }
            // Receive Makeup Gain Message
            if(msg->match("/makeupgain").popFloat(iarg).isOkNoMoreArgs()) {
                mAudioUnit->SetParameter( kParameter_MakeUpGain, iarg);
            }
            // Receive Attack Message
            if(msg->match("/attack").popFloat(iarg).isOkNoMoreArgs()) {
                mAudioUnit->SetParameter( kParameter_Attack, iarg);
            }
            // Receive Release Message
            if(msg->match("/release").popFloat(iarg).isOkNoMoreArgs()) {
                mAudioUnit->SetParameter( kParameter_Release, iarg);
            }
        }
    }
    
    // Assign GUI parameter values to private variables
    ratio = GetParameter (kParameter_Ratio);
    threshold = pow(10, (GetParameter(kParameter_Threshold)/20));
    makeUpGain = GetParameter(kParameter_MakeUpGain);
    
    // Get attack values
    attackDivide = 1000/GetParameter(kParameter_Attack);
    // Attack time in samples
    attackSampleAmount = round(44100/attackDivide);
    
    // Get release values
    releaseDivide = 1000/GetParameter(kParameter_Release);
    // Release time in samples
    releaseSampleAmount = round(44100/releaseDivide);    

    // Compressor DSP Code
    if(!ioSilence){

        // Loop through each sample
        for(int f = 0; f < inFramesToProcess; f += inNumChannels) {            
            
            // Peak has exceeded threshold
            if(fabs(inSourceP[f]) > threshold) {
                
                // Calculate Full Gain Reduction
                difference = fabs(inSourceP[f])-threshold;
                ratioDivide = difference/ratio;
                gainReduction = difference-ratioDivide;
                
                // Calculate amount of gain reduction after dividing the attack amount
                everyAttackGainReduction = gainReduction/attackSampleAmount;                
                currentGainReduction = attackPos*everyAttackGainReduction;
            
                if(attackPos < attackSampleAmount){
                    outSignal = fabs(inSourceP[f]) - currentGainReduction;
                    attackPos++;
                }
                else {
                    outSignal = fabs(inSourceP[f]) - gainReduction; // Should reach here very quickly and stay here but it isn't
                }
                
                releasePos = releaseSampleAmount;
                
            } // End exceeded threshold condition
            
            
            // Peak is below threshold
            else if (fabs(inSourceP[f]) < threshold) {
                
                // Calculate amount of gain reduction after dividing the release amount
                everyReleaseGainIncrease = gainReduction/releaseSampleAmount;
                currentGainIncrease = releasePos*everyReleaseGainIncrease;
                
                if(releasePos > 0){
                    outSignal = fabs(inSourceP[f]) - currentGainIncrease;
                    releasePos--;
                }
                else {
                    outSignal = fabs(inSourceP[f]);
                }
                attackPos = 0;
                
            } // End underneath threshold condition
            
            
            // Processing that takes place on overall signal: (Makeup Gain)
            // Convert amplitude to dB
            outSignaldB = 20*(log10f(outSignal/1));
            // Apply MakeUpGain and convert back to amplitude
            finalOutSignal = pow(10, ((outSignaldB+makeUpGain)/20));
            
            // Condition: If sample is negative amplitude use -, if positive use +
            if(inSourceP[f] < 0){
                inDestP[f] = -finalOutSignal;
            }
            else {
                inDestP[f] = finalOutSignal;
            }
            
        } // End sample loop
        
    } // End not silent condition
    
} // End process method


//  AudioUnit Constructor:
AICompressor::AICompressor(AudioUnit component) : AUEffectBase(component)
{
    
    CreateElements ();
    Globals () -> UseIndexedParameters (kNumberOfParameters);
    
    
    // Set default values for parameters on construction
    SetAFactoryPresetAsCurrent (                    // 1
                                kPresets [kPreset_Default]
                                );
    
    SetParameter ( // 1
                  kParameter_Ratio,
                  kDefaultValue_AICompressor_Ratio
                  );
    
    SetParameter ( // 2
                  kParameter_Threshold,
                  kDefaultValue_AICompressor_Threshold
                  );
    
    SetParameter ( // 3
                  kParameter_MakeUpGain,
                  kDefaultValue_AICompressor_MakeUpGain
                  );
    
    SetParameter ( // 4
                  kParameter_Attack,
                  kDefaultValue_AICompressor_Attack
                  );
    
    SetParameter ( // 5
                  kParameter_Release,
                  kDefaultValue_AICompressor_Release
                  );
    
    
#if AU_DEBUG_DISPATCHER
    mDebugDispatcher = new AUDebugDispatcher (this);
#endif    
}


// The reset method: Refresh all counters
void AICompressorKernel::Reset() {
}

#pragma mark ____Parameters
ComponentResult AICompressor::GetParameterInfo (
                                                AudioUnitScope inScope,
                                                AudioUnitParameterID inParameterID,
                                                AudioUnitParameterInfo &outParameterInfo
                                                )
{
    ComponentResult result = noErr;
    outParameterInfo.flags = kAudioUnitParameterFlag_IsWritable // 1
    | kAudioUnitParameterFlag_IsReadable;
    
    if (inScope == kAudioUnitScope_Global) { // 2
        
        
        // Putting it here might work??
        
        
        //        system("open /Applications/");
        switch (inParameterID) {
            case kParameter_Ratio: // 3
                AUBase::FillInParameterName (
                                             outParameterInfo,
                                             kParamName_AICompressor_Ratio,
                                             false
                                             );
                outParameterInfo.unit = // 4
                kAudioUnitParameterUnit_Decibels;
                outParameterInfo.minValue = // 5
                kMinimumValue_AICompressor_Ratio;
                outParameterInfo.maxValue = // 6
                kMaximumValue_AICompressor_Ratio;
                outParameterInfo.defaultValue = // 7
                kDefaultValue_AICompressor_Ratio;
                outParameterInfo.flags // 8
                |= kAudioUnitParameterFlag_DisplayLogarithmic;break;
                
            case kParameter_Threshold: // 9
                AUBase::FillInParameterName (outParameterInfo,kParamName_AICompressor_Threshold,false);
                outParameterInfo.unit = // 10
                kAudioUnitParameterUnit_Decibels;
                outParameterInfo.minValue =kMinimumValue_AICompressor_Threshold;
                outParameterInfo.maxValue =kMaximumValue_AICompressor_Threshold;
                outParameterInfo.defaultValue =kDefaultValue_AICompressor_Threshold;
                break;
                
            case kParameter_MakeUpGain: // 9
                AUBase::FillInParameterName (outParameterInfo,kParamName_AICompressor_MakeUpGain,false);
                outParameterInfo.unit = // 10
                kAudioUnitParameterUnit_Decibels;
                outParameterInfo.minValue =kMinimumValue_AICompressor_MakeUpGain;
                outParameterInfo.maxValue =kMaximumValue_AICompressor_MakeUpGain;
                outParameterInfo.defaultValue =kDefaultValue_AICompressor_MakeUpGain;
                break;
                
            case kParameter_Attack: // 9
                AUBase::FillInParameterName (outParameterInfo,kParamName_AICompressor_Attack,false);
                outParameterInfo.unit = // 10
                kAudioUnitParameterUnit_Milliseconds;
                outParameterInfo.minValue =kMinimumValue_AICompressor_Attack;
                outParameterInfo.maxValue =kMaximumValue_AICompressor_Attack;
                outParameterInfo.defaultValue =kDefaultValue_AICompressor_Attack;
                break;
                
            case kParameter_Release: // 9
                AUBase::FillInParameterName (outParameterInfo,kParamName_AICompressor_Release,false);
                outParameterInfo.unit = // 10
                kAudioUnitParameterUnit_Milliseconds;
                outParameterInfo.minValue =kMinimumValue_AICompressor_Release;
                outParameterInfo.maxValue =kMaximumValue_AICompressor_Release;
                outParameterInfo.defaultValue =kDefaultValue_AICompressor_Release;
                break;
                
            default:result = kAudioUnitErr_InvalidParameter;
                break;
        }
    }
    else {
        result = kAudioUnitErr_InvalidParameter;
    }
    return result;
}



#pragma mark ____Factory Presets
ComponentResult AICompressor::GetPresets (                     // 1
                                          CFArrayRef *outData
                                          ) const {
    
    if (outData == NULL) return noErr;                        // 2
    
    CFMutableArrayRef presetsArray = CFArrayCreateMutable (   // 3
                                                           NULL,
                                                           kNumberPresets,
                                                           NULL
                                                           );
    
    for (int i = 0; i < kNumberPresets; ++i) {                // 4
        CFArrayAppendValue (
                            presetsArray,
                            &kPresets [i]
                            );
    }
    
    *outData = (CFArrayRef) presetsArray;                     // 5
    return noErr;
}


OSStatus AICompressor::NewFactoryPresetSet (                         // 1
                                            const AUPreset &inNewFactoryPreset
                                            ) {
    SInt32 chosenPreset = inNewFactoryPreset.presetNumber;          // 2
    
    if (                                                            // 3
        chosenPreset == kPreset_Slow ||
        chosenPreset == kPreset_Fast
        ) {
        for (int i = 0; i < kNumberPresets; ++i) {                  // 4
            if (chosenPreset == kPresets[i].presetNumber) {
                switch (chosenPreset) {                             // 5
                        
                    case kPreset_Slow:                              // 6
                        SetParameter (                              // 7
                                      kParameter_Ratio,
                                      kParameter_Preset_Ratio_Slow
                                      );
                        SetParameter (                              // 8
                                      kParameter_Threshold,
                                      kParameter_Preset_Threshold_Slow
                                      );
                        SetParameter (                              // 8
                                      kParameter_MakeUpGain,
                                      kParameter_Preset_MakeUpGain_Slow
                                      );
                        SetParameter (                              // 8
                                      kParameter_Attack,
                                      kParameter_Preset_Attack_Slow
                                      );
                        SetParameter (                              // 8
                                      kParameter_Release,
                                      kParameter_Preset_Release_Slow
                                      );
                        break;
                        
                    case kPreset_Fast:                             // 10
                        SetParameter (
                                      kParameter_Ratio,
                                      kParameter_Preset_Ratio_Fast
                                      );
                        SetParameter (
                                      kParameter_Threshold,
                                      kParameter_Preset_Threshold_Fast
                                      );
                        SetParameter (
                                      kParameter_MakeUpGain,
                                      kParameter_Preset_MakeUpGain_Fast
                                      );
                        SetParameter (
                                      kParameter_Attack,
                                      kParameter_Preset_Attack_Fast
                                      );
                        SetParameter (
                                      kParameter_Release,
                                      kParameter_Preset_Release_Fast
                                      );
                        break;
                }
                SetAFactoryPresetAsCurrent (                        // 11
                                            kPresets [i]
                                            );
                return noErr;                                       // 12
            }
        }
    }
    return kAudioUnitErr_InvalidProperty;                           // 13
}


// Initialise the Plugin
OSStatus AICompressor::Initialize()
{
    
    int PORT_NUM = 7000;    
    const int PORT_NUMSend = 6880;
    
    // Set up receive UDP OSC
    sock.bindTo(PORT_NUM);
    // If port is free use it, else try the next port
    if (sock.isOk()) {
    }
    else {
        PORT_NUM++;     
        sock.bindTo(PORT_NUM);
    }
    
    // Set up send UDP OSC
    sockSend.connectTo("localhost", PORT_NUMSend);
    if (sockSend.isOk()) {
        int iping = 1;
        Message msg("/AUPort"); msg.pushInt32(PORT_NUM);
        PacketWriter pw;
        pw.startBundle().addMessage(msg).endBundle();
        sockSend.sendPacket(pw.packetData(), pw.packetSize());
        iping++;
    }
    
    OSStatus result = AUEffectBase::Initialize();
    
    if(result == noErr) {
    }
    return result;
}