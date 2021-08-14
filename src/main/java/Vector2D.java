import com.google.gson.JsonArray;

public class Vector2D {
    private float x;
    private float y;

    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public JsonArray toJsonArray() {
        JsonArray array = new JsonArray();
        array.add(x);
        array.add(y);
        return array;
    }
}
