import de.elbe5.content.ContentData;
import de.elbe5.data.IJsonData;
import de.elbe5.file.ImageData;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class Test {

    public static void main(String[] args){
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
}
