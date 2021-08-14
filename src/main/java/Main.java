import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Main {

    public static void main(String[] args) throws IOException {
        File bkmsRoot = new File("./bkm");
        if (!bkmsRoot.exists())
            bkmsRoot.mkdir();
        File outputsRoot = new File("./bkm/tbl");
        deleteFolder(outputsRoot);
        outputsRoot.mkdir();
        File[] files = bkmsRoot.listFiles((dir, name) -> name.endsWith(".bkm"));
        for (File file : files) {
            ZipFile zipFile = new ZipFile(file);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            ZipEntry entry = entries.nextElement();
            InputStream stream = zipFile.getInputStream(entry);
            JsonObject jsonObject = (JsonObject) JsonParser.parseReader(new JsonReader(new InputStreamReader(stream)));
            JsonArray cubes = jsonObject.getAsJsonArray("cubes");
            HashMap<String, Cube> cubesMap = new HashMap<>();
            List<Cube> rootCubesMap = new ArrayList<>();
            for (JsonElement cubeElement : cubes) {
                JsonObject cubeObject = cubeElement.getAsJsonObject();
                String cubeName = cubeObject.get("name").getAsString();
                Cube.CubeDimensions dimensions = new Cube.CubeDimensions(cubeObject.get("box").getAsJsonArray().get(0).getAsInt(), cubeObject.get("box").getAsJsonArray().get(1).getAsInt(), cubeObject.get("box").getAsJsonArray().get(2).getAsInt());
                Vector2D textureOffset = new Vector2D(cubeObject.get("txo").getAsJsonArray().get(0).getAsFloat(), cubeObject.get("txo").getAsJsonArray().get(1).getAsFloat());
                Vector3D rotation = new Vector3D(cubeObject.get("rot").getAsJsonArray().get(0).getAsFloat(), cubeObject.get("rot").getAsJsonArray().get(1).getAsFloat(), cubeObject.get("rot").getAsJsonArray().get(2).getAsFloat());
                Vector3D offset = new Vector3D(cubeObject.get("off").getAsJsonArray().get(0).getAsFloat(), cubeObject.get("off").getAsJsonArray().get(1).getAsFloat(), cubeObject.get("off").getAsJsonArray().get(2).getAsFloat());
                Vector3D position = new Vector3D(cubeObject.get("pos").getAsJsonArray().get(0).getAsFloat(), cubeObject.get("pos").getAsJsonArray().get(1).getAsFloat(), cubeObject.get("pos").getAsJsonArray().get(2).getAsFloat());
                Vector3D scale = new Vector3D(1, 1, 1);
                Cube cube = new Cube(cubeName, dimensions, offset, rotation, position, scale, textureOffset, cubeObject.get("txm").getAsBoolean());
                cubesMap.put(cubeName, cube);
                String parentName = cubeObject.get("par").getAsString();
                if (!parentName.equals("null")) {
                    cubesMap.get(parentName).addChildren(cube);
                } else {
                    rootCubesMap.add(cube);
                }
            }
            System.out.println("File name: " + file.getName());
            System.out.println("Cubes: " + cubesMap.size());

            File outputFile = new File(outputsRoot.getPath() + "/" + file.getName().replace(".bkm", ".tbl"));
            outputFile.createNewFile();
            ZipOutputStream outputZipFile = new ZipOutputStream(new FileOutputStream(outputFile));

            JsonObject root = new JsonObject();

            root.addProperty("modelName", file.getName().replace(".bkm", ""));
            root.addProperty("authorName", "grillo78");
            root.addProperty("projVersion", 4);
            root.add("metadata", new JsonArray());
            root.addProperty("textureWidth", jsonObject.get("tw").getAsInt());
            root.addProperty("textureHeight", jsonObject.get("th").getAsInt());

            JsonArray scale = new JsonArray();
            scale.add(1);
            scale.add(1);
            scale.add(1);
            root.add("scale", scale);
            root.add("cubeGroups", new JsonArray());
            JsonArray cubesArray = new JsonArray();
            for (Cube cube : rootCubesMap) {
                cubesArray.add(cube.toJsonObject());
            }

            root.add("cubes", cubesArray);
            root.add("anims", new JsonArray());
            root.addProperty("cubeCount", cubesMap.size());

            ZipEntry e = new ZipEntry("model.json");
            outputZipFile.putNextEntry(e);
            outputZipFile.write(root.toString().getBytes());
            outputZipFile.closeEntry();
            outputZipFile.close();
            zipFile.close();
            System.out.println(file.getName() + " conversion finished.");
        }
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
}
