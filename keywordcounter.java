import java.util.HashMap;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//Class that contains and defines the Node structure of the Nodes in the Fibonacci heap implementation
class Node {

    int val;  //maintains frequency
    int degree; //number of children
    boolean childCut; //childcut value either true or false
    String word; //stores the string
    Node parent; // pointer to the parent node
    Node child; // pointer to the child node
    Node prev; // pointer to the previous node in the doubly linked list
    Node next; // pointer to the the next node in the doubly linked list
    
    
    
    // Constructor of the class used for initializatiion of all the variable
    Node(String word, int val) 
        {
            this.val = val;
            this.word = word;
            this.degree = 0;
            child = null;
            parent = null;
            next = this;
            prev = this;
            
        }
        
        
    
}

public class keywordcounter {
    
   
    
    private MaxFibonacciHeap fheap; // creating a fibonacci heap object

    private Map<String, Node> map;
    
    //Constructor of the class. It initializes the map and the fibonacci heap
    public keywordcounter()  
    {
        
        map = new HashMap<String, Node>();
        fheap = new MaxFibonacciHeap();
    }
     // Method to read all the input words along with there frequency
    
    public void read_file(BufferedReader input, BufferedWriter writer) 
    {
        
        try
        {
            
            List<Node> reinsertList = new ArrayList<Node>();
            
            String line = new String(); 
            
            while ( !(line = input.readLine()).equalsIgnoreCase("STOP") ) 
            {
               
                if ( !(line.startsWith("$")) ) 
                {
                    
                    int query = Integer.parseInt(line); //parse the integer frequency of the word
                   
                    for (int i=1; (i<= query && fheap.maxPointer != null) ; i++) 
                    {

                        Node maxNode = fheap.removeMax();

                        if ( (i==query)|| (maxNode == null))
                            write_file(writer,maxNode,"end");
                        else
                            write_file(writer,maxNode,"not");

                        reinsertList.add(maxNode);
                    }

                    if (!reinsertList.isEmpty())
                        fheap.reinsert(reinsertList);
                }
                
                else 
                { 
                    
                    String splitLine[] = line.split(" ");
                    
                    Node hashTagFreq = map.get(splitLine[0]);
                    
                    if (hashTagFreq != null) 
                    {
                        fheap.increaseKey(hashTagFreq, (hashTagFreq.val + Integer.parseInt(splitLine[1])) ); // increment the frequeny of the word as it is already present
                    }
                     
                    else 
                    {
                        hashTagFreq = fheap.insert( splitLine[0], Integer.parseInt(splitLine[1]) ); // Insert the (word, val) pair in the fibonacci heap as it is not already present
                        map.put(splitLine[0], hashTagFreq);


                    }
                }
            }
            
        } catch (IOException ex) {
            System.out.println(ex);
        }
      
    }
   
    // Method to write out the top results of the search 
    public void write_file(BufferedWriter writer,Node node, String check) 
    {
        
        try 
        {
            
            if (check.equals("end")) 
            {
                writer.write( node.word.substring(node.word.indexOf('$')+1, node.word.length()));
                writer.newLine();
            }
            else
                writer.write( node.word.substring(node.word.indexOf('$')+1, node.word.length())+  ',');
        
        } 
        catch (IOException e)
        {
            System.err.println("Error in writing to output.txt");
        }
        
    }
    
   
    
    
    public static void main(String[] args) throws IOException {
        
        keywordcounter counter = new keywordcounter();
        
        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter("output_file.txt"));
    
            BufferedReader input = new BufferedReader(new FileReader(args[0]));
        
            counter.read_file(input,writer);
        
            try 
            {
                
                input.close();
                writer.close();
                
            } 
            catch (IOException err) 
            {
                System.out.println(err);
            }
        
        } 
        catch (FileNotFoundException err) 
        {
            
            System.out.println(err);
            
        }
      
    }
    
}
