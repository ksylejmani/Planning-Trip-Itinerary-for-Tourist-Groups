/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassLibrary;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class clsSubGroupTrip {
    
    clsSolution [] getTrip(clsGroupData groupdata,float Evaluation[],ArrayList<Integer> [] TouristClusters) throws IOException{      
        clsTabooSearch TabooSearch=new clsTabooSearch();
        clsData TuristSubgroupData[]=this.getDataForTouristsSubgroups(groupdata,TouristClusters);
        clsSolution Trips[]=new clsSolution[TuristSubgroupData.length];
        for(int CurrentCluster=0;CurrentCluster<TuristSubgroupData.length;CurrentCluster++){
            clsSolution solution=TabooSearch.findSolution(TuristSubgroupData[CurrentCluster]);
            Trips[CurrentCluster]=new clsSolution(TuristSubgroupData[CurrentCluster],solution);
            clsTest.PrintTrip(TuristSubgroupData[CurrentCluster],solution);
        }
        Evaluation[0]=this.getTripEvaluation(Trips, TouristClusters, groupdata);
        //System.out.println("Total evaluation:"+ Evaluation[0]);
        return Trips;
    }
    
    float getTripEvaluation(clsSolution [] Trips,ArrayList<Integer> [] TouristClusters,clsGroupData groupdata){
        float result=0;
        for(int i=0;i<TouristClusters.length;i++){
            result=result+this.getClusterEvaluation(Trips[i], TouristClusters[i], groupdata);
        }
        return result;
    }
    float getClusterEvaluation(clsSolution Trip,ArrayList<Integer> TouristCluster,clsGroupData groupdata){
        float result=0;
        for(int i=0;i<TouristCluster.size();i++){
            result=result+this.getTouristEvaluation(TouristCluster.get(i), Trip, TouristCluster, groupdata);
        }
        return result;
    }
    
    float getTouristEvaluation(int TouristID,clsSolution Trip,ArrayList<Integer> TouristCluster,clsGroupData groupdata){
        float result;
        float poi_score=0;
        for(int i=0;i<Trip.Itinerary.length;i++){
            for(int j=0;j<=Trip.TourLastVisit[1][i];j++){
                poi_score=poi_score+groupdata.POI[Trip.Itinerary[i][j].POI_ID-2][4+TouristID];
            }
        }
        float social_relationship_score=0;
        for(int i=0;i<TouristCluster.size();i++){
            if(TouristCluster.get(i)!=TouristID){
                social_relationship_score=social_relationship_score+this.getSocialRelationshipBetweenPersons(
                        TouristID, TouristCluster.get(i), groupdata.Person)*(Trip.POIsInItinerary.size()); 
            }
        }
        result=poi_score + social_relationship_score;
        return result;
    }
    
    float getSocialRelationshipBetweenPersons(int Person1ID,int Person2ID, float Person[][]){
        float result;
        if(Person1ID<Person2ID){
            result=Person[Person1ID][1+Person2ID];
        }
        else
        {
            result=Person[Person1ID][2+Person2ID];
        }
        return result;
    }
    
    clsData [] getDataForTouristsSubgroups(clsGroupData groupdata,  ArrayList<Integer> [] TouristSubgroups ){
        clsData result[]=new clsData[TouristSubgroups.length];
        for(int i=0;i<TouristSubgroups.length;i++){
            float TuristSubgroupDataPOI[][]= getSubgroupPOIs(groupdata.POI,TouristSubgroups[i],groupdata.NumberOfPersons);
            float MinimalBudgetLimitation=this.getMinimumBudgetLimitation(TouristSubgroups[i], groupdata.Person);
            float NumberOfVerticesOfTypeZ[]=this.getMinimumNumberOfVerticesOfTypeZ(groupdata.Person, TouristSubgroups[i]);
            clsData TuristSubgroupData=new clsData(groupdata.NumberOfTours,groupdata.NumberOfVertices,groupdata.NumberOfPersons,
                    groupdata.MinimumNumberOfPersonsInSubgroup,MinimalBudgetLimitation,NumberOfVerticesOfTypeZ,groupdata.StartPoint,
                    TuristSubgroupDataPOI,groupdata.Distance,groupdata.MaxNumberOfPOIsPossiblePerTour);    
            result[i]=TuristSubgroupData;
        }
        return result;
    }
    float getMinimumBudgetLimitation(ArrayList<Integer> TouristSubgroup,float Person[][]){
        float Min=Person[TouristSubgroup.get(0)][1];
        for(int i=1;i<TouristSubgroup.size();i++){
            if(Person[TouristSubgroup.get(i)][1]<Min){
                Min=Person[TouristSubgroup.get(i)][1];
            }
        }
        return Min;
    }
    
    float [] getMinimumNumberOfVerticesOfTypeZ( float Person[][],ArrayList<Integer> TouristSubgroup){
        float result[]=new float [10];
        for(int i=0;i<result.length;i++){
            float Min= Person[TouristSubgroup.get(0)][i+2+2*(Person.length-1)];
            for(int j=1;j<TouristSubgroup.size();j++){
                if(Person[TouristSubgroup.get(j)][i+2+2*(Person.length-1)]<Min){
                    Min=Person[TouristSubgroup.get(j)][i+2+2*(Person.length-1)];
                }
            }
            result[i]=Min;
        }
        return result;
    }
    
    final float  [][] getSubgroupPOIs(float MultipleScorePOIs[][], ArrayList<Integer> TouristSubgroup,int NumberOfPersons){
        float result[][]=new float [MultipleScorePOIs.length][MultipleScorePOIs[0].length-NumberOfPersons+1];
        for(int i=0;i<result.length;i++){
            System.arraycopy(MultipleScorePOIs[i], 0, result[i], 0, 4);
            float SubgroupScore=0;
            for(int j=0;j<TouristSubgroup.size();j++){
                SubgroupScore+=MultipleScorePOIs[i][4+TouristSubgroup.get(j)];
            }
            result[i][4]=SubgroupScore/TouristSubgroup.size();
            System.arraycopy(MultipleScorePOIs[i], 4+NumberOfPersons, result[i], 5, 17);
        }
        return result;
    }
    
    
    
}
