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
public class clsRemove {
    
    clsSolution solution;
    
    clsRemove(clsSolution aSolutin){
        solution=aSolutin;
    }
    //System.out.println("Test");
    clsRemoveSolution getRemoveSolution(clsData data)
    {
        clsRemoveSolution result=new clsRemoveSolution();
        int Shifts[][]=new int[solution.MaxShiftImpactInTrip.length][3];
        System.arraycopy(solution.MaxShiftImpactInTrip, 0, Shifts, 0, Shifts.length);
        ArrayList<int []> RemoveList=getRemoveList(Shifts);
        int i;
        float ShiftingValue=0;
        float UnusedTimeAtTheStartOfTheTour=0;
        for( i=0;i<RemoveList.size();i++){
            int Shift[]=RemoveList.get(i);
            int TourIndex=Shift[2];
            int VisitIndex=Shift[1];
            if(VisitIndex==0){
                UnusedTimeAtTheStartOfTheTour=clsGeneral.getTourUnusedTimeAtTheStart(data,solution.Itinerary[TourIndex][VisitIndex+1].POI_ID);
            }
            int TourLastVisit=solution.TourLastVisit[1][TourIndex];
            ShiftingValue=this.getShiftingValue(data,TourIndex, VisitIndex,TourLastVisit,
                    solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour,
                    UnusedTimeAtTheStartOfTheTour);
            boolean TimeWindowFeasibilityOfAffectedPOIs=getTimeWindowFeasibilityOfAffectedPOIs(TourIndex,VisitIndex,
                    TourLastVisit,ShiftingValue,solution.MaxShift);
            if(TimeWindowFeasibilityOfAffectedPOIs){ 
                result.TourIndex=TourIndex;
                result.VisitIndex=VisitIndex;
                break;
            }
        }
        
        if(i==RemoveList.size())
        {
            int randomTourIndex=clsGeneral.getRandomNumber(solution.Itinerary.length);
            int lastVisitIndex=solution.TourLastVisit[1][randomTourIndex];
                result.TourIndex=randomTourIndex;
                result.VisitIndex=lastVisitIndex;
                ShiftingValue=this.getShiftingValue(data,randomTourIndex, lastVisitIndex, lastVisitIndex,
                        solution.Itinerary[randomTourIndex][lastVisitIndex].constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour,
                        UnusedTimeAtTheStartOfTheTour);
                //System.out.println("29 Qershor");
        }
        result.RemovedPOI_ID=solution.Itinerary[result.TourIndex][result.VisitIndex].POI_ID;
        result.ShiftingValue=ShiftingValue;
        result.DistanceFromPreviousPointToNextPoint=this.getDistanceFromPreviousPointToNextPoint(data,result.TourIndex, result.VisitIndex);
        result.UnusedTimeAtTheStartOfTheTour=UnusedTimeAtTheStartOfTheTour;
        return result;
    }

    float getDistanceFromPreviousPointToNextPoint(clsData data,int TourIndex, int VisitIndex){
        float result;
        if(VisitIndex==0){           
            result=clsGeneral.getDistanceBetweenPoints(1, solution.Itinerary[TourIndex][VisitIndex+1].POI_ID, data.getDistance());
        }           
        else if(VisitIndex==solution.TourLastVisit[1][TourIndex])
            result=clsGeneral.getDistanceBetweenPoints(solution.Itinerary[TourIndex][VisitIndex-1].POI_ID,1 , data.getDistance());
        else
            result=clsGeneral.getDistanceBetweenPoints(solution.Itinerary[TourIndex][VisitIndex-1].POI_ID,
                    solution.Itinerary[TourIndex][VisitIndex+1].POI_ID , data.getDistance());
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
    
    float getShiftingValue(clsData data,int TourIndex, int VisitIndex,int TourLastVisit,
            float RemovedUnusedTimeAtTheStartOfTheTour,float InsertedUnusedTimeAtTheStartOfTheTour){//Te shiqohet efekti ne te gjitha vendet ku perdoret getShiftingValue
            float result;
            if(VisitIndex==TourLastVisit){              
                
                result=solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint+
                        (solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.EndTime-
                        solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.StartTime)+
                        solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceToNextPoint-
                        clsGeneral.getDistanceBetweenPoints(solution.Itinerary[TourIndex][VisitIndex-1].POI_ID,1, data.getDistance());                
            }
            else if(VisitIndex==0){
                result= RemovedUnusedTimeAtTheStartOfTheTour+
                        solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint+
                        (solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.EndTime-
                        solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.StartTime)+
                        solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceToNextPoint-
                        InsertedUnusedTimeAtTheStartOfTheTour-
                        clsGeneral.getDistanceBetweenPoints(1, solution.Itinerary[TourIndex][VisitIndex+1].POI_ID, data.getDistance());
            }
            else
            {
                result= 
                 solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint+
                (solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.EndTime-
                solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.StartTime)+
                + solution.Itinerary[TourIndex][VisitIndex].constraints.TimeWindow.DistanceToNextPoint-
                clsGeneral.getDistanceBetweenPoints(solution.Itinerary[TourIndex][VisitIndex-1].POI_ID, 
                        solution.Itinerary[TourIndex][VisitIndex+1].POI_ID, data.getDistance());
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
