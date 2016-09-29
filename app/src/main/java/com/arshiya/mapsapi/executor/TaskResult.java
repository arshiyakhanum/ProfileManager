package com.arshiya.mapsapi.executor;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Umang Chamaria on 11/16/15.
 */
public final class TaskResult implements Parcelable {

  private boolean mIsSuccess;

  private Object mPayload;

  public TaskResult() {
  }

  public TaskResult(boolean isSuccess, Object payload) {
    mIsSuccess = isSuccess;
    mPayload = payload;
  }

  protected TaskResult(Parcel in) {
    mIsSuccess = in.readInt() == 1;
  }

  public static final Creator<TaskResult> CREATOR = new Creator<TaskResult>() {
    @Override public TaskResult createFromParcel(Parcel in) {
      return new TaskResult(in);
    }

    @Override public TaskResult[] newArray(int size) {
      return new TaskResult[size];
    }
  };

  public Object getPayload() {
    return mPayload;
  }

  public TaskResult setPayload(Object payload) {
    mPayload = payload;
    return this;
  }

  public boolean isSuccess() {
    return mIsSuccess;
  }

  public void setIsSuccess(boolean isSuccess) {
    mIsSuccess = isSuccess;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(mIsSuccess ? 1 : 0);
    dest.writeParcelable((Parcelable) mPayload, flags);
  }
}