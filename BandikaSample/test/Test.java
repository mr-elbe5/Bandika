import de.elbe5.content.ContentData;
import de.elbe5.data.IJsonData;
import de.elbe5.file.ImageData;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class Test {

    public static void main(String[] args){
        new Test().test2();
    }

    void test1(){
        ContentData data = new ContentData();
        data.setId(500);
        data.addChild(new ContentData());
        data.addFile(new ImageData());
        data.setCreationDate(LocalDateTime.now());
        data.setChangeDate(LocalDateTime.now().plusDays(1));
        try {
            String s = data.getJSONString();
            System.out.println(s);
            JSONObject jo = new JSONObject(s);
            IJsonData obj = IJsonData.createIJsonData(jo);
            s = obj.getJSONString();
            System.out.println(s);

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    void test2(){
        String s = toJson(3.1, Float.class);
        System.out.println(s);
    }

    String toJson(Object obj, Class<?> cls){
        System.out.println(obj.getClass().getName());
        if (cls.equals(Integer.class)){
            return Integer.toString((Integer)obj);
        }
        if (cls.equals(Boolean.class)){
            return Boolean.toString((Boolean)obj);
        }
        if (cls.equals(String.class)){
            return (String)obj;
        }
        return "x";
    }

    String toJson(Object obj){
        System.out.println(obj.getClass().getName());
        if (obj instanceof Integer){
            return Integer.toString((Integer)obj);
        }
        if (obj instanceof Boolean){
            return Boolean.toString((Boolean)obj);
        }
        if (obj instanceof String){
            return (String)obj;
        }
        return "x";
    }
}
