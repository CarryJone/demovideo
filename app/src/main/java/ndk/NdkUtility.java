package ndk;

public class NdkUtility {

    public native static String getMode();
    public native static String getKey();

	static {
        System.loadLibrary("my-jni");
    }
}
