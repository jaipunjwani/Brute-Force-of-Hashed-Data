import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Description: "Unhash" NYC TLC's 2013 Yellow Taxi data - hack license and medallion (driver ID, and medallion ID, respectively)
 * The program uses a brute force method to generate all possible medallion and hack license numbers, and find their corresponding hash using the MD5 algorithm
 * 
 * NOTE The hashes in the 2013 dataset are capitalized, so we return capitalized hashes in this program
 * 
 * 
 * @author Jai Punjwani
 * @version 1.0
 */
public class UnhashingTaxiLicenses
{
	 static final int numMedallionArrays = 2; // we split medallions into multiple arrays for memory management 
	 
	 // initializing HashMap with capacity avoids unnecessary re-indexing/re-allocation of memory
	 static HashMap<String, String> hashMedallions = new HashMap(Generate_Licenses.MEDALLION_TOTAL);
	 static HashMap<String, String> medallions1 = new HashMap(Generate_Licenses.MEDALLION_TOTAL/numMedallionArrays);
	 static HashMap<String, String> medallions2 = new HashMap(Generate_Licenses.MEDALLION_TOTAL/numMedallionArrays);
	 static HashMap<String, String> sixDigitHackLicenses = new HashMap(Generate_Licenses.HACKLICENSE6SIZE);
	 static HashMap<String, String> sevenDigitHackLicenses = new HashMap(Generate_Licenses.HACKLICENSE7SIZE);
	 
    
     public static void main (String[] args)throws NoSuchAlgorithmException, FileNotFoundException
     {
         setUp(); 
    	 
    	 System.out.println("Deserializing. Allow approximately 3-4 minutes");
         //hashMedallions = (HashMap) deserializeData("medallions.exclude");
         medallions1 = (HashMap) deserializeData("medallions1.exclude");
         medallions2 = (HashMap) deserializeData("medallions2.exclude");
         
    	 sixDigitHackLicenses = (HashMap) deserializeData("hackLicenses6.exclude");
    	 sevenDigitHackLicenses = (HashMap) deserializeData("hackLicenses7.exclude");
    	 
    	 
         //EXAMPLES
         String medallionHash = "B12DB2625EBD5A2EBECBCD25AC089928";
         System.out.println("Hashed medallion " + medallionHash + " mapped to " + getMedallion(medallionHash));
        
         String licenseHash = "0BB6EDE86969525491B06031E8460F41"; 
         System.out.println("Hashed hack license " + licenseHash + " mapped to " + getLicense(licenseHash));
         
         
         String medallion = "2E42";
         System.out.println("Medallion " + medallion + " hashed as " + MD5(medallion));  
         
         
         String hackLicense = "493092";
         System.out.println("Hack license " + hackLicense + " hashed as " + MD5(hackLicense));
         
           
     }
    
     /**
      * Generates table of medallions and licenses with corresponding hash and saves them to large files used to load the data
      * NOTE: the .exclude extension should be added to .gitignore file to prevent large file from being stored to GitHub 
      * CAVEAT: allow 6-10 minutes for set up. If you run out of memory, run each method call below one at a time
      * @throws NoSuchAlgorithmException
      */
     public static boolean setUp() throws NoSuchAlgorithmException
     {
    	 
    	 System.out.println("Setting up. Allow up to 20 minutes");
    	 
    	 if(!(new File("medallions1.exclude").exists() && new File("medallions2.exclude").exists()))
    	 {
    		 setUpMedallions(); 
    	 }
    	 
    	 if(!(new File("hackLicenses6.exclude").exists()))
    	 {
    		 setUp6DigitHackLicenses();
    	 }
    	 
    	 if(!(new File("hackLicenses7.exclude").exists()))
    	 {
    		 setUp7DigitHackLicenses();
    	 }
         
    	 System.out.println("Files created/loaded, setup complete");
    	 return true;
    	 
     }
     
     private static void setUpMedallions() throws NoSuchAlgorithmException
     {
    	 
    	 String[][] allMedallions = Generate_Licenses.generateMedallionAll(numMedallionArrays);
    	 for(int arr =0; arr<allMedallions.length; arr++)
    	 {
    		 if(!new File("medallions" + (arr+1) + ".exclude").exists()) {
	    		 String[] medallions = allMedallions[arr];
	    		 System.out.println("Hashing Medallions part " + (arr + 1));
	             String[] hashes = MD5(medallions);
	             System.out.println("Creating hashmap part " + (arr+1));
	             HashMap<String, String> medallionsHM = generateHashMap(hashes, medallions, medallions.length);
	             System.out.println("serializing part " + (arr +1));
	             serializeData(medallionsHM, "medallions" + (arr+1) + ".exclude");
    		 }
    	 }
     }
     
     private static void setUp6DigitHackLicenses() throws NoSuchAlgorithmException
     {
    	 System.out.println("Generating 6 digit hack licenses");
         String[] hackLicenses6 = Generate_Licenses.generate6DigitHackLicenses();
         System.out.println("Hashing");
         String[] hackLicenseHashes = MD5(hackLicenses6);
         System.out.println("Generating hashmap");
         HashMap<String, String> hackLicenses6HM = generateHashMap(hackLicenseHashes, hackLicenses6, Generate_Licenses.HACKLICENSE6SIZE);
         System.out.println("serializing");
         serializeData(hackLicenses6HM, "hackLicenses6.exclude"); 
     }
     
     private static void setUp7DigitHackLicenses() throws NoSuchAlgorithmException
     {
    	 System.out.println("Generating 7 digit hack licenses");
         String[] hackLicenses7 = Generate_Licenses.generate7DigitHackLicenses();
         System.out.println("Hashing");
         String[] hackLicenseHashes = MD5(hackLicenses7);
         System.out.println("Generating hashmap");
         HashMap<String, String> hackLicenses7HM = generateHashMap(hackLicenseHashes, hackLicenses7, Generate_Licenses.HACKLICENSE7SIZE);
         System.out.println("serializing");
         serializeData(hackLicenses7HM, "hackLicenses7.exclude");
    	 
     }
     
     /**
      * @param hash hashed medallion
      * @return 'unhashed' medallion 
      */
     public static String getMedallion(String hash)
     {
    	 String medallion =  (String) medallions1.get(hash);
    	 if(medallion!= null)
    	 {
    		 return medallion;
    	 }
    	 
    	 return (String) medallions2.get(hash);
    	
     }
     
     /*
      * @param hash hashed hack license
      * @return 'unhashed' hack license
      */
     public static String getLicense(String hash)
     {
    	 String license = (String) sixDigitHackLicenses.get(hash);
    	 if(license != null)
    	 {
    		 return license; 
    	 }
    	 
    	 return (String) sevenDigitHackLicenses.get(hash);
    	 
     }
     
     
     private static HashMap<String, String> generateHashMap(String[] keys, String[] values, int initialCapacity)
     {
    	 int size = keys.length;
    	 HashMap<String, String> hm = new HashMap(initialCapacity);
    	 
    	 for(int i=0; i<size; i++)
    	 {
    		 hm.put(toUpperCase(keys[i]), values[i]);
    	 }
    	 return hm;
    	 
     }
     
     //capitalizes hash to follow convention of data set
     private static String toUpperCase(String hash)
     {
    	 
    	 int size = hash.length();
    	 String newHash = "";
    	 for (int i =0; i<size; i++)
    	 {
    		 char character = hash.charAt(i);
    		 if(isNumber(character))
    		 {
    			 newHash = newHash + character;
    		 }
    		 else
    		 {
    			 String letter = "" + character;
    			 letter = letter.toUpperCase();
    			 newHash = newHash + letter;
    		 }
    		 
    	 }
    	 return newHash;
    	 
     }
     
     
     private static boolean isNumber(char c)
     {
    	 for(int i =0; i<Generate_Licenses.DIGITS.length; i++)
    	 {
    		 if(Generate_Licenses.DIGITS[i] == c)
    		 {
    			 return true;
    		 }
    	 }
    	 return false;
     }
    
     
     /**
      * @return UPPER CASE hashed hexadecimal String using MD5 algorithm 
      */
     public static String MD5(String str)throws NoSuchAlgorithmException
     {
    	 	if (str == null) {
    	 		return "NULL STR";
    	 	}
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            byte[] buffer = str.getBytes();
            
            md.update(buffer);
            byte[] digest = md.digest();
          
            // http://stackoverflow.com/questions/6120657/how-to-generate-a-unique-hash-code-for-string-input-in-android
            // converts byte array to hexadecimal string
            String hexStr = "";
            for (int i = 0; i < digest.length; i++) {
                hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
            }
            
            String hash = hexStr;
          
          return toUpperCase(hash);
         
     }
     
     /**
      * @return String array of UPPER CASE hexadecimal hashes 
      */
     public static String[] MD5(String[] licenses)throws NoSuchAlgorithmException
     {
     
    	 int size = licenses.length;
    	 String[] hashes = new String[size];
         int hashIndex =0;
         
         
         for(int j=0; j<licenses.length; j++)
         {
            hashes[hashIndex] = MD5(licenses[j]);
            hashIndex++;
          }
          
          return hashes;
       
         
     }
     
     /**
      * @param object object to serialize
      * @param filepath path to store serialized object
      */
      public static void serializeData(Object object, String filepath)
      { 
    	  try
          {
             FileOutputStream fileOut = new FileOutputStream(filepath);
             ObjectOutputStream out = new ObjectOutputStream(fileOut);
             out.writeObject(object);
             out.close();
             fileOut.close();
             System.out.println("Serialized data is saved as " + filepath);
          }
    	  catch(IOException i)
          {
              i.printStackTrace();
          }
    	  
    	  
      }
      
      /**
       * @param filepath path of file to deserialize
       * @return deserialized object 
       */
      public static Object deserializeData(String filepath)
      {
    	  Object input = null;
    	  try
          {
             FileInputStream fileIn = new FileInputStream(filepath);
             ObjectInputStream in = new ObjectInputStream(fileIn);
             input = in.readObject();
             in.close();
             fileIn.close();
          }
    	  
    	  catch(IOException i)
          {
             i.printStackTrace();
             return null;
          }
    	  
    	  catch(ClassNotFoundException c)
          {
             System.out.println("Class not found");
             c.printStackTrace();
             return null;
          }
    	  
    	  return input;
    	  
      }
    		  
}
