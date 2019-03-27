import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStreamCallback;

public class BinaryAudioStreamReader extends PullAudioInputStreamCallback {
	InputStream insputStream;

    BinaryAudioStreamReader(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        insputStream = new FileInputStream(file);
    }

    @Override
    public int read(byte[] dataBuffer) {
        try {
            int retVal = insputStream.read(dataBuffer, 0, dataBuffer.length);
			if(retVal == -1) {
				return 0;
			}
			return retVal;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Closes the audio input stream.
     */
    @Override
    public void close() {
        try {
            insputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
