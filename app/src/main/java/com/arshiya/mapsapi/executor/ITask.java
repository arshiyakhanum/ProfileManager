package com.arshiya.mapsapi.executor;

import android.support.annotation.WorkerThread;

/**
 * @author Umang Chamaria on 11/16/15.
 */
public interface ITask {
  /**
   * The business logic which needs to be run on the worker thread.
   * @return Result of the task {@link TaskResult}
   */
  @WorkerThread
  TaskResult execute();

  /**
   * Processes the result of the {@link ITask#execute()}
   * @param result Result of {@link ITask#execute()}
   */
  void onPostExecute(TaskResult result);

  /**
   * Tag name of the task
   * @return Tag associated with the task.
   */
  String getTaskTag();

  /**
   * Tells whether the task can asynchronous or not.
   * @return true if task should be run synchronously, else false
   */
  boolean isSynchronous();
}