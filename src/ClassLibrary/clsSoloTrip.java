/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassLibrary;

import java.io.IOException;

/**
 *
 * @author user
 */
public class clsSoloTrip {
    
    clsSoloTrip(){
        
    }
    
    clsSolution [] getTrip(clsGroupData groupdata,float Evaluation[]) throws IOException{
        Evaluation[0]=0;
         clsSolution Trips[]=new clsSolution[groupdata.NumberOfPersons] ;
        clsTabooSearch TabooSearch=new clsTabooSearch();
        clsData SingleTuristData[]=this.getDataForTouristsAlone(groupdata);
        for(int i=0;i<SingleTuristData.length;i++){
            clsSolution solution=TabooSearch.findSolution(SingleTuristData[i]);
             Trips[i]=new clsSolution(SingleTuristData[i],solution);
            Evaluation[0]+=solution.Evaluation;
            clsTest.PrintTrip(SingleTuristData[i],solution);
        }
        return Trips;
    }
    
    clsData [] getDataForTouristsAlone(clsGroupData groupdata ){
        clsData result[]=new clsData[groupdata.NumberOfPersons];
        for(int i=0;i<groupdata.NumberOfPersons;i++){
            float SingleTuristDataPOI[][]= getSingleScorePOIs(groupdata.POI,i,groupdata.NumberOfPersons);
            float NumberOfVerticesOfTypeZ[]=new float[10];
            System.arraycopy(groupdata.Person[i], 2+2*(groupdata.NumberOfPersons-1),   NumberOfVerticesOfTypeZ, 0,10);
            clsData SingleTuristData=new clsData(groupdata.NumberOfTours,groupdata.NumberOfVertices,groupdata.NumberOfPersons,
                    groupdata.MinimumNumberOfPersonsInSubgroup,groupdata.Person[i][1],NumberOfVerticesOfTypeZ,groupdata.StartPoint,
                    SingleTuristDataPOI,groupdata.Distance,groupdata.MaxNumberOfPOIsPossiblePerTour);    
            result[i]=SingleTuristData;
        }
        return result;
    }
    
    final float  [][] getSingleScorePOIs(float MultipleScorePOIs[][], int PersonIndex, int NumberOfPersons){
        float result[][]=new float [MultipleScorePOIs.length][MultipleScorePOIs[0].length-NumberOfPersons+1];
        for(int i=0;i<result.length;i++){
            System.arraycopy(MultipleScorePOIs[i], 0, result[i], 0, 4);
            result[i][4]=MultipleScorePOIs[i][4+PersonIndex];
            System.arraycopy(MultipleScorePOIs[i], 4+NumberOfPersons, result[i], 5, 17);
        }
        return result;
    }
}
