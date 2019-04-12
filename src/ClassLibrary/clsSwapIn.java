/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassLibrary;

/**
 *
 * @author user
 */
public class clsSwapIn {
    private clsSolution solution;
    clsOperatorSolution BestNonTabooSolution;
    clsOperatorSolution BestTabooSolution;

    clsSwapIn( clsSolution aSolution) {
        solution = aSolution;
    }
void createSwapSolution(clsData data,int IterationCounter, int RecencyMemory[][], int TABU_LIST_SIZE) {
        clsOperatorSolution TabooSolution = new clsOperatorSolution();
        clsOperatorSolution NonTabooSolution = new clsOperatorSolution();
        TabooSolution.TimeCost=NonTabooSolution.TimeCost=this.getInitialSolutionTimeCost(data);
        // n*(n-1)/2 combinations
         for (int i = 0; i < solution.Itinerary.length; i++) {
             int p=1;
             for (int j = 0; j<=solution.TourLastVisit[1][i]; j++) {
                if((i==solution.Itinerary.length-1 ) && (j==solution.TourLastVisit[1][i]))
                    break;
                 int q=p;
                 for (int k = i; k <solution.Itinerary.length; k++) {
                    for (int l = q; l <= solution.TourLastVisit[1][k]; l++) {
                    int RemovedPOI_ID = solution.Itinerary[i][j].POI_ID;
                    int InsertedPOI_ID = solution.Itinerary[k][l].POI_ID;
                    float UnusedTimeAtTheStartOfRemovingTour=0;
                    float UnusedTimeAtTheStartOfInsertingTour=0;
                    boolean SwapInSameTour=(k==i);
                    boolean SwapBetweenNeighbourPOIs=(SwapInSameTour)&&(l==j+1);
                    if(j==0){
                        UnusedTimeAtTheStartOfRemovingTour=clsGeneral.getTourUnusedTimeAtTheStart(data,InsertedPOI_ID);
                    }
                    if(l==0){
                        UnusedTimeAtTheStartOfInsertingTour=clsGeneral.getTourUnusedTimeAtTheStart(data,RemovedPOI_ID);
                    }
                    float DistanceFromPreviosPointToInsertedPointInRemoveingTour=this.getDistanceFromPreviosPoint(data,i, j, InsertedPOI_ID);
                    float DistanceFromInsertedPointToNextPointInRemoveingTour=
                            this.getDistanceToNextPointWithPossibleSwapBetweenNeighbourPOIs(data,i, j, InsertedPOI_ID, 
                            solution.TourLastVisit[1][i],SwapBetweenNeighbourPOIs);
                    float RemoveingTourShiftingValue=this.getShiftingValue(data,RemovedPOI_ID, InsertedPOI_ID, 
                            DistanceFromPreviosPointToInsertedPointInRemoveingTour, DistanceFromInsertedPointToNextPointInRemoveingTour, 
                            solution.Itinerary[i][j].constraints.TimeWindow.DistanceFromPrevioiusPoint, 
                            solution.Itinerary[i][j].constraints.TimeWindow.DistanceToNextPoint,
                            UnusedTimeAtTheStartOfRemovingTour,
                            solution.Itinerary[i][j].constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour);
                    float DistanceFromPreviosPointToInsertedPointInInsertingTour=
                            this.getDistanceFromPreviosPointWithPossibleSwapBetweenNeighbourPOIs(data,k, l, RemovedPOI_ID,SwapBetweenNeighbourPOIs);
                    float DistanceFromInsertedPointToNextPointInInsertingTour=this.getDistanceToNextPoint(data,k, l, RemovedPOI_ID, solution.TourLastVisit[1][k]);
                    float InsertingTourShiftingValue=this.getShiftingValue(data,InsertedPOI_ID,RemovedPOI_ID, 
                        DistanceFromPreviosPointToInsertedPointInInsertingTour, DistanceFromInsertedPointToNextPointInInsertingTour, 
                            solution.Itinerary[k][l].constraints.TimeWindow.DistanceFromPrevioiusPoint, 
                            solution.Itinerary[k][l].constraints.TimeWindow.DistanceToNextPoint,
                            UnusedTimeAtTheStartOfInsertingTour,
                            solution.Itinerary[k][l].constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour);
                    float[] IndicatedStartAndEndTime =this.getIndicatedStartAndEndTime(data,i, j, k, l, 
                            solution.TourLastVisit[1][i], solution.TourLastVisit[1][k], 
                            RemoveingTourShiftingValue, InsertingTourShiftingValue, 
                            DistanceFromPreviosPointToInsertedPointInRemoveingTour, 
                            DistanceFromInsertedPointToNextPointInRemoveingTour, 
                            DistanceFromPreviosPointToInsertedPointInInsertingTour, 
                            DistanceFromInsertedPointToNextPointInInsertingTour,
                            UnusedTimeAtTheStartOfRemovingTour,
                            UnusedTimeAtTheStartOfInsertingTour,
                            RemovedPOI_ID,InsertedPOI_ID);
                            if (IndicatedStartAndEndTime[0] >= 0) {
                                float CurrentSolutionTimeCost =
                                        this.getSolutionTimeCost(solution,i,k,RemoveingTourShiftingValue, 
                                        InsertingTourShiftingValue);
                                boolean CandidateSolutionTabooStatus =
                                        this.getCandidateSolutionTabooStatus(RemovedPOI_ID,
                                        InsertedPOI_ID, IterationCounter,
                                        RecencyMemory, TABU_LIST_SIZE);
                                boolean AcceptanceCriteriaFullfilled=false;
                                if (CandidateSolutionTabooStatus) {
                                    AcceptanceCriteriaFullfilled=CurrentSolutionTimeCost <TabooSolution.TimeCost;
                                    if (AcceptanceCriteriaFullfilled) {
                                        TabooSolution = this.createCandidateSolution(i, j, k, l,
                                                RemovedPOI_ID,InsertedPOI_ID,
                                                IndicatedStartAndEndTime[0],
                                                IndicatedStartAndEndTime[1],
                                                IndicatedStartAndEndTime[2],
                                                IndicatedStartAndEndTime[3],
                                                DistanceFromPreviosPointToInsertedPointInRemoveingTour,
                                                DistanceFromInsertedPointToNextPointInRemoveingTour,
                                                DistanceFromPreviosPointToInsertedPointInInsertingTour,
                                               DistanceFromInsertedPointToNextPointInInsertingTour,
                                               CurrentSolutionTimeCost,
                                               UnusedTimeAtTheStartOfRemovingTour,
                                               UnusedTimeAtTheStartOfInsertingTour);
                                    }
                                } else {
                                    AcceptanceCriteriaFullfilled=CurrentSolutionTimeCost < NonTabooSolution.TimeCost;
                                    if (AcceptanceCriteriaFullfilled) {
                                        NonTabooSolution = this.createCandidateSolution(i, j, k, l,
                                                RemovedPOI_ID,InsertedPOI_ID,
                                                IndicatedStartAndEndTime[0],
                                                IndicatedStartAndEndTime[1],
                                                IndicatedStartAndEndTime[2],
                                                IndicatedStartAndEndTime[3],
                                                DistanceFromPreviosPointToInsertedPointInRemoveingTour,
                                                DistanceFromInsertedPointToNextPointInRemoveingTour,
                                                DistanceFromPreviosPointToInsertedPointInInsertingTour,
                                               DistanceFromInsertedPointToNextPointInInsertingTour,
                                               CurrentSolutionTimeCost,
                                               UnusedTimeAtTheStartOfRemovingTour,
                                               UnusedTimeAtTheStartOfInsertingTour);
                                    }
                                }
                            }
                        
                    }
                    q=0;
                }
                 p++;
            }
            p=1;
          }
        this.BestTabooSolution = TabooSolution;
        this.BestNonTabooSolution = NonTabooSolution;
       
    }

float[] getIndicatedStartAndEndTime(clsData data,int TourIndexInRemoveingTour, int VisitIndexInRemoveingTour, 
        int TourIndexInInsertingTour, int VisitIndexInInsertingTour, 
             int IndeksOfLastVisitInRemoveingTour, int IndeksOfLastVisitInInsertingTour,float RemoveingTourShiftingValue,    
             float InsertingTourShiftingValue,   float DistanceFromPreviosPointToInsertedPointInRemoveingTour,
        float DistanceFromInsertedPointToNextPointInRemoveingTour ,   float DistanceFromPreviosPointToInsertedPointInInsertingTour,
        float DistanceFromInsertedPointToNextPointInInsertingTour,
        float UnusedTimeAtTheStartOfRemovingTour, float UnusedTimeAtTheStartOfInsertingTour,
        int RemovedPOIID, int InsertedPOIID) {

        float[] result = new float[8];

        float IndicatedStartTimeInRemoveingTour;
        if(VisitIndexInRemoveingTour==0)
            IndicatedStartTimeInRemoveingTour=UnusedTimeAtTheStartOfRemovingTour+DistanceFromPreviosPointToInsertedPointInRemoveingTour;
        else
            IndicatedStartTimeInRemoveingTour=solution.Itinerary[TourIndexInRemoveingTour][VisitIndexInRemoveingTour-1].constraints.
                    TimeWindow.EndTime + DistanceFromPreviosPointToInsertedPointInRemoveingTour;
        float IndicatedEndTimeInRemoveingTour = IndicatedStartTimeInRemoveingTour + 
                data.getPOI()[solution.Itinerary[TourIndexInInsertingTour][VisitIndexInInsertingTour].POI_ID - 2][3];

        if ((IndicatedStartTimeInRemoveingTour >= 
                data.getPOI()[solution.Itinerary[TourIndexInInsertingTour][VisitIndexInInsertingTour].POI_ID - 2][4 + data.getNumberOfPersons()])
                && (IndicatedEndTimeInRemoveingTour <= 
                data.getPOI()[solution.Itinerary[TourIndexInInsertingTour][VisitIndexInInsertingTour].POI_ID - 2][8 + data.getNumberOfPersons()])) 
        {
            float IndicatedTourSpentTimeInRemoveingTour;
            if(TourIndexInRemoveingTour==TourIndexInInsertingTour)
                 IndicatedTourSpentTimeInRemoveingTour = getIndicatedSwapTourSpentTime(RemoveingTourShiftingValue,
                         InsertingTourShiftingValue,TourIndexInRemoveingTour);
            else
                IndicatedTourSpentTimeInRemoveingTour = getIndicatedTourSpentTime(RemoveingTourShiftingValue,TourIndexInRemoveingTour);
            if (IndicatedTourSpentTimeInRemoveingTour <= data.getStartPoint()[6]) {
                boolean TimeWindowFeasibilityOfFollowingPOIsInRemoveingTour;
                if(TourIndexInRemoveingTour==TourIndexInInsertingTour){
                    if(VisitIndexInInsertingTour==VisitIndexInRemoveingTour+1){//If neighbors in same tour are swapt
                        TimeWindowFeasibilityOfFollowingPOIsInRemoveingTour=true;
                    }
                    else
                    {
                        TimeWindowFeasibilityOfFollowingPOIsInRemoveingTour=this.getTimeWindowFeasibilityOfAffectedPOIsInRemoveingTour(
                                TourIndexInRemoveingTour, VisitIndexInRemoveingTour, solution.TourLastVisit[1][TourIndexInRemoveingTour], 
                                RemoveingTourShiftingValue, solution.MaxShift);                       
                    }
                }
                else{
                    TimeWindowFeasibilityOfFollowingPOIsInRemoveingTour =
                    getTimeWindowFeasibilityOfAffectedPOIs(TourIndexInRemoveingTour, VisitIndexInRemoveingTour,
                    solution.TourLastVisit[1][TourIndexInRemoveingTour],
                    solution.Itinerary[TourIndexInRemoveingTour][VisitIndexInRemoveingTour].constraints.TimeWindow.StartTime,
                    solution.Itinerary[TourIndexInRemoveingTour][VisitIndexInRemoveingTour].constraints.TimeWindow.EndTime,
                    IndicatedStartTimeInRemoveingTour, IndicatedEndTimeInRemoveingTour,
                    solution.Itinerary[TourIndexInRemoveingTour][VisitIndexInRemoveingTour].constraints.TimeWindow.DistanceToNextPoint,
                    DistanceFromInsertedPointToNextPointInRemoveingTour, solution.MaxShift,
                    data.getPOI()[RemovedPOIID-2][3],data.getPOI()[InsertedPOIID-2][3]);
                }
                    if(TimeWindowFeasibilityOfFollowingPOIsInRemoveingTour){
                        float IndicatedStartTimeInInsertingTour;
                        if(VisitIndexInInsertingTour==0)
                            IndicatedStartTimeInInsertingTour=
                                    UnusedTimeAtTheStartOfInsertingTour+DistanceFromPreviosPointToInsertedPointInInsertingTour;
                        else
                            IndicatedStartTimeInInsertingTour=
                                    solution.Itinerary[TourIndexInInsertingTour][VisitIndexInInsertingTour-1].constraints.TimeWindow.EndTime 
                                    + DistanceFromPreviosPointToInsertedPointInInsertingTour;
                        if(TourIndexInRemoveingTour==TourIndexInInsertingTour){
                            IndicatedStartTimeInInsertingTour+=RemoveingTourShiftingValue;
                        }
                        float IndicatedEndTimeInInsertingTour = IndicatedStartTimeInInsertingTour + 
                                data.getPOI()[solution.Itinerary[TourIndexInRemoveingTour][VisitIndexInRemoveingTour].POI_ID - 2][3];
                        if((IndicatedStartTimeInInsertingTour >= 
                            data.getPOI()[solution.Itinerary[TourIndexInRemoveingTour][VisitIndexInRemoveingTour].POI_ID - 2]
                                [4 + data.getNumberOfPersons()])
                            && (IndicatedEndTimeInInsertingTour <= 
                            data.getPOI()[solution.Itinerary[TourIndexInRemoveingTour][VisitIndexInRemoveingTour].POI_ID - 2]
                                [8 + data.getNumberOfPersons()]))
                        {
                            float IndicatedTourSpentTimeInInsertingTour;
                            if(TourIndexInRemoveingTour==TourIndexInInsertingTour)
                                IndicatedTourSpentTimeInInsertingTour=IndicatedTourSpentTimeInRemoveingTour;
                            else
                                IndicatedTourSpentTimeInInsertingTour = getIndicatedTourSpentTime(InsertingTourShiftingValue,TourIndexInInsertingTour);
                            if(IndicatedTourSpentTimeInInsertingTour <= data.getStartPoint()[6]){
                                boolean TimeWindowFeasibilityOfFollowingPOIsInInsertingTour; 
                                if(TourIndexInRemoveingTour==TourIndexInInsertingTour){
                                    TimeWindowFeasibilityOfFollowingPOIsInInsertingTour=this.getTimeWindowFeasibilityOfAffectedPOIsInInsertingTour(
                                    TourIndexInInsertingTour, VisitIndexInInsertingTour, solution.TourLastVisit[1][TourIndexInInsertingTour], 
                                    RemoveingTourShiftingValue, InsertingTourShiftingValue, solution.MaxShift);
                                 }
                                 else
                                 {
                                    TimeWindowFeasibilityOfFollowingPOIsInInsertingTour =
                                    getTimeWindowFeasibilityOfAffectedPOIs(TourIndexInInsertingTour, VisitIndexInInsertingTour,
                                    solution.TourLastVisit[1][TourIndexInInsertingTour],
                                    solution.Itinerary[TourIndexInInsertingTour][VisitIndexInInsertingTour].constraints.TimeWindow.StartTime,
                                    solution.Itinerary[TourIndexInInsertingTour][VisitIndexInInsertingTour].constraints.TimeWindow.EndTime,
                                    IndicatedStartTimeInInsertingTour, IndicatedEndTimeInInsertingTour,
                                    solution.Itinerary[TourIndexInInsertingTour][VisitIndexInInsertingTour].constraints.TimeWindow.DistanceToNextPoint,
                                    DistanceFromInsertedPointToNextPointInInsertingTour, solution.MaxShift,
                                    data.getPOI()[InsertedPOIID-2][3],data.getPOI()[RemovedPOIID-2][3]); 
                                 }   
                                    if (TimeWindowFeasibilityOfFollowingPOIsInInsertingTour) {
                                        result[0] = IndicatedStartTimeInRemoveingTour;
                                        result[1] = IndicatedEndTimeInRemoveingTour;
                                        result[2] = IndicatedStartTimeInInsertingTour; 
                                        result[3] = IndicatedEndTimeInInsertingTour;
                                        result[4] = DistanceFromPreviosPointToInsertedPointInRemoveingTour;
                                        result[5] = DistanceFromInsertedPointToNextPointInRemoveingTour;
                                        result[6] = DistanceFromPreviosPointToInsertedPointInInsertingTour;
                                        result[7] = DistanceFromInsertedPointToNextPointInInsertingTour;
                                    } 
                                    else 
                                    {
                                        result[0] = -3; // TimeWindow Feasibility Of Affected POIs is not OK
                                    }
                            }
                            else
                            {
                                result[0] = -2;//No time in tour
                            }
                            
                        }
                        else
                        {
                            result[0] = -1;//No availabel time window
                        }
                    }
                    else
                    {
                        result[0] = -3; // TimeWindow Feasibility Of affected POIs is not OK
                    }
            } 
            else 
            {
                result[0] = -2;//No time in tour
            }
        } 
        else 
        {
            result[0] = -1;//No availabel time window
        }
    //Test
//    if(result[0]==0)
//        System.out.println("Test");
    //Test
        return result;
    }

    float getShiftingValue(clsData data,int RemovedPOI_ID, int InsertedPOI_ID, 
            float DistanceFromPreviosPointToInsertedPoint, float DistanceFromInsertedPointToNextPoint,
            float DistanceFromPrevioiusPointToRemovedPoint, float DistanceFromRemovedPointToNextPoint,
            float InsertedUnusedTimeAtTheStartOfTour,float RemovedUnusedTimeAtTheStartOfTour)
    {
        float result= 
                    + InsertedUnusedTimeAtTheStartOfTour
                    + DistanceFromPreviosPointToInsertedPoint
                    + data.getPOI()[InsertedPOI_ID - 2][3]
                    + DistanceFromInsertedPointToNextPoint
                    - RemovedUnusedTimeAtTheStartOfTour
                    - DistanceFromPrevioiusPointToRemovedPoint
                    - data.getPOI()[RemovedPOI_ID - 2][3]
                    - DistanceFromRemovedPointToNextPoint;
        return result;
    }
    
    float getDistanceFromPreviosPoint(clsData data,int POITourIndeks,int POIVisitIndeks,
        int POI_ID){
        float result;
        if (POIVisitIndeks == 0) {
            result =
                    clsGeneral.getDistanceBetweenPoints(1, POI_ID, data.getDistance());
        } 
        else {
            result =
                    clsGeneral.getDistanceBetweenPoints(solution.Itinerary[POITourIndeks][POIVisitIndeks - 1].POI_ID,
                    POI_ID, data.getDistance());
        }
    return result;
    }
    float getDistanceFromPreviosPointWithPossibleSwapBetweenNeighbourPOIs(clsData data,int POITourIndeks,int POIVisitIndeks,
        int POI_ID, boolean SwapBetweenNeighbourPOIs){
        float result;
        if (POIVisitIndeks == 0) {
            result =
                    clsGeneral.getDistanceBetweenPoints(1, POI_ID, data.getDistance());
        } 
        else {
            if(SwapBetweenNeighbourPOIs){
            result =
                    clsGeneral.getDistanceBetweenPoints(solution.Itinerary[POITourIndeks][POIVisitIndeks].POI_ID,
                    POI_ID, data.getDistance());    
            }
            else{
            result =
                    clsGeneral.getDistanceBetweenPoints(solution.Itinerary[POITourIndeks][POIVisitIndeks - 1].POI_ID,
                    POI_ID, data.getDistance());   
            }

        }
    return result;
    }
    float getDistanceToNextPoint(clsData data,int POITourIndeks,int POIVisitIndeks,int POI_ID,
        int IndeksOfLastVisitIntour){
        float result;
            if (POIVisitIndeks == IndeksOfLastVisitIntour) {
            result =
                    clsGeneral.getDistanceBetweenPoints(POI_ID,
                    1, data.getDistance());
            } 
            else {
            result =
                    clsGeneral.getDistanceBetweenPoints(POI_ID,
                    solution.Itinerary[POITourIndeks][POIVisitIndeks+1].POI_ID, data.getDistance());
            }
        return result;
    }
    float getDistanceToNextPointWithPossibleSwapBetweenNeighbourPOIs(clsData data,int POITourIndeks,int POIVisitIndeks,int POI_ID,
        int IndeksOfLastVisitIntour, boolean SwapBetweenNeighbourPOIs){
        float result;
            if (POIVisitIndeks == IndeksOfLastVisitIntour) {
            result =
                    clsGeneral.getDistanceBetweenPoints(POI_ID,
                    1, data.getDistance());
            } 
            else {
                if(SwapBetweenNeighbourPOIs){
                    result =
                    clsGeneral.getDistanceBetweenPoints(POI_ID,
                    solution.Itinerary[POITourIndeks][POIVisitIndeks].POI_ID, data.getDistance());
                }
                else{
                    result =
                    clsGeneral.getDistanceBetweenPoints(POI_ID,
                    solution.Itinerary[POITourIndeks][POIVisitIndeks+1].POI_ID, data.getDistance());
                }

            }
        return result;
    }    
    float getIndicatedTourSpentTime(float ShiftingValue, int POITourIndeks) {
        float result;
        result = solution.TourTimeSpent[POITourIndeks] +ShiftingValue;
        return result;
    }
    
    float getIndicatedSwapTourSpentTime(float RemoveingTourShiftingValue,float InsertingTourShiftingValue, int POITourIndeks) {
        float result;
        result = solution.TourTimeSpent[POITourIndeks] +RemoveingTourShiftingValue+InsertingTourShiftingValue;
        return result;
    }
    boolean getTimeWindowFeasibilityOfAffectedPOIs(int TourIndex,int VisitIndex,int TourLastVisit,
            float RemovedPOIStartTime, float RemovedPOIEndTime, 
            float InsertedPOIStartTime, float InsertedPOIEndTime, float DistanceFromRemovedPointToNextPoint,
            float DistanceFromSwappingPointToNextPoint,float MaxShift[][][],
            float RemovedPOIVisitDuration, float InsertedPOIVisitDuration){
        boolean result=false;
        if(InsertedPOIEndTime-RemovedPOIEndTime<=MaxShift[TourIndex][0][VisitIndex]){//RightShiftFeasibility of Inserted POI
            if(RemovedPOIStartTime-InsertedPOIStartTime <=MaxShift[TourIndex][1][VisitIndex]){//LeftShiftFeasibility of Inserted POI
                if(VisitIndex==TourLastVisit){//Te vazhdohet 18.04.2012
                    result=true;
                }
                else{
                    if((InsertedPOIEndTime-RemovedPOIEndTime+DistanceFromSwappingPointToNextPoint-DistanceFromRemovedPointToNextPoint)
                            <=MaxShift[TourIndex][0][VisitIndex+1]) //RightShiftFeasibility of Preceiding POIs
                    {
                        if((RemovedPOIStartTime-InsertedPOIStartTime+DistanceFromRemovedPointToNextPoint-DistanceFromSwappingPointToNextPoint
                                +RemovedPOIVisitDuration-InsertedPOIVisitDuration)
                                <=MaxShift[TourIndex][1][VisitIndex+1])//LeftShiftFeasibility  of Preceiding POIs
                        {
                            result=true;                                                    
                        }
                    }                    
                }
            }
        }
        return result;
    }
    boolean getTimeWindowFeasibilityOfAffectedPOIsInRemoveingTour(int TourIndex,int VisitIndex,int TourLastVisit,
        float RemoveingTourShiftingValue, float MaxShift[][][]){
        boolean result=false;
        if(RemoveingTourShiftingValue<=MaxShift[TourIndex][0][VisitIndex+1]) //RightShiftFeasibility of Preceiding POIs
        {
            if(-(RemoveingTourShiftingValue)<=MaxShift[TourIndex][1][VisitIndex+1])//LeftShiftFeasibility  of Preceiding POIs
            {
                result=true;
            }
        }                    
        return result;
    }
    boolean getTimeWindowFeasibilityOfAffectedPOIsInInsertingTour(int TourIndex,int VisitIndex,int TourLastVisit,
            float RemoveingTourShiftingValue,float InsertingTourShiftingValue, float MaxShift[][][]){
        boolean result=false;
        if(VisitIndex==TourLastVisit){
            result=true;
        }
        else{
            if(RemoveingTourShiftingValue+InsertingTourShiftingValue<=MaxShift[TourIndex][0][VisitIndex+1]) //RightShiftFeasibility of Preceiding POIs
            {
                if(-(RemoveingTourShiftingValue+InsertingTourShiftingValue)
                        <=MaxShift[TourIndex][1][VisitIndex+1])//LeftShiftFeasibility  of Preceiding POIs
                {
                    result=true;
                }
            }                    
        }
        return result;
    }

    float getSolutionTimeCost(clsSolution solution,int RemovedPOITourIndex,int InsertedPOITourIndex,
            float RemoveingTourShiftingValue,float InsertingTourShiftingValue){
        float result=0;
         float RemoveingTourSpentTime=getIndicatedTourSpentTime(RemoveingTourShiftingValue,RemovedPOITourIndex);
         float InsertingTourSpentTime=getIndicatedTourSpentTime(InsertingTourShiftingValue,InsertedPOITourIndex);
        
        for(int i=0;i<solution.Itinerary.length;i++){
            if(i==RemovedPOITourIndex){
                result+=RemoveingTourSpentTime;
            }
            else if(i==InsertedPOITourIndex){
                result+=InsertingTourSpentTime;
            }
            else{
                result+=solution.TourTimeSpent[i];
            }
        }
        return result;
    }
    boolean getCandidateSolutionTabooStatus(int RemovedPOI_ID, int InsertedPOI_ID, int iterationIndeks,
            int RecencyMemory[][], int TABU_LIST_SIZE) {
        boolean result = false;
        boolean isRemovedPOI_IDSmaller = RemovedPOI_ID < InsertedPOI_ID;
        if (isRemovedPOI_IDSmaller) {
            //If value of a member in memory is 0 then swap is not tabu
            if (!(RecencyMemory[RemovedPOI_ID - 1][InsertedPOI_ID - 2] == 0)) {
                result = iterationIndeks - RecencyMemory[RemovedPOI_ID - 1][InsertedPOI_ID - 2] < TABU_LIST_SIZE;
            }
        } else {
            if (!(RecencyMemory[InsertedPOI_ID - 1][RemovedPOI_ID - 2] == 0)) {
                result = iterationIndeks - RecencyMemory[InsertedPOI_ID - 1][RemovedPOI_ID - 2] < TABU_LIST_SIZE;
            }
        }
        return result;
    }
    
    float getInitialSolutionTimeCost(clsData data){
        float result;
            result=data.getStartPoint()[6]*data.getNumberOfTours();
        return result;
    }
    
     private clsOperatorSolution createCandidateSolution(int RemoveingTourIndex, int RemoveingVisitIndex, 
             int InsertingTourIndex, int InsertingVisitIndex, int RemovedPOI_ID, int InsertedPOI_ID,
             float VisitStartTimeInRemoveingTour, float VisitEndTimeInRemoveingTour, 
             float VisitStartTimeInInsertingTour, float VisitEndTimeInInsertingTour, 
             float DitanceFromPreviousPointInRemoveingTour, float DistanceToNextPointInRemoveingTour, 
             float DitanceFromPreviousPointInInsertingTour, float DistanceToNextPointInInsertingTour, 
             float CurrentSolutionTimeCost,float UnusedTimeAtTheStartOfRemovingTour,
             float UnusedTimeAtTheStartOfInsertingTour) {
        clsOperatorSolution result = new clsOperatorSolution();
        result.TimeCost = CurrentSolutionTimeCost;
        result.RemovedPOI_ID = RemovedPOI_ID;
        result.InsertedPOI_ID = InsertedPOI_ID;
        result.TourUnusedTimeAtTheStart=UnusedTimeAtTheStartOfRemovingTour;
        result.InsertedVisitStartTime = VisitStartTimeInRemoveingTour;
        result.InsertedVisitEndTime = VisitEndTimeInRemoveingTour;
        result.UnusedTimeAtTheStartOfInsertingTour=UnusedTimeAtTheStartOfInsertingTour;
        result.VisitStartTimeInInsertingTour=VisitStartTimeInInsertingTour;
        result.VisitEndTimeInInsertingTour=VisitEndTimeInInsertingTour;
        result.DistanceFromPrevioiusPoint = DitanceFromPreviousPointInRemoveingTour;
        result.DistanceToNextPoint = DistanceToNextPointInRemoveingTour;
        result.DistanceFromPreviosPointToInsertedPointInInsertingTour=DitanceFromPreviousPointInInsertingTour;
        result.DistanceFromInsertedPointToNextPointInInsertingTour=DistanceToNextPointInInsertingTour;
        result.TourIndex = RemoveingTourIndex;
        result.VisitIndex = RemoveingVisitIndex;
        result.InsertedPOITourIndex=InsertingTourIndex;
        result.InsertedPOIVisitIndex=InsertingVisitIndex;
        return result;
    }
     
     static void updateTimeSpent(float TimeSpent[], clsVisit Itinerary[][], int RemoveingTourIndex, int RemoveingVisitIndex,
             float VisitStartTimeInRemoveingTour,float VisitEndTimeInRemoveingTour,
             float DistanceFromPrevioiusPointInRemoveingTour,float DistanceToNextPointInRemoveingTour, 
             int InsertingTourIndex, int InsertingVisitIndex, 
             float VisitStartTimeInInsertingTour, float VisitEndTimeInInsertingTour,
             float DistanceFromPreviosPointInInsertingTour, 
             float DistanceToNextPointInInsertingTour,
             float InsertedUnusedTimeAtTheStartOfRemoveingTour,
             float RemovedUnusedTimeAtTheStartOfRemoveingTour,
             float InsertedUnusedTimeAtTheStartOfInsertingTour,
             float RemovedUnusedTimeAtTheStartOfInsertingTour) 
     {
        //boolean SwapBetweenNeighbourPOIs=(InsertingTourIndex==RemoveingTourIndex)&&(InsertingVisitIndex==RemoveingVisitIndex+1);
        TimeSpent[RemoveingTourIndex] +=
                (InsertedUnusedTimeAtTheStartOfRemoveingTour
                + DistanceFromPrevioiusPointInRemoveingTour
                + (VisitEndTimeInRemoveingTour - VisitStartTimeInRemoveingTour)
                + DistanceToNextPointInRemoveingTour
                - RemovedUnusedTimeAtTheStartOfRemoveingTour
                - Itinerary[RemoveingTourIndex][RemoveingVisitIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint
                - (Itinerary[RemoveingTourIndex][RemoveingVisitIndex].constraints.TimeWindow.EndTime
                - Itinerary[RemoveingTourIndex][RemoveingVisitIndex].constraints.TimeWindow.StartTime)
                - Itinerary[RemoveingTourIndex][RemoveingVisitIndex].constraints.TimeWindow.DistanceToNextPoint);
        TimeSpent[InsertingTourIndex] +=
                ( InsertedUnusedTimeAtTheStartOfInsertingTour
                + DistanceFromPreviosPointInInsertingTour
                + (VisitEndTimeInInsertingTour - VisitStartTimeInInsertingTour)
                + DistanceToNextPointInInsertingTour
                - RemovedUnusedTimeAtTheStartOfInsertingTour
                - Itinerary[InsertingTourIndex][InsertingVisitIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint
                - (Itinerary[InsertingTourIndex][InsertingVisitIndex].constraints.TimeWindow.EndTime
                - Itinerary[InsertingTourIndex][InsertingVisitIndex].constraints.TimeWindow.StartTime)
                - Itinerary[InsertingTourIndex][InsertingVisitIndex].constraints.TimeWindow.DistanceToNextPoint);    
     //System.out.println("Test");
     }
    static clsVisit getNewSwapVisitForRemoveingPart(clsOperatorSolution SwapInSolution) {
        clsVisit result = new clsVisit();
        result.POI_ID = SwapInSolution.InsertedPOI_ID;
        //Anetaret Replay nuk jane caktuar

        clsConstraint Constraint = new clsConstraint();
        clsTimeWindowConstraint TimeWindow = new clsTimeWindowConstraint();
        TimeWindow.UnusedTimeAtTheStartOfTheTour=SwapInSolution.TourUnusedTimeAtTheStart;
        TimeWindow.DistanceFromPrevioiusPoint = SwapInSolution.DistanceFromPrevioiusPoint;
        TimeWindow.DistanceToNextPoint = SwapInSolution.DistanceToNextPoint;
        TimeWindow.EndTime = SwapInSolution.InsertedVisitEndTime;
        TimeWindow.StartTime = SwapInSolution.InsertedVisitStartTime;
        TimeWindow.TourIndeks = SwapInSolution.TourIndex;
        TimeWindow.VisitIndeks = SwapInSolution.VisitIndex;
        Constraint.TimeWindow = TimeWindow;
        result.constraints = Constraint;
        return result;
    }
    static clsVisit getNewSwapVisitForInsertingPart(clsOperatorSolution SwapInSolution) {
        clsVisit result = new clsVisit();
        result.POI_ID = SwapInSolution.RemovedPOI_ID;
        //Anetaret Replay nuk jane caktuar

        clsConstraint Constraint = new clsConstraint();
        clsTimeWindowConstraint TimeWindow = new clsTimeWindowConstraint();
        TimeWindow.UnusedTimeAtTheStartOfTheTour=SwapInSolution.UnusedTimeAtTheStartOfInsertingTour;
        TimeWindow.DistanceFromPrevioiusPoint = SwapInSolution.DistanceFromPreviosPointToInsertedPointInInsertingTour;
        TimeWindow.DistanceToNextPoint = SwapInSolution.DistanceFromInsertedPointToNextPointInInsertingTour;
        TimeWindow.EndTime = SwapInSolution.VisitEndTimeInInsertingTour;
        TimeWindow.StartTime = SwapInSolution.VisitStartTimeInInsertingTour;
        TimeWindow.TourIndeks = SwapInSolution.InsertedPOITourIndex;
        TimeWindow.VisitIndeks = SwapInSolution.InsertedPOIVisitIndex;
        Constraint.TimeWindow = TimeWindow;
        result.constraints = Constraint;
        return result;
    }
    
    static void updateToursLastVisit(int VisitIndexInRemoveingTour, int TourIndexInRemoveingTour, 
            int VisitIndexInInsertingTour, int TourIndexInInsertingTour, 
            int InsertedPOI_ID,int RemovedPOI_ID, int[][] TourLastVisit) {
        if (VisitIndexInRemoveingTour == TourLastVisit[1][TourIndexInRemoveingTour]) {
            TourLastVisit[0][TourIndexInRemoveingTour] = InsertedPOI_ID;
        }
        if (VisitIndexInInsertingTour == TourLastVisit[1][TourIndexInInsertingTour]) {
            TourLastVisit[0][TourIndexInInsertingTour] = RemovedPOI_ID;
        }
    }

}
