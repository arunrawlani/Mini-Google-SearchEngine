import java.util.*;
import java.io.*;

// This class implements a google-like search engine
public class searchEngine {

    public HashMap<String, LinkedList<String> > wordIndex; // this will contain a set of pairs (String, LinkedList of Strings) 
    public directedGraph internet; // this is our internet graph
    
    
    
    // Constructor initializes everything to empty data structures
    // It also sets the location of the internet files
    searchEngine() {
 // Below is the directory that contains all the internet files
 htmlParsing.internetFilesLocation = "internetFiles";
 wordIndex = new HashMap<String, LinkedList<String> > ();  
 internet = new directedGraph();    
    } // end of constructor2014
    
    
    // Returns a String description of a searchEngine
    public String toString () {
 return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
    }
    
    
    // This does a graph traversal of the internet, starting at the given url.
    // For each new vertex seen, it updates the wordIndex, the internet graph,
    // and the set of visited vertices.
    
    void traverseInternet(String url) throws Exception {

      //Marking each url as soon as it is visited
      //System.out.println(url);
      internet.addVertex(url);
      internet.setVisited(url, true);
      LinkedList<String> neighbours= htmlParsing.getLinks(url);
      LinkedList<String> content= htmlParsing.getContent(url);
      //System.out.println("Size of neighbours = "+neighbours.size());
      //htmlParsing.writeContent(url);
      Iterator<String> i= content.iterator();
      
      ///To the wordIndex, add each and every word in the content
      while(i.hasNext()){
        String w = i.next();
        //System.out.println(w); 
        //Verifying if the word is already present in the index
        if (wordIndex.containsKey(w)){//Case where word is already present in wordIndex
          if(!(wordIndex.get(w)).contains(url)){//Asks to add url for the word w if not present
            wordIndex.get(w).addLast(url);
          }
        }
        else{//Case where word w is not already present in wordIndex
          LinkedList<String> list = new LinkedList<String>();
          list.addLast(url);
          wordIndex.put(w,list);
        }
      }
      i=neighbours.iterator();
      while(i.hasNext()){
        String w= i.next();
        //System.out.println("edge: "+url+" "+w);
        internet.addEdge(url,w);
        if(!internet.getVisited(w)){
          traverseInternet(w);
        }
      }
      
 
 
    } // end of traverseInternet
    
    
    /* This computes the pageRanks for every vertex in the internet graph.
       It will only be called after the internet graph has been constructed using 
       traverseInternet.
       
    */
    void computePageRanks() {

      LinkedList<String> n =internet.getVertices();
      Iterator<String> i =n.iterator();
      while(i.hasNext()){
        internet.setPageRank(i.next(),1);
      }
      
      for (int iteration = 0; iteration<10; iteration++){
        i=n.iterator();
        while(i.hasNext()){
          String a = i.next();
          double pr= 0.5;
          Iterator<String> referers= internet.getEdgesInto(a).iterator();
          while(referers.hasNext()){
            String t= referers.next();
            pr += 0.5*(internet.getPageRank(t)/internet.getOutDegree(t));
          }
          internet.setPageRank(a, pr);
          //System.out.println("pr "+a+" "+pr+"degree is"+internet.getEdgesInto(a).size());
        }
      }
    }// end of computePageRanks
    
 
    /* Returns the URL of the page with the high page-rank containing the query word
       Returns the String "" if no web site contains the query.
       This method can only be called after the computePageRanks method has been executed
    */
    String getBestURL(String query) {

      LinkedList<String> webpages;
      query = query.toLowerCase();
      if( wordIndex.containsKey(query) ) webpages = wordIndex.get(query);
      else return new String("");
      
      Iterator<String> i= webpages.iterator();
      String best= "";
      double bestPR= -1;
      while (i.hasNext()){
        String s=i.next();
        double score = internet.getPageRank(s);
        //System.out.println(s+ " " + score);
        if (score > bestPR) {
          bestPR=score;
          best = s;
        }
      }
      //System.out.println("Best= "+best+" "+bestPR);
      return best;
    } // end of getBestURL
    
    
 
    public static void main(String args[]) throws Exception{  
 searchEngine mySearchEngine = new searchEngine();
 // to debug your program, start with.
 //mySearchEngine.traverseInternet("http://www.cs.mcgill.ca/~blanchem/250/a.html");
 
 // When your program is working on the small example, move on to
 mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");

 

// System.out.println(mySearchEngine);
 
 mySearchEngine.computePageRanks();
 
 BufferedReader stndin = new BufferedReader(new InputStreamReader(System.in));
 String query;
 do {
     System.out.print("Enter query: ");
     query = stndin.readLine();
     if ( query != null && query.length() > 0 ) {
  System.out.println("Best site = " + mySearchEngine.getBestURL(query));
     }
 } while (query!=null && query.length()>0);    
    } // end of main
}