package com.example.mds_user.demovideo.gcm;

import android.util.Log;

public class TraceUtility {
	
	public enum TraceType {
		error,
		verbose,
		debug,
		info,
		warn
	}
	
	public static void trace(TraceType traceType, String tag, String message) {
		tag = "mds" + tag;

		switch (traceType) {
		case error:
			Log.e(tag, message);
			break;
		case verbose:
			Log.v(tag, message);
			break;
		case debug:
			Log.d(tag, message);
			break;
		case info:
			Log.i(tag, message);
			break;
		case warn:
			Log.w(tag, message);
			break;
		}

	}
}
