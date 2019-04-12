/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassLibrary;
/**
 *
 * @author user
 */
public class clsInsert {
    private clsSolution solution;
    clsOperatorSolution BestNonTabooSolution;
    clsOperatorSolution BestTabooSolution;

    clsInsert( clsSolution aSolution) {
        solution = aSolution;
    }
    
    void createInsertSolution(clsData data,int IterationCounter, int RecencyMemory[][], int TABU_LIST_SIZE) {
        clsOperatorSolution TabooSolution = new clsOperatorSolution();
        clsOperatorSolution NonTabooSolution = new clsOperatorSolution();
        for (int i = 0; i < solution.Itinerary.length ; i++) {
//            float TimeInterval;
//            if(solution.TourLastVisit[1][i]==0){
//                TimeInterval=clsData.getAverageDistance();
//            }
//            else{
//                TimeInterval=solution.TourTimeSpent[i]-
//                solution.Itinerary[i][solution.TourLastVisit[1][i]].constraints.TimeWindow.DistanceToNextPoint+
//                        clsData.getAverageDistance();
//            }
//            ArrayList<Integer> OpenPOIs=clsCandidate.getOpenPOIs(TimeInterval, clsData.getNumberOfVertices(), 
//                    clsData.getNumberOfPersons(),solution.POIsOffItinerary);
            for (int k = 0; k < solution.POIsOffItinerary.size(); k++) {
                int InsertedPOI_ID = solution.POIsOffItinerary.get(k);  
                int j = solution.TourLastVisit[1][i]+1; /* (+1) - Tries to insert a POI after last point*/ //Kujdes me vleren e j-se
                float ShiftingValue;
                float DifferenceOffUnusedTourTimeAtTheStart=0;
                float TourUnusedTimeAtTheStart=0;
                boolean CurrentPOIInsertionPossibleInTour;
                 do {
                    float DistanceFromPreviosPointToInsertedPoint=this.getDistanceFromPreviosPointToInsertedPoint(data,i, j, k);
                    float DistanceFromInsertedPointToNextPoint=this.getDistanceFromInsertedPointToNextPoint(data,i, j, k, 
                            solution.TourLastVisit[1][i]);

                    if(j==0){
                        TourUnusedTimeAtTheStart=clsGeneral.getTourUnusedTimeAtTheStart(data,InsertedPOI_ID);
                        DifferenceOffUnusedTourTimeAtTheStart=TourUnusedTimeAtTheStart-
                                solution.Itinerary[i][0].constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour;
                    }
                    if(j==solution.TourLastVisit[1][i]+1){
                        ShiftingValue=0;
                    }
                    else{
                        ShiftingValue=this.getShiftingValue(data,k, DistanceFromPreviosPointToInsertedPoint,
                                DistanceFromInsertedPointToNextPoint,
                                solution.Itinerary[i][j].constraints.TimeWindow.DistanceFromPrevioiusPoint ,
                                DifferenceOffUnusedTourTimeAtTheStart);
                        CurrentPOIInsertionPossibleInTour=
                           ( ShiftingValue<=solution.MaxShift[i][0][j] ) &&
                           ( -ShiftingValue<=solution.MaxShift[i][1][j] ) ; 
                        if(!CurrentPOIInsertionPossibleInTour)
                            break;
                    }
                    float Cost = this.getBudgetFeasibility(data.getBudgetLimitation(), solution.BudgetCost,
                               data.getPOI()[InsertedPOI_ID - 2][10 + data.getNumberOfPersons()]);
                    if (Cost >= 0) {//Testimi
                        clsTypeConstraint TypeConstraintFeasibility =
                                this.getTypeConstraintFeasibility(solution.TypeConstraintCounter,
                                data.getMaximumNumberOfVerticesOfTypeZ(),data.getPOI()[InsertedPOI_ID - 2],
                                data.getNumberOfPersons());
                        if (TypeConstraintFeasibility.Reply == 1) {
                            float[] IndicatedStartAndEndTime = getIndicatedStartAndEndTime(data,i, j, k, solution.TourLastVisit[1][i],
                                    ShiftingValue,DistanceFromPreviosPointToInsertedPoint,
                                    DistanceFromInsertedPointToNextPoint,DifferenceOffUnusedTourTimeAtTheStart,
                                    TourUnusedTimeAtTheStart);
                            if (IndicatedStartAndEndTime[0] >= 0) {
                                float CurrentSolutionEvaluation =
                                        this.getEvaluation(data,InsertedPOI_ID, solution.Evaluation);
                         
                                boolean CandidateSolutionTabooStatus=false;
                                int ShiftedPOI_ID=-1;   //If insertion is at the end of tour, the no POI is shifted (ShiftedPOI_ID=-1)                               
                                boolean AcceptanceCriteriaFullfilled=false;
                                if(j!=solution.TourLastVisit[1][i]+1){// If insertion is at the end of tour, 
                                                                      // then candidate is automatically not taboo
                                    ShiftedPOI_ID=solution.Itinerary[i][j].POI_ID; 
                                    CandidateSolutionTabooStatus =
                                        this.getCandidateSolutionTabooStatus(
                                             ShiftedPOI_ID,
                                             InsertedPOI_ID, IterationCounter,
                                             RecencyMemory, TABU_LIST_SIZE);
                                     
                                }
                                if (CandidateSolutionTabooStatus) {                                   
                                    AcceptanceCriteriaFullfilled=CurrentSolutionEvaluation > TabooSolution.Evaluation;
                                    if (AcceptanceCriteriaFullfilled) {
                                        TabooSolution = this.createCandidateSolution(i, j, k, ShiftedPOI_ID,
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
                                        NonTabooSolution = this.createCandidateSolution(i, j, k, ShiftedPOI_ID,
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
                        } 
                        else 
                        {
                        }
                    } 
                    else 
                    {
                    }
                   j--;
                } while(j>=0 );
            }
        }
        this.BestTabooSolution = TabooSolution;
        this.BestNonTabooSolution = NonTabooSolution;
        //System.out.println("Test");
    }
    
    float getBudgetFeasibility(float BudgetLimit, float ActualCost,
             float InsertedPOIEntreeFee) {
        float result = ActualCost  + InsertedPOIEntreeFee;
        if (result > BudgetLimit) {
            result = -1;
        }
        return result;
    }
    clsTypeConstraint getTypeConstraintFeasibility(int TypeCounter[], 
            float TypeLimit[], float InsertedPOI[],
            int NumberOfTourists) { //Testimi OK
        clsTypeConstraint result = new clsTypeConstraint();
        result.Reply = 1;
        for (int i = 0; i < TypeCounter.length; i++) {
            result.POITypeCounter[i] = 
                    TypeCounter[i] + (int) InsertedPOI[i + 11 + NumberOfTourists];
            if (result.POITypeCounter[i] > TypeLimit[i]) {
                result.Reply = -1;//TypeConstraintViolation
                break;
            }
        }
        return result;
    }
    
     float[] getIndicatedStartAndEndTime(clsData data,int POITourIndeks, int POIVisitIndeks, int IndeksOfPOIinOffList, 
             int IndeksOfLastVisitIntour, float ShiftingValue,    float DistanceFromPreviosPointToInsertedPoint,
        float DistanceFromInsertedPointToNextPoint,float DifferenceOffUnusedTourTimeAtTheStart,
        float TourUnusedTimeAtTheStart) {

        float[] result = new float[4];

        float IndicatedStartTime;
        if(POIVisitIndeks==0)
            IndicatedStartTime=TourUnusedTimeAtTheStart+DistanceFromPreviosPointToInsertedPoint;
        else
            IndicatedStartTime=solution.Itinerary[POITourIndeks][POIVisitIndeks-1].constraints.
                    TimeWindow.EndTime + DistanceFromPreviosPointToInsertedPoint;
        float IndicatedEndTime = IndicatedStartTime + data.getPOI()[solution.POIsOffItinerary.get(IndeksOfPOIinOffList) - 2][3];

        if ((IndicatedStartTime >= data.getPOI()[solution.POIsOffItinerary.get(IndeksOfPOIinOffList) - 2][4 + data.getNumberOfPersons()])
                && (IndicatedEndTime <= data.getPOI()[solution.POIsOffItinerary.get(IndeksOfPOIinOffList) - 2][8 + data.getNumberOfPersons()])) {
            float IndicatedTourSpentTime = getIndicatedTourSpentTime(data,POITourIndeks, POIVisitIndeks, IndeksOfPOIinOffList,
                    DistanceFromPreviosPointToInsertedPoint,DistanceFromInsertedPointToNextPoint,
                    DifferenceOffUnusedTourTimeAtTheStart);
            if (IndicatedTourSpentTime <= data.getStartPoint()[6]) {
                //Si duket nuk ka nevoj te kontrollohet fizibiliteti i pikave pasuese, per shkak se kontrollohet me unazen do-while
//                boolean TimeWindowFeasibilityOfPrecedingPOIs;

//                    TimeWindowFeasibilityOfPrecedingPOIs =
//                    getTimeWindowFeasibilityOfShiftedPOIs(POITourIndeks, POIVisitIndeks,
//                            solution.TourLastVisit[1][POITourIndeks],       
//                            ShiftingValue,
//                            solution.MaxShift);

//                if (TimeWindowFeasibilityOfPrecedingPOIs) {
                    result[0] = IndicatedStartTime;
                    result[1] = IndicatedEndTime;
                    result[2] = DistanceFromPreviosPointToInsertedPoint;
                    result[3] = DistanceFromInsertedPointToNextPoint;
                    

//                } else {
//                    result[0] = -3; // TimeWindow Feasibility Of Preceding POIs is not OK
//                }
            } else {
                result[0] = -2;//No time in tour
            }
        } else {
            result[0] = -1;//No availabel time window
        }
        return result;
    }
     
float getIndicatedTourSpentTime(clsData data,int POITourIndeks, int POIVisitIndeks, int IndeksOfPOIinOffList, 
        float DistanceFromPreviosPointToInsertedPoint, float DistanceFromInsertedPointToNextPoint,
       float DifferenceOffUnusedTourTimeAtTheStart) {
        float result;
        float DistanceToRemove;
        if(POIVisitIndeks > solution.TourLastVisit[1][POITourIndeks])
            DistanceToRemove=solution.Itinerary[POITourIndeks][POIVisitIndeks-1].constraints.TimeWindow.DistanceToNextPoint;
        else
            DistanceToRemove=solution.Itinerary[POITourIndeks][POIVisitIndeks].constraints.TimeWindow.DistanceFromPrevioiusPoint;
        result = solution.TourTimeSpent[POITourIndeks]
                +DifferenceOffUnusedTourTimeAtTheStart
                - DistanceToRemove
                + DistanceFromPreviosPointToInsertedPoint
                + data.getPOI()[solution.POIsOffItinerary.get(IndeksOfPOIinOffList) - 2][3]
                + DistanceFromInsertedPointToNextPoint;

        return result;
    }

float getShiftingValue(clsData data,int IndeksOfPOIinOffList, float DistanceFromPreviosPointToInsertedPoint,
        float DistanceFromInsertedPointToNextPoint,float DistanceFromPrevioiusPoint,
        float DifferenceOffUnusedTourTimeAtTheStart){
    float result=
                + DifferenceOffUnusedTourTimeAtTheStart
                + DistanceFromPreviosPointToInsertedPoint
                + data.getPOI()[solution.POIsOffItinerary.get(IndeksOfPOIinOffList) - 2][3]
                + DistanceFromInsertedPointToNextPoint
                    - DistanceFromPrevioiusPoint;
    
    return result;
}

boolean getTimeWindowFeasibilityOfShiftedPOIs(int TourIndex,int VisitIndex,int TourLastVisit,
              float ShiftingValue,float MaxShift[][][]){
        boolean result=false;
        
        if(VisitIndex== TourLastVisit+1){
            result=true;
        }
        else{

            if(ShiftingValue<=MaxShift[TourIndex][0][VisitIndex]){//RightShiftFeasibility of Inserted POI
                if(-ShiftingValue <=MaxShift[TourIndex][1][VisitIndex]){//LeftShiftFeasibility of Inserted POI
                    result=true;
                }
            }  
        }
        return result;
    }

float getDistanceFromPreviosPointToInsertedPoint(clsData data,int POITourIndeks,int POIVisitIndeks,
        int IndeksOfPOIinOffList){
    float result;
    if (POIVisitIndeks == 0) {
        result =
                clsGeneral.getDistanceBetweenPoints(1, solution.POIsOffItinerary.get(IndeksOfPOIinOffList), data.getDistance());
    } 
    else {
        try{
                result =
                clsGeneral.getDistanceBetweenPoints(solution.Itinerary[POITourIndeks][POIVisitIndeks - 1].POI_ID,
                solution.POIsOffItinerary.get(IndeksOfPOIinOffList), data.getDistance());
           }
        catch (java.lang.NullPointerException ex){
            System.out.println("Test");
        }
        
        result =
                clsGeneral.getDistanceBetweenPoints(solution.Itinerary[POITourIndeks][POIVisitIndeks - 1].POI_ID,
                solution.POIsOffItinerary.get(IndeksOfPOIinOffList), data.getDistance());
    }
    return result;
}

float getDistanceFromInsertedPointToNextPoint(clsData data,int POITourIndeks,int POIVisitIndeks,int IndeksOfPOIinOffList,
        int IndeksOfLastVisitIntour){
    float result;
        if (POIVisitIndeks == IndeksOfLastVisitIntour+1) {
        result =
                clsGeneral.getDistanceBetweenPoints(solution.POIsOffItinerary.get(IndeksOfPOIinOffList),
                1, data.getDistance());
        } 
        else {
        result =
                clsGeneral.getDistanceBetweenPoints(solution.POIsOffItinerary.get(IndeksOfPOIinOffList),
                solution.Itinerary[POITourIndeks][POIVisitIndeks].POI_ID, data.getDistance());
        }
    return result;
}
    private float getEvaluation(clsData data,int InsertedPOI_ID, float CurrentSolutionEvaluation) {
        float result = CurrentSolutionEvaluation;
        for (int i = 0; i < data.getNumberOfPersons(); i++) {
            result = result + data.getPOI()[InsertedPOI_ID - 2][4 + i];
        }
        return result;

    }
    
boolean getCandidateSolutionTabooStatus(int ShiftedPOI_ID, int InsertedPOI_ID,int iterationIndeks,
            int RecencyMemory[][], int TABU_LIST_SIZE) {
        boolean result = false;
            boolean isShiftedPOI_IDSmaller = ShiftedPOI_ID < InsertedPOI_ID;
            if (isShiftedPOI_IDSmaller) {
                //If value of a member in memory is 0 then swap is not tabu
                    if (!(RecencyMemory[ShiftedPOI_ID - 1][InsertedPOI_ID - 2] == 0)) {
                        result = iterationIndeks - RecencyMemory[ShiftedPOI_ID - 1][InsertedPOI_ID - 2] < TABU_LIST_SIZE;
                }
            } 
            else {
                    if (!(RecencyMemory[InsertedPOI_ID - 1][ShiftedPOI_ID - 2] == 0)) {
                        result = iterationIndeks - RecencyMemory[InsertedPOI_ID - 1][ShiftedPOI_ID - 2] < TABU_LIST_SIZE;
                 }
             }
        return result;
    }

    private clsOperatorSolution createCandidateSolution(int TourIndex, int VisitIndex, int IndexInOffList,
            int ShiftedPOI_ID, int InsertedPOI_ID, float Cost,
            int TypeConstraint[], float VisitStartTime, float VisitEndTime, float DitanceFromPreviousPoint,
            float DistanceToNextPoint, float CurrentSolutionEvaluation, float TourUnusedTimeAtTheStart) {
        clsOperatorSolution result = new clsOperatorSolution();
        result.Cost = Cost;
        result.Evaluation = CurrentSolutionEvaluation;
        result.InsertedPOI_ID = InsertedPOI_ID;
        result.RemovedPOI_ID = ShiftedPOI_ID;
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
    
    static void updateTimeSpent(float TimeSpent[], clsVisit Itinerary[][], int InsertTourIndex, int InsertVisitIndex,
            float InsertDistanceFromPrevioiusPoint, float InsertInsertedVisitEndTime,
            float InsertInsertedVisitStartTime, float InsertDistanceToNextPoint,
            int LastVisitInTour,float TourUnusedTimeAtTheStart) {
        
       if(InsertVisitIndex>LastVisitInTour){
           TimeSpent[InsertTourIndex] +=
                   (InsertDistanceFromPrevioiusPoint+
                   (InsertInsertedVisitEndTime - InsertInsertedVisitStartTime)
                   + InsertDistanceToNextPoint
                   -Itinerary[InsertTourIndex][InsertVisitIndex-1].constraints.TimeWindow.DistanceToNextPoint);
       }
       else{
        TimeSpent[InsertTourIndex] +=
                ( 
                TourUnusedTimeAtTheStart
                + InsertDistanceFromPrevioiusPoint
                + (InsertInsertedVisitEndTime - InsertInsertedVisitStartTime)
                + InsertDistanceToNextPoint
                - Itinerary[InsertTourIndex][InsertVisitIndex].constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour
                - Itinerary[InsertTourIndex][InsertVisitIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint
                ); 
       }
       //System.out.println("Test");
    }
    static clsVisit getNewInsertVisit(clsOperatorSolution InsertSolution) {
        clsVisit result = new clsVisit();
        result.POI_ID = InsertSolution.InsertedPOI_ID;
        result.RemoveIndexInOffList = InsertSolution.RemoveIndexInOffList;
        //Anetaret Replay nuk jane caktuar

        clsConstraint Constraint = new clsConstraint();
        clsTimeWindowConstraint TimeWindow = new clsTimeWindowConstraint();
        TimeWindow.UnusedTimeAtTheStartOfTheTour=InsertSolution.TourUnusedTimeAtTheStart;
        TimeWindow.DistanceFromPrevioiusPoint = InsertSolution.DistanceFromPrevioiusPoint;
        TimeWindow.DistanceToNextPoint = InsertSolution.DistanceToNextPoint;
        TimeWindow.EndTime = InsertSolution.InsertedVisitEndTime;
        TimeWindow.StartTime = InsertSolution.InsertedVisitStartTime;
        TimeWindow.TourIndeks = InsertSolution.TourIndex;
        TimeWindow.VisitIndeks = InsertSolution.VisitIndex;
        Constraint.TimeWindow = TimeWindow;
        result.constraints = Constraint;
        return result;
    }
    static void updateTourLastVisit(int InsertVisitIndex, int InsertTourIndex, int InsertedPOI_ID, int[][] TourLastVisit) {
        if (InsertVisitIndex > TourLastVisit[1][InsertTourIndex]) {
            TourLastVisit[0][InsertTourIndex] = InsertedPOI_ID;
        }
        TourLastVisit[1][InsertTourIndex] ++;
    }
    static void updateItinerary(clsVisit CurrentItinerary[][], int CurrentFlatItinerary[][],
            clsVisit NewVisit,int TourIndex, int VisitIndex, int IndexOfLastVisitInTour){
        if(VisitIndex<=IndexOfLastVisitInTour){
            for(int i=IndexOfLastVisitInTour;i>=VisitIndex;i--){
                CurrentItinerary[TourIndex][i+1]= CurrentItinerary[TourIndex][i];
                //shkruar me 14.10.2012
                if(i==0)// if first visit is moved then it can have no used time at the start of the tour.
                {
                    CurrentItinerary[TourIndex][i+1].constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour=0;
                }
                //shkruar me 14.10.2012
                CurrentFlatItinerary[TourIndex][i+1]= CurrentFlatItinerary[TourIndex][i];
            }
        }
        CurrentItinerary[TourIndex][VisitIndex]=NewVisit;
        CurrentFlatItinerary[TourIndex][VisitIndex]=NewVisit.POI_ID;  
    }
    
   
}
