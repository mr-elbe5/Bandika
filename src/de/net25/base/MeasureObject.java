package de.net25.base;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * Class measureObject measures the size of different
 * objects. Including byte, boolean, short, char, integer,
 * long, float, double, object, string, arrays, date,
 * ArrayList, HashSet and HashMap.
 */
public class MeasureObject {

  /* Declarations of all primitive data types. */
  /**
   * Field byteSize - size of the data type byte.
   */
  public final static byte BYTESIZE = 1;

  /**
   * Field booleanSize - size of the data type boolean.
   */
  public final static byte BOOLEANSIZE = 1;

  /**
   * Field shortSize - size of the data type short.
   */
  public final static byte SHORTSIZE = 2;

  /**
   * Field charSize - size of the data type char.
   */
  public final static byte CHARSIZE = 2;

  /**
   * Field intSize - size of the data type integer.
   */
  public final static byte INTSIZE = 4;

  /**
   * Field floatSize - size of the data type float.
   */
  public final static byte FLOATSIZE = 4;

  /**
   * Field longSize - size of the data type long.
   */
  public final static byte LONGSIZE = 8;

  /**
   * Field doubleSize - size of the data type double.
   */
  public final static byte DOUBLESIZE = 8;
  /* ------------------------------------------ */

  /**
   * Field referenceSize - size of a reference to an object.
   */
  private static byte referenceSize = 0;

  /**
   * Field objectSize - overhead of any objects.
   */
  public static byte objectSize = 0;

  /**
   * Field dateSize - size of data structure date.
   */
  public static byte dateSize = 0;

  /**
   * Field stringSize - size of data type string (empty).
   */
  public static byte stringSize = 0;

  /**
   * Field arrayListSize - size of data structure ArrayList (empty).
   */
  public static short arrayListSize = 0;

  /**
   * Field hashMapSize - size of data structure HashMap (empty).
   */
  public static short hashMapSize = 0;

  /**
   * Field hashSetSize - size of data structure HashSet (empty).
   */
  public static short hashSetSize = 0;

  /**
   * Method setEnv sets the data sizes for the operating system.
   */
  public static void setEnv() {

    /* Get information about the current environment */
    String str = System.getProperties().getProperty("java.vm.name");

    /* True, if the system doesn't work with 64 bit.  This values are based on experienced data.*/
    if (!str.contains("64")) {
      referenceSize = 4;
      objectSize = 8;
      dateSize = 24;
      stringSize = 40;
      arrayListSize = 80;
      hashMapSize = 120;
      hashSetSize = 136;
    } else {
      referenceSize = 8;
      objectSize = 16;
      dateSize = 32;
      stringSize = 64;
      arrayListSize = 144;
      hashMapSize = 216;
      hashSetSize = 240;
    }

  }

  /**
   * Method sizeOfDataStructure calculates the size of HashMap hm
   * including the keys and values.
   *
   * @param hm of type HashMap.
   * @return the size of the HashMap hm in bytes.
   */
  private static long sizeOfDataStructure(HashMap hm) {

    long size = hashMapSize;

    Iterator<?> it = hm.keySet().iterator();

    /* Calculates the size of all keys and stored objects. */
    while (it.hasNext()) {
      /* Object - Key */
      Object o_k = it.next();
      /* Object - Value */
      Object o_v = hm.get(o_k);
      size += sizeOfObject(o_k);
      size += sizeOfObject(o_v);
    }

    return size;
  }

  /**
   * Method sizeOfDataStructure calculates the size of the HashSet hs
   * including the object value.
   *
   * @param hs of type HashSet.
   * @return the size of the HashSet hs in bytes.
   */
  private static long sizeOfDataStructure(HashSet hs) {

    long size = hashSetSize;

    for (Object o : hs)
      size += sizeOfObject(o);

    return size;
  }

  /**
   * Method sizeOfDataStructure calculates the size of the
   * ArrayList l.
   *
   * @param l of type ArrayList.
   * @return the size of the ArrayList l in bytes.
   */
  private static long sizeOfDataStructure(ArrayList l) {

    long size = arrayListSize;

    for (int i = 0; i < l.size(); ++i) {
      /* Object value */
      Object o_v = l.get(i);
      size += sizeOfObject(o_v);
    }

    return size;
  }

  /**
   * Method sizeOfPrimClass calculates the size of primitive
   * objects.
   *
   * @param cn of type string and describes the name of the class.
   * @return size of the primitive object.
   */
  private static long sizeOfPrimClass(String cn) {

    if (cn.compareTo("boolean") == 0)
      return BOOLEANSIZE;

    if (cn.compareTo("byte") == 0)
      return BYTESIZE;

    if (cn.compareTo("short") == 0)
      return SHORTSIZE;

    if (cn.compareTo("char") == 0)
      return CHARSIZE;

    if (cn.compareTo("int") == 0)
      return INTSIZE;

    if (cn.compareTo("float") == 0)
      return FLOATSIZE;

    if (cn.compareTo("long") == 0)
      return LONGSIZE;

    if (cn.compareTo("double") == 0)
      return DOUBLESIZE;

    return 0;
  }

  /**
   * Method sizeOfObjectInstance calculates the size of
   * object instances like HashMap, HashSet, ArrayList, date,
   * string, object or other java.lang.objects for example java.lang.Boolean.
   *
   * @param cn of type string and describes the name of the class.
   * @param o  of type object.
   * @return the size of the object o in bytes.
   */
  private static long sizeOfObjectInstance(String cn, Object o) {

    if (cn.compareTo("java.lang.Boolean") == 0)
      return objectSize + BOOLEANSIZE + 7;

    if (cn.compareTo("java.lang.Byte") == 0)
      return objectSize + BYTESIZE + 7;

    if (cn.compareTo("java.lang.Short") == 0)
      return objectSize + SHORTSIZE + 6;

    if (cn.compareTo("java.lang.Character") == 0)
      return objectSize + CHARSIZE + 6;

    if (cn.compareTo("java.lang.Integer") == 0)
      return objectSize + INTSIZE + 4;

    if (cn.compareTo("java.lang.Float") == 0)
      return objectSize + FLOATSIZE + 4;

    if (cn.compareTo("java.lang.Long") == 0)
      return objectSize + LONGSIZE;

    if (cn.compareTo("java.lang.Double") == 0)
      return objectSize + DOUBLESIZE;

    if (cn.compareTo("java.lang.Object") == 0)
      return objectSize;

    if ((cn.compareTo("java.lang.String") == 0)
        && (!o.getClass().isArray()))
      return ((String) o).length() == 0
          ? stringSize
          : stringSize + (((String) o).length()) * CHARSIZE
          ;

    if (cn.compareTo("java.util.Date") == 0)
      return dateSize;

    if ((cn.compareTo("java.util.ArrayList") == 0)
        && (!o.getClass().isArray()))
      return sizeOfDataStructure((ArrayList) o);

    if ((cn.compareTo("java.util.HashSet") == 0)
        && (!o.getClass().isArray()))
      return sizeOfDataStructure((HashSet) o);

    if ((cn.compareTo("java.util.HashMap") == 0)
        && (!o.getClass().isArray()))
      return sizeOfDataStructure((HashMap) o);

    return 0;
  }

  /**
   * Method isValidClass validate the class c.
   *
   * @param c of type class
   * @return true - if the class is a part of opus5 or
   *         a normal class, otherwise false.
   */
  private static boolean isValidClass(Class<?> c) {
    return (c.getPackage() == null)
        || (c.getName().contains("de.net25"))
        ? true : false;
  }

  /**
   * Method isPrimitive looking for a primitive data type.
   *
   * @param cn of type string - name of the type of the class.
   * @return true - if the object is a primitive type, otherwise
   *         false.
   */
  private static boolean isPrimitive(String cn) {
    return (cn.compareTo("boolean") == 0)
        || (cn.compareTo("byte") == 0)
        || (cn.compareTo("short") == 0)
        || (cn.compareTo("int") == 0)
        || (cn.compareTo("char") == 0)
        || (cn.compareTo("float") == 0)
        || (cn.compareTo("double") == 0)
        || (cn.compareTo("long") == 0)
        ? true
        : false;
  }

  /**
   * Method sizeOfAnyClasses calculates the size of
   * different classes including primitive, data structures, any
   * classes and super classes.
   *
   * @param c of type class - class of object o.
   * @param o of type object.
   * @return size of class (type long) in bytes.
   */
  private static long sizeOfAnyClass(Class<?> c, Object o) {

    long size = 0;

    int i = 0;

    /* True - primitive class. */
    if ((size = sizeOfPrimClass(c.getName())) != 0)
      return size;

    /* True - data structure like HasMap, HashSet, ArrayList. */
    if ((size = sizeOfObjectInstance(c.getName(), o)) != 0)
      return size;

    if (isValidClass(c)) {

      /* Calculate Fields. */
      Field fs[] = c.getDeclaredFields();
      for (i = 0; i < fs.length; ++i) {
        try {
          /* Access for private fields. */
          fs[i].setAccessible(true);

          /* Object of field fs[i]. */
          Object o_f = fs[i].get(o);

          /* Only non-static fields */
          if ((fs[i].getModifiers() & Modifier.STATIC) == 0) {
            size += sizeOfObject(o_f);

            /* Including reference if non primitive class */
            if (!isPrimitive(fs[i].getType().getName()))
              size += referenceSize;
          }

        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      /* Calculate super classes. */
      if (c.getSuperclass() != null)
        size += sizeOfAnyClass(c.getSuperclass(), o);

      return size;

    }

    return 0;

  }

  /**
   * Method sizeOfArrayClass calculates the size of
   * an array object.
   *
   * @param c of type class - Information about the component type of
   *          the array.
   * @param o represents the array.
   * @return classSize (type long) of an array class.
   */
  private static long sizeOfArrayClass(Class<?> c, Object o) {

    long size = 0;

    long len = Array.getLength(o);

    /* Class name. */
    String cn = c.getComponentType().getName();

    if ((size = sizeOfPrimClass(cn)) != 0)
      return size * len;

    /* Array is nested. */
    for (int i = 0; i < len; ++i) {
      size += referenceSize + sizeOfObject(Array.get(o, i));
    }

    return size;

  }

  /**
   * Method sizeOfObject calculates the size of the following
   * kinds of objects:
   * Byte, boolean, short, char, integer, long, float, double,
   * object, string, arrays, date, ArrayList, HashSet, HashMap
   * and any classes.
   *
   * @param o of type object - object which should be measured.
   * @return the size of the object o in bytes.
   */
  private static long sizeOfObject(Object o) {

    long size = 0;

    if (o == null)
      return size;

    Class<?> c = o.getClass();

    if (c.isArray())
      size += sizeOfArrayClass(c, o);
    else
      size += sizeOfAnyClass(c, o);

    return size;
  }

  /**
   * Method calculates the size of object o.
   *
   * @param o type of object - object which should be measured.
   * @return the size of object o in bytes.
   */
  public static long sizeOf(Object o) {
    long size = sizeOfObject(o);
		return (size%8) == 0
			   ? size
		       : size + (8-(size%8))
	    ;
	}
}


