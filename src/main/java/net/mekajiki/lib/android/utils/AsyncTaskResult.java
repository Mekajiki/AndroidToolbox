package net.mekajiki.lib.android.utils;

public class AsyncTaskResult<T> {
    public final T content;
    public final int resourceId;
    public final boolean isError;

    private AsyncTaskResult(T content, boolean isError, int resourceId) {
        this.content = content;
        this.isError = isError;
        this.resourceId = resourceId;
    }

    public static <T> AsyncTaskResult<T> createSuccessResult(T content) {
        return new AsyncTaskResult<T>(content, false, 0);
    }

    public static <T> AsyncTaskResult<T> createFailureResult(int resId) {
        return new AsyncTaskResult<T>(null, true, resId);
    }
}