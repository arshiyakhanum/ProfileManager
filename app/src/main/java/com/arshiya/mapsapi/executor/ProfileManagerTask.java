package com.arshiya.mapsapi.executor;

import android.content.Context;

/**
 * @author Umang Chamaria on 11/17/15.
 */
public abstract class ProfileManagerTask implements ITask {

  protected Context mContext;
  protected TaskResult mTaskResult;

  public ProfileManagerTask(Context context) {
    mContext = context;
    mTaskResult = new TaskResult();
    mTaskResult.setIsSuccess(false);
  }

  @Override public void onPostExecute(TaskResult result) {

  }

  public TaskResult createTaskResult(Object payload, boolean taskState){
    mTaskResult.setPayload(payload);
    mTaskResult.setIsSuccess(taskState);
    return mTaskResult;
  }
}