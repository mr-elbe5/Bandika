import de.elbe5.content.ContentData;
import de.elbe5.data.IJsonData;
import de.elbe5.file.ImageData;
import de.elbe5.user.GroupData;
import de.elbe5.log.Log;
import de.elbe5.page.PageData;
import de.elbe5.rights.SystemZone;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class Test {

    public static void main(String[] args) {
        new Test().test5();
    }

    void test1() {
        ContentData data = new ContentData();
        data.setId(500);
        data.addChild(new ContentData());
        data.addFile(new ImageData());
        data.setCreationDate(LocalDateTime.now());
        data.setChangeDate(LocalDateTime.now().plusDays(1));
        try {
            String s = data.getJSONString();
            Log.log(s);
            JSONObject jo = data.toJSONObject();
            IJsonData obj = IJsonData.createIJsonData(jo);
            s = obj.getJSONString();
            Log.log(s);

        } catch (Exception e) {
            Log.log(e.getMessage());
        }
    }

    void test2() {
        String s = toJson(3.1, Float.class);
        Log.log(s);
    }

    void test3() {
        Class<?> cls = PageData.class;
        while (!cls.equals(Object.class)) {
            Class<?>[] ifaces = cls.getInterfaces();
            for (Class<?> iface : ifaces) {
                while (iface != null) {
                    Log.log(iface.toString());
                    iface = iface.getSuperclass();
                }
            }
            cls = cls.getSuperclass();
        }
    }

    void test4(){
        Class<?> cls = Integer.class;
        boolean b = IJsonData.class.isAssignableFrom(cls);
        Log.log(Boolean.toString(b));
    }

    void test5() {
        GroupData data = new GroupData();
        data.setId(500);
        data.setCreationDate(LocalDateTime.now());
        data.setChangeDate(LocalDateTime.now().plusDays(1));
        data.getUserIds().add(5);
        data.getSystemRights().add(SystemZone.USER);
        try {
            String s = data.getJSONString();
            Log.log(s);
            JSONObject jo = data.toJSONObject();
            IJsonData obj = IJsonData.createIJsonData(jo);
            s = obj.getJSONString();
            Log.log(s);
        } catch (Exception e) {
            Log.log(e.getMessage());
        }
    }

    String toJson(Object obj, Class<?> cls) {
        Log.log(obj.getClass().getName());
        if (cls.equals(Integer.class)) {
            return Integer.toString((Integer) obj);
        }
        if (cls.equals(Boolean.class)) {
            return Boolean.toString((Boolean) obj);
        }
        if (cls.equals(String.class)) {
            return (String) obj;
        }
        return "x";
    }

    String toJson(Object obj) {
        Log.log(obj.getClass().getName());
        if (obj instanceof Integer) {
            return Integer.toString((Integer) obj);
        }
        if (obj instanceof Boolean) {
            return Boolean.toString((Boolean) obj);
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        return "x";
    }
}
