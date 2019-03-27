#include <iostream> // cin, cout
#include <speechapi_cxx.h>

using namespace std;
using namespace Microsoft::CognitiveServices::Speech;
using namespace Microsoft::CognitiveServices::Speech::Audio;


void* OpenCompressedFile(const std::string& compressedFileName)
{
    FILE *filep = NULL;
    {
#ifdef WIN32
        std::wstring filename(compressedFileName.begin(), compressedFileName.end());
        _wfopen_s(&filep, filename.c_str(), L"rb");
#else
        filep = fopen(compressedFileName.c_str(), "rb");
#endif
    }
    return filep;
}

void closeStream(void* fp)
{
    fclose((FILE*)fp);
}

int ReadCompressedBinaryData(void *_stream, uint8_t *_ptr, uint32_t _buf_size)
{
    size_t  ret;
    FILE* localRead = (FILE*)_stream;
    ret = fread(_ptr, 1, _buf_size, localRead);
    /*If ret==0 and !feof(stream), there was a read error.*/
    return ret > 0 || feof(localRead) ? (int)ret : -128;
}


void recognizeSpeech(const std::string& subscriptionKey, const std::string& wavFileName) {
    // Creates an instance of a speech config with specified subscription key and service region.
    // Replace with your own subscription key and service region (e.g., "westus").
    std::shared_ptr<SpeechRecognizer> recognizer;
    std::shared_ptr<PullAudioInputStream> pullAudio;

    auto config = SpeechConfig::FromSubscription(subscriptionKey, "westus");
   
    if (wavFileName.find(".mp3") == (wavFileName.size() - 4))
    {
        pullAudio = AudioInputStream::CreatePullStream(
            AudioStreamFormat::GetCompressedFormat(AudioStreamContainerFormat::MP3),
            OpenCompressedFile(wavFileName),
            ReadCompressedBinaryData,
            closeStream
        );
        recognizer = SpeechRecognizer::FromConfig(config, AudioConfig::FromStreamInput(pullAudio));
    }
    else if (wavFileName.find(".opus") == (wavFileName.size() - 5))
    {
        pullAudio = AudioInputStream::CreatePullStream(
            AudioStreamFormat::GetCompressedFormat(AudioStreamContainerFormat::OGG_OPUS),
            OpenCompressedFile(wavFileName),
            ReadCompressedBinaryData,
            closeStream
        );
        recognizer = SpeechRecognizer::FromConfig(config, AudioConfig::FromStreamInput(pullAudio));
    }
    else
    {
        recognizer = SpeechRecognizer::FromConfig(config, AudioConfig::FromWavFileInput(wavFileName));
    }

    auto result = recognizer->RecognizeOnceAsync().get();

    // Checks result.
    if (result->Reason == ResultReason::RecognizedSpeech) {
        cout << "We recognized: " << result->Text << std::endl;
    }
    else if (result->Reason == ResultReason::NoMatch) {
        cout << "NOMATCH: Speech could not be recognized." << std::endl;
    }
    else if (result->Reason == ResultReason::Canceled) {
        auto cancellation = CancellationDetails::FromResult(result);
        cout << "CANCELED: Reason=" << (int)cancellation->Reason << std::endl;

        if (cancellation->Reason == CancellationReason::Error) {
            cout << "CANCELED: ErrorCode= " << (int)cancellation->ErrorCode << std::endl;
            cout << "CANCELED: ErrorDetails=" << cancellation->ErrorDetails << std::endl;
            cout << "CANCELED: Did you update the subscription info?" << std::endl;
        }
    }

    //recognizer->
}

int main(int argc, char **argv) {
    setlocale(LC_ALL, "");
    recognizeSpeech(argv[1], argv[2]);
    return 0;
}
