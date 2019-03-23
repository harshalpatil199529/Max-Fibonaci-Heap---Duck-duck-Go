import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MaxFibonacciHeap {
    
    Node maxPointer = null; // Initializing maxPointer to null

    int size = 0 ; // Initializing the size of the Fibonacci heap to zero
    
    

    //Method to insert the (word,val) pair into the fibonacci heap and meld it with the heap 
    public Node insert(String word, int val)
    {
        
        Node pointer = new Node(word, val); // Creates new Node object with the passed word and its corresponding frequency
        
        maxPointer = meld(maxPointer, pointer); //meld with the already present roots and return the pointer to the maxPointer
        
        size = size + 1; // Increase the size of the Fibonacci heap
        
        return pointer; // return the inserted node object
        
    }
    
    

    //Method to remove the max Node and meld the corresponding children with the already present roots and do pairwise combine 
    public Node removeMax(){

        if(maxPointer == null)
            return null;
        else
        {
            Node old = maxPointer;

            maxPointer.prev.next = maxPointer.next; // remove the maxPointer from doubly linked list of the top level roots
            maxPointer.next.prev = maxPointer.prev;
            
            Node mNext = null;
            if (maxPointer.next != maxPointer) 
                mNext = maxPointer.next;
            else mNext = null;
                                    
            maxPointer.prev = maxPointer;  // reset the prev and next pointers of the maxPointer to itself
            maxPointer.next = maxPointer; 
        
            size = size -1; // reduce the size of the Fibonacci heap     
            
            //Set the pointers in the children of the maxPointer to null
            if (maxPointer.child != null) 
            {
                maxPointer.child.parent = null;
                for(Node i = maxPointer.child.next; i != maxPointer.child; i = i.next)
                    i.parent = null; 
            }
        
            maxPointer = meld(mNext, maxPointer.child); //Meld the children of the maxPointer with the already existing root nodes
            
            if (maxPointer != null) 
                pairwiseCombine();  //Do pairwise combine if multiple root nodes of the same degree
            return old;             // return the removed node 
        }    
        
        
    }
    
    
    // method to increase the value of a preexisting key. 
    public void increaseKey(Node pointer, int newFreq) {
      
        
        pointer.val = newFreq;
        if (pointer.parent != null && pointer.val > pointer.parent.val) 
        {
            
            Node upper = pointer.parent;
            remove(pointer,upper);
            cascadingCut(upper);
        }
       
        if (pointer.val > maxPointer.val)
            maxPointer = pointer;         //Setting the maxPointer
         
    }
    
    
    //Method to meld two nodes
    public Node meld(Node temp1, Node temp2){
        
        // If both nodes are non null
        if (temp1 != null && temp2 != null) {
            
            // Meld the nodes
            Node temp = temp1.next;
            temp1.next = temp2.next;
            temp1.next.prev = temp1;
            temp2.next = temp;
            temp2.next.prev = temp2;   

            
            if (temp1.val < temp2.val) //checks which node is maximum and return the max
                return temp2;
            else return temp1;
        }
        
        else if (temp2 == null) {
            return temp1; //returns temp1 as it is maximum
        }    

        else if (temp1 == null) {
            return temp2; //returns temp2 as it is maximum
        }
        else return null;
        
    }
    
    

    //Method to perform the cascading cut operation on the Fibonacci heap. 
    public void cascadingCut(Node pointer){
        
        Node upper = pointer.parent; //saving the parent node whose child we removed
        
        if (upper != null) {
            
            if ( pointer.childCut == false ) //If the value of the childcut field is false then we need to set it true.
                pointer.childCut = true;
            
            else {
                remove(pointer,upper);   //If the value of the childcut field is already true we remove the node and carryon with the cascading cut operation
                cascadingCut(upper);
            }    
        }
        
    }
    


    //Method to remove a node from its parent and then perform a meld operation with the already present list of roots
    public void remove(Node pointer, Node upper){

        
        pointer.next.prev = pointer.prev; // remove the node by changing the prev point to the next one
        pointer.prev.next = pointer.next; 
        
        Node nNode = pointer.next;  // pointer to the next node in the linked list
        
        pointer.prev = pointer;
        pointer.next = pointer;
        
        upper.degree = upper.degree - 1;  // Since the parent lost a child we need to decrease its degree by 1
        
        if (upper.child == pointer) // Since the parent has a pointer to only one child we meet lose it hence assign this to the next node
        {
            if (nNode != pointer)
                upper.child = nNode;
            else upper.child = null;    
        }
        
        pointer.parent = null; //resetting the removed nodes parent to null
        
        maxPointer = meld(maxPointer,pointer); //Meld the node with the already existing roots
    
        pointer.childCut = false; //Now the nodes childcut field will be reset to false
        
    }
    
    
    //Method used to merge two equal degree roots 
    public void pairwiseCombine() {
        
        List<Node> rootList = new ArrayList<Node>(); //List to store all the root nodes
        List<Node> degreeList = new ArrayList<Node>(); //List to maintain the degree of nodes that we have already come across
        
        Node temp = maxPointer;
        do                  //traverse
        {
            rootList.add(temp);
            temp = temp.next;
        } 
        while(maxPointer != temp);
        
 
        for (Node i : rootList)  //Go through the rootlist and do pairwise combine operation
        {
            
            while (i.degree >= degreeList.size()) // Add the degree of the node into the degreelist
            {
                    degreeList.add(null);
            }
            

            while (degreeList.get(i.degree) != null) //Keep performing the merge until we find a equal degree root
            {
                
                Node temp2 = degreeList.get(i.degree); //If we find a root of the same degree then get that root from the degree list
                
                degreeList.set(i.degree, null); // reset the degree list after removing the required node
                
                Node maximum;
                Node minimum; 
                if (i.val >temp2.val) 
                {
                    maximum = i;
                    minimum = temp2;
                }
                else 
                {
                    maximum = temp2;
                    minimum = i;
                }    
                
                minimum.prev.next = minimum.next;  // remove the minimum from the top level root list and meld it along with the children of the maximum node
                minimum.next.prev = minimum.prev; 
                
                            
                minimum.next = minimum; //reset its next and prev pointers to itself
                minimum.prev = minimum;
                
                maximum.child = meld(maximum.child, minimum); // melding the children of the maximum with the minimum and setting the maximum out of them as the child of the parent
            
                minimum.parent = maximum; //resetting the parent field of the minimum node to the maximum
                
                minimum.childCut = false; // also resetting the childcut field value to false

                maximum.degree = maximum.degree + 1;   // increase the degree field of the maximum node             
                
                i = maximum; //Now add this to the degree list and continue

                while (i.degree >= degreeList.size()) 
                {
                    degreeList.add(null);
                }
            }
            
            if (degreeList.get(i.degree) == null) 
                degreeList.set(i.degree, i);
        }
        
        maxPointer = null;
        for (Node j: degreeList) {
            if (j!=null) {
            j.prev = j;
            j.next = j;
            maxPointer = meld(maxPointer, j);
            }
        }
    }
    
    
    //Method to reinsert all the the max nodes back into the fibonacci heap
    public void reinsert(List<Node> nodeList) {
       
       for(int i =0; i<nodeList.size(); i++) 
       {
           
           nodeList.get(i).child = null;
           nodeList.get(i).parent = null;
           nodeList.get(i).degree = 0;
           maxPointer = meld(maxPointer, nodeList.get(i));
       } 
       
       nodeList.clear();      
    }
    
}
