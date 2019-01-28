package main.util;

import main.analyzer.backward.UnitContainer;
import main.util.manifest.ProcessManifest;
import org.apache.commons.lang3.StringUtils;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;
import soot.Scene;
import soot.SootClass;
import soot.Unit;
import soot.ValueBox;
import soot.options.Options;
import soot.util.Chain;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * <p>Utils class.</p>
 *
 * @author RigorityJTeam
 * @since V01.00.00
 */
public class Utils {
    /**
     * //TODO Possible work usages
     * {@link main.util.Utils#getClassNamesFromJarArchive}
     * {@link main.util.Utils#retrieveFullyQualifiedName}
     * - Enhance this to look for package declarations not at the top of the file
     * License - TLDR
     * package org.main.hello;
     * {@link main.util.Utils#getClassNamesFromJarArchive}
     * {@link main.util.Utils#getClassNamesFromJarArchive}
     */

    private static String fileSep = System.getProperty("file.separator");

    /**
     * <p>getClassNamesFromJarArchive.</p>
     *
     * @param jarPath a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     * @throws java.io.IOException if any.
     */
    public static List<String> getClassNamesFromJarArchive(String jarPath) throws IOException {
        List<String> classNames = new ArrayList<>();
        ZipInputStream zip = new ZipInputStream(new FileInputStream(jarPath));
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                String className = entry.getName().replace('/', '.');
                classNames.add(className.substring(0, className.length() - ".class".length()));
            }
        }
        return classNames;
    }

    /**
     * <p>getBasePackageNameFromApk.</p>
     *
     * @param apkPath a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     */
    public static String getBasePackageNameFromApk(String apkPath) throws IOException {
        ProcessManifest processManifest = new ProcessManifest();
        processManifest.loadManifestFile(apkPath);
        return processManifest.getPackageName();
    }

    /**
     * <p>getBasePackageNameFromJar.</p>
     *
     * @param jarPath a {@link java.lang.String} object.
     * @param isMain  a boolean.
     * @return a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     */
    public static String getBasePackageNameFromJar(String jarPath, boolean isMain) throws IOException {

        ZipInputStream zip = new ZipInputStream(new FileInputStream(jarPath));

        List<String> basePackages = new ArrayList<>();

        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                String className = entry.getName().replace('/', '.');
                className = className.substring(0, className.length() - ".class".length());

                String[] splits = className.split("\\.");
                StringBuilder basePackage = new StringBuilder();

                if (splits.length > 3) { // assumption package structure is org.apache.xyz.main
                    basePackage.append(splits[0])
                            .append(".")
                            .append(splits[1])
                            .append(".")
                            .append(splits[2]);
                } else if (splits.length == 3) {
                    basePackage.append(splits[0])
                            .append(".")
                            .append(splits[1]);
                } else {
                    basePackage.append(splits[0]);
                }

                String basePackageStr = basePackage.toString();

                if (!basePackages.toString().contains(basePackageStr)) {
                    basePackages.add(basePackageStr);
                }
            }
        }

        if (basePackages.size() == 1) {
            return basePackages.get(0);
        } else if (basePackages.size() > 1) {

//            if (isMain) {
//                System.out.println("***Multiple Base packages of " + jarPath + " : " + basePackages.toString());
//            }

            for (String basePackage : basePackages) {
                if (basePackage.split("\\.").length > 2 && jarPath.contains(basePackage.split("\\.")[2])) {
                    return basePackage;
                }
            }
        }

        return null;

    }

    /**
     * <p>getClassNamesFromApkArchive.</p>
     *
     * @param apkfile a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     * @throws java.io.IOException if any.
     */
    public static List<String> getClassNamesFromApkArchive(String apkfile) throws IOException {
        List<String> classNames = new ArrayList<>();

        File zipFile = new File(apkfile);

        DexFile dexFile = DexFileFactory.loadDexEntry(zipFile, "classes.dex", true, Opcodes.forApi(23));

        for (ClassDef classDef : dexFile.getClasses()) {
            String className = classDef.getType().replace('/', '.');
            if (!className.contains("android.")) {
                classNames.add(className.substring(1, className.length() - 1));
            }
        }

        return classNames;
    }

    /**
     * <p>buildSootClassPath.</p>
     *
     * @param paths a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String buildSootClassPath(String... paths) {
        return buildSootClassPath(Arrays.asList(paths));
    }

    /**
     * <p>buildSootClassPath.</p>
     *
     * @param paths a {@link java.util.List} object.
     * @return a {@link java.lang.String} object.
     */
    public static String buildSootClassPath(List<String> paths) {

        StringBuilder classPath = new StringBuilder();

        for (String path : paths) {

            if (path.endsWith(".jar")) {
                classPath.append(path);
                classPath.append(":");
            } else {
                File dir = new File(path);

                if (dir.isDirectory()) {
                    File[] files = dir.listFiles();

                    if (files == null) {
                        continue;
                    }

                    for (File file : files) {
                        if (file.getName().endsWith(".jar")) {
                            classPath.append(file.getAbsolutePath());
                            classPath.append(":");
                        }
                    }
                }
            }
        }

        return classPath.toString();
    }

    /**
     * <p>getJarsInDirectory.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    public static List<String> getJarsInDirectory(String path) {

        List<String> jarFiles = new ArrayList<>();
        File dir = new File(path);

        if (dir.isDirectory()) {
            File[] files = dir.listFiles();

            if (files == null) {
                return jarFiles;
            }

            for (File file : files) {
                if (file.getName().endsWith(".jar")) {
                    jarFiles.add(file.getAbsolutePath());
                }
            }
        }

        return jarFiles;
    }

    /**
     * <p>getClassHierarchyAnalysis.</p>
     *
     * @param classNames a {@link java.util.List} object.
     * @return a {@link java.util.Map} object.
     */
    public static Map<String, List<SootClass>> getClassHierarchyAnalysis(List<String> classNames) {

        Map<String, List<SootClass>> classHierarchyMap = new HashMap<>();

        for (String className : classNames) {

            SootClass sClass = Scene.v().getSootClass(className);
            Chain<SootClass> parents = sClass.getInterfaces();

            if (sClass.hasSuperclass()) {
                SootClass superClass = sClass.getSuperclass();

                List<SootClass> childList = classHierarchyMap.get(superClass.getName());

                if (childList == null) {
                    childList = new ArrayList<>();
                    classHierarchyMap.put(superClass.getName(), childList);
                }

                if (childList.isEmpty()) {
                    childList.add(superClass);
                }
                childList.add(sClass);
            }

            for (SootClass parent : parents) {
                List<SootClass> childList = classHierarchyMap.get(parent.getName());

                if (childList == null) {
                    childList = new ArrayList<>();
                    classHierarchyMap.put(parent.getName(), childList);
                }

                if (childList.isEmpty()) {
                    childList.add(parent);
                }
                childList.add(sClass);
            }
        }

        return classHierarchyMap;
    }

    /**
     * <p>getXmlFiles.</p>
     *
     * @param projectJarPath a {@link java.lang.String} object.
     * @param excludes       a {@link java.util.List} object.
     * @return a {@link java.util.Map} object.
     * @throws java.io.IOException if any.
     */
    public static Map<String, String> getXmlFiles(String projectJarPath, List<String> excludes) throws IOException {
        Map<String, String> fileStrs = new HashMap<>();

        if (new File(projectJarPath).isDirectory()) {
            return fileStrs;
        }

        List<String> fileNames = getXmlFileNamesFromJarArchive(projectJarPath, excludes);

        for (String fileName : fileNames) {
            InputStream stream = readFileFromZip(projectJarPath, fileName);
            fileStrs.put(fileName, convertStreamToString(stream));
        }

        return fileStrs;
    }

    private static List<String> getXmlFileNamesFromJarArchive(String jarPath, List<String> excludes) throws IOException {
        List<String> classNames = new ArrayList<>();
        ZipInputStream zip = new ZipInputStream(new FileInputStream(jarPath));
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            for (String exclude : excludes) {
                if (!entry.isDirectory() && entry.getName().endsWith(".xml") && !entry.getName().endsWith(exclude)) {
                    String className = entry.getName();
                    classNames.add(className);
                }
            }
        }
        return classNames;
    }

    private static InputStream readFileFromZip(String jarPath, String file) throws IOException {
        ZipFile zipFile = new ZipFile(jarPath);
        ZipEntry entry = zipFile.getEntry(file);
        return zipFile.getInputStream(entry);
    }

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * <p>findInfluencingParamters.</p>
     *
     * @param analysisResult a {@link java.util.List} object.
     * @return a {@link java.util.List} object.
     */
    public static List<Integer> findInfluencingParamters(List<UnitContainer> analysisResult) {
        List<Integer> influencingParam = new ArrayList<>();

        for (int index = analysisResult.size() - 1; index >= 0; index--) {
            UnitContainer unit = analysisResult.get(index);

            for (ValueBox useBox : unit.getUnit().getUseBoxes()) {
                String useboxStr = useBox.getValue().toString();
                if (useboxStr.contains("@parameter")) {
                    Integer param = Integer.valueOf(useboxStr.substring("@parameter".length(), useboxStr.indexOf(':')));
                    influencingParam.add(param);
                }
            }
        }

        return influencingParam;
    }

    /**
     * <p>isSpecialInvokeOn.</p>
     *
     * @param currInstruction a {@link soot.Unit} object.
     * @param usebox          a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean isSpecialInvokeOn(Unit currInstruction, String usebox) {
        return currInstruction.toString().contains("specialinvoke")
                && currInstruction.toString().contains(usebox + ".<");
    }

    /**
     * <p>listf.</p>
     *
     * @param directoryName a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    public static List<File> listf(String directoryName) {
        File directory = new File(directoryName);

        List<File> resultList = new ArrayList<File>();

        File[] fList = directory.listFiles();
        resultList.addAll(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile()) {
            } else if (file.isDirectory()) {
                resultList.addAll(listf(file.getAbsolutePath()));
            }
        }

        return resultList;
    }

    /**
     * <p>getClassNamesFromSnippet.</p>
     *
     * @param sourcePaths a {@link java.util.List} object.
     * @return a {@link java.util.List} object.
     */
    public static List<String> getClassNamesFromSnippet(List<String> sourcePaths) {

        List<String> classNames = new ArrayList<>();

        for (String sourcePath : sourcePaths) {

            List<File> files = listf(sourcePath);

            if (files == null) {
                return classNames;
            }

            for (File file : files) {
                String name = file.getAbsolutePath();
                if (name.endsWith(".java")) {
                    String className = name.substring(sourcePath.length() + 1, name.length() - 5);
                    classNames.add(className.replaceAll("/", "."));
                }
            }
        }

        return classNames;
    }

    public static List<String> retrieveFullyQualifiedName(List<String> sourceJavaFile) {
        List<String> fullPath = new ArrayList<>();
        for (String in : sourceJavaFile)
            fullPath.add(Utils.retrieveFullyQualifiedName(in));

        return fullPath;
    }


    public static String retrieveFullyQualifiedName(String in) {

        String sourcePackage = trimFilePath(in);
        if (in.endsWith(".java")) {
            sourcePackage = sourcePackage.replace(".java", "");
            try (BufferedReader br = new BufferedReader(new FileReader(in))) {
                String firstLine = br.readLine();

                if (firstLine.startsWith("package ") && firstLine.endsWith(";")) {
                    sourcePackage = firstLine.substring("package ".length(), firstLine.length() - 1) + "." + sourcePackage;
                } else //File has no package declaration, retrieving the last folder path
                {
                    String[] paths = Utils.retrieveFullFilePath(in).split(fileSep);

                    sourcePackage = paths[paths.length - 2] + "." + sourcePackage;
                }

            } catch (IOException e) {
                System.out.println("Issue Reading File: " + in);
            }
        } else if (in.endsWith(".class")) {
            sourcePackage = sourcePackage.replace(".class", "");

            String[] paths = Utils.retrieveFullFilePath(in).split(fileSep);

            //sourcePackage = paths[paths.length - 2] + "." + sourcePackage;
        }
        return sourcePackage;
    }

    public static List<String> retrieveTrimmedSourcePaths(List<String> files) {
        List<String> filePaths = new ArrayList<>();
        for (String relativeFile : files) {
            String relativeFilePath = "";

            File file = new File(relativeFile);

            try {
                relativeFilePath = file.getCanonicalPath().replace(file.getName(), "");
            } catch (IOException e) {

            }

            if (!filePaths.contains(relativeFilePath))
                filePaths.add(relativeFilePath);
        }
        return filePaths;
    }

    public static String retrieveBaseSourcePath(List<String> sourcePaths, String dependencyPath) {
        String tempDependencyPath = sourcePaths.get(0);
        for (String in : sourcePaths)
            if (!in.equals(tempDependencyPath)) {
                tempDependencyPath = System.getProperty("user.dir");
                break;
            }
        return tempDependencyPath + System.getProperty("file.separator") + dependencyPath;
    }

    public static String retrieveFullFilePath(String filename) {
        File file = new File(filename);
        if (file.exists())
            try {
                return file.getCanonicalPath();
            } catch (IOException e) {
                return filename;
            }
        else
            return filename;
    }

    /**
     * This method trims the file path and package from the absolute path.
     * <p>EX: src/main/java/com/test/me/main.java {@literal -}{@literal >} main.java</p>
     *
     * @param fullFilePath {@link String} - The full file path
     * @return {@link String} - The file name with the extension attached
     */
    public static String trimFilePath(String fullFilePath) {
        String[] folderSplit = fullFilePath.split(Pattern.quote(System.getProperty("file.separator")));
        return folderSplit[folderSplit.length - 1];
    }

    public static String osPathJoin(String... elements) {
        return Utils.join(Utils.fileSep, elements);
    }

    public static String join(String delimiter, String... elements) {
        return join(delimiter, Arrays.asList(elements));
    }

    public static String join(String delimiter, List<String> elements) {
        StringBuilder tempString = new StringBuilder();
        for (String in : elements) {
            tempString.append(in);
            if (!in.equals(elements.get(elements.size() - 1)))
                tempString.append(delimiter);
        }

        return tempString.toString();
    }

    public static String getJAVA_HOME() {
        String JAVA_HOME = System.getenv("JAVA_HOME");
        if (StringUtils.isEmpty(JAVA_HOME)) {
            System.out.println("Please Set JAVA_HOME");
            System.exit(1);
        }
        return JAVA_HOME;
    }

    public static String getJAVA7_HOME() {
        String JAVA7_HOME = System.getenv("JAVA7_HOME");
        if (StringUtils.isEmpty(JAVA7_HOME)) {
            System.out.println("Please Set JAVA7_HOME");
            System.exit(1);
        }
        return JAVA7_HOME;
    }

    public static String getANDROID() {
        String ANDROID_HOME = System.getenv("ANDROID_HOME");
        if (StringUtils.isEmpty(ANDROID_HOME)) {
            System.out.println("Please Set ANDROID_HOME");
            System.exit(1);
        }
        return ANDROID_HOME;
    }

    public static String getBaseSOOT() {
        String rt = Utils.join(Utils.fileSep, "jre", "lib", "rt.jar:");
        String jce = Utils.join(Utils.fileSep, "jre", "lib", "jce.jar");

        return Utils.getJAVA_HOME() + Utils.fileSep + Utils.join(Utils.getJAVA_HOME() + Utils.fileSep, rt, jce);
    }

    public static String getBaseSOOT7() {
        String rt = Utils.join(Utils.fileSep, "jre", "lib", "rt.jar:");
        String jce = Utils.join(Utils.fileSep, "jre", "lib", "jce.jar");

        return Utils.getJAVA7_HOME() + Utils.fileSep + Utils.join(Utils.getJAVA7_HOME() + Utils.fileSep, rt, jce);
    }

    public static void loadSootClasses(List<String> classes) {
        Options.v().set_keep_line_number(true);
        Options.v().set_allow_phantom_refs(true);

        if (classes != null)
            for (String clazz : classes)
                Options.v().classes().add(clazz);

        Scene.v().loadBasicClasses();
    }


}
