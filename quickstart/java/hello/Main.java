
import java.util.concurrent.Future;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioStreamFormat;
import com.microsoft.cognitiveservices.speech.internal.AudioStreamContainerFormat;
import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStream;
import com.microsoft.cognitiveservices.speech.audio.AudioInputStream;
/**
 * Quickstart: recognize speech using the Speech SDK for Java.
 */
public class Main {

    /**
     * @param args Arguments are ignored in this sample.
     */
    public static void main(String[] args) {
    	System.out.println("Hello, World");
        try {
			AudioConfig audConfig = null;
            // Replace below with your own subscription key
            String speechSubscriptionKey = args[0];
            // Replace below with your own service region (e.g., "westus").
            String serviceRegion = args[1];

            int exitCode = 1;
            SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);

            assert(config != null);
			if(args[2].contains(".wav"))
			{
				audConfig = AudioConfig.fromWavFileInput(args[2]);
			}
			else if(args[2].contains(".mp3")){
				PullAudioInputStream pullAudio = AudioInputStream.createPullStream(new BinaryAudioStreamReader(args[2]),
						AudioStreamFormat.getCompressedFormat(AudioStreamContainerFormat.MP3));
				audConfig = AudioConfig.fromStreamInput(pullAudio);
			}
			else if(args[2].contains(".opus")){
				PullAudioInputStream pullAudio = AudioInputStream.createPullStream(new BinaryAudioStreamReader(args[2]),
						AudioStreamFormat.getCompressedFormat(AudioStreamContainerFormat.OGG_OPUS));
				audConfig = AudioConfig.fromStreamInput(pullAudio);
			}

            SpeechRecognizer reco = new SpeechRecognizer(config, audConfig);
            assert(reco != null);

            System.out.println("Say something...");

            Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
            assert(task != null);

            SpeechRecognitionResult result = task.get();
            assert(result != null);

            if (result.getReason() == ResultReason.RecognizedSpeech) {
                System.out.println("We recognized: " + result.getText());
                exitCode = 0;
            }
            else if (result.getReason() == ResultReason.NoMatch) {
                System.out.println("NOMATCH: Speech could not be recognized.");
            }
            else if (result.getReason() == ResultReason.Canceled) {
                CancellationDetails cancellation = CancellationDetails.fromResult(result);
                System.out.println("CANCELED: Reason=" + cancellation.getReason());

                if (cancellation.getReason() == CancellationReason.Error) {
                    System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                    System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                    System.out.println("CANCELED: Did you update the subscription info?");
                }
            }
			
			System.out.println("Closing reco");
            //reco.close();
            
            System.exit(exitCode);
        } catch (Exception ex) {
            System.out.println("Unexpected exception: " + ex.getMessage());

            assert(false);
            System.exit(1);
        }
    }
}
