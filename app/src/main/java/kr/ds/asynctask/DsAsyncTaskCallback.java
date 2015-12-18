package kr.ds.asynctask;

public interface DsAsyncTaskCallback<T> {
	public void onPreExecute();
	public void onPostExecute(T result);
	public void onCancelled();
	public void Exception(Exception e);
	
}
