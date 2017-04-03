package de.bandika.base;

import java.util.ArrayList;

public class DataConverter {

  public static String getIntString(ArrayList<Integer> ints){
    StringBuffer sb=new StringBuffer();
    for (int i=0;i<ints.size();i++){
      if (i>0)
        sb.append(',');
      sb.append(ints.get(i));
    }
    return sb.toString();
  }

}
