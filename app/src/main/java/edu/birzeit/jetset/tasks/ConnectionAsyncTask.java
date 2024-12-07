package edu.birzeit.jetset.tasks;

import android.os.AsyncTask;
import android.util.Log;

import edu.birzeit.jetset.api.HttpManager;


public class ConnectionAsyncTask extends AsyncTask<String, String, String> {
    private static final String TAG = "ConnectionAsyncTask";
    private TaskCallback taskCallback;

    public ConnectionAsyncTask(TaskCallback taskCallback) {
        this.taskCallback = taskCallback;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = HttpManager.getData(params[0]);
        return result != null && !result.isEmpty() ? result : null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            taskCallback.onTaskSuccess(result);
            Log.d(TAG, "Task succeeded: " + result);
        } else {
            taskCallback.onTaskFailure();
        }
    }

    public interface TaskCallback {
        void onTaskSuccess(String result);

        void onTaskFailure();
    }
}

