package com.uphs.sleepDiary.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.uphs.sleepDiary.shared.Result;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String name, AsyncCallback<Result> asyncCallback);

	void getSubjectInfo(String username, long[] time, boolean isTap,
			AsyncCallback<Result> callback);

	void getDataFileName(String username, long[] time, boolean isTap,
			AsyncCallback<String> callback);

}
