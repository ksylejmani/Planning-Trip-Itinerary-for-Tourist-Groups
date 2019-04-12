/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassLibrary;

import java.util.ArrayList;

/**
 *
 * @author Kadri Sylejmani
 */
public class clsSolution {
    float TourTimeSpent[];
    float TourUnusedTimeAtTheStart[];
    float BudgetCost;
    int TypeConstraintCounter[]=new int[10];
    clsVisit  Itinerary [][];
    int  FlatItinerary [][];
    int  TourLastVisit [][];
    float  MaxShift [][][];
    int MaxShiftImpactInTrip[][];
    ArrayList <Integer> POIsInItinerary;
    ArrayList <Integer> POIsOffItinerary;
    float Evaluation;  
    float TripTimeCost;
   // int NumberOfTWConflicts;
    
    clsSolution(clsData data){
        Initialize( data);
    }
    clsSolution(clsData data,clsSolution AnotherSolution){
        Initialize(data);
        this.BudgetCost=AnotherSolution.BudgetCost;
        System.arraycopy(AnotherSolution.TourTimeSpent , 0, this.TourTimeSpent,
                    0, this.TourTimeSpent.length);
        System.arraycopy(AnotherSolution.TourUnusedTimeAtTheStart , 0, this.TourUnusedTimeAtTheStart,
                    0, this.TourUnusedTimeAtTheStart.length);
        System.arraycopy(AnotherSolution.TypeConstraintCounter , 0, this.TypeConstraintCounter,
                    0, this.TypeConstraintCounter.length);
        this.Itinerary=getItinerary(AnotherSolution.Itinerary,AnotherSolution.TourLastVisit );
        this.FlatItinerary=this.get2DArrayCopy(AnotherSolution.FlatItinerary);
        this.TourLastVisit=this.get2DArrayCopy(AnotherSolution.TourLastVisit);
        this.MaxShift=this.get3DArrayCopy(AnotherSolution.MaxShift);
        this.MaxShiftImpactInTrip= this.get2DArrayCopy(AnotherSolution.MaxShiftImpactInTrip);
        this.POIsInItinerary.addAll(AnotherSolution.POIsInItinerary);
        this.POIsOffItinerary.addAll(AnotherSolution.POIsOffItinerary);
        this.Evaluation=AnotherSolution.Evaluation;
        this.TripTimeCost=AnotherSolution.TripTimeCost;
    }
    
    final void Initialize(clsData data){
        TourTimeSpent=new float[data.getNumberOfTours()];
        TourUnusedTimeAtTheStart=new float[data.getNumberOfTours()];
        Itinerary =new clsVisit [data.getNumberOfTours()][data.getMaxNumberOfPOIsPossiblePerTour()];
        FlatItinerary=new int[data.getNumberOfTours()][data.getMaxNumberOfPOIsPossiblePerTour()];
        TourLastVisit=new int [2][data.getNumberOfTours()];
        POIsInItinerary=new ArrayList();
        POIsOffItinerary=new ArrayList();
        MaxShift=new float[data.getNumberOfTours()][2][data.getMaxNumberOfPOIsPossiblePerTour()];//Ka qene -1
        // Second dimension in MaxShift stands for: First row is MaxRightShift, Second row is MaxLeftShift
        MaxShiftImpactInTrip=new int[2*data.getNumberOfTours()][3];
    }
    
    clsSolution(clsGroupData GroupData,clsSolution AnotherSolution){
        TourTimeSpent=new float[GroupData.NumberOfTours];
        TourUnusedTimeAtTheStart=new float[GroupData.NumberOfTours];
        Itinerary =new clsVisit [GroupData.NumberOfTours][GroupData.getMaxNumberOfPOIsPossiblePerTour()];
        FlatItinerary=new int[GroupData.NumberOfTours][GroupData.getMaxNumberOfPOIsPossiblePerTour()];
        TourLastVisit=new int [2][GroupData.NumberOfTours];
        POIsInItinerary=new ArrayList();
        POIsOffItinerary=new ArrayList();
        MaxShift=new float[GroupData.NumberOfTours][2][GroupData.getMaxNumberOfPOIsPossiblePerTour()];//Ka qene -1
        // Second dimension in MaxShift stands for: First row is MaxRightShift, Second row is MaxLeftShift
        MaxShiftImpactInTrip=new int[2*GroupData.NumberOfTours][3];
        this.BudgetCost=AnotherSolution.BudgetCost;
        System.arraycopy(AnotherSolution.TourTimeSpent , 0, this.TourTimeSpent,
                    0, this.TourTimeSpent.length);
        System.arraycopy(AnotherSolution.TourUnusedTimeAtTheStart , 0, this.TourUnusedTimeAtTheStart,
                    0, this.TourUnusedTimeAtTheStart.length);
        System.arraycopy(AnotherSolution.TypeConstraintCounter , 0, this.TypeConstraintCounter,
                    0, this.TypeConstraintCounter.length);
        this.Itinerary=getItinerary(AnotherSolution.Itinerary,AnotherSolution.TourLastVisit );
        this.FlatItinerary=this.get2DArrayCopy(AnotherSolution.FlatItinerary);
        this.TourLastVisit=this.get2DArrayCopy(AnotherSolution.TourLastVisit);
        this.MaxShift=this.get3DArrayCopy(AnotherSolution.MaxShift);
        this.MaxShiftImpactInTrip= this.get2DArrayCopy(AnotherSolution.MaxShiftImpactInTrip);
        this.POIsInItinerary.addAll(AnotherSolution.POIsInItinerary);
        this.POIsOffItinerary.addAll(AnotherSolution.POIsOffItinerary);
        this.Evaluation=AnotherSolution.Evaluation;
        this.TripTimeCost=AnotherSolution.TripTimeCost;
    }
    
    float getInitialEvaluation(clsData data){
        float result=0;
        for (int i=0;i<POIsInItinerary.size();i++){
            for(int j=0;j<data.getNumberOfPersons();j++){
                result+=data.getPOI()[POIsInItinerary.get(i)-2][4+j];
            }
        }
        return result;
    }
    
    
    final clsVisit [][] getItinerary(clsVisit[][] AnotherItinerary, int [][] TourLastVisit){
        clsVisit [][] result=new clsVisit[AnotherItinerary.length][AnotherItinerary[0].length];
        for(int i=0;i<AnotherItinerary.length;i++){
            for(int j=0;(j<= TourLastVisit[1][i] && TourLastVisit[0][i]!=0);j++){
                clsVisit visit=new clsVisit();
                visit.POI_ID=AnotherItinerary[i][j].POI_ID;  
                visit.Reply=AnotherItinerary[i][j].Reply;
                visit.RemoveIndexInOffList=AnotherItinerary[i][j].RemoveIndexInOffList;
                
                clsConstraint Constraint=new clsConstraint();
                Constraint.Reply=AnotherItinerary[i][j].constraints.Reply;
                clsTypeConstraint TypeConstraint=new clsTypeConstraint();
                Constraint.POIType=TypeConstraint;
                Constraint.Cost=AnotherItinerary[i][j].constraints.Cost;
                
                clsTimeWindowConstraint TimeWindowConstraint=new clsTimeWindowConstraint();
                TimeWindowConstraint.UnusedTimeAtTheStartOfTheTour=
                        AnotherItinerary[i][j].constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour;
                
                TimeWindowConstraint.DistanceFromPrevioiusPoint=
                        AnotherItinerary[i][j].constraints.TimeWindow.DistanceFromPrevioiusPoint;
                TimeWindowConstraint.DistanceToNextPoint=
                        AnotherItinerary[i][j].constraints.TimeWindow.DistanceToNextPoint;
                TimeWindowConstraint.Replay=
                        AnotherItinerary[i][j].constraints.TimeWindow.Replay;                
                 TimeWindowConstraint.StartTime=
                        AnotherItinerary[i][j].constraints.TimeWindow.StartTime;                   
                 TimeWindowConstraint.EndTime=
                        AnotherItinerary[i][j].constraints.TimeWindow.EndTime;   
                 TimeWindowConstraint.TourIndeks=
                        AnotherItinerary[i][j].constraints.TimeWindow.TourIndeks; 
                 TimeWindowConstraint.VisitIndeks=
                        AnotherItinerary[i][j].constraints.TimeWindow.VisitIndeks; 
                  Constraint.TimeWindow=TimeWindowConstraint;
                  visit.constraints=Constraint;
                  result[i][j]=visit;//Te testohet...
            }
        }
       return result;
    }
    
    final int  [][] get2DArrayCopy(int OriginalArray[][]){
        int result[][]=new int [OriginalArray.length][OriginalArray[0].length];
        for(int i=0;i<result.length;i++)
            System.arraycopy(OriginalArray[i], 0, result[i], 0, result[0].length);
        return result;
    }
    
    final float [][][] get3DArrayCopy(float OriginalArray[][][]){
         float result[][][]=new float [OriginalArray.length][OriginalArray[0].length][OriginalArray[0][0].length];
         for(int i=0;i<result.length;i++)
             for(int j=0;j<result[0].length;j++)
                 System.arraycopy(OriginalArray[i][j], 0, result[i][j], 0, result[0][0].length);
         return result;
    }
    final int [][][] get3DArrayCopy(int OriginalArray[][][]){
         int result[][][]=new int [OriginalArray.length][OriginalArray[0].length][OriginalArray[0][0].length];
         for(int i=0;i<result.length;i++)
             for(int j=0;j<result[0].length;j++)
                 System.arraycopy(OriginalArray[i][j], 0, result[i][j], 0, result[0][0].length);
         return result;
    }
}
