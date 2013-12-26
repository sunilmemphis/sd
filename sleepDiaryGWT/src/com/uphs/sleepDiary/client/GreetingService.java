package com.uphs.sleepDiary.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.uphs.sleepDiary.shared.Result;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	Result greetServer(String name) throws IllegalArgumentException;
	Result getSubjectInfo(String username, long[] time,boolean isTap);
	String getDataFileName(String username, long[] time,boolean isTap);
}
