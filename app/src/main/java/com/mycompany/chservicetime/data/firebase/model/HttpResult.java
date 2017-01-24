package com.mycompany.chservicetime.data.firebase.model;

import com.google.auto.value.AutoValue;

/**
 * 包装类，统一处理返回值.
 *
 * @param <T> the return type
 */
@AutoValue
public abstract class HttpResult<T> {
    public abstract int resultCode();

    public abstract String resultMessage();

    // Data
    public abstract T data();
}
