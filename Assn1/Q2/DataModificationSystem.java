import java.io.*;
import java.util.*;


class Teacher extends Thread {
    private DataModificationSystem dataModificationSystem;  
    private ArrayList<ArrayList<String>> Operations_list; 
    private boolean synctype;    // indicates if the marks need to be updated synchronously or not

    // Constructor
    Teacher(DataModificationSystem dataModificationSystem, String Name, int priority) {
        this.dataModificationSystem = dataModificationSystem;
        setName(Name);
        setPriority(priority);                                              // set priority of the teacher
        Operations_list = new ArrayList<>();
        synctype = false;
    }

    @Override
    public void run() {

        while (Operations_list.size() > 0) {  
        	
                      // run till all the operation being performed
            if (synctype) {
                dataModificationSystem.syncupdate(Operations_list.get(0).get(0), Integer.parseInt(Operations_list.get(0).get(1)), getName());
            } else {
                dataModificationSystem.nosyncupdate(Operations_list.get(0).get(0), Integer.parseInt(Operations_list.get(0).get(1)), getName());
            }
            Operations_list.remove(0);                      // remove the operation performed
        }
    }

    void addInputToThreadBuffer(String roll, String markchange) {
        ArrayList<String> temp = new ArrayList<>();
        temp.add(roll);
        temp.add(markchange);
        Operations_list.add(temp);
    }

    void setsync(boolean sync) {
        synctype = sync;
    }
}

public class DataModificationSystem {
    private final static Scanner scanner = new Scanner(System.in);          // For reading input
    private Map<String, ArrayList<String>> StudDetails;                     //Roll no. -> [name, email, marks, last_updated_by]
    private ArrayList<ArrayList<String>> Operations_list;                   // buffer of operations performed by teachers

    // Constructor
    private DataModificationSystem() {
        StudDetails = new HashMap<>();
        Operations_list = new ArrayList<>();
    }

    private void AddOperations_list() {
        
        String teachertype,roll,markchange ;
        while(true){
            System.out.println("Enter teacher's type (CC or TA1 or TA2) : ");
            teachertype = scanner.next();
            if (teachertype.equals("CC") || teachertype.equals("TA1") || teachertype.equals("TA2")) {
                break;
            } else {
                System.out.println("Invalid teacher name."); 
            }
        }
        System.out.println("Enter Roll Number : ");
        roll = scanner.next();
        System.out.println("Enter amount of marks you want to change (put a minus sign if u want to decrease) : ");
        markchange = scanner.next();

        // Add to the buffer
        ArrayList<String> temp = new ArrayList<>();
        temp.add(teachertype);
        temp.add(roll);
        temp.add(markchange);
        Operations_list.add(temp);
    }


    /*
     *  Read the input buffer and
     *  Update the marks of the students
     * */
    private void UpdateMarks() throws IOException, InterruptedException {
        // ask if updating the files synchronously or asynchronously for the previous inputs.
        System.out.println("Select one:\n" +
                "   1 for Without Synchronization\n" +
                "   2 for With Synchronization");
        int synctype = scanner.nextInt();

        // Create the threads for updating the marks of the students
        
        Teacher ta1 = new Teacher(this,"TA1",Thread.NORM_PRIORITY);
        Teacher ta2 = new Teacher(this,"TA2",Thread.NORM_PRIORITY);
        Teacher cc = new Teacher(this,"CC",Thread.MAX_PRIORITY);

        // Set the mode of updating the marks according to the update
        // synchronized or not
        if (synctype == 1) {                        // without synchronisation
            ta1.setsync(false);
            ta2.setsync(false);
            cc.setsync(false);
        } else if (synctype == 2) {                 // with synchronization
            ta1.setsync(true);
            ta2.setsync(true);
            cc.setsync(true);
        } else {
            System.out.println("Invalid type");
            return;
        }

        // Adding the operations to the respective buffer for  dedicated teacher(threads)
        for (ArrayList<String> temp : Operations_list) {
            String teachertype = temp.get(0);
            String roll = temp.get(1);
            String markchange = temp.get(2);
            if (teachertype.equals("TA1")) {
                ta1.addInputToThreadBuffer(roll, markchange);
            } else if (teachertype.equals("TA2")) {
                ta2.addInputToThreadBuffer(roll, markchange);
            } else if (teachertype.equals("CC")) {
                cc.addInputToThreadBuffer(roll, markchange);
            }
        }
        // Clear the list of operations 
        Operations_list.clear();
        System.out.println("------------------------------------------------------------------------------------------------------");
        // Start the threads
        cc.start();
        ta1.start();
        ta2.start();

        // Wait for the threads to complete
        try {
            cc.join();
            ta1.join();
            ta2.join();
            System.out.println("------------------------------------------------------------------------------------------------------");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Write the final results back to the file
        writeFinalStudDetails();
    }

    void syncupdate(String roll, int markchange, String updatedBy) {
        if (StudDetails.get(roll) != null) {
            /* Using block synchronisation*/
            synchronized (StudDetails.get(roll)) {
                if (StudDetails.get(roll).get(3).equals("CC") && !updatedBy.equals("CC")) {
                	System.out.println("Marks of "+roll+" cannot be updated by "+updatedBy+". Only CC can update his marks.");
                    return;
                }
                int marks = Integer.parseInt(StudDetails.get(roll).get(2).trim());
                int newmarks = marks + markchange;
                StudDetails.get(roll).set(2, String.valueOf(newmarks));
                StudDetails.get(roll).set(3, updatedBy);
                System.out.println("Marks of "+roll+" changed from "+marks+" to "+newmarks+" by "+updatedBy+ " (with synchronization)");
            }
        }else{
        	System.out.println("Roll no. "+roll+" is not present in the student data. ");
        }
    }

    void nosyncupdate(String roll, int markchange, String updatedBy) {
        if (StudDetails.get(roll) != null) {
            if (StudDetails.get(roll).get(3).equals("CC") && !updatedBy.equals("CC")) {
            	System.out.println("Marks of "+roll+" cannot be updated by "+updatedBy+". Only CC can update his marks.");
                return;
            }
            int marks = Integer.parseInt(StudDetails.get(roll).get(2).trim());
            int newmarks = marks + markchange;
            StudDetails.get(roll).set(2, String.valueOf(newmarks));
            StudDetails.get(roll).set(3, updatedBy);
            System.out.println("Marks of "+roll+" changed from "+marks+" to "+newmarks+" by "+updatedBy + " (without synchronization)");
            
        }else{
        	System.out.println("Roll no. "+roll+" is not present in the student data. ");
        }
    }


    /*
     * Read the initial data of the files and store in the memory
     * */
    private void Loaddata() throws IOException, InterruptedException {
        BufferedReader br = new BufferedReader(new FileReader("stud_info.txt"));
        String line;
        while ((line = br.readLine()) != null) {
            // use comma as separator
            String[] temp = line.split(",");
            ArrayList<String> temp1 = new ArrayList<>();
            temp1.add(temp[1]);
            temp1.add(temp[2]);
            temp1.add(temp[3]);
            temp1.add(temp[4]);
            StudDetails.put(temp[0], temp1);

        }
    }

    public static void sortbyname(String arr[][]) 
    { 
        // Using built-in sort function Arrays.sort 
        Arrays.sort(arr, new Comparator<String[]>() { 
            
          @Override              
          // Compare values according to columns 
          public int compare(final String[] entry1,  
                             final String[] entry2) { 
  

            return entry1[1].compareTo(entry2[1]) ;
          } 
        });  // End of function call sort(). 
    } 
    public static void sortbyroll(String arr[][]) 
    { 
        // Using built-in sort function Arrays.sort 
        Arrays.sort(arr, new Comparator<String[]>() { 
            
          @Override              
          // Compare values according to columns 
          public int compare(final String[] entry1,  
                             final String[] entry2) { 
  

            return entry1[0].compareTo(entry2[0]) ;
          } 
        });  // End of function call sort(). 
    } 


    // Write the data from the memory back to the files
    /* First I am storing the orginal order of roll no. in an array and then 
    ** write back in original file using the updated details in Hashpap for StudDetails  */

    private void writeFinalStudDetails() throws IOException, InterruptedException {
       
        BufferedReader br = new BufferedReader(new FileReader("stud_info.txt"));
        String line;
        ArrayList<String> temp1 = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            // use comma as separator
            String[] temp = line.split(",");
            temp1.add(temp[0]);
        }

        /* Creating a nx5 matrix to store the updated details will use it for getting details 
        ** in sorted order  */
        String [][] updata = new String[temp1.size()][5];
        for(int i=0;i<temp1.size();i++){
            updata[i][0]=temp1.get(i);
            for(int j=1;j<5;j++){
                updata[i][j]=StudDetails.get(updata[i][0]).get(j-1);
            }
        }

         /* Write back to the original file. */
        BufferedWriter writer = null, writer1 = null;
        try {
            writer = new BufferedWriter(new FileWriter("stud_info.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert writer != null;
        for (int i=0;i<temp1.size();i++) {
            try {
                writer.append(updata[i][0]);
                for (int j=1;j<5;j++) {
                    writer.append(',');
                    writer.append(updata[i][j]);
                }
                writer.append('\n');
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sortbyroll(updata);                     // sort on tha basis of roll no.


        // writing the content in Sorted_roll.txt
        try {
            writer = new BufferedWriter(new FileWriter("Sorted_roll.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert writer != null;
        for (int i=0;i<temp1.size();i++) {
            try {
                writer.append(updata[i][0]);
                for (int j=1;j<5;j++) {
                    writer.append(',');
                    writer.append(updata[i][j]);
                }
                writer.append('\n');
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sortbyname(updata);                   // sort on tha basis of roll no.

        // writing the content in Sorted_roll.txt
        try {
            writer = new BufferedWriter(new FileWriter("Sorted_name.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert writer != null;
        for (int i=0;i<temp1.size();i++) {
            try {
                writer.append(updata[i][0]);
                for (int j=1;j<5;j++) {
                    writer.append(',');
                    writer.append(updata[i][j]);
                }
                writer.append('\n');
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public static void main(String[] args) throws IOException, InterruptedException {

        DataModificationSystem dataModificationSystem = new DataModificationSystem();

        // Read the current data from the file.
        dataModificationSystem.Loaddata();


        while (true) {
            int input;
            System.out.println("Choose one option\n" +
                    "       1 to Create a set of operations to Update student marks\n" +
                    "       2 to Execute all the operations concurrently \n" +
                    "       0 to Exit the system.");
            input = scanner.nextInt();
            if(input==1){
                dataModificationSystem.AddOperations_list();
            }else if(input==2){
                dataModificationSystem.UpdateMarks();
            }else if(input==0){
                break;
            }else{
                System.out.println("Invalid Option!");
            }

            
        }
    }
}