import de.elbe5.data.IJsonData;
import de.elbe5.test.TestData;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class Test {

    public static void main(String[] args){
        TestData data = new TestData();
        data.setId(500);
        data.setCreationDate(LocalDateTime.now());
        data.setChangeDate(LocalDateTime.now().plusDays(1));
        data.setTitle("testing");
        data.subData = new TestData();
        data.subData.setTitle("sub1");
        try {
            String s = data.getJSONString();
            System.out.println(s);
            JSONObject jo = new JSONObject(s);
            IJsonData obj = IJsonData.createFromJSONObject(jo);
            s = obj.getJSONString();
            System.out.println(s);

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
