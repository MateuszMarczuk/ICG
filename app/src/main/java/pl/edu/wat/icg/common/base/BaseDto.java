package pl.edu.wat.icg.common.base;

import java.io.Serializable;


public class BaseDto<T extends Serializable> {
    T id;

    public BaseDto(T id) {
        this.id = id;
    }
}
