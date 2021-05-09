import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestClass {
    TestClass(){
        JSONObject member = new JSONObject();
        try {
            member.put("a","a");
            member.put("b","a");
            member.put("c","a");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray array = new JSONArray();
        array.put(member);
        array.put(member);
        array.put(member);
        JSONArray arr2 = new JSONArray(array.toString() + member.toString());
        System.out.println(arr2.toString());
        //System.out.println(arr2.get(1));
        //System.out.println(arr2.length());

    }
    public static void main(String[] args) {
        TestClass tc = new TestClass();
    }
}