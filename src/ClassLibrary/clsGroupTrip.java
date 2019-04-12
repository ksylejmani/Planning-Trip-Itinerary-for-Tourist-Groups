package ClassLibrary;

import java.io.IOException;
import java.util.ArrayList;

public class clsGroupTrip {
    
    clsSolution getTrip(clsGroupData groupdata,  float Evaluation[]) throws IOException{
        ArrayList<Integer> TouristicGroup=this.getTouristicGroup(groupdata.NumberOfPersons);
        clsTabooSearch TabooSearch=new clsTabooSearch();
        clsData TouristicGroupData=this.getDataForTouristicGroup(groupdata,TouristicGroup);
        clsSolution solution=TabooSearch.findSolution(TouristicGroupData);
        clsTest.PrintTrip(TouristicGroupData,solution);
        Evaluation[0]=this.getTripEvaluation(solution, TouristicGroup, groupdata);
        //System.out.println("Total evaluation:"+Evaluation[0]);
        return solution;
    }
    
    float getTripEvaluation(clsSolution Trip,ArrayList<Integer> TouristicGroup,clsGroupData groupdata){
        float result=0;
        for(int i=0;i<TouristicGroup.size();i++){
            result=result+this.getTouristEvaluation(TouristicGroup.get(i), Trip, TouristicGroup, groupdata);
        }
        return result;
    }
    
    float getTouristEvaluation(int TouristID,clsSolution Trip,ArrayList<Integer> TouristicGroup,clsGroupData groupdata){
        float result;
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
    
    clsData getDataForTouristicGroup(clsGroupData groupdata,ArrayList<Integer> TouristicGroup ){

        float TuristGroupDataPOI[][]= getSubgroupPOIs(groupdata.POI,TouristicGroup);
        float MinimalBudgetLimitation=this.getMinimumBudgetLimitation(TouristicGroup, groupdata.Person);
        float NumberOfVerticesOfTypeZ[]=this.getMinimumNumberOfVerticesOfTypeZ(groupdata.Person, TouristicGroup);
        clsData TuristicGroupData=new clsData(groupdata.NumberOfTours,groupdata.NumberOfVertices,groupdata.NumberOfPersons,
                groupdata.MinimumNumberOfPersonsInSubgroup,MinimalBudgetLimitation,NumberOfVerticesOfTypeZ,groupdata.StartPoint,
                TuristGroupDataPOI,groupdata.Distance,groupdata.MaxNumberOfPOIsPossiblePerTour);    
        return TuristicGroupData;
    }
    ArrayList<Integer>  getTouristicGroup(int NumberOfPersons){
        ArrayList<Integer> result=new ArrayList();
        for(int i=0;i<NumberOfPersons;i++){
            result.add(i);
        }
        return result;
        
    }
    
    float getMinimumBudgetLimitation(ArrayList<Integer> TouristicGroup,float Person[][]){
        float Min=Person[TouristicGroup.get(0)][1];
        for(int i=1;i<TouristicGroup.size();i++){
            if(Person[TouristicGroup.get(i)][1]<Min){
                Min=Person[TouristicGroup.get(i)][1];
            }
        }
        return Min;
    }
    
    float [] getMinimumNumberOfVerticesOfTypeZ( float Person[][],ArrayList<Integer> TouristicGroup){
        float result[]=new float [10];
        for(int i=0;i<result.length;i++){
            float Min= Person[TouristicGroup.get(0)][i+2+2*(Person.length-1)];
            for(int j=1;j<TouristicGroup.size();j++){
                if(Person[TouristicGroup.get(j)][i+2+2*(Person.length-1)]<Min){
                    Min=Person[TouristicGroup.get(j)][i+2+2*(Person.length-1)];
                }
            }
            result[i]=Min;
        }
        return result;
    }
    
    final float  [][] getSubgroupPOIs(float MultipleScorePOIs[][], ArrayList<Integer> TouristicGroup){
        float result[][]=new float [MultipleScorePOIs.length][MultipleScorePOIs[0].length-TouristicGroup.size()+1];
        for(int i=0;i<result.length;i++){
            System.arraycopy(MultipleScorePOIs[i], 0, result[i], 0, 4);
            float SubgroupScore=0;
            for(int j=0;j<TouristicGroup.size();j++){
                SubgroupScore+=MultipleScorePOIs[i][4+TouristicGroup.get(j)];
            }
            result[i][4]=SubgroupScore/TouristicGroup.size();
            System.arraycopy(MultipleScorePOIs[i], 4+TouristicGroup.size(), result[i], 5, 17);
        }
        return result;
    }
}
