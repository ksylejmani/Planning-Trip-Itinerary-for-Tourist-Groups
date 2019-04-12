/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassLibrary;

import java.util.ArrayList;

/**
 *
 * @author user
 */
public class clsVirtualSubGroupTrip {
    
    ArrayList<Float> [] getRealTouristClusters(ArrayList<Integer> [] TouristClusters, ArrayList<Float> [] VirtualTouristSubgroups){
        ArrayList<Float> [] result=new ArrayList[TouristClusters.length];
        for(int i=0;i<result.length;i++){
            ArrayList<Float> List=new ArrayList();
            for(int j=0;j<TouristClusters[i].size();j++){
                for(int k=0;k<VirtualTouristSubgroups[TouristClusters[i].get(j)].size();k++){
                    List.add(VirtualTouristSubgroups[TouristClusters[i].get(j)].get(k));
                }
            }
            result[i]=List;
        }
        return result;
    }
    float getTripEvaluation(clsSolution [] Trips,ArrayList<Float> [] TouristClusters,clsGroupData groupdata){
        float result=0;
        for(int i=0;i<TouristClusters.length;i++){
            result=result+this.getClusterEvaluation(Trips[i], TouristClusters[i], groupdata);
        }
        return result;
    }
    float getClusterEvaluation(clsSolution Trip,ArrayList<Float> TouristCluster,clsGroupData groupdata){
        float result=0;
        for(int i=0;i<TouristCluster.size();i++){
            result=result+this.getTouristEvaluation(Math.round(TouristCluster.get(i)), Trip, TouristCluster, groupdata);
        }
        return result;
    }
    
    float getTouristEvaluation(int TouristID,clsSolution Trip,ArrayList<Float> TouristCluster,clsGroupData groupdata){
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
                        TouristID, Math.round(TouristCluster.get(i)), groupdata.Person)*(Trip.POIsInItinerary.size()); 
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
}
