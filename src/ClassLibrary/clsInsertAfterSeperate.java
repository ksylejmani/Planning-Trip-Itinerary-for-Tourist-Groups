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
public class clsInsertAfterSeperate {
    
    void Apply(clsGroupSolution GroupSolution,clsGroupData GroupData){
        for(int TourIndex=0;TourIndex<GroupSolution.Solutions[0].Itinerary.length;TourIndex++){
            float TourLength=GroupData.StartPoint[6];
            float StartOfTheTour=GroupData.StartPoint[5];
            for(int VisitIndex=0;VisitIndex<=GroupSolution.Solutions[0].TourLastVisit[1][TourIndex]+1 /* to try and add a new visit */;VisitIndex++){
                ArrayList<ArrayList<Integer>> TouristSubgroupsAtATourPosition=
                        this.getTouristSubgroupsAtATourPosition(GroupSolution.Solutions, TourIndex, VisitIndex,
                                                                GroupSolution.Solutions[0].TourLastVisit[1][TourIndex]);
                for(int SubGroupIndex=0;SubGroupIndex<TouristSubgroupsAtATourPosition.size();SubGroupIndex++){
                    ArrayList<Integer> CurrentTouristSubGroup =TouristSubgroupsAtATourPosition.get(SubGroupIndex);
                    for(int PersonIndexInSubGroup=0;PersonIndexInSubGroup<CurrentTouristSubGroup.size();PersonIndexInSubGroup++){
                        int PersonID=CurrentTouristSubGroup.get(PersonIndexInSubGroup);
                        int IndexOfLastVisit=GroupSolution.Solutions[PersonID].TourLastVisit[1][TourIndex];
                        int PreviousPoint=this.getPreviousPoint(GroupSolution.Solutions[PersonID].Itinerary,TourIndex,VisitIndex,IndexOfLastVisit);
                        int NextPoint=this.getNextPoint(GroupSolution.Solutions[PersonID].Itinerary,TourIndex, VisitIndex,IndexOfLastVisit);
                        float TimeLimit=this.getTimeLimitForInsertionOfVisits(GroupSolution, PersonID, TourIndex, VisitIndex,
                                                                              IndexOfLastVisit, TourLength);
                        if(TimeLimit>0){
                            float PreviousPointEndTime=this.getPreviousPointEndTime(GroupSolution, PersonID, TourIndex, VisitIndex);
                            ArrayList<Integer> ListOfReachablePOIs=
                                    this.getListOfOpenPOIsOnRouteBetweenTwoPOIs(VisitIndex,PreviousPoint, NextPoint, TimeLimit,StartOfTheTour,
                                    PreviousPointEndTime, GroupData.POI,GroupData.Distance, 
                                    GroupSolution.Solutions[PersonID].POIsOffItinerary,GroupData.NumberOfPersons);
                            this.SortListOfPOIsBasedOnScore(ListOfReachablePOIs, GroupData.POI, PersonID);
                            this.addAdditionalPOIs(GroupSolution, GroupData, PersonID, TourIndex, VisitIndex,  IndexOfLastVisit,
                                    PreviousPoint, NextPoint, ListOfReachablePOIs);
                                                    
                        
                            
                            
                            //Test
                            //System.out.println("TimeLimit = "+ TimeLimit);
                        //Test
                        
                            
                        }

                        
//                        int MostSuitablePOI=this.getMostSuitablePOI(PersonID,TourIndex,VisitIndex,AvoidedPOI, 
//                                    TouristSubgroupsAtATourPosition,SubGroupIndex, PreviousPoint, 
//                                NextPoint,GroupSolution, ListOfReachablePOIs, GroupSolution.Solutions, GroupData);
//                            if(MostSuitablePOI !=-1 ){
//                            clsAdaptSeperate AS=new clsAdaptSeperate();    
//                            AS.Apply(PersonID, MostSuitablePOI, TourIndex, VisitIndex,GroupSolution,
//                                    GroupData,TouristSubgroupsAtATourPosition,SubGroupIndex);
//                            }
                    }
                }
            }
        }
    }
    void addAdditionalPOIs(clsGroupSolution GroupSolution,clsGroupData GroupData,
                           int PersonID,int TourIndex,int VisitIndex,int IndexOfLastVisit,
                           int PreviousPoint, int NextPoint,
                           ArrayList<Integer> ListOfPOIs){
        boolean InsertionPossible=true;
        while (InsertionPossible){
            InsertionPossible=this.addPOI(GroupSolution, GroupData, PersonID, TourIndex,
                                          VisitIndex,IndexOfLastVisit, PreviousPoint, NextPoint, ListOfPOIs);
        } 
    }
    boolean  addPOI(clsGroupSolution GroupSolution,clsGroupData GroupData,
                           int PersonID,int TourIndex,int VisitIndex,int IndexOfLastVisit,
                           int PreviousPoint,int NextPoint,
                           ArrayList<Integer> ListOfPOIs){
        boolean result=false;
        if(!ListOfPOIs.isEmpty()){
            for(int i=0;i<ListOfPOIs.size();i++){
                float InsertedPOI[]=GroupData.POI[ListOfPOIs.get(i)-2];
                float PersonBudget=GroupData.Person[PersonID][1];
                if(this.IsCostConstraintValid(PersonBudget, InsertedPOI, 
                        GroupSolution.Solutions[PersonID].BudgetCost, GroupData.NumberOfPersons)){
                    if(this.IsTourDurationConstraintValid(PersonID, Math.round(InsertedPOI[0]),
                            TourIndex, VisitIndex,IndexOfLastVisit,PreviousPoint,NextPoint,
                            GroupSolution.Solutions,GroupData)){
                        if(this.IsPOICategoryConstraintValid(GroupData.Person[PersonID], GroupSolution.Solutions[PersonID].TypeConstraintCounter, 
                                                             InsertedPOI, GroupData.NumberOfPersons)){
                                result=true;
                                
                                //1. Update current solution
                                //2. Remove inserted POI from ListOfPOIs possible
                        } 
                    }
                }
            }  
        }
        return result;
   }
    void SortListOfPOIsBasedOnScore(ArrayList<Integer> ListOfPOIs,float POI[][], int PersonID){
        for(int i=0;i<ListOfPOIs.size()-1;i++){
            for(int j=i;j<ListOfPOIs.size();j++){
                if(POI[ListOfPOIs.get(i)-2][4+PersonID] < POI[ListOfPOIs.get(j)-2][4+PersonID]){
                    int SparePOI=ListOfPOIs.get(i);
                    ListOfPOIs.set(i, ListOfPOIs.get(j));
                    ListOfPOIs.set(j, SparePOI);
                }
            }
        }
    }
    ArrayList<ArrayList<Integer>> getTouristSubgroupsAtATourPosition(clsSolution Solutions [],
            int TourIndex,int VisitIndex, int IndexOfLastVisit){
        if(VisitIndex>IndexOfLastVisit)
            VisitIndex=IndexOfLastVisit;// Subgroupin after last visit is the same
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
//         ArrayList<Integer> CurrentGroup=new ArrayList();
//         CurrentGroup.add(0);
//         result.add(CurrentGroup);
//         for(int i=1;i<Solutions.length;i++){
//             int CurrentTouristPOI=Solutions[i].Itinerary[TourIndex][VisitIndex].POI_ID;
//             int TouristSubGroupIndex=this.getIndexOfSubGroupContainingPOI(result, CurrentTouristPOI);
//             if(TouristSubGroupIndex!=-1){
//                 result.get(TouristSubGroupIndex).add(i);
//             }
//             else
//             {
//                 CurrentGroup=new ArrayList();
//                 CurrentGroup.add(i);
//                 result.add(CurrentGroup);
//             }
//         }
//         return result;
//     }
        
//     int getIndexOfSubGroupContainingPOI(ArrayList<ArrayList<Integer>> CurrentTouristSubgroups,int POIID)
//     {
//         int result=-1;
//         Outer:for(int i=0;i<CurrentTouristSubgroups.size();i++){
//             ArrayList<Integer> TouristSubGroup=CurrentTouristSubgroups.get(i);
//             for(int j=0;j<TouristSubGroup.size();j++){
//                 if(TouristSubGroup.get(j)== POIID){
//                     result=i;
//                     break Outer;
//                 }
//             }
//         }
//         return result;
//     }
//     int getIndexOfPersonVisitingAPOI(clsSolution Solutions [],int CurrentPersonBeingExamined,int POIID,
//             int TourIndex,int VisitIndex){
//         int result=-1;
//         for(int i=0;i<CurrentPersonBeingExamined;i++)
//         {
//             if(Solutions[i].Itinerary[TourIndex][VisitIndex].POI_ID==POIID){
//                 result=i;
//                 break;
//             }
//         }
//         return result;
//     }
     int getPreviousPoint(clsVisit Itinerary[][], int TourIndex, int VisitIndex,int IndexOfLastVisit){
         int result;
         if(VisitIndex==0)
             result=1;
         else
             result=Itinerary[TourIndex][VisitIndex-1].POI_ID;     
         return result;
     }
     int getNextPoint(clsVisit Itinerary[][], int TourIndex, int VisitIndex, int IndexOfLastVisit){
         int result;
         if(VisitIndex>IndexOfLastVisit)
             result=1;
         else
             result= Itinerary[TourIndex][VisitIndex].POI_ID;
         return result;
    }
     float getTimeLimitForInsertionOfVisits(clsGroupSolution GroupSolution,int PersonID, int TourIndex, 
                                            int VisitIndex, int IndexOfLastVisit,float TourLength){
         float result=0;
         if(VisitIndex<=IndexOfLastVisit)
         {
            if(GroupSolution.Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.WaitingTime>0)
            {
               result =GroupSolution.Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint +
                     GroupSolution.Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.WaitingTime+
                     GroupSolution.Solutions[PersonID].MaxShift[TourIndex][0][VisitIndex];
            }
         }
         else
             result+=(TourLength-GroupSolution.Solutions[PersonID].Itinerary[TourIndex][IndexOfLastVisit].constraints.TimeWindow.EndTime);
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
    ArrayList<Integer> getListOfOpenPOIsOnRouteBetweenTwoPOIs(int VisitIndex,int PreviousPoint,int NextPoint,
            float TimeLimit, float StartOfTheTour,float PreviousPointEndTime, float POI[][], float Distance[][], 
            ArrayList <Integer> POIsOffItinerary,int NumberOfPersons){
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
     boolean IsCostConstraintValid(float PersonBudget,float InsertedPOI[],
                                   float CurrentCost,int NumberOfPersons){
        boolean result=CurrentCost+InsertedPOI[10+NumberOfPersons]<=PersonBudget;
        return result;
    }
     
    boolean IsTourDurationConstraintValid(int PersonID,int CurrentPOI,int TourIndex, int VisitIndex, int IndexOfLastVisit,
                                          int PreviousPoint,int NextPoint,clsSolution Solutions[],clsGroupData GroupData ){

        float NewVisitTimeCost=this.getVisitTimeCost(CurrentPOI, TourIndex, VisitIndex,IndexOfLastVisit, PreviousPoint,NextPoint,
                Solutions[PersonID].Itinerary, GroupData);
        float OldVisitTimeCost;
        if( VisitIndex<=IndexOfLastVisit)
        {
            OldVisitTimeCost=
                Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour
                + Solutions[PersonID].Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint;
        }
        else
        {
            OldVisitTimeCost=Solutions[PersonID].Itinerary[TourIndex][IndexOfLastVisit].constraints.TimeWindow.DistanceToNextPoint;
        }

        float VisitTimeCostChange=NewVisitTimeCost-OldVisitTimeCost;
        boolean result= Solutions[PersonID].TourTimeSpent[TourIndex]+VisitTimeCostChange<=GroupData.StartPoint[6];
        return result;
    } 
    float getVisitTimeCost(int CurrentPOI,int TourIndex,int VisitIndex,int IndexOfLastVisit,
                           int PreviousPoint,int NextPoint,clsVisit Itinerary[][],clsGroupData GroupData){
        float result;
        float TourUnusedTimeAtTheStartOfTour=0;
        if(VisitIndex<=IndexOfLastVisit)
        {
            TourUnusedTimeAtTheStartOfTour=this.getTourUnusedTimeAtTheStartOfTour(CurrentPOI, VisitIndex, 
                    GroupData.NumberOfPersons,GroupData.StartPoint[5], GroupData.POI[CurrentPOI-2], GroupData.Distance);
        }
        result= TourUnusedTimeAtTheStartOfTour+
                clsGeneral.getDistanceBetweenPoints(PreviousPoint, CurrentPOI, GroupData.Distance)+
                GroupData.POI[CurrentPOI-2][3]+
                clsGeneral.getDistanceBetweenPoints(CurrentPOI, NextPoint, GroupData.Distance);
        return result;
    }
    boolean IsPOICategoryConstraintValid(float Person[],int TypeConstraintCounter[],
                                         float InsertedPOI[], int NumberOfPersons){
        boolean result=true;
        for(int i=0;i<10;i++){
            result=(TypeConstraintCounter[i]+InsertedPOI[11+NumberOfPersons+i]
                    <=Person[2+2*(NumberOfPersons-1)+i]);
            if(!result){
                return false;
            }
        }
        return result;
    }
}
