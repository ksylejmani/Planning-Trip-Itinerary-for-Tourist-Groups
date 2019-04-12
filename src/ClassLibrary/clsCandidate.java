/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassLibrary;
import java.util.*;
/**
 *
 * @author user
 */
public class clsCandidate {
    

    public static void sortOpenPOIs(clsData data,float TimeInterval,int NumberOfVertices, 
            int NumberOfPersons, ArrayList<Integer> POIs){//Testim OK, te vazhdohet

            int X;
            for(int i=0;i<POIs.size()-1;i++){
                for(int j=i+1;j<POIs.size();j++){
                    if(Math.abs(TimeInterval-data.getPOIMiddleOfTimeWindow(POIs.get(j))) < 
                            Math.abs(TimeInterval-data.getPOIMiddleOfTimeWindow(POIs.get(i)))){
                        X=POIs.get(i);
                        POIs.add(i, POIs.get(j));
                        POIs.remove(i+1);
                        POIs.add(j, X);
                        POIs.remove(j+1);
                    }
                }
            }
    } 
        public static ArrayList<Integer> getOpenPOIs(clsData data,float TimeInterval,int NumberOfVertices, int NumberOfPersons,
                ArrayList<Integer> POIsInOffItinerary){
         ArrayList<Integer> result=new ArrayList();
         for(int i=0;i<POIsInOffItinerary.size();i++){
             if(data.getPOI()[POIsInOffItinerary.get(i)-2][4+NumberOfPersons]<=TimeInterval && 
                   data.getPOI()[POIsInOffItinerary.get(i)-2][8+NumberOfPersons]>=TimeInterval ){
                    result.add(POIsInOffItinerary.get(i));
             }
         }
         sortOpenPOIs(data,TimeInterval,NumberOfVertices,NumberOfPersons,result);
         return result;
    } 
}
