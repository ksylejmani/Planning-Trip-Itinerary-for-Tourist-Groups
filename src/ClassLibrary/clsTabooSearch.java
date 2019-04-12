/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassLibrary;


/**
 *
 * @author Kadri Sylejmani
 */

    public class clsTabooSearch {
    private int RecencyMemory[][];
    private int FrequencyMemory[][];
    private int FREQUENCY_MEMORY_HORIZON=150;
    private int INITIAL_TABU_LIST_SIZE=10;        
    int TABU_LIST_SIZE=INITIAL_TABU_LIST_SIZE;
    private int MAX_ITERATIONS=46000;
    private int DELETE_OPERATOR_FREQUENCY=30;
    private int ITERATIONS_TO_RESTART_FROM_PERTURBATED_BEST_FOUND_SOLUTION=150;
    private int ITERATIONS_TO_RESTART_FROM_RANDOM_SOLUTION=160;
    final int   DIVERSIFICATION_ITERATIONS=240;
    final int   DIVERSIFICATION_APPLICATION_LIMIT=10;
    final int   TWO_OPT_OPERATOR_FREQUENCY=30;
    final int   HeuristicFunctionWeightStep=1250;
    int   MinConflictsIteration=10000000;
    

    clsTabooSearch(){

    }
    clsSolution findSolution(clsData data){
        RecencyMemory=new int[data.getNumberOfVertices()][data.getNumberOfVertices()];
        FrequencyMemory=new int[data.getNumberOfVertices()][data.getNumberOfVertices()];
        final int DIVERSIFICATION_TABU_LIST_SIZE=2*TABU_LIST_SIZE;
        final int ITERATIONS_WITHOUT_IMPROVEMENT_LIMIT = (int)(MAX_ITERATIONS*0.3);
        clsSolution CurrentSolution;
        clsInitialSolution InitialSolution=new clsInitialSolution();
        CurrentSolution=InitialSolution.getInitialSolution(data);
        clsSolution BestSolution=new clsSolution(data,CurrentSolution);
        int  IterationCounter=1;
        int IterationWithoutImprovement=0;
        int operator;
        while(IterationCounter<=MAX_ITERATIONS){
            operator=this.getOperatorNumber(IterationCounter);
            clsOperatorSolution OperatorSolution []= this.getOperatorSolution(data,CurrentSolution,IterationCounter,operator,
                    BestSolution.Evaluation,TABU_LIST_SIZE); //NonTaboo - OperatorSolution[0] ;  Taboo-OperatorSolution[1];
            this.adaptCurrentSolution(data,CurrentSolution, OperatorSolution[0], OperatorSolution[1], 
                    IterationCounter, operator, BestSolution.Evaluation,BestSolution.TripTimeCost);
            
            if(     CurrentSolution.Evaluation>BestSolution.Evaluation ||               
                    (CurrentSolution.Evaluation==BestSolution.Evaluation && 
                     CurrentSolution.TripTimeCost<BestSolution.TripTimeCost) 
              )
            {
                    BestSolution=this.updateBestSolution(data,CurrentSolution);
//                    System.out.println("BestSolution improvement at Iteration:"+IterationCounter+" "+BestSolution.Evaluation);
                    IterationWithoutImprovement=0;
            }
            else
            {
                IterationWithoutImprovement++;
            }
            if(IterationWithoutImprovement > 0 && IterationWithoutImprovement%DELETE_OPERATOR_FREQUENCY==0){
               if(clsGeneral.EveryTourHasAtLeastTwoPOIs(CurrentSolution)){
                    this.applyDeleteOperator(data,CurrentSolution,IterationCounter); 
                }
               if(clsGeneral.EveryTourHasAtLeastTwoPOIs(CurrentSolution)){
                    this.applyDeleteOperator(data,CurrentSolution,IterationCounter); 
                }
            }  
           if(IterationWithoutImprovement > 0 && IterationWithoutImprovement%ITERATIONS_TO_RESTART_FROM_PERTURBATED_BEST_FOUND_SOLUTION==0){
               CurrentSolution =new clsSolution(data,BestSolution);             
               if(clsGeneral.EveryTourHasAtLeastTwoPOIs(CurrentSolution)){
                    this.applyDeleteOperator(data,CurrentSolution,IterationCounter); 
                }
               if(clsGeneral.EveryTourHasAtLeastTwoPOIs(CurrentSolution)){
                    this.applyDeleteOperator(data,CurrentSolution,IterationCounter); 
                }
            }
            if(IterationWithoutImprovement > 0 && IterationWithoutImprovement%ITERATIONS_TO_RESTART_FROM_RANDOM_SOLUTION==0){
                clsInitialSolution RandomSolution=new clsInitialSolution();
                CurrentSolution =RandomSolution.getInitialSolution(data);
            }
            if(IterationWithoutImprovement > 0 && IterationWithoutImprovement%DIVERSIFICATION_ITERATIONS==0){
                clsDiversification.updateRecencyMemory(RecencyMemory, FrequencyMemory, 
                        IterationCounter, DIVERSIFICATION_APPLICATION_LIMIT, DIVERSIFICATION_TABU_LIST_SIZE);
            }
           
            if(IterationWithoutImprovement>ITERATIONS_WITHOUT_IMPROVEMENT_LIMIT){
                break;
            }
              //clsTest.StartTimeTest(CurrentSolution);
             //clsTest.OnlyStartTimeTest(CurrentSolution);

            IterationCounter++; 
        }
        //Prova       
//        clsTest.PrintTrip(data,BestSolution);
//        System.out.println(" Evaluation OK:"+clsTest.chechkEvaluation(data,BestSolution));  
//        System.out.println(" BudgetCost OK:"+clsTest.chechkCost(data,BestSolution));        
//        System.out.println("TypeConstraintCounting OK :"+clsTest.chechkTypeConstraintCounting(data,BestSolution));
//        System.out.println("TypeConstraintLimit OK :"+clsTest.chechkTypeConstraintLimit(data,BestSolution));
//        System.out.println("TimeSpent OK :"+clsTest.chechkTripTimeSpent(data,BestSolution));
//        clsTest.PrintMemory(RecencyMemory,"RecencyMemory");
//        clsTest.PrintMemory(FrequencyMemory,"FrequencyMemory");
//        System.out.println("Test cost :"+clsTest.chechkCost(data,BestSolution));
//        System.out.println("Generated cost :"+BestSolution.BudgetCost);
//        System.out.println("TimeWindows OK :"+clsTest.chechkTimeWindows(data,BestSolution));
//        clsTest.OnlyStartTimeTest(data,BestSolution);
//        clsTest.generateSwapInCombination(BestSolution);
//        System.out.println("Distance:"+data.getAverageDistance());       
//        clsTest.chechkTimeWindows(data,BestSolution);
         //Prova   
        return BestSolution;
    
    }
    int getOperatorNumber(int IterationCounter){
        int rez;
        if(IterationCounter % 2==0)
            rez=1;
        else{
            if(IterationCounter % 4==1)
                rez=2;
            else 
                rez=3;
        }      
        return rez;
    }
   int getRandomOperatorNumber(){
        int rez;
        rez=clsGeneral.getRandomNumber(4);
        if(rez==0)
            rez=2;
        return rez;
    }
clsOperatorSolution [] getOperatorSolution(clsData data,clsSolution CurrentSolution,int IterationCounter, 
        int operator,float BestSolutionEvaluation,  int TABU_LIST_SIZE){
    clsOperatorSolution [] result=new clsOperatorSolution [2];
        switch(operator)
        {

            case 1: // SWAP OUT
            {
                clsSwapOut swap=new clsSwapOut(CurrentSolution);
                swap.createSwapSolution(data,IterationCounter,RecencyMemory,TABU_LIST_SIZE);
                result[0]=swap.BestNonTabooSolution;
                result[1]=swap.BestTabooSolution;
                break;
            }
            case 2: //INSERT
            {
                clsInsert insert=new clsInsert(CurrentSolution);
                insert.createInsertSolution(data,IterationCounter, RecencyMemory, TABU_LIST_SIZE);
                result[0]=insert.BestNonTabooSolution;
                result[1]=insert.BestTabooSolution;
                break;
            }
            case 3://SWAP IN
            {
                clsSwapIn swapin=new clsSwapIn(CurrentSolution);
                swapin.createSwapSolution(data,IterationCounter, RecencyMemory, TABU_LIST_SIZE);
                result[0]=swapin.BestNonTabooSolution;
                result[1]=swapin.BestTabooSolution;
                //System.out.println("Test");
                break;

            } 
        }
        return result;
}

void adaptCurrentSolution(clsData data,clsSolution CurrentSolution,clsOperatorSolution BestNonTabooSolution,
       clsOperatorSolution BestTabooSolution,int IterationCounter, int operator,
       float BestSolutionEvaluation,float BestSolutionTimeCost){
    boolean AspirationCriteria, NonTabooSolutionIsAccepted;;
    boolean NonTabooSolutionFound=(BestNonTabooSolution.InsertedPOI_ID>=2);
    boolean TabooSolutionFound=(BestTabooSolution.InsertedPOI_ID>=2);    
    if(operator==3)
    {
        AspirationCriteria=CurrentSolution.Evaluation>=BestSolutionEvaluation && 
                BestTabooSolution.TimeCost<BestSolutionTimeCost;  
        NonTabooSolutionIsAccepted= 
        /* -1- */ (BestNonTabooSolution.TimeCost<BestTabooSolution.TimeCost ) || 
        /* -2- */ ( !AspirationCriteria );
    }
    else
    {
        AspirationCriteria=BestTabooSolution.Evaluation>BestSolutionEvaluation;   
        NonTabooSolutionIsAccepted=
        /* -1- */ (BestNonTabooSolution.Evaluation>=BestTabooSolution.Evaluation ) || 
        /* -2- */ ( !AspirationCriteria );
    }

    if(NonTabooSolutionFound && NonTabooSolutionIsAccepted )
    {
        switch(operator)
        {
            case 1://SWAP OUT
                this.updateCurrentSolutionAfterSWAP_OUT(data,CurrentSolution, BestNonTabooSolution,IterationCounter);
                break;
            case 2: //INSERT
                this.updateCurrentSolutionAfterINSERT(data,CurrentSolution, BestNonTabooSolution,IterationCounter);
                break;
            case 3:  //SWAP IN
                this.updateCurrentSolutionAfterSWAP_IN(data,CurrentSolution, BestNonTabooSolution, IterationCounter);
        }
    }
    else if(AspirationCriteria && TabooSolutionFound)
    {
        switch(operator)
        {
            case 1://SWAP OUT
                this.updateCurrentSolutionAfterSWAP_OUT(data,CurrentSolution, BestTabooSolution,IterationCounter);
                break;
            case 2: //INSERT
                this.updateCurrentSolutionAfterINSERT(data,CurrentSolution, BestTabooSolution,IterationCounter);
                //System.out.println("Insert");
                break;
            case 3: //SWAP IN
                this.updateCurrentSolutionAfterSWAP_IN(data,CurrentSolution, BestTabooSolution, IterationCounter);
        }
    }
}

clsSolution  updateBestSolution(clsData data,clsSolution CurrentSolution){
    clsSolution result=new clsSolution(data,CurrentSolution);
    return result;
}
void updateCurrentSolutionAfterSWAP_OUT(clsData data,clsSolution CurrentSolution, clsOperatorSolution SwapSolution,int IterationCounter){
    
    clsSwapOut.updateTimeSpent(CurrentSolution.TourTimeSpent, CurrentSolution.Itinerary, SwapSolution.TourIndex, 
            SwapSolution.VisitIndex, SwapSolution.DistanceFromPrevioiusPoint,
            SwapSolution.InsertedVisitEndTime,SwapSolution.InsertedVisitStartTime,
            SwapSolution.DistanceToNextPoint,SwapSolution.TourUnusedTimeAtTheStart);
    this.updateAffectedPOIsAfterSWAP_OUT(data,CurrentSolution, SwapSolution);
          
    clsVisit NewVisit=clsSwapOut.getNewSwapVisit(SwapSolution);
    CurrentSolution.Itinerary[SwapSolution.TourIndex][SwapSolution.VisitIndex]=NewVisit;  
    
    clsGeneral.updateTourMaxShift(data,SwapSolution.TourIndex,CurrentSolution.TourLastVisit, 
    CurrentSolution.Itinerary, CurrentSolution.MaxShift);
  
    CurrentSolution.FlatItinerary[SwapSolution.TourIndex][SwapSolution.VisitIndex]=
           SwapSolution.InsertedPOI_ID;
    CurrentSolution.BudgetCost=SwapSolution.Cost;
    CurrentSolution.Evaluation=SwapSolution.Evaluation;
    CurrentSolution.TripTimeCost=clsGeneral.getTripSpentTime(CurrentSolution.TourTimeSpent);
    clsSwapOut.updateTourLastVisit(SwapSolution.VisitIndex, SwapSolution.TourIndex, 
            SwapSolution.InsertedPOI_ID,CurrentSolution.TourLastVisit);
    CurrentSolution.POIsInItinerary.remove(
            CurrentSolution.POIsInItinerary.indexOf(SwapSolution.RemovedPOI_ID));
    CurrentSolution.POIsInItinerary.add(SwapSolution.InsertedPOI_ID);
    CurrentSolution.POIsOffItinerary.remove(SwapSolution.RemoveIndexInOffList);
    CurrentSolution.POIsOffItinerary.add(SwapSolution.RemovedPOI_ID);
    System.arraycopy(SwapSolution.TypeConstraintCounter, 0, CurrentSolution.TypeConstraintCounter, 
            0, CurrentSolution.TypeConstraintCounter.length);
    clsGeneral.updateRecencyMemory(SwapSolution.RemovedPOI_ID, 
                       SwapSolution.InsertedPOI_ID, IterationCounter, RecencyMemory);
    clsGeneral.updateFrequencyMemory(SwapSolution.RemovedPOI_ID,SwapSolution.InsertedPOI_ID, 
            FrequencyMemory, FREQUENCY_MEMORY_HORIZON); 
 
}
void updateAffectedPOIsAfterSWAP_OUT(clsData data,clsSolution CurrentSolution, clsOperatorSolution SwapSolution){
      
        //Update Start time and End time of Preceiding POIs
      
        if(SwapSolution.VisitIndex!=CurrentSolution.TourLastVisit[1][SwapSolution.TourIndex]){

            float NextVisitStartTime=SwapSolution.InsertedVisitEndTime+clsGeneral.getDistanceBetweenPoints(SwapSolution.InsertedPOI_ID,
                  CurrentSolution.Itinerary[SwapSolution.TourIndex][SwapSolution.VisitIndex+1].POI_ID, data.getDistance());
                
            float TimeShiftOfFollowingPOIs=NextVisitStartTime
                -CurrentSolution.Itinerary[SwapSolution.TourIndex][SwapSolution.VisitIndex+1].constraints.TimeWindow.StartTime;            
            
           for(int i=SwapSolution.VisitIndex+1;i<=CurrentSolution.TourLastVisit[1][SwapSolution.TourIndex];i++){
                CurrentSolution.Itinerary[SwapSolution.TourIndex][i].constraints.TimeWindow.StartTime+=(TimeShiftOfFollowingPOIs);
                CurrentSolution.Itinerary[SwapSolution.TourIndex][i].constraints.TimeWindow.EndTime+=(TimeShiftOfFollowingPOIs);           
//           //Test
//                if(clsData.getPOI()[CurrentSolution.Itinerary[SwapSolution.TourIndex][i].POI_ID-2]
//                        [4+clsData.getNumberOfPersons()] > 
//                        CurrentSolution.Itinerary[SwapSolution.TourIndex][i].constraints.TimeWindow.StartTime)
//                    System.out.println();
//                
//          //Test
           
           
           }
        }
      
        //Update distance to next point of previous point and distance to previous point of next point...
        if(SwapSolution.VisitIndex>0)
            CurrentSolution.Itinerary[SwapSolution.TourIndex][SwapSolution.VisitIndex-1].
                    constraints.TimeWindow.DistanceToNextPoint=SwapSolution.DistanceFromPrevioiusPoint;
        if(SwapSolution.VisitIndex<CurrentSolution.TourLastVisit[1][SwapSolution.TourIndex])
            CurrentSolution.Itinerary[SwapSolution.TourIndex][SwapSolution.VisitIndex+1].
                    constraints.TimeWindow.DistanceFromPrevioiusPoint=SwapSolution.DistanceToNextPoint;       
    }
void updateCurrentSolutionAfterINSERT(clsData data,clsSolution CurrentSolution, clsOperatorSolution InsertSolution,int IterationCounter){
    
    if(InsertSolution.InsertedPOI_ID!=0){// If an insertion is made
        
        boolean InsertionDidNotOccureAtTheEndOfTour=
                InsertSolution.VisitIndex<=CurrentSolution.TourLastVisit[1][InsertSolution.TourIndex];
        clsInsert.updateTimeSpent(CurrentSolution.TourTimeSpent, CurrentSolution.Itinerary, InsertSolution.TourIndex, 
                InsertSolution.VisitIndex, InsertSolution.DistanceFromPrevioiusPoint,
                InsertSolution.InsertedVisitEndTime,InsertSolution.InsertedVisitStartTime,
                InsertSolution.DistanceToNextPoint,CurrentSolution.TourLastVisit[1][InsertSolution.TourIndex],
                InsertSolution.TourUnusedTimeAtTheStart);
        this.updateAffectedPOIsAfterINSERT(data,CurrentSolution, InsertSolution);

        clsVisit NewVisit=clsInsert.getNewInsertVisit(InsertSolution);
        clsInsert.updateItinerary(CurrentSolution.Itinerary,CurrentSolution.FlatItinerary, NewVisit, InsertSolution.TourIndex, 
                InsertSolution.VisitIndex, CurrentSolution.TourLastVisit[1][InsertSolution.TourIndex]);
        
        clsInsert.updateTourLastVisit(InsertSolution.VisitIndex, InsertSolution.TourIndex, 
                InsertSolution.InsertedPOI_ID,CurrentSolution.TourLastVisit);     
        
        clsGeneral.updateTourMaxShift(data,InsertSolution.TourIndex, CurrentSolution.TourLastVisit, 
        CurrentSolution.Itinerary, CurrentSolution.MaxShift);
        CurrentSolution.BudgetCost=InsertSolution.Cost;
        CurrentSolution.Evaluation=InsertSolution.Evaluation;
        CurrentSolution.TripTimeCost=clsGeneral.getTripSpentTime(CurrentSolution.TourTimeSpent);
        CurrentSolution.POIsInItinerary.add(InsertSolution.InsertedPOI_ID);
        CurrentSolution.POIsOffItinerary.remove(InsertSolution.RemoveIndexInOffList);

        System.arraycopy(InsertSolution.TypeConstraintCounter, 0, CurrentSolution.TypeConstraintCounter, 
                0, CurrentSolution.TypeConstraintCounter.length);
        if(InsertionDidNotOccureAtTheEndOfTour){
            clsGeneral.updateRecencyMemory(InsertSolution.RemovedPOI_ID, 
                            InsertSolution.InsertedPOI_ID, IterationCounter, RecencyMemory);
            clsGeneral.updateFrequencyMemory(InsertSolution.RemovedPOI_ID,InsertSolution.InsertedPOI_ID, 
                    FrequencyMemory, FREQUENCY_MEMORY_HORIZON); 
        }
    }
}



void updateAffectedPOIsAfterINSERT(clsData data,clsSolution CurrentSolution, clsOperatorSolution InsertSolution){
      
        if(InsertSolution.VisitIndex<=CurrentSolution.TourLastVisit[1][InsertSolution.TourIndex]){
            float NextVisitStartTime=InsertSolution.InsertedVisitEndTime+clsGeneral.getDistanceBetweenPoints(InsertSolution.InsertedPOI_ID,
                  CurrentSolution.Itinerary[InsertSolution.TourIndex][InsertSolution.VisitIndex].POI_ID, data.getDistance());
            float TimeShiftOfFollowingPOIs=NextVisitStartTime
                -CurrentSolution.Itinerary[InsertSolution.TourIndex][InsertSolution.VisitIndex].constraints.TimeWindow.StartTime;            
          
            //Update Start time and End time of Preceiding POIs
           for(int i=InsertSolution.VisitIndex;i<=CurrentSolution.TourLastVisit[1][InsertSolution.TourIndex];i++){
                CurrentSolution.Itinerary[InsertSolution.TourIndex][i].constraints.TimeWindow.StartTime+=(TimeShiftOfFollowingPOIs);
                CurrentSolution.Itinerary[InsertSolution.TourIndex][i].constraints.TimeWindow.EndTime+=(TimeShiftOfFollowingPOIs);           
           }
            //Update distance to next and previous point of affected POIs
            if(InsertSolution.VisitIndex==0)
                CurrentSolution.Itinerary[InsertSolution.TourIndex][InsertSolution.VisitIndex].
                        constraints.TimeWindow.DistanceFromPrevioiusPoint=InsertSolution.DistanceToNextPoint;
            else{
                CurrentSolution.Itinerary[InsertSolution.TourIndex][InsertSolution.VisitIndex-1].
                        constraints.TimeWindow.DistanceToNextPoint=InsertSolution.DistanceFromPrevioiusPoint;
                CurrentSolution.Itinerary[InsertSolution.TourIndex][InsertSolution.VisitIndex].
                        constraints.TimeWindow.DistanceFromPrevioiusPoint=InsertSolution.DistanceToNextPoint;
            }
        }
        else{
            //Update distance to next point of last POI in tour
            CurrentSolution.Itinerary[InsertSolution.TourIndex][InsertSolution.VisitIndex-1].
                                constraints.TimeWindow.DistanceToNextPoint=InsertSolution.DistanceFromPrevioiusPoint;
        }
    }

void updateCurrentSolutionAfterSWAP_IN(clsData data,clsSolution CurrentSolution, clsOperatorSolution SwapInSolution,int IterationCounter){
    
             float RemovedUnusedTimeAtTheStartOfRemoveingTour=
                     CurrentSolution.Itinerary[SwapInSolution.TourIndex][SwapInSolution.VisitIndex].
                     constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour;
             float RemovedUnusedTimeAtTheStartOfInsertingTour=
                     CurrentSolution.Itinerary[SwapInSolution.InsertedPOITourIndex][SwapInSolution.InsertedPOIVisitIndex].
                     constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour;
    
    clsSwapIn.updateTimeSpent(CurrentSolution.TourTimeSpent, CurrentSolution.Itinerary, 
                SwapInSolution.TourIndex, SwapInSolution.VisitIndex, 
                SwapInSolution.InsertedVisitStartTime,SwapInSolution.InsertedVisitEndTime,
                SwapInSolution.DistanceFromPrevioiusPoint,SwapInSolution.DistanceToNextPoint,
                SwapInSolution.InsertedPOITourIndex,SwapInSolution.InsertedPOIVisitIndex,
                SwapInSolution.VisitStartTimeInInsertingTour,SwapInSolution.VisitEndTimeInInsertingTour,
                SwapInSolution.DistanceFromPreviosPointToInsertedPointInInsertingTour,
                SwapInSolution.DistanceFromInsertedPointToNextPointInInsertingTour,
                SwapInSolution.TourUnusedTimeAtTheStart,
                RemovedUnusedTimeAtTheStartOfRemoveingTour,
                SwapInSolution.UnusedTimeAtTheStartOfInsertingTour,
                RemovedUnusedTimeAtTheStartOfInsertingTour);

    this.updateAffectedPOIsAfterSWAP_IN(data,CurrentSolution, SwapInSolution);

    clsVisit NewVisitInRemoveingPart=clsSwapIn.getNewSwapVisitForRemoveingPart(SwapInSolution);
    clsVisit NewVisitInInsertingPart=clsSwapIn.getNewSwapVisitForInsertingPart(SwapInSolution);
    CurrentSolution.Itinerary[SwapInSolution.TourIndex][SwapInSolution.VisitIndex]=NewVisitInRemoveingPart;  
    CurrentSolution.Itinerary[SwapInSolution.InsertedPOITourIndex][SwapInSolution.InsertedPOIVisitIndex]=NewVisitInInsertingPart;

    clsGeneral.updateTourMaxShift(data,SwapInSolution.TourIndex,CurrentSolution.TourLastVisit, 
    CurrentSolution.Itinerary, CurrentSolution.MaxShift); 
    if(SwapInSolution.TourIndex!=SwapInSolution.InsertedPOITourIndex){
        clsGeneral.updateTourMaxShift(data,SwapInSolution.InsertedPOITourIndex,CurrentSolution.TourLastVisit, 
        CurrentSolution.Itinerary, CurrentSolution.MaxShift); 
    }

    CurrentSolution.FlatItinerary[SwapInSolution.TourIndex][SwapInSolution.VisitIndex]=
           SwapInSolution.InsertedPOI_ID;
    CurrentSolution.FlatItinerary[SwapInSolution.InsertedPOITourIndex][SwapInSolution.InsertedPOIVisitIndex]=
           SwapInSolution.RemovedPOI_ID;    
    CurrentSolution.TripTimeCost=SwapInSolution.TimeCost;

    clsSwapIn.updateToursLastVisit(SwapInSolution.VisitIndex, SwapInSolution.TourIndex, 
                                    SwapInSolution.InsertedPOIVisitIndex,SwapInSolution.InsertedPOITourIndex,
                                    SwapInSolution.InsertedPOI_ID,SwapInSolution.RemovedPOI_ID,CurrentSolution.TourLastVisit);

    //Same as SwapOut
    clsGeneral.updateRecencyMemory(SwapInSolution.RemovedPOI_ID, 
                       SwapInSolution.InsertedPOI_ID, IterationCounter, RecencyMemory);
    //Same as SwapOut
    clsGeneral.updateFrequencyMemory(SwapInSolution.RemovedPOI_ID,SwapInSolution.InsertedPOI_ID, 
            FrequencyMemory, FREQUENCY_MEMORY_HORIZON); 
     //System.out.println("Test");
}

void updateAffectedPOIsAfterSWAP_IN(clsData data,clsSolution CurrentSolution, clsOperatorSolution SwapInSolution){
      
        if(SwapInSolution.InsertedPOITourIndex==SwapInSolution.TourIndex){
            if(SwapInSolution.InsertedPOIVisitIndex==SwapInSolution.VisitIndex+1){
                this.updateTourWithNeighbourSwap(data,CurrentSolution, SwapInSolution.InsertedPOITourIndex, 
                    SwapInSolution.InsertedPOIVisitIndex, 
                    SwapInSolution.VisitEndTimeInInsertingTour, SwapInSolution.RemovedPOI_ID, 
                    SwapInSolution.DistanceFromPreviosPointToInsertedPointInInsertingTour, 
                    SwapInSolution.DistanceFromInsertedPointToNextPointInInsertingTour,
                    SwapInSolution.DistanceFromPrevioiusPoint); 
            }
            else
            {
                   //Update Removeing part
                this.updateTourInRemoveingPart(data,CurrentSolution, SwapInSolution.TourIndex, SwapInSolution.VisitIndex,
                     SwapInSolution.InsertedPOIVisitIndex,SwapInSolution.InsertedVisitEndTime, SwapInSolution.InsertedPOI_ID, 
                     SwapInSolution.DistanceFromPrevioiusPoint, SwapInSolution.DistanceToNextPoint);
                
                //Update Inserting part
                this.updateTourInInsertingPart(data,CurrentSolution, SwapInSolution.InsertedPOITourIndex, SwapInSolution.InsertedPOIVisitIndex, 
                    SwapInSolution.VisitEndTimeInInsertingTour, SwapInSolution.RemovedPOI_ID, 
                    SwapInSolution.DistanceFromPreviosPointToInsertedPointInInsertingTour, 
                    SwapInSolution.DistanceFromInsertedPointToNextPointInInsertingTour);
            }
        }
        else
        {
            //Update Removeing Tour
            this.updateTour(data,CurrentSolution, SwapInSolution.TourIndex, SwapInSolution.VisitIndex, 
                    SwapInSolution.InsertedVisitEndTime, SwapInSolution.InsertedPOI_ID, 
                    SwapInSolution.DistanceFromPrevioiusPoint, SwapInSolution.DistanceToNextPoint);

            //Update Inserting Tour
            this.updateTour(data,CurrentSolution, SwapInSolution.InsertedPOITourIndex, SwapInSolution.InsertedPOIVisitIndex, 
                    SwapInSolution.VisitEndTimeInInsertingTour, SwapInSolution.RemovedPOI_ID, 
                    SwapInSolution.DistanceFromPreviosPointToInsertedPointInInsertingTour, 
                    SwapInSolution.DistanceFromInsertedPointToNextPointInInsertingTour);

        }        
    }
    void updateTour(clsData data,clsSolution CurrentSolution,int TourIndex, int VisitIndex,float VisitEndTime,int InsertedPOI_ID,
            float DistanceFromPrevioiusPoint, float DistanceToNextPoint){
        //Update Start time and End time of Preceiding POIs

        float TimeShiftOfFollowingPOIs=0;
        if(VisitIndex!=CurrentSolution.TourLastVisit[1][TourIndex]){
            float NextVisitStartTime=VisitEndTime+clsGeneral.getDistanceBetweenPoints(InsertedPOI_ID,
                  CurrentSolution.Itinerary[TourIndex][VisitIndex+1].POI_ID, data.getDistance()); 
            TimeShiftOfFollowingPOIs=NextVisitStartTime
                -CurrentSolution.Itinerary[TourIndex][VisitIndex+1].constraints.TimeWindow.StartTime;            
           for(int i=VisitIndex+1;i<=CurrentSolution.TourLastVisit[1][TourIndex];i++){           
                CurrentSolution.Itinerary[TourIndex][i].constraints.TimeWindow.StartTime+=(TimeShiftOfFollowingPOIs);
                CurrentSolution.Itinerary[TourIndex][i].constraints.TimeWindow.EndTime+=(TimeShiftOfFollowingPOIs);           
           }
        }
      
        //Update distance to next point of previous point and distance to previous point of next point...
        if(VisitIndex>0)
            CurrentSolution.Itinerary[TourIndex][VisitIndex-1].
                    constraints.TimeWindow.DistanceToNextPoint=DistanceFromPrevioiusPoint;
        if(VisitIndex<CurrentSolution.TourLastVisit[1][TourIndex])
            CurrentSolution.Itinerary[TourIndex][VisitIndex+1].
                    constraints.TimeWindow.DistanceFromPrevioiusPoint=DistanceToNextPoint;  
    }
    void updateTourWithNeighbourSwap(clsData data,clsSolution CurrentSolution,int TourIndex, int VisitIndex,float VisitEndTime,int InsertedPOI_ID,
            float DistanceFromPrevioiusPoint, float DistanceToNextPoint,float DistanceFromPrevioiusPointInRemoveingTour){
        //Update Start time and End time of Preceiding POIs
        float TimeShiftOfFollowingPOIs=0;
        if(VisitIndex!=CurrentSolution.TourLastVisit[1][TourIndex]){

            float NextVisitStartTime=VisitEndTime+clsGeneral.getDistanceBetweenPoints(InsertedPOI_ID,
                  CurrentSolution.Itinerary[TourIndex][VisitIndex+1].POI_ID, data.getDistance());
                
            TimeShiftOfFollowingPOIs=NextVisitStartTime
                -CurrentSolution.Itinerary[TourIndex][VisitIndex+1].constraints.TimeWindow.StartTime;            
            
           for(int i=VisitIndex+1;i<=CurrentSolution.TourLastVisit[1][TourIndex];i++){           
                CurrentSolution.Itinerary[TourIndex][i].constraints.TimeWindow.StartTime+=(TimeShiftOfFollowingPOIs);
                CurrentSolution.Itinerary[TourIndex][i].constraints.TimeWindow.EndTime+=(TimeShiftOfFollowingPOIs);           
           }
        }
      
        //Update distance to next point of previous point and distance to previous point of next point...
        if(VisitIndex>1)
            CurrentSolution.Itinerary[TourIndex][VisitIndex-2].
                    constraints.TimeWindow.DistanceToNextPoint=DistanceFromPrevioiusPointInRemoveingTour;
        if(VisitIndex<CurrentSolution.TourLastVisit[1][TourIndex])
            CurrentSolution.Itinerary[TourIndex][VisitIndex+1].
                    constraints.TimeWindow.DistanceFromPrevioiusPoint=DistanceToNextPoint;  
    }
void updateTourInRemoveingPart(clsData data,clsSolution CurrentSolution,int TourIndex, int VisitIndex,int InsertingTourIndex,
        float VisitEndTime,int InsertedPOI_ID, float DistanceFromPrevioiusPoint, float DistanceToNextPoint){
        //Update Start time and End time of Preceiding POIs
        float TimeShiftOfFollowingPOIs=0;
        float NextVisitStartTime=VisitEndTime+clsGeneral.getDistanceBetweenPoints(InsertedPOI_ID,
                  CurrentSolution.Itinerary[TourIndex][VisitIndex+1].POI_ID, data.getDistance());
                
        TimeShiftOfFollowingPOIs=NextVisitStartTime
            -CurrentSolution.Itinerary[TourIndex][VisitIndex+1].constraints.TimeWindow.StartTime;            

        for(int i=VisitIndex+1;i<InsertingTourIndex;i++){           
            CurrentSolution.Itinerary[TourIndex][i].constraints.TimeWindow.StartTime+=(TimeShiftOfFollowingPOIs);
            CurrentSolution.Itinerary[TourIndex][i].constraints.TimeWindow.EndTime+=(TimeShiftOfFollowingPOIs);           
        }
        //Update distance to next point of previous point and distance to previous point of next point...
        if(VisitIndex>0)
            CurrentSolution.Itinerary[TourIndex][VisitIndex-1].
                    constraints.TimeWindow.DistanceToNextPoint=DistanceFromPrevioiusPoint;
        CurrentSolution.Itinerary[TourIndex][VisitIndex+1].
            constraints.TimeWindow.DistanceFromPrevioiusPoint=DistanceToNextPoint;  
    }
    void updateTourInInsertingPart(clsData data,clsSolution CurrentSolution,int TourIndex, int VisitIndex,float VisitEndTime,int InsertedPOI_ID,
            float DistanceFromPrevioiusPoint, float DistanceToNextPoint){
        //Update Start time and End time of Preceiding POIs

        float TimeShiftOfFollowingPOIs=0;
        if(VisitIndex!=CurrentSolution.TourLastVisit[1][TourIndex]){
            float NextVisitStartTime=VisitEndTime+clsGeneral.getDistanceBetweenPoints(InsertedPOI_ID,
                  CurrentSolution.Itinerary[TourIndex][VisitIndex+1].POI_ID, data.getDistance());
            TimeShiftOfFollowingPOIs=NextVisitStartTime
                -CurrentSolution.Itinerary[TourIndex][VisitIndex+1].constraints.TimeWindow.StartTime;            
           for(int i=VisitIndex+1;i<=CurrentSolution.TourLastVisit[1][TourIndex];i++){           
                CurrentSolution.Itinerary[TourIndex][i].constraints.TimeWindow.StartTime+=(TimeShiftOfFollowingPOIs);
                CurrentSolution.Itinerary[TourIndex][i].constraints.TimeWindow.EndTime+=(TimeShiftOfFollowingPOIs);           
           }
        }
      
        //Update distance to next point of previous point and distance to previous point of next point...
        CurrentSolution.Itinerary[TourIndex][VisitIndex-1].
                    constraints.TimeWindow.DistanceToNextPoint=DistanceFromPrevioiusPoint;
        if(VisitIndex<CurrentSolution.TourLastVisit[1][TourIndex])
            CurrentSolution.Itinerary[TourIndex][VisitIndex+1].
                    constraints.TimeWindow.DistanceFromPrevioiusPoint=DistanceToNextPoint;  
    }
    
    void applyDeleteOperator(clsData data,clsSolution CurrentSolution, int IterationCounter){
            CurrentSolution.MaxShiftImpactInTrip=clsGeneral.getMaxShiftImpactInTrip(CurrentSolution.TourLastVisit, CurrentSolution.MaxShift);
            clsRemove remove=new clsRemove(CurrentSolution);
            clsRemoveSolution RemoveSolution= remove.getRemoveSolution(data);
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

            boolean RemovalDidNotOccureAtTheStartOfTour=RemoveSolution.VisitIndex!=0;
        if(RemovalDidNotOccureAtTheStartOfTour){
            int PreviousPOI_ID=CurrentSolution.Itinerary[RemoveSolution.TourIndex][RemoveSolution.VisitIndex-1].POI_ID;
            clsGeneral.updateRecencyMemory(RemoveSolution.RemovedPOI_ID, PreviousPOI_ID, IterationCounter, RecencyMemory);
            clsGeneral.updateFrequencyMemory(RemoveSolution.RemovedPOI_ID,PreviousPOI_ID, 
                    FrequencyMemory, FREQUENCY_MEMORY_HORIZON); 
        }
//             System.out.println("Current solution length:"+CurrentSolution.TourLastVisit[1][0]);
    }
    void updateAffectedPOIsAfterRemove(clsSolution CurrentSolution,  clsRemoveSolution RemoveSolution){
        
        if( RemoveSolution.VisitIndex<CurrentSolution.TourLastVisit[1][RemoveSolution.TourIndex]){
//            //Update Start time and End time of Preceiding POIs
           for(int i=RemoveSolution.VisitIndex+1;i<=CurrentSolution.TourLastVisit[1][RemoveSolution.TourIndex];i++){
                CurrentSolution.Itinerary[RemoveSolution.TourIndex][i].constraints.TimeWindow.StartTime-=(RemoveSolution.ShiftingValue);
                CurrentSolution.Itinerary[RemoveSolution.TourIndex][i].constraints.TimeWindow.EndTime-=(RemoveSolution.ShiftingValue);           
           }
            //Update distance to next and previous point of affected POIs
            if(RemoveSolution.VisitIndex==0){
                CurrentSolution.Itinerary[RemoveSolution.TourIndex][RemoveSolution.VisitIndex+1].
                        constraints.TimeWindow.DistanceFromPrevioiusPoint=RemoveSolution.DistanceFromPreviousPointToNextPoint;
                CurrentSolution.Itinerary[RemoveSolution.TourIndex][RemoveSolution.VisitIndex+1].
                        constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour=RemoveSolution.UnusedTimeAtTheStartOfTheTour;
            }
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
    int getFrequencyMemoryValue(int IdOfPOIToBeSwapt, int IdOfPOIThatSwaps)
    {
        int result;
        boolean isSweptPOIIDSmaller=IdOfPOIToBeSwapt<IdOfPOIThatSwaps;
        if(isSweptPOIIDSmaller)
        {
            result=FrequencyMemory[IdOfPOIToBeSwapt-1][IdOfPOIThatSwaps-2];
        }
        else
        {
            result=FrequencyMemory[IdOfPOIThatSwaps-1][IdOfPOIToBeSwapt-2];
        }
        return result;
    }

}
