package com.example.myapplication.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import retrofit2.Response;

public class Result<T> {

    private final T result;
    private final Throwable error;

    private Result(@Nullable T result, @Nullable Throwable error) {
        this.result = result;
        this.error = error;
    }

    @NonNull
    public static <T> Result<T> success(@NonNull T result) {
        return new Result(result, null);
    }

    @NonNull
    public static <T> Result<T> error(@NonNull Throwable error) {
        return new Result(null, error);
    }

    @NonNull
    public static <T> Result<T> errorResponse(String shortDescription, Response response) {
        String errorString = String.format("%d %s", response.code(), shortDescription);
        return new Result(null, new Throwable(errorString));
    }

    public boolean isSuccessful() {
        return this.error == null;
    }

    @Nullable
    public T getResult() {
        return result;
    }

    @Nullable
    public Throwable getError() {
        return error;
    }
}
