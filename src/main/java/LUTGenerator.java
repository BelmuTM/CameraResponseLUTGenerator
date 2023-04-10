import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LUTGenerator {

    private static final String redCurveName   = "dscs315R1";
    private static final String greenCurveName = "dscs315G1";
    private static final String blueCurveName  = "dscs315B1";

    private static final String curveName = redCurveName.replace("R", "");

    private static final File curveDataFile = new File("src/main/java/input/dorfCurves.txt");

    private static final String irradianceLUTPath = "src/main/java/output/cameraIrradiance" + curveName + ".dat";
    private static final String intensityLUTPath  = "src/main/java/output/cameraIntensity"  + curveName + ".dat";

    public static float[] stringToFloatArray(String string) {
        String[] elements = string.split("   ");

        float[] output = new float[elements.length];
        for(int i = 0; i < elements.length; i++) {
            output[i] = Float.parseFloat(elements[i]);
        }
        return output;
    }

    public static String findCurveInFile(String curveName, String type) {
        try {
            Scanner scanner = new Scanner(curveDataFile);

            while(scanner.hasNextLine()) {
                if(scanner.nextLine().equalsIgnoreCase(curveName)) {
                    while(scanner.hasNextLine()) {
                        if (scanner.nextLine().contains(type + " =")) {
                            return scanner.nextLine();
                        }
                    }
                }
            }
        } catch(IOException ioe) { ioe.printStackTrace(); }
        return "";
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        float[] irradianceArrR = stringToFloatArray(findCurveInFile(redCurveName,   "I"));
        float[] irradianceArrG = stringToFloatArray(findCurveInFile(greenCurveName, "I"));
        float[] irradianceArrB = stringToFloatArray(findCurveInFile(blueCurveName,  "I"));

        float[] intensityArrR = stringToFloatArray(findCurveInFile(redCurveName,   "B"));
        float[] intensityArrG = stringToFloatArray(findCurveInFile(greenCurveName, "B"));
        float[] intensityArrB = stringToFloatArray(findCurveInFile(blueCurveName,  "B"));

        List<Float> irradiance = new ArrayList<>();
        List<Float> intensity = new ArrayList<>();

        for(int i = 0; i < 1024; i++) {
            irradiance.add(irradianceArrR[i]); irradiance.add(irradianceArrG[i]); irradiance.add(irradianceArrB[i]);
            intensity.add(intensityArrR[i]); intensity.add(intensityArrG[i]); intensity.add(intensityArrB[i]);
        }

        try {
            FileOutputStream irradianceOutputStream     = new FileOutputStream(irradianceLUTPath);
            DataOutputStream irradianceDataOutputStream = new DataOutputStream(irradianceOutputStream);

            for (Float data : irradiance) {
                irradianceDataOutputStream.writeByte(Float.floatToIntBits(data));
            }
            irradianceDataOutputStream.close();

            FileOutputStream intensityOutputStream     = new FileOutputStream(intensityLUTPath);
            DataOutputStream intensityDataOutputStream = new DataOutputStream(intensityOutputStream);

            for (Float data : intensity) {
                intensityDataOutputStream.writeByte(Float.floatToIntBits(data));
            }
            intensityDataOutputStream.close();
        } catch(IOException ioe) { ioe.printStackTrace(); }

        System.out.println("[SUCCESS] Task executed in " + (System.currentTimeMillis() - start) + "ms.");
    }
}
