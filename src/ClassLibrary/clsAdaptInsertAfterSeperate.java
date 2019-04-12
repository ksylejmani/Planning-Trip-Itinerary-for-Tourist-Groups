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
public class clsAdaptInsertAfterSeperate {
    clsInsertAfterSeperate IAS=new clsInsertAfterSeperate();
    
    void Apply(int PersonID, int MostSuitablePOI,int TourIndex,int VisitIndex,
            clsGroupSolution GroupSolution,clsGroupData GroupData, ArrayList<ArrayList<Integer>> TouristSubgroupsAtATourPosition,
            int SubGroupIndex){
        ArrayList<Integer> TouristSubGroup= TouristSubgroupsAtATourPosition.get(SubGroupIndex);
        int IndexOfLastVisit=GroupSolution.Solutions[PersonID].TourLastVisit[1][TourIndex];

        int PreviousPoint=IAS.getPreviousPoint(GroupSolution.Solutions[PersonID].Itinerary, TourIndex, VisitIndex,IndexOfLastVisit);
        int NextPoint=IAS.getNextPoint(GroupSolution.Solutions[PersonID].Itinerary, TourIndex, VisitIndex, IndexOfLastVisit);
        ArrayList <Integer> POIsOffItinerary=GroupSolution.Solutions[PersonID].POIsOffItinerary;
        clsVisit  Itinerary []=GroupSolution.Solutions[PersonID].Itinerary[TourIndex];
        clsVisit NewVisit=this.getNewVisit(PreviousPoint, NextPoint, MostSuitablePOI, 
                POIsOffItinerary, PersonID, TourIndex, VisitIndex, GroupData, Itinerary);
        Itinerary[VisitIndex]=NewVisit;

//        float WaitingTime=this.getWaitingTime(GroupSolution.Solutions, PersonID, TourIndex, VisitIndex, IndexOfLastVisit);       
//        this.UpdateTourTimeSpent(WaitingTime, PersonID, TourIndex, VisitIndex, IndexOfLastVisit, GroupSolution.Solutions, 
//                TouristSubgroupsAtATourPosition, GroupData);
//        this.updateWaitingTime(WaitingTime, GroupSolution.Solutions, PersonID, TourIndex, VisitIndex, 
//                IndexOfLastVisit, TouristSubgroupsAtATourPosition);   
//        this.updateAffectedPOIs(PreviousPoint,NextPoint,WaitingTime, GroupSolution.Solutions, PersonID, TourIndex, VisitIndex, 
//                IndexOfLastVisit, MostSuitablePOI, GroupData.Distance);
//
//        this.updateMaxShift(WaitingTime, PersonID, TourIndex, GroupSolution.Solutions, GroupData, TouristSubGroup);
//        GroupSolution.Solutions[PersonID].FlatItinerary[TourIndex][VisitIndex]=MostSuitablePOI;
//        GroupSolution.Solutions[PersonID].BudgetCost=GroupSolution.Solutions[PersonID].BudgetCost+
//                GroupData.POI[MostSuitablePOI-2][GroupData.NumberOfPersons+10]-
//                GroupData.POI[AvoidedPOIID-2][GroupData.NumberOfPersons+10];
//        GroupSolution.Solutions[PersonID].Evaluation=this.getUpdatedEvaluationForSeperatedPerson(PersonID, 
//                GroupSolution.Solutions[PersonID].Evaluation,GroupData.Person, 
//                GroupData.POI[MostSuitablePOI-2],GroupData.POI[AvoidedPOIID-2],TouristSubgroupsAtATourPosition,
//                SubGroupIndex,TourIndex,VisitIndex,GroupSolution);
//        this.UpdateEvaluationForNotSeperatedPersons(GroupSolution.Solutions, PersonID, GroupData.Person, TouristSubGroup);
////        this.updateGroupSolutionEvaluation(GroupSolution, OldEvaluationForSeperatedTourist, GroupSolution.Solutions[PersonID].Evaluation);
//        this.updateGroupSolutionEvaluation(GroupSolution);
//        this.updateTripTimeSpent(GroupSolution.Solutions, PersonID, WaitingTime, TouristSubGroup);
//        this.updateTourLastVisit(VisitIndex, TourIndex, MostSuitablePOI, GroupSolution.Solutions[PersonID].TourLastVisit);
//        GroupSolution.Solutions[PersonID].POIsInItinerary.remove(GroupSolution.Solutions[PersonID].POIsInItinerary.indexOf(AvoidedPOIID));
//        GroupSolution.Solutions[PersonID].POIsInItinerary.add(MostSuitablePOI);
//        GroupSolution.Solutions[PersonID].POIsOffItinerary.remove(GroupSolution.Solutions[PersonID].POIsOffItinerary.indexOf(MostSuitablePOI));
//        GroupSolution.Solutions[PersonID].POIsOffItinerary.add(AvoidedPOIID);
//        this.updateTypeConstraintCounter(GroupData.Person[PersonID], GroupSolution.Solutions[PersonID].TypeConstraintCounter, 
//                GroupData.POI[MostSuitablePOI-2], GroupData.POI[AvoidedPOIID-2], GroupData.NumberOfPersons);
//        this.updateTouristSubgroupsAtATourPosition(TouristSubgroupsAtATourPosition, 
//                PersonID, SubGroupIndex,MostSuitablePOI,TourIndex,VisitIndex,GroupSolution);
             
        //Test
        clsTest_MC_M_TOP_TW Test=new clsTest_MC_M_TOP_TW();
        Test.TestSolution(GroupData, GroupSolution, TouristSubGroup);
        //Test
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
                IAS.getTourUnusedTimeAtTheStartOfTour(MostSuitablePOI, VisitIndex, GroupData.NumberOfPersons,
                GroupData.StartPoint[5],GroupData.POI[MostSuitablePOI-2], GroupData.Distance);
        TimeWindow.DistanceFromPrevioiusPoint = 
                clsGeneral.getDistanceBetweenPoints(PreviousPoint, MostSuitablePOI, GroupData.Distance);
        TimeWindow.DistanceToNextPoint =
                clsGeneral.getDistanceBetweenPoints(MostSuitablePOI, NextPoint, GroupData.Distance);
//        TimeWindow.StartTime = this.getVisitStartTime(TimeWindow.UnusedTimeAtTheStartOfTheTour, 
//                                                       TimeWindow.DistanceFromPrevioiusPoint,
//                                                       VisitIndex, Itinerary);
        TimeWindow.EndTime =  TimeWindow.StartTime+GroupData.POI[MostSuitablePOI-2][3];
        TimeWindow.TourIndeks = TourIndex;
        TimeWindow.VisitIndeks =VisitIndex;
        Constraint.TimeWindow = TimeWindow;
        result.constraints = Constraint;
        return result;
    }
    
         float getVisitStartTime(float UnusedTimeAtTheStartOfTheTour,float DistanceFromPrevioiusPoint,
                                 int VisitIndex,int IndexOfLastVisit,clsVisit  Itinerary []){
         float result;
         if(VisitIndex==0){
             result=UnusedTimeAtTheStartOfTheTour+DistanceFromPrevioiusPoint;
         }
         else if(VisitIndex<=IndexOfLastVisit)
         {
             result= Itinerary[VisitIndex-1].constraints.TimeWindow.EndTime+DistanceFromPrevioiusPoint;
         }
         else
         {
             result= Itinerary[VisitIndex].constraints.TimeWindow.EndTime+DistanceFromPrevioiusPoint;
         }
         return result;
     }
}
