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
public class clsRemoveConflict {

   
    clsSolution getSolutionWithoutTWConflicts(clsData data,clsSolution Solution){
       ArrayList<float []> ListToRepair= getListOfPOIsOutOfTimeWindows(data,Solution);
       for(int i=0;i<ListToRepair.size();i++){
          int TourIndex=(int)ListToRepair.get(i)[0];
          int VisitIndex=(int)ListToRepair.get(i)[1];
          int POIWithTimeWindowConflict=(int)ListToRepair.get(i)[2];
          applySingleRemoveWithConflict(data,Solution,TourIndex,VisitIndex,POIWithTimeWindowConflict);
          ListToRepair= getListOfPOIsOutOfTimeWindows(data,Solution);
          if(ListToRepair.size()>0)
              i=-1;
          else
              break;
         // System.out.println("Test");
       }
       
       return Solution;
    }
    ArrayList<float []> getListOfPOIsOutOfTimeWindows(clsData data,clsSolution Solution)
    {
        ArrayList<float []>  result=new ArrayList();
        for(int i=0;i<Solution.Itinerary.length;i++)
        {
            for(int j=0;j<=Solution.TourLastVisit[1][i];j++)
            {
                if((Solution.Itinerary[i][j].constraints.TimeWindow.StartTime <
                        data.getPOI()[Solution.Itinerary[i][j].POI_ID-2][4+data.getNumberOfPersons()]) ||
                        (Solution.Itinerary[i][j].constraints.TimeWindow.EndTime > 
                        data.getPOI()[Solution.Itinerary[i][j].POI_ID-2][8+data.getNumberOfPersons()])
                        ){
                    float POIOutOfTimeWindow[]=new float[5];
                    POIOutOfTimeWindow[0]=i;
                    POIOutOfTimeWindow[1]=j;
                    POIOutOfTimeWindow[2]=Solution.Itinerary[i][j].POI_ID;
                    POIOutOfTimeWindow[3]=Solution.Itinerary[i][j].constraints.TimeWindow.StartTime;
                    POIOutOfTimeWindow[4]=Solution.Itinerary[i][j].constraints.TimeWindow.EndTime;
                    result.add(POIOutOfTimeWindow);
                }
            }
        }
        return result;
    }
    
    clsRemoveSolution applySingleRemoveConflict(clsData data,clsSolution Solution, int TourIndex, int VisitIndex, int POIWithTimeWindowConflict)
    {
        clsRemoveSolution result=new clsRemoveSolution();
        float ShiftingValue=0;
        int TourLastVisit=Solution.TourLastVisit[1][TourIndex];
        ShiftingValue=this.getShiftingValue(data,Solution,TourIndex, VisitIndex,TourLastVisit );
        result.TourIndex=TourIndex;
        result.VisitIndex=VisitIndex;
        result.RemovedPOI_ID=POIWithTimeWindowConflict;
        result.ShiftingValue=ShiftingValue;
        result.DistanceFromPreviousPointToNextPoint=this.getDistanceFromPreviousPointToNextPoint(
                data,Solution,result.TourIndex, result.VisitIndex);
        return result;
    }
void applySingleRemoveWithConflict(clsData data,clsSolution CurrentSolution, int TourIndex, int VisitIndex, int POIWithTimeWindowConflict){
            clsRemoveSolution RemoveSolution= applySingleRemoveConflict(data,CurrentSolution,TourIndex,VisitIndex,POIWithTimeWindowConflict);
            this.updateAffectedPOIsAfterRemove(CurrentSolution, RemoveSolution);
            clsRemove.updateFlatItinerary(CurrentSolution, RemoveSolution);
            clsRemove.updateItinerary(CurrentSolution, RemoveSolution);
            clsRemove.updateTourLastVisit(RemoveSolution.TourIndex, RemoveSolution.VisitIndex, 
                    CurrentSolution.TourLastVisit, CurrentSolution.Itinerary);
            clsRemove.updateTourMaxShift(data,RemoveSolution.TourIndex, CurrentSolution.TourLastVisit, 
            CurrentSolution.Itinerary, CurrentSolution.MaxShift);
            clsRemove.updateCost(data,CurrentSolution, RemoveSolution);
            clsRemove.updateEvaluation(data,CurrentSolution, RemoveSolution.RemovedPOI_ID);
            clsRemove.updateTourSpenTime(CurrentSolution, RemoveSolution.ShiftingValue, RemoveSolution.TourIndex);                     
            CurrentSolution.TripTimeCost=clsGeneral.getTripSpentTime(CurrentSolution.TourTimeSpent);
            CurrentSolution.POIsInItinerary.remove((Integer)RemoveSolution.RemovedPOI_ID);
            CurrentSolution.POIsOffItinerary.add((Integer)RemoveSolution.RemovedPOI_ID);
            clsRemove.updateTypeConstraint(data,CurrentSolution.TypeConstraintCounter, 
                    data.getPOI()[RemoveSolution.RemovedPOI_ID-2]);
    }
void updateAffectedPOIsAfterRemove(clsSolution CurrentSolution,  clsRemoveSolution RemoveSolution){
        
        if( RemoveSolution.VisitIndex<CurrentSolution.TourLastVisit[1][RemoveSolution.TourIndex]){
//            //Update Start time and End time of Preceiding POIs
           for(int i=RemoveSolution.VisitIndex+1;i<=CurrentSolution.TourLastVisit[1][RemoveSolution.TourIndex];i++){
                CurrentSolution.Itinerary[RemoveSolution.TourIndex][i].constraints.TimeWindow.StartTime-=(RemoveSolution.ShiftingValue);
                CurrentSolution.Itinerary[RemoveSolution.TourIndex][i].constraints.TimeWindow.EndTime-=(RemoveSolution.ShiftingValue);           
           }
            //Update distance to next and previous point of affected POIs
            if(RemoveSolution.VisitIndex==0)
                CurrentSolution.Itinerary[RemoveSolution.TourIndex][RemoveSolution.VisitIndex+1].
                        constraints.TimeWindow.DistanceFromPrevioiusPoint=RemoveSolution.DistanceFromPreviousPointToNextPoint;
            else{
                CurrentSolution.Itinerary[RemoveSolution.TourIndex][RemoveSolution.VisitIndex-1].
                        constraints.TimeWindow.DistanceToNextPoint=RemoveSolution.DistanceFromPreviousPointToNextPoint;
                CurrentSolution.Itinerary[RemoveSolution.TourIndex][RemoveSolution.VisitIndex+1].
                        constraints.TimeWindow.DistanceFromPrevioiusPoint=RemoveSolution.DistanceFromPreviousPointToNextPoint;
                }
        }
        else{
            CurrentSolution.Itinerary[RemoveSolution.TourIndex][RemoveSolution.VisitIndex-1].
                        constraints.TimeWindow.DistanceToNextPoint=RemoveSolution.DistanceFromPreviousPointToNextPoint;
        }         
    }
    float getDistanceFromPreviousPointToNextPoint(clsData data,clsSolution Solution,int TourIndex, int VisitIndex){
        float result;
        if(VisitIndex==0){           
            result=clsGeneral.getDistanceBetweenPoints(1, Solution.Itinerary[TourIndex][VisitIndex+1].POI_ID, data.getDistance());
        }           
        else if(VisitIndex==Solution.TourLastVisit[1][TourIndex])
            result=clsGeneral.getDistanceBetweenPoints(Solution.Itinerary[TourIndex][VisitIndex-1].POI_ID,1 , data.getDistance());
        else
            result=clsGeneral.getDistanceBetweenPoints(Solution.Itinerary[TourIndex][VisitIndex-1].POI_ID,
                    Solution.Itinerary[TourIndex][VisitIndex+1].POI_ID , data.getDistance());
        return result;
    }

    ArrayList<int []> getRemoveList(int [][]Shifts){
        ArrayList<int[]> result=new ArrayList();
        this.sortShifts(Shifts);
        for(int i=0;i<Shifts.length;i++){
            result.add(Shifts[i]);
        }
        return result;
    }
    void sortShifts(int Shifts[][]){
        for(int i=0;i<Shifts.length-1;i++){
            for(int j=i+1;j<Shifts.length;j++){
                if(Shifts[i][0]<Shifts[j][0]){
                    int X[]=Shifts[i];
                    Shifts[i]=Shifts[j];
                    Shifts[j]=X;
                }
            }
        }
   
    }
    
    int getNumberOfTourShiftsWithHighestImpact(int Shifts[][]){
        int result=0;
        int MaxImpact=Shifts[0][0];
        for(int i=0;i<Shifts.length;i++)
        {
            if(Shifts[i][0]==MaxImpact)
                result++;
            else
                break;
        }
        return result;
    }
//    int [] getTourToRemove(){
////        int [] result=new int [2];
////        int Shifts[][]=new int[solution.MaxShiftImpactInTrip.length][2];
////        System.arraycopy(solution.MaxShiftImpactInTrip, 0, Shifts, 0, Shifts.length);
////        this.sortShifts(Shifts);
////        ArrayList<int []> RemoveList=getRemoveList(Shifts);  
////        int NumberOfTourShiftsWithHighestImpact=getNumberOfTourShiftsWithHighestImpact(Shifts);
////        int TourToRemove=clsGeneral.getRandomNumber(NumberOfTourShiftsWithHighestImpact);
////        result[0]=Shifts[TourToRemove][2];
////        result[1]=Shifts[TourToRemove][1];
////        System.out.println("Test");
//        return result;
//    }
    
    float getShiftingValue(clsData data,clsSolution Solution,int TourIndex, int VisitIndex,int TourLastVisit){//Te shiqohet efekti ne te gjitha vendet ku perdoret getShiftingValue
            float result;
            if(VisitIndex==TourLastVisit){
              
                result=Solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint+
                        (Solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.EndTime-
                        Solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.StartTime)+
                        Solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceToNextPoint-
                        clsGeneral.getDistanceBetweenPoints(Solution.Itinerary[TourIndex][VisitIndex-1].POI_ID,1,  data.getDistance());                
            }
            else if(VisitIndex==0){
                result=Solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint+
                        (Solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.EndTime-
                        Solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.StartTime)+
                        Solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceToNextPoint-
                        clsGeneral.getDistanceBetweenPoints(1, Solution.Itinerary[TourIndex][VisitIndex+1].POI_ID, data.getDistance());
            }
            else
            {
                result= 
                 Solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint+
                (Solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.EndTime-
                Solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.StartTime)+
                + Solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceToNextPoint-
                clsGeneral.getDistanceBetweenPoints(Solution.Itinerary[TourIndex][VisitIndex-1].POI_ID, 
                        Solution.Itinerary[TourIndex][VisitIndex+1].POI_ID, data.getDistance());
            }
    return result;
   }
    
    boolean getTimeWindowFeasibilityOfAffectedPOIs(int TourIndex,int VisitIndex,int TourLastVisit,
        float ShiftingValue, float MaxShift[][][]){
        boolean result=false;
        if(VisitIndex==TourLastVisit)
            result=true;
        else if(ShiftingValue<=MaxShift[TourIndex][1][VisitIndex+1])//LeftShiftFeasibility  of Preceiding POIs
        {
            result=true;
        }                   
        return result;
    }
    
    static void updateTimeSpent(clsSolution CurrentSolution, clsRemoveSolution RemoveSolution){
        CurrentSolution.TourTimeSpent[RemoveSolution.TourIndex]-=RemoveSolution.ShiftingValue;
    }
    static void updateItinerary(clsSolution CurrentSolution, clsRemoveSolution RemoveSolution){
            //Remove a point from itinerary
            if(RemoveSolution.VisitIndex==CurrentSolution.TourLastVisit[1][RemoveSolution.TourIndex]){
                CurrentSolution.Itinerary[RemoveSolution.TourIndex][RemoveSolution.VisitIndex]=null;
            }
            else{
                for(int i=RemoveSolution.VisitIndex;i<=CurrentSolution.TourLastVisit[1][RemoveSolution.TourIndex];i++){
                    if(i<CurrentSolution.TourLastVisit[1][RemoveSolution.TourIndex]){
                         CurrentSolution.Itinerary[RemoveSolution.TourIndex][i]=CurrentSolution.Itinerary[RemoveSolution.TourIndex][i+1];
                    }
                    else{
                        CurrentSolution.Itinerary[RemoveSolution.TourIndex][i]=null;
                    }
                }   
            }
    }
    
    static void updateFlatItinerary(clsSolution CurrentSolution,clsRemoveSolution RemoveSolution){
           if(RemoveSolution.VisitIndex==CurrentSolution.TourLastVisit[1][RemoveSolution.TourIndex]){
                CurrentSolution.FlatItinerary[RemoveSolution.TourIndex][RemoveSolution.VisitIndex]=0;
            }
            else{
                for(int i=RemoveSolution.VisitIndex;i<=CurrentSolution.TourLastVisit[1][RemoveSolution.TourIndex];i++){
                    if(i<CurrentSolution.TourLastVisit[1][RemoveSolution.TourIndex]){
                         CurrentSolution.FlatItinerary[RemoveSolution.TourIndex][i]=CurrentSolution.FlatItinerary[RemoveSolution.TourIndex][i+1];
                    }
                    else{
                        CurrentSolution.FlatItinerary[RemoveSolution.TourIndex][i]=0;
                    }
                }   
            }
    }
    static void updateTourLastVisit(int TourIndex,int VisitIndex, int[][] TourLastVisit, clsVisit Itinerary[][]) {
        if (VisitIndex == TourLastVisit[1][TourIndex]) {
            TourLastVisit[0][TourIndex] = Itinerary[TourIndex][VisitIndex-1].POI_ID;
        }
        TourLastVisit[1][TourIndex] --;
    }
    static void updateCost(clsData data,clsSolution CurrentSolution, clsRemoveSolution RemoveSolution){
        float RemovedPOICost=data.getPOI()[RemoveSolution.RemovedPOI_ID-2][10+data.getNumberOfPersons()];
        CurrentSolution.BudgetCost-=RemovedPOICost;
    }
    static void updateEvaluation(clsData data,clsSolution CurrentSolution, int RemovedPOI_ID) {
        for (int i = 0; i < data.getNumberOfPersons(); i++) {
            CurrentSolution.Evaluation -= data.getPOI()[RemovedPOI_ID - 2][4 + i];
        }
    }
    static void updateTourSpenTime(clsSolution CurrentSolution, float ShiftingValue, int TourIndex){
        CurrentSolution.TourTimeSpent[TourIndex]-=ShiftingValue;
    }
    static void updateTypeConstraint(clsData data,int TypeCounter[],float POI[]){
        for(int i=0;i<10;i++){
            if(POI[i+11+data.getNumberOfPersons()]==1)
                TypeCounter[i]--;
        }
    }
static void updateTourMaxShift(clsData data,int TourIndex, int TourLastVisit[][], 
        clsVisit Itinerary[][], float MaxShift[][][]) {
            //Set to zero Max Shift of previous Last POI
            MaxShift[TourIndex][0][TourLastVisit[1][TourIndex]+1]=0;
            MaxShift[TourIndex][1][TourLastVisit[1][TourIndex]+1]=0;
            
            MaxShift[TourIndex][0][TourLastVisit[1][TourIndex]]=
                    clsGeneral.getMaxRightShift(data,Itinerary[TourIndex][TourLastVisit[1][TourIndex]].POI_ID,
                    Itinerary[TourIndex][TourLastVisit[1][TourIndex]].constraints.TimeWindow.EndTime);
            MaxShift[TourIndex][1][TourLastVisit[1][TourIndex]]=
                    clsGeneral.getMaxLeftShift(data,Itinerary[TourIndex][TourLastVisit[1][TourIndex]].POI_ID,
                    Itinerary[TourIndex][TourLastVisit[1][TourIndex]].constraints.TimeWindow.StartTime);
            
            for(int i=TourLastVisit[1][TourIndex]-1;i>=0;i--){
                 float CurrentVisitMaxRightShift;
                 float CurrentVisitMaxLeftShift;
                 CurrentVisitMaxRightShift= clsGeneral.getMaxRightShift(data,Itinerary[TourIndex][i].POI_ID,
                            Itinerary[TourIndex][i].constraints.TimeWindow.EndTime);
                 CurrentVisitMaxLeftShift= clsGeneral.getMaxLeftShift(data,Itinerary[TourIndex][i].POI_ID,
                        Itinerary[TourIndex][i].constraints.TimeWindow.StartTime);
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
  
}
