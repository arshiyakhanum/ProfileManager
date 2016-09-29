package com.arshiya.mapsapi.executor;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Umang Chamaria on 11/16/15.
 */
public class TaskProcessor {

  private BlockingDeque<ITask> mTaskQueue;

  private ExecutorService mExecutorService = Executors.newCachedThreadPool();

  private ArrayList<WeakReference<OnTaskCompleteListener>> mTaskCompleteListeners;

  private ITask mActive;

  private final Object lock = new Object();

  private static TaskProcessor _INSTANCE = null;

  private TaskProcessor() {
    mTaskQueue = new LinkedBlockingDeque<>();
    mTaskCompleteListeners = new ArrayList<>();
  }

  public static TaskProcessor getInstance(){
    if (_INSTANCE == null){
      _INSTANCE = new TaskProcessor();
    }
    return _INSTANCE;
  }

  public void addTask(ITask task) {
    if (task != null) {
      mTaskQueue.add(task);
      startExecution();
    }
  }

  public void addTaskToFront(ITask task) {
    if (task != null) {
      mTaskQueue.addFirst(task);
      startExecution();
    }
  }

  public void startExecution() {
    if (mActive == null) {
      scheduleNext();
    }
  }

  private void scheduleNext() {
    if ((mActive = mTaskQueue.poll()) != null) {
      mExecutorService.submit(new Runnable() {
        @Override
        public void run() {
          executeTask(mActive);
          scheduleNext();
        }
      });
    }
  }

  private void executeTask(ITask task) {
    TaskResult result = task.execute();
    String action = task.getTaskTag();
    if (!TextUtils.isEmpty(action))
      notifyListener(action, result);

    task.onPostExecute(result);
  }

  public void removeOnTaskCompleteListener(@NonNull OnTaskCompleteListener listener) {
    if (mTaskCompleteListeners != null && listener != null) {
      int index = mTaskCompleteListeners.indexOf(listener);
      if (index != -1) mTaskCompleteListeners.remove(index);
    }
  }

  public void setOnTaskCompleteListener(OnTaskCompleteListener listener) {
    mTaskCompleteListeners.add(new WeakReference<OnTaskCompleteListener>(listener));
  }

  public void notifyListener(String tag, TaskResult result) {
    synchronized (lock) {
      if (mTaskCompleteListeners != null) {
        for (WeakReference<OnTaskCompleteListener> taskCompleteListener : mTaskCompleteListeners){
          if (taskCompleteListener.get() != null){
            taskCompleteListener.get().onTaskComplete(tag, result);
          }
        }
      }
    }
  }
}