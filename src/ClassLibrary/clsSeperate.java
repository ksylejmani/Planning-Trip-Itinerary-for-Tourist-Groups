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
public class clsSeperate {
    
            
     clsGroupSolution getGroupSolution(clsSolution Trip,clsGroupData GroupData){
        clsGroupSolution GroupSolution=new clsGroupSolution(Trip,GroupData);
        for(int TourIndex=0;TourIndex<GroupSolution.Solutions[0].Itinerary.length;TourIndex++){
            float TourLength=GroupData.StartPoint[6];
            float StartOfTheTour=GroupData.StartPoint[5];
            for(int VisitIndex=0;VisitIndex<GroupSolution.Solutions[0].TourLastVisit[1][TourIndex];VisitIndex++){
                ArrayList<ArrayList<Integer>> TouristSubgroupsAtATourPosition=
                        this.getTouristSubgroupsAtATourPosition(GroupSolution.Solutions, TourIndex, VisitIndex);
                for(int SubGroupIndex=0;SubGroupIndex<TouristSubgroupsAtATourPosition.size();SubGroupIndex++){
                    if(TouristSubgroupsAtATourPosition.get(SubGroupIndex).size()>=2){//When a sub group has a single tourist no need to apply the Seperate operator
                        int AvoidedPOI=GroupSolution.Solutions[TouristSubgroupsAtATourPosition.get(SubGroupIndex).get(0)].Itinerary[TourIndex][VisitIndex].POI_ID;
                        ArrayList<Integer> NotMaximallySatisfiedTourists=
                            this.getNotMaximallySatisfiedTouristsInPOI(AvoidedPOI, TouristSubgroupsAtATourPosition.get(SubGroupIndex), GroupData);
                        for(int PersonIndexInSubGroup=0;PersonIndexInSubGroup<NotMaximallySatisfiedTourists.size();PersonIndexInSubGroup++){
                            int PersonID=NotMaximallySatisfiedTourists.get(PersonIndexInSubGroup);
                            int IndexOfLastVisit=GroupSolution.Solutions[PersonID].TourLastVisit[1][TourIndex];
                            //int AvoidedPOI=GroupSolution.Solutions[PersonID].Itinerary[TourIndex][TourIndex].POI_ID;
                            int PreviousPoint=this.getPreviousPoint(GroupSolution.Solutions[PersonID].Itinerary,TourIndex,VisitIndex);
                            int NextPoint=this.getNextPoint(GroupSolution.Solutions[PersonID].Itinerary,TourIndex, VisitIndex,IndexOfLastVisit);
                            float TimeLimit=this.getTimeLimitForChangeOfVisit(GroupSolution, PersonID, TourIndex, VisitIndex,IndexOfLastVisit, TourLength);
                            float PreviousPointEndTime=this.getPreviousPointEndTime(GroupSolution, PersonID, TourIndex, VisitIndex);
                            ArrayList<Integer> ListOfReachablePOIs=
                                    this.getListOfOpenPOIsOnRouteBetweenTwoPOIs(VisitIndex,PreviousPoint, NextPoint, TimeLimit,StartOfTheTour,
                                    PreviousPointEndTime, GroupData.POI,GroupData.Distance, 
                                    GroupSolution.Solutions[PersonID].POIsOffItinerary,GroupData.NumberOfPersons); 
                            int MostSuitablePOI=this.getMostSuitablePOI(PersonID,TourIndex,VisitIndex,AvoidedPOI, 
                                     TouristSubgroupsAtATourPosition,SubGroupIndex, PreviousPoint, 
                                    NextPoint,GroupSolution, ListOfReachablePOIs, GroupSolution.Solutions, GroupData);
                             if(MostSuitablePOI !=-1 ){
                                clsAdaptSeperate AS=new clsAdaptSeperate();    
                                AS.Apply(PersonID, MostSuitablePOI, TourIndex, VisitIndex,GroupSolution,
                                        GroupData,TouristSubgroupsAtATourPosition,SubGroupIndex);

                             }
                        }
                    }
                }
            }
        }

        return GroupSolution;
    }
    
    int getMostSuitablePOI(int PersonID,int TourIndex, int VisitIndex,
            int AvoidedPOIID,ArrayList<ArrayList<Integer>> TouristSubgroupsAtATourPosition, 
            int SubGroupIndex,int PreviousPoint,int NextPoint,clsGroupSolution GroupSolution,
            ArrayList<Integer> ListOfReachablePOIs, clsSolution Solutions[],clsGroupData GroupData){
        int result=-1;
        int MaximalEvaluatingPOI=-1;
        float MaximalEvaluation=0;
        if(!ListOfReachablePOIs.isEmpty()){
            for(int i=0;i<ListOfReachablePOIs.size();i++){
                float InsertedPOI[]=GroupData.POI[ListOfReachablePOIs.get(i)-2];
                float AvoidedPOI[]=GroupData.POI[AvoidedPOIID-2];
                float PersonBudget=GroupData.Person[PersonID][1];
                if(this.IsCostConstraintValid(PersonBudget, InsertedPOI, AvoidedPOI, Solutions[PersonID].BudgetCost, GroupData.NumberOfPersons)){
                    if(this.IsTourDurationConstraintValid(PersonID, Math.round(InsertedPOI[0]),TourIndex, VisitIndex,PreviousPoint,NextPoint,
                            Solutions,GroupData)){
                        if(this.IsPOICategoryConstraintValid(GroupData.Person[PersonID], Solutions[PersonID].TypeConstraintCounter, 
                                InsertedPOI,AvoidedPOI, GroupData.NumberOfPersons)){
                            MaximalEvaluation=this.getEvaluationForAvoidingAPOI(PersonID, Solutions[PersonID].Evaluation, GroupData.Person, 
                                                   InsertedPOI, AvoidedPOI, TouristSubgroupsAtATourPosition,SubGroupIndex,
                                                   TourIndex,VisitIndex,GroupSolution);
                            MaximalEvaluatingPOI=ListOfReachablePOIs.get(i);
                        } 
                    }
                }
            }  
        }
        if(MaximalEvaluation>Solutions[PersonID].Evaluation){
            result=MaximalEvaluatingPOI;
        }
        return result;
    }
    float getEvaluationForAvoidingAPOI(int PersonID,float CurrentEvaluation,float Persons[][],float InsertedPOI[],float AvoidedPOI[],
            ArrayList<ArrayList<Integer>> TouristSubgroupsAtATourPosition,int SubGroupIndex,
            int TourIndex, int VisitIndex, clsGroupSolution GroupSolution){
       ArrayList<Integer> CurrentTouristSubgroup=TouristSubgroupsAtATourPosition.get(SubGroupIndex);
        float result;
       float poi_score_change=InsertedPOI[4+PersonID]-AvoidedPOI[4+PersonID];//Satisfaction with POIs
        
        //Satisfaction for social relationship with other persons
        float seperate_social_relationship_score_change = - /* Why Minus? -because it has to be deducted from overall social relationship*/
            this.getSocialRelationshipBetweenAPersonAndItsSubGroupMembers(PersonID, CurrentTouristSubgroup, Persons);
        float join_social_relationship_score_change=0;
        int TheOtherSubgoupAlreadyAssignedToPOI=
            this.getOtherSubgoupAlreadyAssignedToPOI(TouristSubgroupsAtATourPosition, 
            SubGroupIndex,(int)InsertedPOI[0],TourIndex, VisitIndex, GroupSolution);
        if(TheOtherSubgoupAlreadyAssignedToPOI!=-1){
            CurrentTouristSubgroup=TouristSubgroupsAtATourPosition.get(TheOtherSubgoupAlreadyAssignedToPOI);
            join_social_relationship_score_change=
                this.getSocialRelationshipBetweenPersonAndSubGroup(PersonID, CurrentTouristSubgroup, Persons);
        }
        result=CurrentEvaluation+poi_score_change+
               seperate_social_relationship_score_change+
               join_social_relationship_score_change;
        
        return result;
    }
    
    float getSocialRelationshipBetweenAPersonAndItsSubGroupMembers(int PersonID,
            ArrayList<Integer> CurrentTouristSubgroup,float Persons[][]){
        float result=0;
        for(int i=0;i<CurrentTouristSubgroup.size();i++){
            for(int j=0;j<CurrentTouristSubgroup.size();j++){
                if(i!=j){
                    if(CurrentTouristSubgroup.get(i)==PersonID || CurrentTouristSubgroup.get(j)==PersonID){
                        result+=this.getSocialRelationshipBetweenPersons(
                                CurrentTouristSubgroup.get(i), CurrentTouristSubgroup.get(j), Persons); 
                    } 
                }
            }
        }
        return result;
    }
    float getSocialRelationshipBetweenPersonAndSubGroup(int PersonID,
            ArrayList<Integer> CurrentTouristSubgroup,float Persons[][]){
        float result=0;
        for(int i=0;i<CurrentTouristSubgroup.size();i++){
            result+=this.getSocialRelationshipBetweenPersons(
                    PersonID,CurrentTouristSubgroup.get(i), Persons); 
            result+=this.getSocialRelationshipBetweenPersons(
                    CurrentTouristSubgroup.get(i),PersonID, Persons); 
        }
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
    boolean IsTourDurationConstraintValid(int PersonID,int CurrentPOI,int TourIndex, int VisitIndex,
                                           int PreviousPoint,int NextPoint,clsSolution Solutions[],clsGroupData GroupData ){

        int IndexOfLastVisit=Solutions[PersonID].TourLastVisit[1][TourIndex];
        float NewVisitTimeCost=this.getVisitTimeCost(CurrentPOI, TourIndex, VisitIndex,IndexOfLastVisit, PreviousPoint,NextPoint,
                Solutions[PersonID].Itinerary, GroupData);
        float OldVisitTimeCost=
              Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour
            + Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint
            +(Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.EndTime - 
                Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.StartTime)
            + Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceToNextPoint;
        float VisitTimeCostChange=NewVisitTimeCost-OldVisitTimeCost;
        boolean result= Solutions[PersonID].TourTimeSpent[TourIndex]+VisitTimeCostChange<=GroupData.StartPoint[6];
        return result;
    }   
    float getVisitTimeCost(int CurrentPOI,int TourIndex,int VisitIndex,int IndexOfLastVisit,
            int PreviousPoint,int NextPoint,clsVisit Itinerary[][],clsGroupData GroupData){
        float result;
        float TourUnusedTimeAtTheStartOfTour=this.getTourUnusedTimeAtTheStartOfTour(CurrentPOI, VisitIndex, 
                GroupData.NumberOfPersons,GroupData.StartPoint[5], GroupData.POI[CurrentPOI-2], GroupData.Distance);
        result=TourUnusedTimeAtTheStartOfTour+
               clsGeneral.getDistanceBetweenPoints(PreviousPoint, CurrentPOI, GroupData.Distance)+
               GroupData.POI[CurrentPOI-2][3]+
               clsGeneral.getDistanceBetweenPoints(CurrentPOI, NextPoint, GroupData.Distance);
        
        
        return result;
    }
    float getTourUnusedTimeAtTheStartOfTour(int POI_ID,int VisitIndex,int NumberOfPersons,
            float StartOfTheTour,float POI[],float Distance[][]){
        float result=0;
        if(VisitIndex==0){
            float DistanceFromStartPointToCurrentPOI=clsGeneral.getDistanceBetweenPoints(1,POI_ID,Distance);
            float OpeningTimeOfPOI=POI[NumberOfPersons+4];
            if(DistanceFromStartPointToCurrentPOI<OpeningTimeOfPOI)
                result=OpeningTimeOfPOI-DistanceFromStartPointToCurrentPOI-StartOfTheTour;
        }       
        return result;
    }
    boolean IsCostConstraintValid(float PersonBudget,float InsertedPOI[],float AvoidedPOI[],
            float CurrentCost,int NumberOfPersons){
        boolean result=CurrentCost+InsertedPOI[10+NumberOfPersons]-AvoidedPOI[10+NumberOfPersons]<=PersonBudget;
        return result;
    }
    boolean IsPOICategoryConstraintValid(float Person[],int TypeConstraintCounter[],float InsertedPOI[],
            float AvoidedPOI[],int NumberOfPersons){
        boolean result=true;
        for(int i=0;i<10;i++){
            result=(TypeConstraintCounter[i]+InsertedPOI[11+NumberOfPersons+i]-AvoidedPOI[11+NumberOfPersons+i]
                    <=Person[2+2*(NumberOfPersons-1)+i]);
            if(!result){
                return false;
            }
        }
        return result;
    }
    float getPreviousPointEndTime(clsGroupSolution GroupSolution,int PersonID,int TourIndex,int VisitIndex){
        float result;
        if(VisitIndex==0){
            result=0;
        }
        else
        {
            result=GroupSolution.Solutions[PersonID].Itinerary[TourIndex][VisitIndex-1].constraints.TimeWindow.EndTime;
        }
        return result;
    }
    
    ArrayList<ArrayList<Integer>> getTouristSubgroupsAtATourPosition(clsSolution Solutions [],int TourIndex,int VisitIndex){
        int [][] PersonPOIMatrix=this.getPersonPOIMatrix(Solutions, TourIndex, VisitIndex);
        this.sortPersonPOIMatrix(PersonPOIMatrix);
        ArrayList<ArrayList<Integer>> result=new ArrayList();
        int CurrentTouristPOI=PersonPOIMatrix[0][1];
        ArrayList<Integer> CurrentGroup=new ArrayList();
        CurrentGroup.add(PersonPOIMatrix[0][0]);
        for(int i=1;i<PersonPOIMatrix.length;i++){
            if(PersonPOIMatrix[i][1]==CurrentTouristPOI){
                CurrentGroup.add(PersonPOIMatrix[i][0]);
            }
            else
            {
                result.add(CurrentGroup);
                CurrentGroup=new ArrayList();
                CurrentTouristPOI=PersonPOIMatrix[i][1];
                CurrentGroup.add(PersonPOIMatrix[i][0]);
            }
        }
        result.add(CurrentGroup);
        return result;
    }
    int [][] getPersonPOIMatrix(clsSolution Solutions [],int TourIndex,int VisitIndex){
        int [][] result=new int[Solutions.length][2];
        for(int i=0;i<result.length;i++){
            result[i][0]=i;
            result[i][1]=Solutions[i].Itinerary[TourIndex][VisitIndex].POI_ID;
        }
        return result;
    }
    void sortPersonPOIMatrix(int [][] PersonPOIMatrix){
        for(int i=0;i<PersonPOIMatrix.length-1;i++){
            for(int j=i;j<PersonPOIMatrix.length;j++){
                if(PersonPOIMatrix[i][1]>PersonPOIMatrix[j][1]){
                    int PersonSpareVariable=PersonPOIMatrix[i][0];
                    int POISpareVariable=PersonPOIMatrix[i][1];
                    
                    PersonPOIMatrix[i][0]=PersonPOIMatrix[j][0];
                    PersonPOIMatrix[i][1]=PersonPOIMatrix[j][1];
                    
                    PersonPOIMatrix[j][0]=PersonSpareVariable;
                    PersonPOIMatrix[j][1]=POISpareVariable;
                }
            }
        }
    }
//    ArrayList<ArrayList<Integer>> getTouristSubgroupsAtATourPosition(clsSolution Solutions [],int TourIndex,int VisitIndex){
//         ArrayList<ArrayList<Integer>> result=new ArrayList();
//         int CurrentTouristPOI=Solutions[0].Itinerary[TourIndex][VisitIndex].POI_ID;
//         ArrayList<Integer> CurrentGroup=new ArrayList();
//         CurrentGroup.add(0);
//         for(int i=1;i<Solutions.length;i++){
//             if(Solutions[i].Itinerary[TourIndex][VisitIndex].POI_ID==CurrentTouristPOI){
//                 CurrentGroup.add(i);
//             }
//             else
//             {
//                 result.add(CurrentGroup);
//                 CurrentGroup=new ArrayList();
//                 CurrentTouristPOI=Solutions[i].Itinerary[TourIndex][VisitIndex].POI_ID;
//                 CurrentGroup.add(i);
//             }
//         }
//         result.add(CurrentGroup);
//         return result;
//     }
    
     int getPreviousPoint(clsVisit Itinerary[][], int TourIndex, int VisitIndex){
         int result;
         if(VisitIndex==0)
             result=1;
         else
             result=Itinerary[TourIndex][VisitIndex-1].POI_ID;
         return result;
     }
     int getNextPoint(clsVisit Itinerary[][], int TourIndex, int VisitIndex, int IndexOfLastVisit){
         int result;
         if(VisitIndex==IndexOfLastVisit)
             result=1;
         else
             result= Itinerary[TourIndex][VisitIndex+1].POI_ID;
         return result;
    }
     float getTimeLimitForChangeOfVisit(clsGroupSolution GroupSolution,int PersonID, int TourIndex, 
             int VisitIndex, int IndexOfLastVisit,float TourLength){
         float result;
         result=GroupSolution.Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint+
                 GroupSolution.Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.WaitingTime+
                (GroupSolution.Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.EndTime-
                GroupSolution.Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.StartTime)+
                GroupSolution.Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceToNextPoint;
         if(VisitIndex<IndexOfLastVisit)
         {
             result+=GroupSolution.Solutions[PersonID].Itinerary[TourIndex][VisitIndex+1].constraints.TimeWindow.WaitingTime;
             result+=GroupSolution.Solutions[PersonID].MaxShift[TourIndex][0][VisitIndex+1];
         }
         else
             result+=(TourLength-GroupSolution.Solutions[PersonID].TourTimeSpent[TourIndex]);
         return result;
     }
     
    ArrayList<Integer> getListOfOpenPOIsOnRouteBetweenTwoPOIs(int VisitIndex,int PreviousPoint,int NextPoint,
            float TimeLimit, float StartOfTheTour,float PreviousPointEndTime, float POI[][], float Distance[][], 
            ArrayList <Integer> POIsOffItinerary,int NumberOfPersons){
        //te shiqohet nese madhesia tourunusedtimeatthesatart eshte perdorur ne menyre te drejte
        ArrayList<Integer> result=new ArrayList();
        for(int i=0;i<POIsOffItinerary.size();i++){
            float NeededTime=0;
            if(PreviousPoint==1){
                float TourUnusedTimeAtTheStartOfTour=
                this.getTourUnusedTimeAtTheStartOfTour(POIsOffItinerary.get(i), VisitIndex, NumberOfPersons,StartOfTheTour , 
                        POI[POIsOffItinerary.get(i)-2], Distance);
                NeededTime += TourUnusedTimeAtTheStartOfTour+clsGeneral.getDistanceBetweenPoints(1, POIsOffItinerary.get(i), Distance);
            }
            else
            {
                NeededTime +=clsGeneral.getDistanceBetweenPoints(PreviousPoint, POIsOffItinerary.get(i), Distance);
            }
            if(PreviousPointEndTime+NeededTime<POI[POIsOffItinerary.get(i)-2][4+NumberOfPersons]){//Chechking the opening of time window
                continue;
            }
            NeededTime += POI[POIsOffItinerary.get(i)-2][3];
            if(PreviousPointEndTime+NeededTime>POI[POIsOffItinerary.get(i)-2][8+NumberOfPersons]){//Chechking the closing of time window
                continue;
            }            
            NeededTime += clsGeneral.getDistanceBetweenPoints(POIsOffItinerary.get(i),NextPoint, Distance);
            if(NeededTime<=TimeLimit){
                result.add(POIsOffItinerary.get(i));
            }
        }
        return result;
    }
    
//    ArrayList<int []> [] getListOfUnsatisfiedTouristsInTrip( clsSolution Trip,clsGroupData GroupData){
//         ArrayList<int[]> [] result=new ArrayList[Trip.Itinerary.length];
//         for(int TourIndex=0;TourIndex<result.length;TourIndex++){
//             ArrayList<int []> ListOfUnsatisfiedTouristsInTour=
//                     this.getListOfUnsatisfiedTouristsInTour(Trip.Itinerary[TourIndex], Trip.TourLastVisit[1][TourIndex], GroupData);
//             result[TourIndex]=ListOfUnsatisfiedTouristsInTour;
//         }
//         return result;
//    }
    
//     ArrayList<int []> getListOfUnsatisfiedTouristsInTour( clsVisit  TourItinerary [],int  TourLastVisitIndex,clsGroupData GroupData){
//         ArrayList<int[]> result=new ArrayList();
//         for(int TourIndex=0;TourIndex<=TourLastVisitIndex;TourIndex++){
//             int [] LeastSatisfiedTouristsInPOI=
//                     this.getLeastSatisfiedTouristsInPOI(TourItinerary[TourIndex].POI_ID, GroupData.NumberOfPersons, GroupData);
//             result.add(LeastSatisfiedTouristsInPOI);
//         }
//         return result;
//    }
     
    ArrayList<Integer> getNotMaximallySatisfiedTouristsInPOI(int POIID,ArrayList<Integer> TouristSubgroups,clsGroupData GroupData){
        ArrayList<Integer> result=new ArrayList();
        float TouristSatisfaction[][]=new float[2][TouristSubgroups.size()];
        for(int i=0;i<TouristSubgroups.size();i++){
            TouristSatisfaction[0][i]=TouristSubgroups.get(i);
            TouristSatisfaction[1][i]=GroupData.POI[POIID-2][4+i];
        }
        this.SortTouristBasedOnPOISatisfaction(TouristSatisfaction);
        float MaxSatisfaction=TouristSatisfaction[1][TouristSubgroups.size()-1];
        for(int i=TouristSubgroups.size()-2;i>=0;i--){
            if(TouristSatisfaction[1][i]<MaxSatisfaction){
                result.add(0, Math.round(TouristSatisfaction[0][i]));
    //                result.add(0, TouristSubgroups.get(Math.round(TouristSatisfaction[0][i])));
            }
        }
        return result;
    }
     
    
//    int [] getLeastSatisfiedTouristsInPOI(int AvoidedPOI,int NumberOfPersons,clsGroupData GroupData){
//        int [] result=new int[SizeOfListOfLeastSatisfiedTourists];
//        float TouristSatisfaction[][]=new float[2][NumberOfPersons];
//        for(int TourIndex=0;TourIndex<NumberOfPersons;TourIndex++){
//            TouristSatisfaction[0][TourIndex]=TourIndex;
//            TouristSatisfaction[1][TourIndex]=GroupData.InsertedPOI[AvoidedPOI-2][4+TourIndex];
//        }
//        this.SortTouristBasedOnPOISatisfaction(TouristSatisfaction);
//        for(int TourIndex=0;TourIndex<result.length;TourIndex++){
//            result[TourIndex]=Math.round(TouristSatisfaction[0][TourIndex]);
//        }
//        return result;
//    }
//    
    void SortTouristBasedOnPOISatisfaction(float TouristSatisfaction[][]){
        for(int i=0;i<TouristSatisfaction[0].length-1;i++){
            for(int j=i+1;j<TouristSatisfaction[0].length;j++){
                if(TouristSatisfaction[1][i]>TouristSatisfaction[1][j])
                {
                    float Index=TouristSatisfaction[0][i];
                    float Value=TouristSatisfaction[1][i];
                    
                    TouristSatisfaction[0][i]=TouristSatisfaction[0][j];
                    TouristSatisfaction[1][i]=TouristSatisfaction[1][j];
                    
                    TouristSatisfaction[0][j]=Index;
                    TouristSatisfaction[1][j]=Value;
                }
            }
        }
    }
    
    int getOtherSubgoupAlreadyAssignedToPOI(ArrayList<ArrayList<Integer>> TouristSubgroupsAtATourPosition,int SubGroupIndex,
                                            int MostSuitablePOI,int TourIndex,int VisitIndex,clsGroupSolution GroupSolution){
        int result=-1;
        for(int i=0;i<TouristSubgroupsAtATourPosition.size();i++){
            if(i!=SubGroupIndex){
                for(int j=0;j<TouristSubgroupsAtATourPosition.get(i).size();j++){
                    ArrayList<Integer> CurrentSubGroup=TouristSubgroupsAtATourPosition.get(i);
                    int CurrentPerson=CurrentSubGroup.get(j);
                    int CurrentPersonPOI= GroupSolution.Solutions[CurrentPerson].Itinerary[TourIndex][VisitIndex].POI_ID;
                    if(CurrentPersonPOI==MostSuitablePOI){
                        result=i;
                        return result;
                    }
                }
            }
        }
        return result;
    }

}
