package kr.ds.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.Callable;

public class DsAsyncTask<T> extends AsyncTask<Void, Integer, T> {
	private static final String TAG = "CustomAsyncTask";
	private DsAsyncTaskCallback<T> mCallback;
	
	private Callable<T> mCallable; 
	private Exception mException;
	
	public DsAsyncTask<T> setCallable(Callable<T> callable) {
		this.mCallable = callable;
		return this;
	}
	
	public DsAsyncTask<T> setCallback(DsAsyncTaskCallback<T> callback) {
		this.mCallback = callback;
		return this;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		if(this.mCallback != null){
			this.mCallback.onPreExecute();
		}
	}
	@Override
	protected T doInBackground(Void... params) {
		// TODO Auto-generated method stub
		try {
			return mCallable.call();
		} catch (Exception ex) {
			Log.e(TAG,"exception occured while doing in background: " + ex.getMessage(), ex);
			this.mException = ex;
			return null;
		}
	}
	@Override
	protected void onPostExecute(T result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (isException()) {
			if (mCallback != null){
				mCallback.Exception(mException);
				return;
			}
		}
		if(this.mCallback != null){
			this.mCallback.onPostExecute(result);
		}
	}
	private boolean isException() {
		return mException != null;
	}
	
	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
		if(this.mCallback != null){
			this.mCallback.onCancelled();
		}
	}


}
