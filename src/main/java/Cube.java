import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cube {
    private String name;
    private CubeDimensions dimensions;
    private Vector3D offset;
    private Vector3D rotation;
    private Vector3D position;
    private Vector3D scale;
    private Vector2D textureOffset;
    private boolean textureMirrored;
    private final List<Cube> childrens = new ArrayList();

    public Cube(String name, CubeDimensions dimensions, Vector3D offset, Vector3D rotation, Vector3D position, Vector3D scale, Vector2D textureOffset, boolean textureMirrored) {
        this.name = name;
        this.dimensions = dimensions;
        this.offset = offset;
        this.rotation = rotation;
        this.position = position;
        this.scale = scale;
        this.textureOffset = textureOffset;
        this.textureMirrored = textureMirrored;
    }

    public void addChildren(Cube children){
        childrens.add(children);
    }

    public JsonObject toJsonObject(){
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.add("dimensions", dimensions.toJsonArray());
        object.add("position", position.toJsonArray());
        object.add("offset", offset.toJsonArray());
        object.add("rotation", rotation.toJsonArray());
        object.add("scale", scale.toJsonArray());
        object.add("txOffset", textureOffset.toJsonArray());
        object.addProperty("txMirror", textureMirrored);
        object.addProperty("mcScale", 0.0F);
        object.addProperty("opacity", 100.0F);
        object.addProperty("hidden", false);
        object.add("metadata", new JsonArray());
        JsonArray childrensArray = new JsonArray();
        for (int i = 0;i<this.childrens.size();i++) {
            childrensArray.add(childrens.get(i).toJsonObject());
        }
        object.add("children", childrensArray);
        Random r = new Random();
        String identifier = "";
        for (int i = 0; i < 15; i++) {
            identifier += (char)(r.nextInt(26) + 'a');
        }

        object.addProperty("identifier", identifier);
        return object;
    }

    public static class CubeDimensions {
        private int x;
        private int y;
        private int z;

        public CubeDimensions(int x, int y, int z) {
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
}
