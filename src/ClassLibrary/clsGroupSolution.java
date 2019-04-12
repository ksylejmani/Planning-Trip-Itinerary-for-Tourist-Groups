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
public class clsGroupSolution {
    
    clsSolution [] Solutions;
    float Evaluation;
    
    clsGroupSolution(clsSolution Solution,clsGroupData GroupData){
        Solutions=this.getSolutions(Solution, GroupData);
        Evaluation=this.getGroupSolutionEvaluation();
    }
    
    final float getGroupSolutionEvaluation(){
        float result=0;
        for(int i=0;i<Solutions.length;i++)
            result+=Solutions[i].Evaluation;
        return result;
    }
    final clsSolution [] getSolutions(clsSolution Solution,clsGroupData GroupData){
         clsSolution [] result=new clsSolution[GroupData.NumberOfPersons];
         for(int i=0;i<GroupData.NumberOfPersons;i++){
             clsSolution solution= new clsSolution(GroupData,Solution);
             solution.Evaluation=this.getTouristEvaluation(i, solution, GroupData);
             result[i]=solution;
         }
         return result;
    }

//    float getEvaluationForSinglePerson(ArrayList <Integer> POIsInItinerary, int PersonIndex, float POI[][]){
//        float result=0;
//        for (int i=0;i<POIsInItinerary.size();i++){
//            result+=POI[POIsInItinerary.get(i)-2][4+PersonIndex];
//        }
//        return result;
//    }
    float getTouristEvaluation(int TouristID,clsSolution Trip,clsGroupData groupdata){
        float result;
        ArrayList<Integer> TouristicGroup=this.getTouristicGroup(groupdata.NumberOfPersons);
        float poi_score=0;
        for(int i=0;i<Trip.Itinerary.length;i++){
            for(int j=0;j<=Trip.TourLastVisit[1][i];j++){
                poi_score=poi_score+groupdata.POI[Trip.Itinerary[i][j].POI_ID-2][4+TouristID];
            }
        }
        float social_relationship_score=0;
        for(int i=0;i<TouristicGroup.size();i++){
            if(TouristicGroup.get(i)!=TouristID){
                social_relationship_score=social_relationship_score+this.getSocialRelationshipBetweenPersons(
                        TouristID, TouristicGroup.get(i), groupdata.Person)*(Trip.POIsInItinerary.size()); 
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
        ArrayList<Integer>  getTouristicGroup(int NumberOfPersons){
        ArrayList<Integer> result=new ArrayList();
        for(int i=0;i<NumberOfPersons;i++){
            result.add(i);
        }
        return result;
        
    }
}
