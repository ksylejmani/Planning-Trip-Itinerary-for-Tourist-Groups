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
public class clsAdaptSeperate {
    
    clsSeperate Seperate=new clsSeperate();
    clsTest_MC_M_TOP_TW Test=new clsTest_MC_M_TOP_TW();
    
    void Apply(int PersonID, int MostSuitablePOI,int TourIndex,int VisitIndex,
            clsGroupSolution GroupSolution,clsGroupData GroupData, ArrayList<ArrayList<Integer>> TouristSubgroupsAtATourPosition,
            int SubGroupIndex){
        ArrayList<Integer> TouristSubGroup= TouristSubgroupsAtATourPosition.get(SubGroupIndex);
        int IndexOfLastVisit=GroupSolution.Solutions[PersonID].TourLastVisit[1][TourIndex];

        int PreviousPoint=Seperate.getPreviousPoint(GroupSolution.Solutions[PersonID].Itinerary, TourIndex, VisitIndex);
        int NextPoint=Seperate.getNextPoint(GroupSolution.Solutions[PersonID].Itinerary, TourIndex, VisitIndex, IndexOfLastVisit);
        int AvoidedPOIID=GroupSolution.Solutions[PersonID].Itinerary[TourIndex][VisitIndex].POI_ID;
        ArrayList <Integer> POIsOffItinerary=GroupSolution.Solutions[PersonID].POIsOffItinerary;
        clsVisit  Itinerary []=GroupSolution.Solutions[PersonID].Itinerary[TourIndex];
        clsVisit NewVisit=this.getNewVisit(PreviousPoint, NextPoint, MostSuitablePOI, 
                POIsOffItinerary, PersonID, TourIndex, VisitIndex, GroupData, Itinerary);
        Itinerary[VisitIndex]=NewVisit;

        float WaitingTime=this.getWaitingTime(GroupSolution.Solutions, PersonID, TourIndex, VisitIndex, IndexOfLastVisit);       
        this.UpdateTourTimeSpent(WaitingTime, PersonID, TourIndex, VisitIndex, IndexOfLastVisit, GroupSolution.Solutions, 
                TouristSubgroupsAtATourPosition, GroupData);
        this.updateWaitingTime(WaitingTime, GroupSolution.Solutions, PersonID, TourIndex, VisitIndex, 
                IndexOfLastVisit, TouristSubgroupsAtATourPosition);   
        this.updateAffectedPOIs(PreviousPoint,NextPoint,WaitingTime, GroupSolution.Solutions, PersonID, TourIndex, VisitIndex, 
                IndexOfLastVisit, MostSuitablePOI, GroupData.Distance);

        this.updateMaxShift(WaitingTime, PersonID, TourIndex, GroupSolution.Solutions, GroupData, TouristSubGroup);
        GroupSolution.Solutions[PersonID].FlatItinerary[TourIndex][VisitIndex]=MostSuitablePOI;
        GroupSolution.Solutions[PersonID].BudgetCost=GroupSolution.Solutions[PersonID].BudgetCost+
                GroupData.POI[MostSuitablePOI-2][GroupData.NumberOfPersons+10]-
                GroupData.POI[AvoidedPOIID-2][GroupData.NumberOfPersons+10];
        GroupSolution.Solutions[PersonID].Evaluation=this.getUpdatedEvaluationForSeperatedPerson(PersonID, 
                GroupSolution.Solutions[PersonID].Evaluation,GroupData.Person, 
                GroupData.POI[MostSuitablePOI-2],GroupData.POI[AvoidedPOIID-2],TouristSubgroupsAtATourPosition,
                SubGroupIndex,TourIndex,VisitIndex,GroupSolution);
        this.UpdateEvaluationForNotSeperatedPersons(GroupSolution.Solutions, PersonID, GroupData.Person, TouristSubGroup);
//        this.updateGroupSolutionEvaluation(GroupSolution, OldEvaluationForSeperatedTourist, GroupSolution.Solutions[PersonID].Evaluation);
        this.updateGroupSolutionEvaluation(GroupSolution);
        this.updateTripTimeSpent(GroupSolution.Solutions, PersonID, WaitingTime, TouristSubGroup);
        this.updateTourLastVisit(VisitIndex, TourIndex, MostSuitablePOI, GroupSolution.Solutions[PersonID].TourLastVisit);
        GroupSolution.Solutions[PersonID].POIsInItinerary.remove(GroupSolution.Solutions[PersonID].POIsInItinerary.indexOf(AvoidedPOIID));
        GroupSolution.Solutions[PersonID].POIsInItinerary.add(MostSuitablePOI);
        GroupSolution.Solutions[PersonID].POIsOffItinerary.remove(GroupSolution.Solutions[PersonID].POIsOffItinerary.indexOf(MostSuitablePOI));
        GroupSolution.Solutions[PersonID].POIsOffItinerary.add(AvoidedPOIID);
        this.updateTypeConstraintCounter(GroupData.Person[PersonID], GroupSolution.Solutions[PersonID].TypeConstraintCounter, 
                GroupData.POI[MostSuitablePOI-2], GroupData.POI[AvoidedPOIID-2], GroupData.NumberOfPersons);
        this.updateTouristSubgroupsAtATourPosition(TouristSubgroupsAtATourPosition, 
                PersonID, SubGroupIndex,MostSuitablePOI,TourIndex,VisitIndex,GroupSolution);
             
        //Test
        clsTest_MC_M_TOP_TW Test=new clsTest_MC_M_TOP_TW();
        Test.TestSolution(GroupData, GroupSolution, TouristSubGroup);
        //Test
    }
//    void updateWaitingTimeXXX(float VisitTimeCostChange,clsSolution [] Solutions, int PersonID,int TourIndex, int VisitIndex,
//                          int IndexOfLastVisit,ArrayList<Integer> TouristSubGroup){
//        if(VisitIndex<IndexOfLastVisit){
//            if(VisitTimeCostChange<0){
//                Solutions[PersonID].Itinerary[TourIndex][VisitIndex+1].constraints.TimeWindow.WaitingTime=(-VisitTimeCostChange);
//                System.out.println("Negative");
//            }
//            else if(VisitTimeCostChange>0)
//            {
//                for(int i=0;i<TouristSubGroup.size();i++){
//                    if(TouristSubGroup.get(i)!=PersonID){
//                        if(Solutions[TouristSubGroup.get(i)].Itinerary[TourIndex][VisitIndex+1].constraints.TimeWindow.WaitingTime <
//                                VisitTimeCostChange )
//                            Solutions[TouristSubGroup.get(i)].Itinerary[TourIndex][VisitIndex+1].constraints.TimeWindow.WaitingTime
//                                = VisitTimeCostChange;
//                    }
//                }
//            } 
//        }
//    }
    // **** Me ka nxjerre mjaft pune per t'i zbuluar gabimet - 3 dite pune
    void updateWaitingTime(float WaitingTime, clsSolution [] Solutions, 
                              int PersonID,int TourIndex, int VisitIndex,
                              int IndexOfLastVisit,ArrayList<ArrayList<Integer>> TouristSubgroupsAtATourPosition){
        if(VisitIndex<IndexOfLastVisit){
            if(WaitingTime<=0){
                Solutions[PersonID].Itinerary[TourIndex][VisitIndex+1].constraints.TimeWindow.WaitingTime=(-WaitingTime);
            }
            else //if(WaitingTime>0)
            {
                if(Solutions[PersonID].Itinerary[TourIndex][VisitIndex+1].constraints.TimeWindow.WaitingTime>0){
                    Solutions[PersonID].Itinerary[TourIndex][VisitIndex+1].constraints.TimeWindow.WaitingTime=0;
                }
                for(int i=0;i<TouristSubgroupsAtATourPosition.size();i++){
                    ArrayList<Integer> TouristSubGroup=TouristSubgroupsAtATourPosition.get(i);
                    for(int j=0;j<TouristSubGroup.size();j++){
                        if(TouristSubGroup.get(j)!=PersonID){
                            Solutions[TouristSubGroup.get(j)].Itinerary[TourIndex][VisitIndex+1].constraints.TimeWindow.WaitingTime
                                +=(WaitingTime); // Other persons have to wait until current person finishes his visit
                        }                        
                    }
                }
            } 
        }
    } 
    float getWaitingTime(clsSolution [] Solutions,int PersonID,int TourIndex, int VisitIndex,int IndexOfLastVisit){
        float WaitingTime=0;       
        if(VisitIndex<IndexOfLastVisit){
            WaitingTime = 
                    (Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.EndTime
                     + Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceToNextPoint)
                    -
                    Solutions[PersonID].Itinerary[TourIndex][VisitIndex+1].constraints.TimeWindow.StartTime;
        }
        return WaitingTime;
    }
    
//    float getVisitTimeCost(int CurrentPOI,int TourIndex,int VisitIndex,int IndexOfLastVisit,clsVisit Itinerary[][],clsGroupData GroupData){
//        float result;
//        int PreviousPoint=Seperate.getPreviousPoint(Itinerary, TourIndex, VisitIndex);
//        int NextPoint=Seperate.getNextPoint(Itinerary, TourIndex, VisitIndex, IndexOfLastVisit);
//        float TourUnusedTimeAtTheStartOfTour=Seperate.getTourUnusedTimeAtTheStartOfTour(CurrentPOI, VisitIndex, 
//                GroupData.NumberOfPersons,GroupData.StartPoint[5], GroupData.POI[CurrentPOI-2], GroupData.Distance);
//        result=TourUnusedTimeAtTheStartOfTour+
//               clsGeneral.getDistanceBetweenPoints(PreviousPoint, CurrentPOI, GroupData.Distance)+
//               GroupData.POI[CurrentPOI-2][3]+
//               clsGeneral.getDistanceBetweenPoints(CurrentPOI, NextPoint, GroupData.Distance);
//        
//        return result;
//    }

//     float getVistTimeCostChange(clsSolution [] Solutions, int PersonID,int TourIndex, int VisitIndex,
//                          int IndexOfLastVisit,int MostSuitablePOI,clsGroupData GroupData,
//                          ArrayList<Integer> TouristSubGroup) {
//        float NewVisitTimeCost=this.getVisitTimeCost(MostSuitablePOI, TourIndex, VisitIndex,IndexOfLastVisit, 
//                                                    Solutions[PersonID].Itinerary, GroupData);
//        float OldVisitTimeCost=
//              Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour
//            + Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint
//            +(Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.EndTime - 
//                Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.StartTime)
//            + Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceToNextPoint;
//       float VistTimeCostChange=NewVisitTimeCost-OldVisitTimeCost;
//       return VistTimeCostChange;
//    }
    void UpdateTourTimeSpent(float WaitingTime,int PersonID,int TourIndex,int VisitIndex,
                             int IndexOfLastVisit, clsSolution[] Solutions,
                             ArrayList<ArrayList<Integer>> TouristSubgroupsAtATourPosition, clsGroupData GroupData){
        if(VisitIndex==IndexOfLastVisit){
            Solutions[PersonID].TourTimeSpent[TourIndex] += ( WaitingTime );
            //There is no need to update the TourTimeSpent of other Persons
        }
        else
        {
            if(WaitingTime>0){
                for(int i=0;i<TouristSubgroupsAtATourPosition.size();i++){
                    ArrayList<Integer> TouristSubGroup= TouristSubgroupsAtATourPosition.get(i);
                    for(int j=0;j<TouristSubGroup.size();j++){
                        Solutions[TouristSubGroup.get(j)].TourTimeSpent[TourIndex] += ( WaitingTime );
                    }
                } 
            }
       }
    }
    
    void updateAffectedPOIs(int PreviousPoint,int NextPoint,float WaitingTime,clsSolution [] Solutions, 
                            int PersonID,int TourIndex, int VisitIndex,
                            int IndexOfLastVisit,int MostSuitablePOI,
                            float Distance[][]){
        //Update Start time and End time of following POIs
        if(VisitIndex!=IndexOfLastVisit){
            if(WaitingTime>0){
                for(int i=0;i<Solutions.length;i++){
                    for(int j=VisitIndex+1;j<=Solutions[i].TourLastVisit[1][TourIndex];j++){
                        Solutions[i].Itinerary[TourIndex][j].constraints.TimeWindow.StartTime+=(WaitingTime);
                        Solutions[i].Itinerary[TourIndex][j].constraints.TimeWindow.EndTime+=(WaitingTime); 
                    }
                }
            }          
        }
        
        //Update distance to next point of previous point and distance to previous point of next point...
        if(VisitIndex>0)
            Solutions[PersonID].Itinerary[TourIndex][VisitIndex-1].
            constraints.TimeWindow.DistanceToNextPoint=clsGeneral.getDistanceBetweenPoints(PreviousPoint, MostSuitablePOI, Distance);
        if(VisitIndex<IndexOfLastVisit)
            Solutions[PersonID].Itinerary[TourIndex][VisitIndex+1].
                    constraints.TimeWindow.DistanceFromPrevioiusPoint=
                    clsGeneral.getDistanceBetweenPoints(MostSuitablePOI, NextPoint, Distance);
    }
     clsVisit getNewVisit(int PreviousPoint,int NextPoint,int MostSuitablePOI,ArrayList <Integer> POIsOffItinerary,
             int PersonID,int TourIndex, int VisitIndex, clsGroupData GroupData,clsVisit  Itinerary []) {
        clsVisit result = new clsVisit();
        result.POI_ID = MostSuitablePOI;
       // result.RemoveIndexInOffList = POIsOffItinerary.indexOf(MostSuitablePOI); Kjo komande eshte realizuar ne metoden Apply
        //Anetaret Replay nuk jane caktuar

        clsConstraint Constraint = new clsConstraint();
        clsTimeWindowConstraint TimeWindow = new clsTimeWindowConstraint();
        TimeWindow.UnusedTimeAtTheStartOfTheTour=
                Seperate.getTourUnusedTimeAtTheStartOfTour(MostSuitablePOI, VisitIndex, GroupData.NumberOfPersons,
                GroupData.StartPoint[5],GroupData.POI[MostSuitablePOI-2], GroupData.Distance);
        TimeWindow.DistanceFromPrevioiusPoint = 
                clsGeneral.getDistanceBetweenPoints(PreviousPoint, MostSuitablePOI, GroupData.Distance);
        TimeWindow.DistanceToNextPoint =
                clsGeneral.getDistanceBetweenPoints(MostSuitablePOI, NextPoint, GroupData.Distance);
        TimeWindow.StartTime = this.getVisitStartTime(TimeWindow.UnusedTimeAtTheStartOfTheTour, 
                                                       TimeWindow.DistanceFromPrevioiusPoint,
                                                       VisitIndex, Itinerary);
        TimeWindow.EndTime =  TimeWindow.StartTime+GroupData.POI[MostSuitablePOI-2][3];
        TimeWindow.TourIndeks = TourIndex;
        TimeWindow.VisitIndeks =VisitIndex;
        Constraint.TimeWindow = TimeWindow;
        result.constraints = Constraint;
        return result;
    }
     
     float getVisitStartTime(float UnusedTimeAtTheStartOfTheTour,float DistanceFromPrevioiusPoint,int VisitIndex,
             clsVisit  Itinerary []){
         float result;
         if(VisitIndex==0){
             result=UnusedTimeAtTheStartOfTheTour+DistanceFromPrevioiusPoint;
         }
         else
         {
             result= Itinerary[VisitIndex-1].constraints.TimeWindow.EndTime+DistanceFromPrevioiusPoint;
         }
         return result;
     }
     
    void updateMaxShift(float WaitingTime,int PersonID,int TourIndex,
                        clsSolution[] Solutions,clsGroupData GroupData,ArrayList<Integer> TouristSubGroup){
        this.updateTourMaxShift(GroupData.POI, TourIndex, Solutions[PersonID].TourLastVisit,
                Solutions[PersonID].Itinerary, Solutions[PersonID].MaxShift, GroupData.NumberOfPersons);
        if(WaitingTime>0){
            for(int i=0;i<TouristSubGroup.size();i++){
                int CurrentPersonID=TouristSubGroup.get(i);
                if(CurrentPersonID!=PersonID){
                    this.updateTourMaxShift(GroupData.POI, TourIndex, Solutions[CurrentPersonID].TourLastVisit,
                    Solutions[CurrentPersonID].Itinerary, Solutions[CurrentPersonID].MaxShift, GroupData.NumberOfPersons);
                }
            }
        }
    }
     
    void updateTourMaxShift(float POIs[][],int TourIndex, int TourLastVisit[][], 
        clsVisit Itinerary[][], float MaxShift[][][],int NumberOfPersons) {
            int LastVistPOIID=TourLastVisit[0][TourIndex];
            int LastVistPOIIndex=TourLastVisit[1][TourIndex];
            MaxShift[TourIndex][0][LastVistPOIIndex]=
                    this.getMaxRightShift(POIs[LastVistPOIID-2],
                    Itinerary[TourIndex][LastVistPOIIndex].constraints.TimeWindow.EndTime,NumberOfPersons);
            MaxShift[TourIndex][1][LastVistPOIIndex]=
                    this.getMaxLeftShift(POIs[LastVistPOIID-2],
                    Itinerary[TourIndex][LastVistPOIIndex].constraints.TimeWindow.StartTime,NumberOfPersons);
            
            for(int i=LastVistPOIIndex-1;i>=0;i--){
                 float CurrentVisitMaxRightShift;
                 float CurrentVisitMaxLeftShift;
                 CurrentVisitMaxRightShift= this.getMaxRightShift(POIs[Itinerary[TourIndex][i].POI_ID-2],
                            Itinerary[TourIndex][i].constraints.TimeWindow.EndTime,NumberOfPersons);
                 CurrentVisitMaxLeftShift= this.getMaxLeftShift(POIs[Itinerary[TourIndex][i].POI_ID-2],
                        Itinerary[TourIndex][i].constraints.TimeWindow.StartTime,NumberOfPersons);
                if(CurrentVisitMaxRightShift< MaxShift[TourIndex][0][i+1]){
                    MaxShift[TourIndex][0][i]=CurrentVisitMaxRightShift;
                }
                else{
                     MaxShift[TourIndex][0][i]=MaxShift[TourIndex][0][i+1];                    
                }
                
                if(CurrentVisitMaxLeftShift<MaxShift[TourIndex][1][i+1]){
                    MaxShift[TourIndex][1][i]=CurrentVisitMaxLeftShift;
                }
                else{
                    MaxShift[TourIndex][1][i]=MaxShift[TourIndex][1][i+1];
                } 
               
            }
    } 
   float getMaxRightShift(float POI[], float VisitEndTime,int NumberOfPersons) {
        float result;
        result = POI[8 + NumberOfPersons] - VisitEndTime;
        return result;
    }
   float  getMaxLeftShift(float POI[], float VisitStartTime,int NumberOfPersons) {
        float result;
        result = VisitStartTime-POI[4 + NumberOfPersons] ;
        return result;
    }
   void updateTripTimeSpent(clsSolution[] Solutions,int PersonID,float WaitingTime,
                            ArrayList<Integer> TouristSubGroup){
        if(WaitingTime>0){
            for(int i=0;i<TouristSubGroup.size();i++){
                Solutions[TouristSubGroup.get(i)].TripTimeCost += ( WaitingTime );
            }
        }
    }
   void updateTourLastVisit(int VisitIndex, int TourIndex, int InsertedPOI_ID, int[][] TourLastVisit) {
        if (VisitIndex == TourLastVisit[1][TourIndex]) {
            TourLastVisit[0][TourIndex] = InsertedPOI_ID;
        }
    }
    void updateTypeConstraintCounter(float Person[],int TypeConstraintCounter[],float InsertedPOI[],
            float AvoidedPOI[],int NumberOfPersons){
        for(int i=0;i<10;i++){
            TypeConstraintCounter[i]+=(InsertedPOI[11+NumberOfPersons+i]-AvoidedPOI[11+NumberOfPersons+i]);
        }
    }
    
//    void updateGroupSolutionEvaluation(clsGroupSolution GroupSolution,
//            float OldEvaluationForSeperatedTourist,float NewEvaluationForSeperatedTourist){
//        GroupSolution.Evaluation+=(NewEvaluationForSeperatedTourist-OldEvaluationForSeperatedTourist);
//    }
    void updateGroupSolutionEvaluation(clsGroupSolution GroupSolution){
       GroupSolution.Evaluation=0;
       for(int i=0;i<GroupSolution.Solutions.length;i++){
           GroupSolution.Evaluation += GroupSolution.Solutions[i].Evaluation;
       }
    }

    float getUpdatedEvaluationForSeperatedPerson(int PersonID,float CurrentEvaluation,float Persons[][],float InsertedPOI[],
                                                  float AvoidedPOI[],ArrayList<ArrayList<Integer>> TouristSubgroupsAtATourPosition,
                                                  int SubGroupIndex, int TourIndex, int VisitIndex, clsGroupSolution GroupSolution){
        float result;
        ArrayList<Integer> CurrentTouristSubgroup= TouristSubgroupsAtATourPosition.get(SubGroupIndex);
        float poi_score_change=InsertedPOI[4+PersonID]-AvoidedPOI[4+PersonID];//Satisfaction with POIs

        //Satisfaction for social relationship with other persons
        float social_relationship_score_change=0;
        for(int i=0;i<CurrentTouristSubgroup.size();i++){
            if(PersonID != CurrentTouristSubgroup.get(i)){
                social_relationship_score_change -= 
                    Seperate.getSocialRelationshipBetweenPersons(
                    PersonID, CurrentTouristSubgroup.get(i), Persons); 
            }
        }
        float join_social_relationship_score_change=0;
        int TheOtherSubgoupAlreadyAssignedToPOI=
            Seperate.getOtherSubgoupAlreadyAssignedToPOI(TouristSubgroupsAtATourPosition, 
            SubGroupIndex,(int)InsertedPOI[0],TourIndex, VisitIndex, GroupSolution);
        if(TheOtherSubgoupAlreadyAssignedToPOI!=-1){
            CurrentTouristSubgroup=TouristSubgroupsAtATourPosition.get(TheOtherSubgoupAlreadyAssignedToPOI);
            join_social_relationship_score_change=
                Seperate.getSocialRelationshipBetweenPersonAndSubGroup(PersonID, CurrentTouristSubgroup, Persons);
        }
        
        result=CurrentEvaluation+poi_score_change+social_relationship_score_change+
                join_social_relationship_score_change;
        return result;
    }
    void  UpdateEvaluationForNotSeperatedPersons(clsSolution [] Solutions,int PersonID,
                                                  float Persons[][], ArrayList<Integer> CurrentTouristSubgroup){
        for(int i=0;i<CurrentTouristSubgroup.size();i++){
            for(int j=0;j<CurrentTouristSubgroup.size();j++){
                if(i!=j){
                    //CurrentTouristSubgroup.get(i)!=PersonID && CurrentTouristSubgroup.get(j)==PersonID
                    if(CurrentTouristSubgroup.get(j)==PersonID){
                        Solutions[CurrentTouristSubgroup.get(i)].Evaluation -= 
                        Seperate.getSocialRelationshipBetweenPersons(CurrentTouristSubgroup.get(i),PersonID, Persons); 
                    } 
                }
            }
        }
    }
    
    void updateTouristSubgroupsAtATourPosition (ArrayList<ArrayList<Integer>> TouristSubgroupsAtATourPosition,
                                                int PersonID,  int SubGroupIndex, int InsertedPOI,int TourIndex,
                                                int VisitIndex, clsGroupSolution GroupSolution){
        
        int TheOtherSubgoupAlreadyAssignedToPOI=
            Seperate.getOtherSubgoupAlreadyAssignedToPOI(TouristSubgroupsAtATourPosition, 
            SubGroupIndex,InsertedPOI,TourIndex, VisitIndex, GroupSolution);
        if(TheOtherSubgoupAlreadyAssignedToPOI!=-1){
            TouristSubgroupsAtATourPosition.get(TheOtherSubgoupAlreadyAssignedToPOI).add(PersonID);
        }
        else
        {
            ArrayList<Integer> NewSubGroup=new ArrayList();
            NewSubGroup.add(PersonID);
            TouristSubgroupsAtATourPosition.add(NewSubGroup); 
        }       
        int IndexOfPersonForRemoval= TouristSubgroupsAtATourPosition.get(SubGroupIndex).indexOf(PersonID);
        TouristSubgroupsAtATourPosition.get(SubGroupIndex).remove(IndexOfPersonForRemoval);
    }
}
