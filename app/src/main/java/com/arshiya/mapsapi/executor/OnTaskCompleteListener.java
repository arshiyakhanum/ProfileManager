package com.arshiya.mapsapi.executor;

/**
 * Callback Listener for task completion.
 * @author Umang Chamaria on 11/16/15.
 */
public interface OnTaskCompleteListener {

  void onTaskComplete(String tag, TaskResult taskResult);
}