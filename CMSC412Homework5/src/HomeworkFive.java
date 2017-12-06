import java.io.*;
import java.util.*;
import java.io.FileInputStream;

/**
 * Project: CMSC412Homework5
 * Created by David on 11/16/2017.
 * Objective: To create a simple OS style program that will allow the user to perform multiple actions within a directory.
 */
public class HomeworkFive {

    public static void main(String[] args) throws Exception {
        BufferedReader object = new BufferedReader(new InputStreamReader(System.in));
        String directory = "";
        Boolean isDirectory = false;

        try {
            System.out.println("Please select from the below options: ");
            while (true) {

                System.out.println("0 - Exit \n"
                        + "1 - Select directory \n"
                        + "2 - List directory content (first level)\n"
                        + "3 - List directory content (all levels) \n"
                        + "4 - Delete file \n"
                        + "5 - Display file \n"
                        + "6 - Encrypt file (XOR with password)\n"
                        + "7 - Decrypt file (XOR with password)");
                System.out.println("\nChoose next option: ");
                int opt = Integer.parseInt(object.readLine());
                switch (opt) {
                    case 0:
                        System.exit(0);
                        break;
                    case 1:
                        System.out.println("Enter a directory (Absolute Name)");
                        Scanner readInput = new Scanner(System.in);
                        directory = readInput.nextLine();

                        isDirectory = confirmIfDirectory(directory);
                        continue;
                    //Case to display files in root
                    case 2:
                        if (isDirectory) {
                            listFilesFirstLevel(directory);
                        } else
                            System.out.println("Please go back to step 1 and enter a valid directory.\n");//
                        continue;
                    //Case to show all files
                    case 3:
                        if (isDirectory) {
                            System.out.println("Files in this directory are:");
                            listFilesAllLevels(directory, false);
                            System.out.println();
                        } else
                            System.out.println("Please go back to step 1 and enter a valid directory.\n");//
                        continue;
                    //Case to Delete file
                    case 4:
                        if (isDirectory) {
                            System.out.println("Enter the name of the file to delete:");
                            String fileDelete = new Scanner(System.in).nextLine();
                            deleteFile(directory + "\\" + fileDelete);

                            System.out.println();
                        } else
                            System.out.println("Please go back to step 1 and enter a valid directory.\n");//
                        continue;
                    //Case to display Hex
                    case 5:
                        if (isDirectory) {
                            System.out.println("Enter the name of the file to display hex:");
                            String hexFile = new Scanner(System.in).nextLine();
                            displayHex(directory + "\\" + hexFile);

                            System.out.println("\n");
                        } else
                            System.out.println("Please go back to step 1 and enter a valid directory.\n");//
                        continue;

                    // Case to Encrypt the file
                    case 6:
                        if(isDirectory) {
                            processEncryptDecrypt(directory, "encrypt");
                        }else
                            System.out.println("Please go back to step 1 and enter a valid directory.\n");

                        continue;

                    // Case to Decrypt the file
                    case 7:
                        if(isDirectory) {
                            processEncryptDecrypt(directory, "decrypt");
                        }else
                            System.out.println("Please go back to step 1 and enter a valid directory.\n");

                        continue;

                }
            }
        } catch (IOException e){

        }
    }
    //region Methods
    //Confirms if the entered path is a directory.
    public static boolean confirmIfDirectory(String directoryName) {
        Boolean isDirectory = false;

        File EdFile = new File(directoryName);
        if (EdFile.isDirectory()) {
            isDirectory = true;
            System.out.println(directoryName + " is a valid directory.\n");
        } else {
            System.out.println("Please enter a valid directory to continue. \n");
        }

        return isDirectory;
    }
    //List all files and sub directory names in the root
    public static void listFilesFirstLevel(String directoryName) {
        File directory = new File(directoryName);
        File[] fList = directory.listFiles();
        System.out.println("Files in this directory are:");
        for (File file : fList) {
            if (file.isFile())
                System.out.println("File: " + file.getName());
            else if (file.isDirectory())
                System.out.println("Directory: " + file.getName());
        }
        System.out.println();
    }
    // Method to get the list of all files, and all files in sub directories
    public static void listFilesAllLevels(String directoryName, Boolean SubDirectory) {
        File directory = new File(directoryName);
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                if (SubDirectory)
                    System.out.println("Sub Directory File: " + file.getParentFile().getName() + "/" + file.getName());
                else
                    System.out.println("File: " + file.getName());
            } else if (file.isDirectory()) {
                System.out.println("Directory: " + file.getName());
                listFilesAllLevels(file.getAbsolutePath(), true);
            }
        }
    }
    //Deletes requested file if it exists
    public static void deleteFile(String directoryName) {
        File deleteFile = new File(directoryName);
        if (deleteFile.exists()) {
            deleteFile.delete();
            System.out.println(deleteFile.getName() + " successfully deleted.");
        } else {
            System.out.println("File does not exist.");
        }
    }
    //Displays the requested file in HEX if it exits
    public static void displayHex(String directoryName){
        try {
            if(new File(directoryName).exists()) {
                FileInputStream fis = new FileInputStream(directoryName);
                int i = 0;
                int count = 0;
                int memory = 0;
                StringBuilder sb = new StringBuilder();
                while ((i = fis.read()) != -1) {
                    sb.append(String.format("%02X ", i));
                    count++;
                    if (count == 16) {
                        System.out.printf("%010X", memory);
                        memory += count;
                        System.out.print("\t");
                        System.out.print(sb.toString());
                        System.out.println("");
                        sb.setLength(0);
                        sb.trimToSize();
                        count = 0;
                    }
                }
                //This is to handle any remaining items when at the end of the file.
                if (sb.length() > 0) {
                    Integer memoryAddress = memory + 16;
                    System.out.printf("%010X", memoryAddress);
                    System.out.print("\t");
                    System.out.print(sb.toString());
                }

                fis.close();
            } else{
                System.out.println("Entered file does not exist in this directory.");
            }
        }catch(Exception e){

        }
    }
    //Encrypts and decrypts the file, since XOR can use same algorithm
    public static void encryptDecrypt(String password, String inputFile, String outputFile, String directory){

        try {
            byte[] pwdBytes = password.getBytes();

            String inFile = directory + "\\" + inputFile;
            String outFile = directory + "\\" + outputFile;

            FileInputStream inStream = new FileInputStream(inFile);
            FileOutputStream outStream = new FileOutputStream(outFile);

            int inByte;
            int pwdIndex = 0;
            while ((inByte = inStream.read()) != -1) {
                outStream.write((byte) (inByte ^ pwdBytes[pwdIndex++ % pwdBytes.length]));
            }
            outStream.close();
            inStream.close();

            System.out.println("File processed successfully. ");
        }
        catch(Exception e){

        }
    }
    //Handles the error checking for the Input / output, and if all true, will call encryptDecrypt method
    public static void processEncryptDecrypt(String directory, String encryptOrDecrypt){

            System.out.println("Please enter the input file name.");
            String inFile = new Scanner(System.in).nextLine();
            if (!checkIfFileExists(directory, inFile, ""))
                return;

            System.out.println("Please enter the output file name.");
            String outFile = new Scanner(System.in).nextLine();
            if (!checkIfFileExists(directory, outFile, inFile))
                return;

            System.out.println("Please enter the password to "+ encryptOrDecrypt +" the file.");
            String key = new Scanner(System.in).nextLine();

            encryptDecrypt(key, inFile, outFile, directory);

    }
    //Checks to see if file exists for encryption, and handles error message
    public static boolean checkIfFileExists(String directory, String file, String previousFile){
        if(file.equals(previousFile)){
            System.out.println("The output file can not be the same as the input file.\n");
            return false;
        }
        file = directory + "\\" + file;
        if (new File(file).exists() == false) {
            System.out.println("Entered file does not exist in current directory.\n");
            return false;
        }
        return true;
    }
    //endregion
}
