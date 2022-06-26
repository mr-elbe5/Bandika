package de.elbe5.companion;

import de.elbe5.data.JsonField;
import de.elbe5.log.Log;

import java.lang.reflect.Field;

public interface FieldCompanion extends JsonCompanion{

    default Object toJson(Field field, Object obj) {
        Class<?> cls = field.getType();
        return toJson(cls.cast(obj));
    }

    default void fromJson(Field field, Object jobj){
        Class<?> cls = field.getType();
        if (field.isAnnotationPresent(JsonField.class)){
            Object obj = null;
            if (field.isAnnotationPresent(JsonField.class)) {
                JsonField annotation = field.getAnnotation(JsonField.class);
                if (!annotation.valueClass().equals(Object.class)){
                    if (!annotation.keyClass().equals(Object.class)){
                        obj = fromJson(jobj, annotation.baseClass(), annotation.keyClass(), annotation.valueClass());
                    }
                    else{
                        obj = fromJson(jobj, annotation.baseClass(), annotation.valueClass());
                    }
                }
                else{
                    obj = fromJson(jobj, annotation.baseClass());
                }
                if (obj != null) {
                    try {
                        field.set(this, obj);
                    } catch (Exception e) {
                        Log.error("could not get object from json", e);
                    }
                }
            }
        }
    }

}
