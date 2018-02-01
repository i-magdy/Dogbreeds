package breeds.dog.stratos.dogbreeds.utilities;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import breeds.dog.stratos.dogbreeds.MainActivity;

/**
 * Created by botsaef on 06/12/2017.
 */

public class JsonUtils {

    public static List<String> parseJSON(String jsonData) {
        List<String> list = new ArrayList<String>();

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String status = jsonObject.getString("status");
            if ("success".equals(status)) {
                JSONArray jsonArray = jsonObject.getJSONArray("message");
                for (int i=0; i < jsonArray.length(); i++) {
                    list.add(jsonArray.getString(i));
                }
            }
        } catch (JSONException ex) {
            //Toast.makeText(MainActivity.this, "problem", Toast.LENGTH_LONG).show();
        }
        return list;
    }
}
