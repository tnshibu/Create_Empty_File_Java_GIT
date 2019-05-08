import java.io.*;
import java.util.*;

public class CreateEmptyFile {
  public static String OUTPUT_BASE_FOLDER="D:\\Empty_Files\\";
  public static String ERROR_FILE="error.log.txt";
  public static String currentDriveLetter = "";
  public static String currentCommonFolder = "";
  public static List<String> NEW_FILE_LIST = null;
  public static List<String> EXISTING_FILE_LIST = null;
  //--------------------------------------------------------------------------------------------------------------------------------
  public static void main(String[] args) throws Exception {
      List<String> inputFilesList = new ArrayList<String>();
      if(args == null || args.length == 0) {
          System.out.println("input file not specified on the command line");
          inputFilesList = FileUtil.getFileList("input");
      } else {
          inputFilesList.add(args[0]);
      }
      FileOutputStream errFOS = new FileOutputStream(new File(ERROR_FILE));
      for(int x=0;x<inputFilesList.size();x++) {
          currentCommonFolder = "";
          List<String> fileList = readFileListFromFile(inputFilesList.get(x));
          //readDiffFileList(inputFilesList.get(x));
          
          //Create the new files
          for(int i=0;i<fileList.size();i++) {
              String fileName = fileList.get(i);
              //System.out.println("fileName=    " + fileName );
              try {
                //System.out.println("-------------------------------------------------");
                String fileName = fileName.replaceAll("[^\\p{L}\\p{Z}]","");
                File newFile = new File(fileName);
                File parent = newFile.getParentFile();
                parent.mkdirs();
                newFile.createNewFile();
              } catch(Exception e) {
                  e.printStackTrace();
                  errFOS.write(("Unable to create : "+fileName+"\r\n").getBytes());
              }
          }

/*          
          //Delete non-existent files
          for(int i=0;i<EXISTING_FILE_LIST.size();i++) {
              String fileName = EXISTING_FILE_LIST.get(i);
              //System.out.println("fileName=    " + fileName );
              try {
                //System.out.println("-------------------------------------------------");
                File newFile = new File(fileName);
                newFile.delete();
              } catch(Exception e) {
                  e.printStackTrace();
                  errFOS.write(("Unable to delete : "+fileName+"\r\n").getBytes());
              }
          }
*/
      }
      errFOS.close();
  }
  //--------------------------------------------------------------------------------------------------------------------------------
  public static List<String> readDiffFileList(String fileName) throws Exception {
    NEW_FILE_LIST = readFileListFromFile(fileName);
    EXISTING_FILE_LIST = FileUtil.getFileList(currentCommonFolder); //this filename already contains filesize...
    
    //remove existing files from new List...
    for(int i=0;i<EXISTING_FILE_LIST.size();i++) {
      String existingFileName = EXISTING_FILE_LIST.get(i);
      if(NEW_FILE_LIST.contains(existingFileName)) {
          //System.out.println("this has to be removed:=    " + existingFileName );
          NEW_FILE_LIST.remove(existingFileName);
          EXISTING_FILE_LIST.remove(i);
          i--;
      }
    }
    
    // by now, NEW_FILE_LIST contains actual new files
    // and EXISTING_FILE_LIST contains filenames which no longer should exist

    //print values now
    /*
    for(int i=0;i<NEW_FILE_LIST.size();i++) {
      String fileName = NEW_FILE_LIST.get(i);
      System.out.println("new :=    " + fileName );
    }
    for(int i=0;i<EXISTING_FILE_LIST.size();i++) {
      String fileName = EXISTING_FILE_LIST.get(i);
      System.out.println("del :=    " + fileName );
    }
    */
    
    return NEW_FILE_LIST;
  }
  //--------------------------------------------------------------------------------------------------------------------------------
  public static List<String> readFileListFromFile(String fileName1) throws Exception {
    ArrayList<String> returnArray = new ArrayList<String>();
    BufferedReader input =  new BufferedReader(new FileReader(new File(fileName1)));
    try {
        String line = null;
        String firstLine = input.readLine();
        String driveName=firstLine.substring(11);
        System.out.println("driveName ="+driveName);
        while (( line = input.readLine()) != null){
          currentDriveLetter = line.substring(0,1);
          line = line.substring(3); //remove the drive letter
          line = OUTPUT_BASE_FOLDER + driveName +"\\" + line; 
          
          
            String[] splitArray = line.split("\\|");
            String fileName = splitArray[0];
            String fileSize = splitArray[1];
            //System.out.println(fileName );
            //System.out.println("fileSize=    " + fileSize );

            int dot = fileName.lastIndexOf('.');
            String base = (dot == -1) ? fileName : fileName.substring(0, dot);
            String extension = (dot == -1) ? "" : fileName.substring(dot+1);

            String newFileName = base+".("+fileSize+")."+extension;
            //System.out.println("newFileName=    " + newFileName );
          
          //System.out.println("line=    " + line );
          currentCommonFolder = getCommonFolder(newFileName);
          //System.out.println("currentCommonFolder=" + currentCommonFolder );
          returnArray.add(newFileName);
        }
      }
      finally {
        input.close();
      }
    return returnArray;
  }
  //--------------------------------------------------------------------------------------------------------------------------------
  public static String getCommonFolder(String inFileName) throws Exception {
      if(currentCommonFolder.equals("")) {
          File file = new File(inFileName);
          currentCommonFolder = file.getParent();
      }
      if(inFileName.indexOf(currentCommonFolder) > -1) {
          return currentCommonFolder;
      }
      //if no match, keep reducing folders until match is found....
      while(true) {
          currentCommonFolder = (new File(currentCommonFolder)).getParent();
          if(inFileName.indexOf(currentCommonFolder) > -1) {
              return currentCommonFolder;
          }
      }
      //return currentCommonFolder;
    
  }
  //--------------------------------------------------------------------------------------------------------------------------------
}
