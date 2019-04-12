/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassLibrary;

/**
 *
 * @author user
 */
public class clsTwoOptSwap {// Te merret parasysh kur invertohet  pjesa e itinerarit...
    

clsTwoOptTourSolution createTwoOptSolution(clsData data,int TourIndex, int LeftPOIIndex,int RightPOIIndex,int LeftPOIID,int RightPOIID,
 int TourLastVisit,float TourSpentTime,clsVisit TourItinerary[], float TourMaxShift[][]) {
        
    clsTwoOptTourSolution result = new clsTwoOptTourSolution();
        result.TourTimeCost=this.getInitialSolutionTimeCost(data);

        clsVisit SubTourItinerary[]=null;
        boolean SubTourGenerationNeeded=RightPOIIndex-LeftPOIIndex>2;
        boolean IsSubTourReversed=false;
        if(SubTourGenerationNeeded){
            clsVisit NonReversedSubTourItinerary[]=this.extractSubtour(TourItinerary, TourLastVisit, LeftPOIIndex,RightPOIIndex);
            float NonReversedSubTourDuration=this.getSubtourLength(NonReversedSubTourItinerary, 
                    TourItinerary[LeftPOIIndex].POI_ID,  TourItinerary[RightPOIIndex].POI_ID);
            float NonReversedTwoOptChangeLength=this.getTwoOptChangeLength(data,NonReversedSubTourItinerary, LeftPOIID, RightPOIID);
            clsVisit [] ReversedSubTourItinerary=this.getReversedSubTour(NonReversedSubTourItinerary);
            float ReversedSubTourDuration=this.getSubtourLength(ReversedSubTourItinerary, 
                    TourItinerary[LeftPOIIndex].POI_ID,  TourItinerary[RightPOIIndex].POI_ID);
            float ReversedTwoOptChangeLength=this.getTwoOptChangeLength(data,ReversedSubTourItinerary, LeftPOIID, RightPOIID);
            if(NonReversedSubTourDuration+NonReversedTwoOptChangeLength <= 
                    ReversedSubTourDuration+ReversedTwoOptChangeLength){
                SubTourItinerary=NonReversedSubTourItinerary;
            }
            else
            {
                SubTourItinerary=ReversedSubTourItinerary;
                IsSubTourReversed=true;
            }
        }
        boolean SwapBetweenNeighbourPOIs=(RightPOIIndex==LeftPOIIndex+1);
        float DistanceFromPreviosPointToInsertedPointInRemoveingTour=
                this.getDistanceFromPreviosPoint(data,LeftPOIIndex, RightPOIID,TourItinerary);
        float DistanceFromInsertedPointToNextPointInRemoveingTour=
                this.getDistanceToNextPointWithPossibleSwapBetweenNeighbourPOIs(data,LeftPOIIndex,RightPOIIndex, RightPOIID, 
                SwapBetweenNeighbourPOIs,TourItinerary,IsSubTourReversed);       
        
        float RemoveingTourShiftingValue=this.getShiftingValue(data,LeftPOIID, RightPOIID, 
                DistanceFromPreviosPointToInsertedPointInRemoveingTour, DistanceFromInsertedPointToNextPointInRemoveingTour, 
                TourItinerary[LeftPOIIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint, 
                TourItinerary[LeftPOIIndex].constraints.TimeWindow.DistanceToNextPoint);
        float DistanceFromPreviosPointToInsertedPointInInsertingTour=
                this.getDistanceFromPreviosPointWithPossibleSwapBetweenNeighbourPOIs(data,
                LeftPOIIndex,RightPOIIndex, LeftPOIID,SwapBetweenNeighbourPOIs,TourItinerary,IsSubTourReversed);
        float DistanceFromInsertedPointToNextPointInInsertingTour=this.getDistanceToNextPoint(data,RightPOIIndex, LeftPOIID, 
                TourLastVisit,TourItinerary);
        float InsertingTourShiftingValue=this.getShiftingValue(data,RightPOIID,LeftPOIID, 
            DistanceFromPreviosPointToInsertedPointInInsertingTour, DistanceFromInsertedPointToNextPointInInsertingTour, 
                TourItinerary[RightPOIIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint, 
                TourItinerary[RightPOIIndex].constraints.TimeWindow.DistanceToNextPoint);
        float[] IndicatedStartAndEndTime =this.getIndicatedStartAndEndTime( data,
                LeftPOIIndex, RightPOIIndex, TourLastVisit,
                RemoveingTourShiftingValue, InsertingTourShiftingValue, 
                DistanceFromPreviosPointToInsertedPointInRemoveingTour, 
                DistanceFromInsertedPointToNextPointInRemoveingTour, 
                DistanceFromPreviosPointToInsertedPointInInsertingTour, 
                DistanceFromInsertedPointToNextPointInInsertingTour,
                TourItinerary,TourMaxShift,TourSpentTime,IsSubTourReversed,SubTourItinerary);
                if (IndicatedStartAndEndTime[0] >= 0) {
                    
                            result = this.createCandidateSolution(TourIndex, LeftPOIIndex,RightPOIIndex,
                                    LeftPOIID,RightPOIID,
                                    IndicatedStartAndEndTime[0],
                                    IndicatedStartAndEndTime[1],
                                    IndicatedStartAndEndTime[2],
                                    IndicatedStartAndEndTime[3],
                                    DistanceFromPreviosPointToInsertedPointInRemoveingTour,
                                    DistanceFromInsertedPointToNextPointInRemoveingTour,
                                    DistanceFromPreviosPointToInsertedPointInInsertingTour,
                                    DistanceFromInsertedPointToNextPointInInsertingTour,
                                    SubTourItinerary,
                                    IndicatedStartAndEndTime[8],IsSubTourReversed);
                }
                else
                {
                    result.LeftPOI_ID=-1;// TwoOptSwap Solution is Not Feasible
                }
        return result;
       
    }

float[] getIndicatedStartAndEndTime(clsData data, int LeftVisitIndex, 
         int RightVisitIndex, int TourLastVisitIndex,float RemoveingTourShiftingValue,    
             float InsertingTourShiftingValue,   float DistanceFromPreviosPointToInsertedPointInRemoveingTour,
        float DistanceFromInsertedPointToNextPointInRemoveingTour ,   float DistanceFromPreviosPointToInsertedPointInInsertingTour,
        float DistanceFromInsertedPointToNextPointInInsertingTour, clsVisit TourItinerary[], 
        float TourMaxShift[][], float TourSpentTime,boolean SubTourReversed,clsVisit SubTourItinerary[]) {

        float[] result = new float[9];

        float IndicatedStartTimeInRemoveingTour;
        if(LeftVisitIndex==0)
            IndicatedStartTimeInRemoveingTour=DistanceFromPreviosPointToInsertedPointInRemoveingTour;
        else
            IndicatedStartTimeInRemoveingTour=TourItinerary[LeftVisitIndex-1].constraints.
                    TimeWindow.EndTime + DistanceFromPreviosPointToInsertedPointInRemoveingTour;
        float IndicatedEndTimeInRemoveingTour = IndicatedStartTimeInRemoveingTour + 
                data.getPOI()[TourItinerary[RightVisitIndex].POI_ID - 2][3];

        if ((IndicatedStartTimeInRemoveingTour >= 
                data.getPOI()[TourItinerary[RightVisitIndex].POI_ID - 2][4 + data.getNumberOfPersons()])
                && (IndicatedEndTimeInRemoveingTour <= 
                data.getPOI()[TourItinerary[RightVisitIndex].POI_ID - 2][8 + data.getNumberOfPersons()])) 
        {
            float IndicatedTourSpentTimeInRemoveingTour = getIndicatedSwapTourSpentTime(RemoveingTourShiftingValue,
                         InsertingTourShiftingValue,TourSpentTime);
            if (IndicatedTourSpentTimeInRemoveingTour <= data.getStartPoint()[6]) {
                boolean TimeWindowFeasibilityOfPrecedingPOIsInRemoveingTour;
                if(RightVisitIndex==LeftVisitIndex+1){//If neighbors in same tour are swapt
                    TimeWindowFeasibilityOfPrecedingPOIsInRemoveingTour=true;
                }
                else
                {
                    if(SubTourReversed){
                        TimeWindowFeasibilityOfPrecedingPOIsInRemoveingTour=
                                this.getTimeWindowFeasibilityOfAffectedPOIsInReversingPartOfTour(data,
                                                         LeftVisitIndex, RightVisitIndex, SubTourItinerary, 
                                                         IndicatedEndTimeInRemoveingTour, 
                                                         DistanceFromInsertedPointToNextPointInRemoveingTour);
                    }
                    else
                    {
                        TimeWindowFeasibilityOfPrecedingPOIsInRemoveingTour=
                            this.getTimeWindowFeasibilityOfAffectedPOIsInRemoveingTour(
                             LeftVisitIndex, TourLastVisitIndex,  RemoveingTourShiftingValue, TourMaxShift);                           
                    }
                    
                }
                    if(TimeWindowFeasibilityOfPrecedingPOIsInRemoveingTour){
                        float IndicatedStartTimeInInsertingTour;
                        if(RightVisitIndex==0)
                            IndicatedStartTimeInInsertingTour=DistanceFromPreviosPointToInsertedPointInInsertingTour;
                        else
                            IndicatedStartTimeInInsertingTour=TourItinerary[RightVisitIndex-1].constraints.
                                    TimeWindow.EndTime + DistanceFromPreviosPointToInsertedPointInInsertingTour;
                        IndicatedStartTimeInInsertingTour+=RemoveingTourShiftingValue;

                        float IndicatedEndTimeInInsertingTour = IndicatedStartTimeInInsertingTour + 
                                data.getPOI()[TourItinerary[LeftVisitIndex].POI_ID - 2][3];
                        if((IndicatedStartTimeInInsertingTour >= 
                            data.getPOI()[TourItinerary[LeftVisitIndex].POI_ID - 2]
                                [4 + data.getNumberOfPersons()])
                            && (IndicatedEndTimeInInsertingTour <= 
                            data.getPOI()[TourItinerary[LeftVisitIndex].POI_ID - 2]
                                [8 + data.getNumberOfPersons()]))
                        {
                            float IndicatedTourSpentTimeInInsertingTour=IndicatedTourSpentTimeInRemoveingTour;
                            if(IndicatedTourSpentTimeInInsertingTour <= data.getStartPoint()[6]){
                                boolean TimeWindowFeasibilityOfPrecedingPOIsInInsertingTour; 
                                    TimeWindowFeasibilityOfPrecedingPOIsInInsertingTour=
                                            this.getTimeWindowFeasibilityOfAffectedPOIsInInsertingTour(
                                     RightVisitIndex, TourLastVisitIndex, 
                                    RemoveingTourShiftingValue, InsertingTourShiftingValue, TourMaxShift);
                                    if (TimeWindowFeasibilityOfPrecedingPOIsInInsertingTour) {
                                        result[0] = IndicatedStartTimeInRemoveingTour;
                                        result[1] = IndicatedEndTimeInRemoveingTour;
                                        result[2] = IndicatedStartTimeInInsertingTour; 
                                        result[3] = IndicatedEndTimeInInsertingTour;
                                        result[4] = DistanceFromPreviosPointToInsertedPointInRemoveingTour;
                                        result[5] = DistanceFromInsertedPointToNextPointInRemoveingTour;
                                        result[6] = DistanceFromPreviosPointToInsertedPointInInsertingTour;
                                        result[7] = DistanceFromInsertedPointToNextPointInInsertingTour;
                                        result[8]=IndicatedTourSpentTimeInInsertingTour;
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
    if(result[0]==0)
        System.out.println("Test");
    //Test
        return result;
    }

    float getShiftingValue(clsData data,int LeftPOIID, int RightPOIID, 
            float DistanceFromPreviosPointToInsertedPoint, float DistanceFromInsertedPointToNextPoint,
            float DistanceFromPrevioiusPointToRemovedPoint, float DistanceFromRemovedPointToNextPoint)
    {
        float result= 
                    + DistanceFromPreviosPointToInsertedPoint
                    + data.getPOI()[RightPOIID - 2][3]
                    + DistanceFromInsertedPointToNextPoint
                    - DistanceFromPrevioiusPointToRemovedPoint
                    - data.getPOI()[LeftPOIID - 2][3]
                    - DistanceFromRemovedPointToNextPoint;
        
        //Test
        System.out.println("DistanceFromPreviosPointToInsertedPoint="+DistanceFromPreviosPointToInsertedPoint);
        System.out.println("DistanceFromInsertedPointToNextPoint="+DistanceFromInsertedPointToNextPoint);
        System.out.println("DistanceFromPrevioiusPointToRemovedPoint="+DistanceFromPrevioiusPointToRemovedPoint);
        System.out.println("DistanceFromRemovedPointToNextPoint="+DistanceFromRemovedPointToNextPoint);
        //Test
        
        return result;
    }
    
    float getDistanceFromPreviosPoint(clsData data,int POIVisitIndeks,
        int POI_ID, clsVisit TourItinerary[]){
        float result;
        if (POIVisitIndeks == 0) {
            result =
                    clsGeneral.getDistanceBetweenPoints(1, POI_ID, data.getDistance());
        } 
        else {
            result =
                    clsGeneral.getDistanceBetweenPoints(TourItinerary[POIVisitIndeks - 1].POI_ID,
                    POI_ID, data.getDistance());
        }
    return result;
    }
    float getDistanceFromPreviosPointWithPossibleSwapBetweenNeighbourPOIs(clsData data,int LeftPOIVisitIndex, int RightPOIVisitIndex,
        int POI_ID, boolean SwapBetweenNeighbourPOIs, clsVisit TourItinerary[], boolean SubTourReveresed){
        float result;
        if (SubTourReveresed) {
            result =TourItinerary[LeftPOIVisitIndex].constraints.TimeWindow.DistanceToNextPoint;
        } 
        else 
        {
            if(SwapBetweenNeighbourPOIs){
            result =TourItinerary[LeftPOIVisitIndex].constraints.TimeWindow.DistanceToNextPoint;
                
                //            result =
//                    clsGeneral.getDistanceBetweenPoints(TourItinerary[RightPOIVisitIndex].POI_ID,
//                    POI_ID, clsData.getDistance());    
            }
            else{
            result =
                    clsGeneral.getDistanceBetweenPoints(TourItinerary[RightPOIVisitIndex - 1].POI_ID,
                    POI_ID, data.getDistance());   
            }

        }
    return result;
    }
    float getDistanceToNextPoint(clsData data,int POIVisitIndeks,int POI_ID,
        int IndeksOfLastVisitIntour,clsVisit TourItinerary[]){
        float result;
            if (POIVisitIndeks == IndeksOfLastVisitIntour) {
            result =
                    clsGeneral.getDistanceBetweenPoints(POI_ID,
                    1, data.getDistance());
            } 
            else {
            result =
                    clsGeneral.getDistanceBetweenPoints(POI_ID,
                    TourItinerary[POIVisitIndeks+1].POI_ID, data.getDistance());
            }
        return result;
    }
    float getDistanceToNextPointWithPossibleSwapBetweenNeighbourPOIs(clsData data,int LeftVisitIndex,int RightVisitIndex,int POI_ID,
       boolean SwapBetweenNeighbourPOIs, clsVisit TourItinerary[],boolean SubTourReversed){
        float result;
        if(SubTourReversed){
            result =TourItinerary[RightVisitIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint;
        }
        else
        {
            if(SwapBetweenNeighbourPOIs){
                result =TourItinerary[RightVisitIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint;
//                result =
//                clsGeneral.getDistanceBetweenPoints(POI_ID,
//                TourItinerary[LeftVisitIndex].POI_ID, clsData.getDistance());
            }
            else{
                result =
                clsGeneral.getDistanceBetweenPoints(POI_ID,
                TourItinerary[LeftVisitIndex+1].POI_ID, data.getDistance());
            }       
        }

        return result;
    }    
    
    float getIndicatedSwapTourSpentTime(float RemoveingTourShiftingValue,float InsertingTourShiftingValue,
            float TourSpentTime) {
        float result;
        result = TourSpentTime +RemoveingTourShiftingValue+InsertingTourShiftingValue;
        return result;
    }
    boolean getTimeWindowFeasibilityOfAffectedPOIs(int TourIndex,int VisitIndex,int TourLastVisit,
            float RemovedPOIStartTime, float RemovedPOIEndTime, 
            float InsertedPOIStartTime, float InsertedPOIEndTime, float DistanceFromRemovedPointToNextPoint,
            float DistanceFromSwappingPointToNextPoint,float MaxShift[][][]){
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
                        if((RemovedPOIStartTime-InsertedPOIStartTime+DistanceFromRemovedPointToNextPoint-DistanceFromSwappingPointToNextPoint)
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
    boolean getTimeWindowFeasibilityOfAffectedPOIsInRemoveingTour(int VisitIndex,int TourLastVisit,
        float RemoveingTourShiftingValue, float MaxShift[][]){
        boolean result=false;
        if(RemoveingTourShiftingValue<=MaxShift[0][VisitIndex+1]) //RightShiftFeasibility of Preceiding POIs
        {
            if(-(RemoveingTourShiftingValue)<=MaxShift[1][VisitIndex+1])//LeftShiftFeasibility  of Preceiding POIs
            {
                result=true;
            }
        }                    
        return result;
    }
    boolean getTimeWindowFeasibilityOfAffectedPOIsInInsertingTour(int VisitIndex,int TourLastVisit,
            float RemoveingTourShiftingValue,float InsertingTourShiftingValue, float MaxShift[][]){
        boolean result=false;
        if(VisitIndex==TourLastVisit){
            result=true;
        }
        else{
            if(RemoveingTourShiftingValue+InsertingTourShiftingValue<=MaxShift[0][VisitIndex+1]) //RightShiftFeasibility of Preceiding POIs
            {
                if(-(RemoveingTourShiftingValue+InsertingTourShiftingValue)
                        <=MaxShift[1][VisitIndex+1])//LeftShiftFeasibility  of Preceiding POIs
                {
                    result=true;
                }
            }                    
        }
        return result;
    }
    boolean getTimeWindowFeasibilityOfAffectedPOIsInReversingPartOfTour(clsData data,int LeftVisitIndex,int RightVisitIndex, 
            clsVisit SubTourItinerary[], float IndicatedEndTimeInRemoveingTour, float DistanceFromInsertedPOIInRemoveingTour){
        boolean result=true;
        float IndicatedStartTime=IndicatedEndTimeInRemoveingTour+DistanceFromInsertedPOIInRemoveingTour;
        float IndicatedEndTime;
        for(int i=0;i<SubTourItinerary.length;i++){
            if(IndicatedStartTime>=data.getPOI()[SubTourItinerary[i].POI_ID - 2][4 + data.getNumberOfPersons()]){
                IndicatedEndTime=IndicatedStartTime+ data.getPOI()[SubTourItinerary[i].POI_ID - 2][3];
                if(IndicatedEndTime >  data.getPOI()[SubTourItinerary[i].POI_ID - 2][8 + data.getNumberOfPersons()]){
                    result=false;
                    break;
                }
            }
            else
            {
                result=false;
                break;
            }
            IndicatedStartTime=IndicatedEndTime+SubTourItinerary[i].constraints.TimeWindow.DistanceFromPrevioiusPoint;
        }      
        return result;
    }
    boolean getCandidateSolutionTabooStatus(int LeftPOIID, int RightPOIID, int iterationIndeks,
            int RecencyMemory[][], int TABU_LIST_SIZE) {
        boolean result = false;
        boolean isLeftPOIIDSmaller = LeftPOIID < RightPOIID;
        if (isLeftPOIIDSmaller) {
            //If value of a member in memory is 0 then swap is not tabu
            if (!(RecencyMemory[LeftPOIID - 1][RightPOIID - 2] == 0)) {
                result = iterationIndeks - RecencyMemory[LeftPOIID - 1][RightPOIID - 2] < TABU_LIST_SIZE;
            }
        } else {
            if (!(RecencyMemory[RightPOIID - 1][LeftPOIID - 2] == 0)) {
                result = iterationIndeks - RecencyMemory[RightPOIID - 1][LeftPOIID - 2] < TABU_LIST_SIZE;
            }
        }
        return result;
    }
    
    float getInitialSolutionTimeCost(clsData data){
        float result;
            result=data.getStartPoint()[6];
        return result;
    }
    
     private clsTwoOptTourSolution createCandidateSolution(int TourIndex, int LeftPOIIndex,int RightPOIIndex, 
             int LeftPOIID, int RightPOIID,
             float VisitStartTimeInRemoveingTour, float VisitEndTimeInRemoveingTour, 
             float VisitStartTimeInInsertingTour, float VisitEndTimeInInsertingTour, 
             float DitanceFromPreviousPointInRemoveingTour, float DistanceToNextPointInRemoveingTour, 
             float DitanceFromPreviousPointInInsertingTour, float DistanceToNextPointInInsertingTour,
             clsVisit SubTourItinerary[], float TimeCost, boolean IsSubTourReversed) {
        clsTwoOptTourSolution result = new clsTwoOptTourSolution();
        result.TourIndex=TourIndex;
        
        result.LeftPOI_ID = LeftPOIID;
        result.RightPOI_ID = RightPOIID;
        result.LeftPOIIndex=LeftPOIIndex;
        result.RightPOIIndex=RightPOIIndex;
        result.LeftVisitStartTime = VisitStartTimeInRemoveingTour;
        result.LeftVisitEndTime = VisitEndTimeInRemoveingTour;
        result.RightVisitStartTime=VisitStartTimeInInsertingTour;
        result.RightVisitEndTime=VisitEndTimeInInsertingTour;
        result.LeftDistanceFromPrevioiusPoint = DitanceFromPreviousPointInRemoveingTour;
        result.LeftDistanceToNextPoint = DistanceToNextPointInRemoveingTour;
        result.RightDistanceFromPrevioiusPoint=DitanceFromPreviousPointInInsertingTour;
        result.RightDistanceToNextPoint=DistanceToNextPointInInsertingTour;
        
        result.SubTourItinerary=SubTourItinerary;
        result.IsSubTourReversed=IsSubTourReversed;       
        result.TourTimeCost=TimeCost;
        
        return result;
    }
     
     static void updateTimeSpent(float TimeSpent[], clsVisit Itinerary[][], int RemoveingTourIndex, int RemoveingVisitIndex,
             float VisitStartTimeInRemoveingTour,float VisitEndTimeInRemoveingTour,
             float DistanceFromPrevioiusPointInRemoveingTour,float DistanceToNextPointInRemoveingTour, 
             int InsertingTourIndex, int InsertingVisitIndex, 
             float VisitStartTimeInInsertingTour, float VisitEndTimeInInsertingTour,
             float DistanceFromPreviosPointInInsertingTour, 
             float DistanceToNextPointInInsertingTour) 
     {
        //boolean SwapBetweenNeighbourPOIs=(InsertingTourIndex==RemoveingTourIndex)&&(InsertingVisitIndex==RemoveingVisitIndex+1);
        TimeSpent[RemoveingTourIndex] +=
                (DistanceFromPrevioiusPointInRemoveingTour
                + (VisitEndTimeInRemoveingTour - VisitStartTimeInRemoveingTour)
                + DistanceToNextPointInRemoveingTour
                - Itinerary[RemoveingTourIndex][RemoveingVisitIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint
                - (Itinerary[RemoveingTourIndex][RemoveingVisitIndex].constraints.TimeWindow.EndTime
                - Itinerary[RemoveingTourIndex][RemoveingVisitIndex].constraints.TimeWindow.StartTime)
                - Itinerary[RemoveingTourIndex][RemoveingVisitIndex].constraints.TimeWindow.DistanceToNextPoint);
        TimeSpent[InsertingTourIndex] +=
                (DistanceFromPreviosPointInInsertingTour
                + (VisitEndTimeInInsertingTour - VisitStartTimeInInsertingTour)
                + DistanceToNextPointInInsertingTour
                - Itinerary[InsertingTourIndex][InsertingVisitIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint
                - (Itinerary[InsertingTourIndex][InsertingVisitIndex].constraints.TimeWindow.EndTime
                - Itinerary[InsertingTourIndex][InsertingVisitIndex].constraints.TimeWindow.StartTime)
                - Itinerary[InsertingTourIndex][InsertingVisitIndex].constraints.TimeWindow.DistanceToNextPoint);    
     //System.out.println("Test");
     }
    static clsVisit getNewSwapVisitForRemoveingPart(clsOperatorSolution SwapSolution) {
        clsVisit result = new clsVisit();
        result.POI_ID = SwapSolution.InsertedPOI_ID;
        //Anetaret Replay nuk jane caktuar

        clsConstraint Constraint = new clsConstraint();
        clsTimeWindowConstraint TimeWindow = new clsTimeWindowConstraint();
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
    static clsVisit getNewSwapVisitForInsertingPart(clsOperatorSolution SwapSolution) {
        clsVisit result = new clsVisit();
        result.POI_ID = SwapSolution.RemovedPOI_ID;
        //Anetaret Replay nuk jane caktuar

        clsConstraint Constraint = new clsConstraint();
        clsTimeWindowConstraint TimeWindow = new clsTimeWindowConstraint();
        TimeWindow.DistanceFromPrevioiusPoint = SwapSolution.DistanceFromPreviosPointToInsertedPointInInsertingTour;
        TimeWindow.DistanceToNextPoint = SwapSolution.DistanceFromInsertedPointToNextPointInInsertingTour;
        TimeWindow.EndTime = SwapSolution.VisitEndTimeInInsertingTour;
        TimeWindow.StartTime = SwapSolution.VisitStartTimeInInsertingTour;
        TimeWindow.TourIndeks = SwapSolution.InsertedPOITourIndex;
        TimeWindow.VisitIndeks = SwapSolution.InsertedPOIVisitIndex;
        Constraint.TimeWindow = TimeWindow;
        result.constraints = Constraint;
        return result;
    }
    
    static void updateToursLastVisit(int VisitIndexInRemoveingTour, int TourIndexInRemoveingTour, 
            int VisitIndexInInsertingTour, int TourIndexInInsertingTour, 
            int RightPOIID,int LeftPOIID, int[][] TourLastVisit) {
        if (VisitIndexInRemoveingTour == TourLastVisit[1][TourIndexInRemoveingTour]) {
            TourLastVisit[0][TourIndexInRemoveingTour] = RightPOIID;
        }
        if (VisitIndexInInsertingTour == TourLastVisit[1][TourIndexInInsertingTour]) {
            TourLastVisit[0][TourIndexInInsertingTour] = LeftPOIID;
        }
    }
   clsVisit [] createNewTourItinerary(clsVisit TourItinerary[],int TourLastVisit, int LeftPOIIndex, int RightPOIIndex,
             boolean SubTourGenerationNeeded,clsVisit SubTourItinerary[])
    {
        clsVisit [] result= new clsVisit[TourItinerary.length];
        for(int i=0;i<LeftPOIIndex;i++){
            result[i]=TourItinerary[i];
        }      
        result[LeftPOIIndex]=TourItinerary[RightPOIIndex];
       
        if(SubTourGenerationNeeded){
                System.arraycopy(SubTourItinerary, 0, result, LeftPOIIndex+1, SubTourItinerary.length);
        }
        else if(RightPOIIndex-LeftPOIIndex==2)
        {
            result[LeftPOIIndex+1]=TourItinerary[LeftPOIIndex+1];
        }
        result[RightPOIIndex]=TourItinerary[LeftPOIIndex];
        for(int i=RightPOIIndex+1;i<=TourLastVisit;i++){
            result[i]=TourItinerary[i];
        }               
        return result;
    }
    
    clsVisit [] extractSubtour(clsVisit TourItinerary[],int TourLastVisit, int LeftPOIIndex, int RightPOIIndex){
        clsVisit result[]= new clsVisit[RightPOIIndex-LeftPOIIndex-1];
        for(int i=LeftPOIIndex+1;i<RightPOIIndex;i++){
            result[i-LeftPOIIndex-1]=TourItinerary[i];
        }
        return result;
    }
    
    float getSubtourLength(clsVisit SubTourItinerary[], int LeftPOI_ID, int RightPOI_ID){
        float result=0;
        for(int i=0;i<SubTourItinerary.length;i++){
            if(i<SubTourItinerary.length-1){
                result+=(SubTourItinerary[i].constraints.TimeWindow.EndTime-
                        SubTourItinerary[i].constraints.TimeWindow.StartTime)+
                        SubTourItinerary[i].constraints.TimeWindow.DistanceToNextPoint;
            }
            else{
                result+=(SubTourItinerary[i].constraints.TimeWindow.EndTime-
                        SubTourItinerary[i].constraints.TimeWindow.StartTime);
            }
        }
        return result;
    }
    
        float getTwoOptChangeLength(clsData data,clsVisit SubTourItinerary[], int LeftPOI_ID, int RightPOI_ID){
        float result=clsGeneral.getDistanceBetweenPoints(RightPOI_ID, 
                SubTourItinerary[0].POI_ID, data.getDistance())+
                clsGeneral.getDistanceBetweenPoints(SubTourItinerary[SubTourItinerary.length -2].POI_ID, 
                LeftPOI_ID, data.getDistance());
        return result;
    }
        
 clsVisit [] getReversedSubTour(clsVisit SubTourItinerary[]){
        clsVisit result[]=new clsVisit[SubTourItinerary.length];
        for(int i=0;i<SubTourItinerary.length/2;i++)
        {
            result[i]=SubTourItinerary[SubTourItinerary.length-1-i];
            result[SubTourItinerary.length-1-i]=SubTourItinerary[i];
        }
        if(SubTourItinerary.length%2==1){
            result[SubTourItinerary.length/2]=SubTourItinerary[SubTourItinerary.length/2];
        }
         return result;
    }
}
