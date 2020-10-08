import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

class ShelfManager {

    private final int countp[];
    private static final String[] Colors={"White","Black","Blue","Grey"};

    // Constructor for the shelfManager. Create an shelf with four compartments.
    ShelfManager() {
        countp = new int[4];
        for (int i=0;i<countp.length;i++) 
            countp[i]=0;
    }

    // Function for adding a sockpair.
    synchronized void Pairit(int sock) {
        countp[sock-1]++;
        System.out.println(" A pair of "+Colors[sock-1]+" socks added to the shelf");
    
    }

    // Print the collected socks finally when the program ends.
    void Printfinalcount() {
        System.out.println("------------------------------------------Printing total pairs of socks in the shelf-----------------------------------------------");
        System.out.println(String.format("WhitePairs: %d\tBlackPairs: %d\tBluePairs: %d\tGreyPairs: %d", countp[0], countp[1], countp[2], countp[3]));
    }
}

class SockMatcher {
    public static  ShelfManager shelfManager;
    public static int counts[];
    private static final String[] Colors={"White","Black","Blue","Grey"};
    
    // Constructor for sock matching machine. Machine will have a buffer capacity for storing a sock of each color.
    SockMatcher(ShelfManager shelfManager) {
        counts = new int[4];
        for (int i=0;i<counts.length;i++) 
            counts[i]=0;
        this.shelfManager = shelfManager;
    }

    // Function for matching a sock pair in the machine
    public static synchronized void matchit(int sock) {
			if(counts[sock-1]==1){
                System.out.println(" A pair of "+Colors[sock-1]+" socks found in machine. Tansferred to the shelf manager");
				shelfManager.Pairit(sock);
				counts[sock-1]=0;
                
			}else{
				counts[sock-1]+=1;
			}
		
    }
}

class Roboarm extends Thread {
    private Sockmatchingsystem sockmatchingsystem;                              // To acces the buffer of socks and pick one sock
    private SockMatcher sockMatcher;                                            // To pass the picked sock the sock matcher
    private static final String[] Colors={"White","Black","Blue","Grey"};       // An array of colors

    // Constructor for the roboarm
    Roboarm(Sockmatchingsystem sockmatchingsystem, SockMatcher sockMatcher, int name) {
        super();
        this.sockmatchingsystem = sockmatchingsystem;
        this.sockMatcher = sockMatcher;
        setName(String.valueOf(name));
    }

    @Override
    public void run() {
        while (true) {
        	
            int sockReceived = sockmatchingsystem.PickSock();
            if (sockReceived == -1) {  
                System.out.println("Thread "+ getName()+ " Stopped!");
                stop();
            }
            System.out.println(" Sock of color " + Colors[sockReceived-1] + " recieved by Roboarm " + getName());
            sockMatcher.matchit(sockReceived);
        }
    }
}
public class Sockmatchingsystem {
    private int Roboarmcount;               // Number of robot arms
    private List<Roboarm> Roboarms;         // List of Robot Threads
    private final List<Integer> Socks;      // Socks buffer
    private List<Semaphore> Locks;          // Semaphore locks for the socks
    private SockMatcher sockMatcher;        // Sock matcher
    private ShelfManager shelfManager;      // Shelf manager
    private Random randnum = new Random();  // random generator

  
    // Constructor for the whole system
    private Sockmatchingsystem(int n, List<Integer> socks) {
        Roboarmcount = n;
        Socks = socks;

        // create roboarms for the given count
	    Roboarms = new ArrayList<>();
        for (int i = 0; i < Roboarmcount; i++) {
            Roboarm roboarm = new Roboarm(this, this.sockMatcher, i);
            Roboarms.add(roboarm);
        }

        // Create semaphore lock for each sock
        Locks = new ArrayList<>();
        for (int i = 0; i < Socks.size(); i++) {
            Semaphore Lock = new Semaphore(1);
            Locks.add(Lock);
        }

        shelfManager = new ShelfManager();
        sockMatcher = new SockMatcher(shelfManager);

    }

    
    //  Start all the roboarms and wait for each roboarm to terminate. Also print the final status of shelf
    
    private void startsockmatchsystem() throws InterruptedException {

        System.out.println("--------------------------------------------Machine is starting-----------------------------------------------");

        for (Roboarm roboarm : Roboarms) {       // Activate all the robot arms
            roboarm.start();
        }
        for (Roboarm roboarm : Roboarms) {       // wait for all robotarms to stop
            roboarm.join();
        }
        shelfManager.Printfinalcount();         // Print the collected socks count
    }

    
    int PickSock() {
        int sock;
        int n;

        // Generate a random number and lock that sock. If no sock found then return -1.
        synchronized (Socks) {
            if (Socks.size() > 0) {
                n = randnum.nextInt(Socks.size());
		
            } else {
                return -1;
            }
        }

        boolean flag = Locks.get(n).tryAcquire();

        // Lock the sock and return the locked object
        // Release the lock so that it can be acquired by some other thread
        if (flag ) {
            synchronized (Socks) {
                sock = Socks.get(n);
                Socks.remove(n);
            }
            Locks.get(n).release();
            return sock;
        } else {
            return PickSock();
        }
    }

    // The main function to run the Sock matching machine
     
    public static void main(String[] args) throws IOException, InterruptedException {
        
        File file = new File("input.txt");              // File to take input from
        Scanner scanner = new Scanner(file);

        
        int n = scanner.nextInt();                      // Take the number of robots as input
        
        List<Integer> sockheap = new ArrayList<>();     // Create the heap of socks as an array
        while (scanner.hasNextInt()) {  
            sockheap.add(scanner.nextInt());
        }

        // Create a sock matching machine
        Sockmatchingsystem sms = new Sockmatchingsystem(n, sockheap);

        // Start the Sockmatching
        sms.startsockmatchsystem();
    }


}
