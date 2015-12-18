package kr.ds.data;

import java.util.ArrayList;

public interface BaseResultListener {
	public <T> void OnComplete(T data);
	public void OnMessage(String str);
}
