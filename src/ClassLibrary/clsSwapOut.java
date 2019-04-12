/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassLibrary;

/**
 *
 * @author Kadri Sylejmani
 */
public class clsSwapOut {
    private clsSolution solution;
    clsOperatorSolution BestNonTabooSolution;
    clsOperatorSolution BestTabooSolution;

    clsSwapOut( clsSolution aSolution) {
        solution = aSolution;
    }

    void createSwapSolution(clsData data,int IterationCounter, int RecencyMemory[][], int TABU_LIST_SIZE) {
        clsOperatorSolution TabooSolution = new clsOperatorSolution();
        clsOperatorSolution NonTabooSolution = new clsOperatorSolution();
        for (int i = 0; i < solution.Itinerary.length &&  solution.Itinerary[i]!=null; i++) {
            for (int j = 0; j <= solution.TourLastVisit[1][i] && solution.TourLastVisit[0][i]!=0; j++) {
                for (int k = 0; k < solution.POIsOffItinerary.size(); k++) {
                    int RemovedPOI_ID = solution.Itinerary[i][j].POI_ID;
                    int InsertedPOI_ID = solution.POIsOffItinerary.get(k);
                    float DifferenceOffUnusedTourTimeAtTheStart=0;
                    float TourUnusedTimeAtTheStart=0;
                    if(j==0){
                        TourUnusedTimeAtTheStart=clsGeneral.getTourUnusedTimeAtTheStart(data,InsertedPOI_ID);
                        DifferenceOffUnusedTourTimeAtTheStart=TourUnusedTimeAtTheStart-
                                solution.Itinerary[i][0].constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour;
                    }
                    float Cost = this.getBudgetFeasibility(data.getBudgetLimitation(), solution.BudgetCost,
                            data.getPOI()[RemovedPOI_ID - 2][10 + data.getNumberOfPersons()],
                            data.getPOI()[InsertedPOI_ID - 2][10 + data.getNumberOfPersons()]);
                    if (Cost >= 0) {//Testimi
                        clsTypeConstraint TypeConstraintFeasibility =
                                this.getTypeConstraintFeasibility(solution.TypeConstraintCounter,
                                data.getMaximumNumberOfVerticesOfTypeZ(),
                                data.getPOI()[RemovedPOI_ID - 2], data.getPOI()[InsertedPOI_ID - 2],
                                data.getNumberOfPersons());
                        if (TypeConstraintFeasibility.Reply == 1) {
                            float[] IndicatedStartAndEndTime = getIndicatedStartAndEndTime(data,i, j, k, solution.TourLastVisit[1][i],
                                    DifferenceOffUnusedTourTimeAtTheStart,TourUnusedTimeAtTheStart);
                            if (IndicatedStartAndEndTime[0] >= 0) {
                                float CurrentSolutionEvaluation =
                                        this.getEvaluation(data,RemovedPOI_ID, InsertedPOI_ID,
                                        solution.Evaluation);
//                                float ShiftingValue=DifferenceOffUnusedTourTimeAtTheStart+
//                                        IndicatedStartAndEndTime[2]+IndicatedStartAndEndTime[3]+
//                                        clsData.getPOI()[InsertedPOI_ID-2][3]-
//                                        solution.Itinerary[i][j].constraints.TimeWindow.DistanceFromPrevioiusPoint-
//                                        solution.Itinerary[i][j].constraints.TimeWindow.DistanceToNextPoint-
//                                        clsData.getPOI()[RemovedPOI_ID-2][3];
                                boolean CandidateSolutionTabooStatus =
                                        this.getCandidateSolutionTabooStatus(RemovedPOI_ID,
                                        InsertedPOI_ID, IterationCounter,
                                        RecencyMemory, TABU_LIST_SIZE);
                                    boolean AcceptanceCriteriaFullfilled=false;
                                if (CandidateSolutionTabooStatus) {
                                    //Te testohet
                                    AcceptanceCriteriaFullfilled=CurrentSolutionEvaluation > TabooSolution.Evaluation;
                                    if (AcceptanceCriteriaFullfilled) {
                                        TabooSolution = this.createCandidateSolution(i, j, k, RemovedPOI_ID,
                                                InsertedPOI_ID, Cost, TypeConstraintFeasibility.POITypeCounter,
                                                IndicatedStartAndEndTime[0],
                                                IndicatedStartAndEndTime[1],
                                                IndicatedStartAndEndTime[2],
                                                IndicatedStartAndEndTime[3],
                                                CurrentSolutionEvaluation,
                                                TourUnusedTimeAtTheStart);
                                    }
                                } else {
                                    AcceptanceCriteriaFullfilled=CurrentSolutionEvaluation > NonTabooSolution.Evaluation;
                                    if (AcceptanceCriteriaFullfilled) {
                                        NonTabooSolution = this.createCandidateSolution(i, j, k, RemovedPOI_ID,
                                                InsertedPOI_ID, Cost, TypeConstraintFeasibility.POITypeCounter,
                                                IndicatedStartAndEndTime[0],
                                                IndicatedStartAndEndTime[1],
                                                IndicatedStartAndEndTime[2],
                                                IndicatedStartAndEndTime[3],
                                                CurrentSolutionEvaluation,
                                                TourUnusedTimeAtTheStart);
                                    }
                                }
                            }
                        } else {
                        }
                    } else {
                    }

                }
            }
        }
        this.BestTabooSolution = TabooSolution;
        this.BestNonTabooSolution = NonTabooSolution;
    }

    float[] getIndicatedStartAndEndTime(clsData data,int POITourIndeks, int POIVisitIndeks, int IndeksOfPOIinOffList, int IndeksOfLastVisitIntour,
            float DifferenceOffUnusedTourTimeAtTheStart, float TourUnusedTimeAtTheStart) {
        float[] result = new float[4];
        float DistanceFromPreviosPointToSwappingPoint;
        float DistanceFromSwappingPointToNextPoint;
        if (POIVisitIndeks == 0) {
            DistanceFromPreviosPointToSwappingPoint =
                    clsGeneral.getDistanceBetweenPoints(1, solution.POIsOffItinerary.get(IndeksOfPOIinOffList), data.getDistance());
        } else {
            DistanceFromPreviosPointToSwappingPoint =
                    clsGeneral.getDistanceBetweenPoints(solution.Itinerary[POITourIndeks][POIVisitIndeks - 1].POI_ID,
                    solution.POIsOffItinerary.get(IndeksOfPOIinOffList), data.getDistance());
        }
        if (POIVisitIndeks == IndeksOfLastVisitIntour) {
            DistanceFromSwappingPointToNextPoint =
                    clsGeneral.getDistanceBetweenPoints(solution.POIsOffItinerary.get(IndeksOfPOIinOffList),
                    1, data.getDistance());
        } else {
            DistanceFromSwappingPointToNextPoint =
                    clsGeneral.getDistanceBetweenPoints(solution.POIsOffItinerary.get(IndeksOfPOIinOffList),
                    solution.Itinerary[POITourIndeks][POIVisitIndeks + 1].POI_ID, data.getDistance());
        }
        float IndicatedStartTime = 0;       
        if(POIVisitIndeks==0)
            IndicatedStartTime=TourUnusedTimeAtTheStart+DistanceFromPreviosPointToSwappingPoint;
        else
            IndicatedStartTime=solution.Itinerary[POITourIndeks][POIVisitIndeks-1].constraints.
                    TimeWindow.EndTime + DistanceFromPreviosPointToSwappingPoint;
           
        
        float IndicatedEndTime = IndicatedStartTime + data.getPOI()[solution.POIsOffItinerary.get(IndeksOfPOIinOffList) - 2][3];
        
        if ((IndicatedStartTime >= data.getPOI()[solution.POIsOffItinerary.get(IndeksOfPOIinOffList) - 2][4 + data.getNumberOfPersons()])
                && (IndicatedEndTime <= data.getPOI()[solution.POIsOffItinerary.get(IndeksOfPOIinOffList) - 2][8 + data.getNumberOfPersons()])) {
            
            
            float IndicatedTourSpentTime = getIndicatedTourSpentTime(data,POITourIndeks, POIVisitIndeks, IndeksOfPOIinOffList,
                    DistanceFromPreviosPointToSwappingPoint,TourUnusedTimeAtTheStart);
            
            
            if (IndicatedTourSpentTime <= data.getStartPoint()[6]) {
                boolean TimeWindowFeasibilityOfFollowingPOIs;
                    TimeWindowFeasibilityOfFollowingPOIs =
                    getTimeWindowFeasibilityOfAffectedPOIs(data,POITourIndeks, POIVisitIndeks,
                    solution.TourLastVisit[1][POITourIndeks], IndeksOfPOIinOffList,
                    solution.Itinerary[POITourIndeks][POIVisitIndeks].constraints.TimeWindow.StartTime,
                    solution.Itinerary[POITourIndeks][POIVisitIndeks].constraints.TimeWindow.EndTime,
                    IndicatedStartTime, IndicatedEndTime,
                    solution.Itinerary[POITourIndeks][POIVisitIndeks].constraints.TimeWindow.DistanceToNextPoint,
                    DistanceFromSwappingPointToNextPoint,
                    solution.MaxShift);

                if (TimeWindowFeasibilityOfFollowingPOIs) {
                    //System.out.println("Test");  
                    result[0] = IndicatedStartTime;
                    result[1] = IndicatedEndTime;
                    result[2] = DistanceFromPreviosPointToSwappingPoint;
                    result[3] = DistanceFromSwappingPointToNextPoint;
                                              
                    
                    //Test
//                    if((IndicatedStartTime <
//                            clsData.getPOI()[solution.POIsOffItinerary.get(IndeksOfPOIinOffList)-2][4+clsData.getNumberOfPersons()]) ||
//                            (IndicatedEndTime > 
//                            clsData.getPOI()[solution.POIsOffItinerary.get(IndeksOfPOIinOffList)-2][8+clsData.getNumberOfPersons()])
//                            ){
//                           System.out.println("Test");
//                    }
//                    
                    //Test
                    
                   
                    
                    
                } else {
                    result[0] = -3; // TimeWindow Feasibility Of Preceding POIs is not OK
                }
            } else {
                result[0] = -2;//No time in tour
            }
        } else {
            result[0] = -1;//No availabel time window
        }
        return result;
    }

    float getIndicatedTourSpentTime(clsData data,int POITourIndeks, int POIVisitIndeks, int IndeksOfPOIinOffList, 
            float DistanceFromPreviosPointToSwappingPoint,float UnusedTourTimeAtTheStart) {
        float result;
        float DistanceFromSwappingPointToNextPoint;
        if (POIVisitIndeks == solution.TourLastVisit[1][POITourIndeks]) {
            DistanceFromSwappingPointToNextPoint = clsGeneral.getDistanceBetweenPoints(solution.POIsOffItinerary.get(IndeksOfPOIinOffList),
                    1, data.getDistance());
        } else {

               DistanceFromSwappingPointToNextPoint = clsGeneral.getDistanceBetweenPoints(solution.POIsOffItinerary.get(IndeksOfPOIinOffList),
                    solution.Itinerary[POITourIndeks][POIVisitIndeks + 1].POI_ID, data.getDistance());// Gabim :OK - java.lang.NullPointerException

        }
        result  =  solution.TourTimeSpent[POITourIndeks]
                - solution.Itinerary[POITourIndeks][POIVisitIndeks].constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour
                - solution.Itinerary[POITourIndeks][POIVisitIndeks].constraints.TimeWindow.DistanceFromPrevioiusPoint
                - data.getPOI()[solution.Itinerary[POITourIndeks][POIVisitIndeks].POI_ID - 2][3]
                - solution.Itinerary[POITourIndeks][POIVisitIndeks].constraints.TimeWindow.DistanceToNextPoint
                + UnusedTourTimeAtTheStart
                + DistanceFromPreviosPointToSwappingPoint
                + data.getPOI()[solution.POIsOffItinerary.get(IndeksOfPOIinOffList) - 2][3]
                + DistanceFromSwappingPointToNextPoint;
        return result;
    }

    float getBudgetFeasibility(float BudgetLimit, float ActualCost,
            float RemovedPOIEntreeFee, float InsertedPOIEntreeFee) {
        float result = ActualCost - RemovedPOIEntreeFee + InsertedPOIEntreeFee;
        if (result > BudgetLimit) {
            result = -1;
        }
        return result;
    }

    clsTypeConstraint getTypeConstraintFeasibility(int TypeCounter[], float TypeLimit[],
            float RemovedPOI[], float InsertedPOI[],
            int NumberOfTourists) { //Testimi OK
        clsTypeConstraint result = new clsTypeConstraint();
        result.Reply = 1;
        for (int i = 0; i < TypeCounter.length; i++) {
            result.POITypeCounter[i] = TypeCounter[i]
                    - (int) RemovedPOI[i + 11 + NumberOfTourists] + (int) InsertedPOI[i + 11 + NumberOfTourists];
            if (result.POITypeCounter[i] > TypeLimit[i]) {
                result.Reply = -1;//TypeConstraintViolation
                break;
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

    clsOperatorSolution createCandidateSolution(int TourIndex, int VisitIndex, int IndexInOffList,
            int RemovedPOI_ID, int InsertedPOI_ID, float Cost,
            int TypeConstraint[], float VisitStartTime, float VisitEndTime, float DitanceFromPreviousPoint,
            float DistanceToNextPoint, float CurrentSolutionEvaluation,float TourUnusedTimeAtTheStart) {
        clsOperatorSolution result = new clsOperatorSolution();
        result.Cost = Cost;
        result.Evaluation = CurrentSolutionEvaluation;
        result.InsertedPOI_ID = InsertedPOI_ID;
        result.RemovedPOI_ID = RemovedPOI_ID;
        result.TourUnusedTimeAtTheStart=TourUnusedTimeAtTheStart;
        result.InsertedVisitStartTime = VisitStartTime;
        result.InsertedVisitEndTime = VisitEndTime;
        result.DistanceFromPrevioiusPoint = DitanceFromPreviousPoint;
        result.DistanceToNextPoint = DistanceToNextPoint;
        result.TourIndex = TourIndex;
        result.VisitIndex = VisitIndex;
        result.RemoveIndexInOffList = IndexInOffList;
        System.arraycopy(TypeConstraint, 0, result.TypeConstraintCounter,
                0, TypeConstraint.length);
        return result;
    }

    private float getEvaluation(clsData data,int RemovedPOI_ID,
            int InsertedPOI_ID, float CurrentSolutionEvaluation) {//Testimi OK
        float result = CurrentSolutionEvaluation;
        for (int i = 0; i < data.getNumberOfPersons(); i++) {
            result = result - data.getPOI()[RemovedPOI_ID - 2][4 + i]
                    + data.getPOI()[InsertedPOI_ID - 2][4 + i];
        }
        return (result);
    }

    static clsVisit getNewSwapVisit(clsOperatorSolution SwapSolution) {
        clsVisit result = new clsVisit();
        result.POI_ID = SwapSolution.InsertedPOI_ID;
        result.RemoveIndexInOffList = SwapSolution.RemoveIndexInOffList;
        //Anetaret Replay nuk jane caktuar

        clsConstraint Constraint = new clsConstraint();
        clsTimeWindowConstraint TimeWindow = new clsTimeWindowConstraint();
        TimeWindow.UnusedTimeAtTheStartOfTheTour=SwapSolution.TourUnusedTimeAtTheStart;
        TimeWindow.DistanceFromPrevioiusPoint = SwapSolution.DistanceFromPrevioiusPoint;
        TimeWindow.DistanceToNextPoint = SwapSolution.DistanceToNextPoint;
        TimeWindow.EndTime = SwapSolution.InsertedVisitEndTime;
        TimeWindow.StartTime = SwapSolution.InsertedVisitStartTime;
        TimeWindow.TourIndeks = SwapSolution.TourIndex;
        TimeWindow.VisitIndeks = SwapSolution.VisitIndex;
        Constraint.TimeWindow = TimeWindow;
        result.constraints = Constraint;
        return result;
    }

    static void updateTimeSpent(float TimeSpent[], clsVisit Itinerary[][], int SwapTourIndex, int SwapVisitIndex,
            float SwapDistanceFromPrevioiusPoint, float SwapInsertedVisitEndTime,
            float SwapInsertedVisitStartTime, float SwapDistanceToNextPoint,float TourUnusedTimeAtTheStart) {
        TimeSpent[SwapTourIndex] +=
                (
                + TourUnusedTimeAtTheStart
                + SwapDistanceFromPrevioiusPoint
                + (SwapInsertedVisitEndTime - SwapInsertedVisitStartTime)
                + SwapDistanceToNextPoint
                - Itinerary[SwapTourIndex][SwapVisitIndex].constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour
                - Itinerary[SwapTourIndex][SwapVisitIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint
                - (Itinerary[SwapTourIndex][SwapVisitIndex].constraints.TimeWindow.EndTime - 
                   Itinerary[SwapTourIndex][SwapVisitIndex].constraints.TimeWindow.StartTime)
                - Itinerary[SwapTourIndex][SwapVisitIndex].constraints.TimeWindow.DistanceToNextPoint
                );
    }

    static void updateTourLastVisit(int SwapVisitIndex, int SwapTourIndex, int InsertedPOI_ID, int[][] TourLastVisit) {
        if (SwapVisitIndex == TourLastVisit[1][SwapTourIndex]) {
            TourLastVisit[0][SwapTourIndex] = InsertedPOI_ID;
        }
    }

    static float[] calculateMaxShift(float MaxShift[][][], int TourIndex, int VisitIndex,
            float RemovedPOIStartTime, float RemovedPOIEndTime, float InsertedPOIStartTime,
            float InsertedPOIEndTime) {
        float result[] = new float[2];
        result[0] = MaxShift[TourIndex][0][VisitIndex] + (RemovedPOIEndTime - InsertedPOIEndTime);
        result[1] = MaxShift[TourIndex][1][VisitIndex] + (InsertedPOIStartTime - RemovedPOIStartTime);
   
        //Test
      
//            if (result[0] < 0) {
//                System.out.println("Test");
//            }
//            if (result[1] < 0) {
//                System.out.println("Test");
//            }
            
        //Test
        return result;
    }

    
    
    
 boolean getTimeWindowFeasibilityOfAffectedPOIs(clsData data,int TourIndex,int VisitIndex,int TourLastVisit, int IndexOfPOIInOffList,
            float RemovedPOIStartTime, float RemovedPOIEndTime, //Te sigurohemi qe kjo kontrollon fizibiitetin per tere itinerarin...
            float InsertedPOIStartTime, float InsertedPOIEndTime, float DistanceFromRemovedPointToNextPoint,
            float DistanceFromSwappingPointToNextPoint,float MaxShift[][][]){//Duket te funksionoj mire!
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
                        if(    (RemovedPOIStartTime-InsertedPOIStartTime
                                +DistanceFromRemovedPointToNextPoint-DistanceFromSwappingPointToNextPoint
                                +data.getPOI()[solution.Itinerary[TourIndex][VisitIndex].POI_ID-2][3]
                                -data.getPOI()[solution.POIsOffItinerary.get(IndexOfPOIInOffList)-2][3]
                                )
                                <=MaxShift[TourIndex][1][VisitIndex+1])//LeftShiftFeasibility  of Preceiding POIs
                        {
                            result=true;
//                            Test
//                                float MaxRight=MaxShift[TourIndex][0][VisitIndex+1]+(RemovedPOIEndTime-InsertedPOIEndTime)+
//                                        (DistanceFromRemovedPointToNextPoint-DistanceFromSwappingPointToNextPoint);
//                                float MaxLeft=MaxShift[TourIndex][1][VisitIndex+1]+(InsertedPOIStartTime-RemovedPOIStartTime)
//                                        -DistanceFromRemovedPointToNextPoint+DistanceFromSwappingPointToNextPoint
//                                        -clsData.getPOI()[solution.Itinerary[TourIndex][VisitIndex].POI_ID-2][3]
//                                        +clsData.getPOI()[solution.POIsOffItinerary.get(IndexOfPOIInOffList)-2][3];
//                              
//                                if(MaxRight<0)
//                                        System.out.println("Test");
//                                if(MaxLeft<0)
//                                        System.out.println("Test");
//                           Test
                        }
                    }                    
                }
            }
        }
        return result;
    }
}
