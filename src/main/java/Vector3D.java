import com.google.gson.JsonArray;

public class Vector3D {
    private float x;
    private float y;
    private float z;

    public Vector3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public JsonArray toJsonArray() {
        JsonArray array = new JsonArray();
        array.add(x);
        array.add(y);
        array.add(z);
        return array;
    }
}
