/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassLibrary;

import java.util.*;
import java.text.DecimalFormat;

/**
 *
 * @author Kadri Sylejmani
 */
public class clsGeneral {
    static DecimalFormat df = new DecimalFormat("0.000");
//    static Random randomGenerator = new Random(18031978);//Ditelindja e Sadetes
     static Random randomGenerator = new Random();

public static float roundToDecimals(float d, int c) {
        int temp=(int)((d*Math.pow(10,c)));
        return (((float)temp)/((float)Math.pow(10,c)));
}
    
public static float round(float value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    long factor = (long) Math.pow(10, places);
    value = value * factor;
    long tmp = Math.round(value);
    return (float) tmp / factor;
}
    
    
    static int getRandomNumber(int MaxNumber)
    {
         return randomGenerator.nextInt(MaxNumber );
    }
    
    static float CheckBudget(float BudgetLimit, float ActualCost, float EntreeFee ){
        float result=-1;
        if(ActualCost+EntreeFee<=BudgetLimit)
            result=EntreeFee;
        return result;
    }
    
    static clsTypeConstraint CheckTypeConstraint(int TypeCounter[], float TypeLimit[],
            float POI[],int NumberOfTourists){
        clsTypeConstraint result=new clsTypeConstraint();
        boolean NoTypeDefined=false;
        int NoTypeDefinedCounter=0;
        for(int i=0;i<TypeCounter.length;i++){
            if(POI[i+11+NumberOfTourists]==1){
                if(TypeCounter[i]+1<=TypeLimit[i]){
                    result.Reply=1;
                    result.POITypeCounter[i]=TypeCounter[i]+1;
                }
                else{
                    result.Reply=-1;
                    break;
                }
            }
            else
            {
                result.POITypeCounter[i]=TypeCounter[i];
                NoTypeDefinedCounter++;
            }
        }
        if(NoTypeDefinedCounter==10)
            NoTypeDefined=true;
        if(NoTypeDefined)
            result.Reply=0;
        return result;
    }
    static  clsTimeWindowConstraint ChechkPOITimeWindow(
                                        clsData data,
                                        int POI_ID,
                                        float TourSpentTime[], 
                                        float TourTimeLimit,
                                        float VisitDuration,
                                        int LastVisit[][], 
                                        float distance[][],
                                        float ShiftStartTime,
                                        float ShiftEndTime,
                                        boolean ThereIsNoPOIOpenAtTheStartOfTheTour,
                                        int  TourLastVisit [][])
    {
        clsTimeWindowConstraint result=new clsTimeWindowConstraint(); 
                
        result.Replay=-1;// (-1) - No time window at proposed time, (1) - There is a time window, (-2)- No more space in any tours
                        //  (-3) - All tours have two POIs
        int TourLengthCounter=0;
        int TourIndexToStart=0;
//        int TourIndexToStart= getTourIndexToStartInsertionInInitialSolution(TourLastVisit);
        if(TourIndexToStart==-1){
            result.Replay=-3;
        }
        else
        {
         OuterFor:for(int i=TourIndexToStart;i<TourSpentTime.length;i++){
            float DistanceFromLastPOI;
            float DistanceToEndPoint;
            float UnusedTimeAtTheStartOfTheTour=0;
            if(LastVisit[0][i]==0)
            {
                 DistanceFromLastPOI=getDistanceBetweenPoints(POI_ID,1,distance);// 1 - for Start/End point
                 DistanceToEndPoint=DistanceFromLastPOI;
                 result.VisitIndeks=0;
                 if(ThereIsNoPOIOpenAtTheStartOfTheTour)
                 {
                     UnusedTimeAtTheStartOfTheTour=getTourUnusedTimeAtTheStart(data,POI_ID);
                 }
            }
            else
            {
                 DistanceFromLastPOI=getDistanceBetweenPoints(POI_ID,LastVisit[0][i],distance);
                 DistanceToEndPoint=getDistanceBetweenPoints(POI_ID,1,distance);
                 result.VisitIndeks=LastVisit[1][i]+1;
            }
            
            if(ChechkTourLength(TourSpentTime[i],VisitDuration,DistanceFromLastPOI,
                    DistanceToEndPoint,TourTimeLimit,UnusedTimeAtTheStartOfTheTour))
            {
                float ProposedStartTime;
                if(LastVisit[0][i]==0){
                    ProposedStartTime=UnusedTimeAtTheStartOfTheTour+DistanceFromLastPOI;
                }
                else{
                    ProposedStartTime=TourSpentTime[i]-clsGeneral.getDistanceBetweenPoints(LastVisit[0][i], 1, distance)+DistanceFromLastPOI;
                }
                float ProposedEndTime=ProposedStartTime+VisitDuration;
                //Multiple Time Windows
//                for(int j=0;j<Shift.length-1;j++){
//                    if(ProposedStartTime>=Shift[j]&&(ProposedEndTime<=Shift[j+1]))
//                    {
//                        result=1;
//                        break OuterFor;
//                    }
//                }
                //Single Time Window
                    if(ProposedStartTime>=ShiftStartTime &&(ProposedEndTime<=ShiftEndTime))
                    {
                        result.Replay=1;
                        result.DistanceFromPrevioiusPoint=DistanceFromLastPOI;
                        result.DistanceToNextPoint=DistanceToEndPoint;
                        result.EndTime=ProposedEndTime;
                        result.StartTime=ProposedStartTime;
                        result.UnusedTimeAtTheStartOfTheTour=UnusedTimeAtTheStartOfTheTour;
                        result.TourIndeks=i;
                        break OuterFor;
                    }                
            }
            else
                TourLengthCounter++;
          }
        }
        if(TourLengthCounter==TourSpentTime.length)
            result.Replay=-2;
        return result;
    }
    
   static ArrayList<Integer>  getPOIsWithTimeWindowsInPeriod(ArrayList<Integer> POIsOffItinerary,ArrayList<Integer> IndeksInOffList,
           float TourSpentTime[],float TourEndTime,
           int LastVisit[][], float POI[][],float distance[][],int NumberOfToursits){
       ArrayList<Integer>  result=new ArrayList() ;
       for(int i=0;i<POIsOffItinerary.size();i++){
           for(int j=0;j<TourSpentTime.length;j++){//Te eleminohen vlerat e shumfishta.......
               float ProposedStartTime;
               if(TourSpentTime[j]==0){
                   ProposedStartTime=clsGeneral.getDistanceBetweenPoints(1, POIsOffItinerary.get(i), distance);
               }
               else{
                   ProposedStartTime=TourSpentTime[j]-clsGeneral.getDistanceBetweenPoints(LastVisit[0][j], 1, distance) +
                       clsGeneral.getDistanceBetweenPoints(LastVisit[0][j], POIsOffItinerary.get(i), distance);
               }

               float ProposedEndTime=ProposedStartTime+POI[POIsOffItinerary.get(i)-2][3];
               // Verejteje: Nuk jane trajtuar dritaret e shumefishta kohore
               if(ProposedStartTime>=POI[POIsOffItinerary.get(i)-2][4+NumberOfToursits] &&
                      ProposedEndTime<=POI[POIsOffItinerary.get(i)-2][8+NumberOfToursits] )   
               {
                   result.add(POIsOffItinerary.get(i));
                   IndeksInOffList.add(i);
                   break;
               }
           }
       }
       
       
       return result;
   }
   static float getDistanceBetweenPoints(int Point1,int Point2, float distance[][]){
       float result=-1;
       if(Point1>Point2)
           result=distance[Point2-1][Point1-2];
       else
           result=distance[Point1-1][Point2-2];
       return result;
   }
    
    static int [] getLastVisit(int [][] TourLastVisit){
        int [] result= TourLastVisit[0] ;
        return result;
    }
    static boolean ChechkTourLength(float SpentTime,
                                    float VisitDuration,
                                    float TravelingTimeFromLastVisit,
                                    float TravelingTimeToNextVisit,   
                                    float TourTimeLimit,
                                    float UnusedTimeAtTheStartOfTheTour){
      boolean result=false;
      if(SpentTime+UnusedTimeAtTheStartOfTheTour+TravelingTimeFromLastVisit+VisitDuration+TravelingTimeToNextVisit<=TourTimeLimit)
          result=true;
      return result;
    }
        
    
    static int [] getPOITypes(float POI []){
        int result []=new int [10];
        for(int i=POI.length-1;i>POI.length-11;i--)
            result[10-(POI.length-i)]=(int)POI[i];
        return result;
    }
    
//    static float[] getShift(float POI[],int NumberOfPersons){//Tested
//        float result[]=new float[5];
//        for(int i=0;i<result.length;i++)
//            result[i]=POI[4+NumberOfPersons+i];
//        return result;
//    }
    
    static void updateTypeConstraintCounter(int TypeCounter[],int POIType[]){
        for(int i=0;i<POIType.length;i++){
            if(POIType[i]==1)
                TypeCounter[i]++;
        }
    }
    static void updateTourLastVisit(int LastVisit[][],int POI_ID,int TourIndeks,int VisitIndeks){
        LastVisit[0][TourIndeks]=POI_ID;
        LastVisit[1][TourIndeks]=VisitIndeks;
    }
    
    static void UpdateMaxShift(int POI_ID, clsTimeWindowConstraint TimeWindowConstraint, 
            float MaxShift[][][], float POI[][],int NumberOfTourists, 
            int TourLastVisit[][]){
        
//        if(TimeWindowConstraint.VisitIndeks>=MaxShift[0][0].length )
//            System.out.println("Stop");
        //0-RightShift

        MaxShift[TimeWindowConstraint.TourIndeks][0][TimeWindowConstraint.VisitIndeks]=
                    POI[POI_ID-2][8+NumberOfTourists]-TimeWindowConstraint.EndTime;
        // 1-LeftShift
        MaxShift[TimeWindowConstraint.TourIndeks][1][TimeWindowConstraint.VisitIndeks]=
                TimeWindowConstraint.StartTime-POI[POI_ID-2][4+NumberOfTourists];
    
        for(int i=0;i<=TimeWindowConstraint.VisitIndeks-1;i++){//Mund te jete e nevojshme te testohet akoma funksionimi i kesaj metode
                if(MaxShift[TimeWindowConstraint.TourIndeks][0][TimeWindowConstraint.VisitIndeks]<
                        MaxShift[TimeWindowConstraint.TourIndeks][0][i]){
                    MaxShift[TimeWindowConstraint.TourIndeks][0][i]=
                            MaxShift[TimeWindowConstraint.TourIndeks][0][TimeWindowConstraint.VisitIndeks];
                            //MaxShiftNeighbourImpact[TimeWindowConstraint.TourIndeks][0][TimeWindowConstraint.VisitIndeks]++;
                            
                }
                if(MaxShift[TimeWindowConstraint.TourIndeks][1][TimeWindowConstraint.VisitIndeks]<
                        MaxShift[TimeWindowConstraint.TourIndeks][1][i]){
                    MaxShift[TimeWindowConstraint.TourIndeks][1][i]=
                            MaxShift[TimeWindowConstraint.TourIndeks][1][TimeWindowConstraint.VisitIndeks];
                    //MaxShiftNeighbourImpact[TimeWindowConstraint.TourIndeks][1][TimeWindowConstraint.VisitIndeks]++;
                }
        }                
    }
   static float getMaxRightShift(clsData data,int POI_ID, float VisitEndTime) {
        float result;
        result = data.getPOI()[POI_ID - 2][8 + data.getNumberOfPersons()] - VisitEndTime;
        return result;
    }
   static float  getMaxLeftShift(clsData data,int POI_ID, float VisitStartTime) {
        float result;
        result = VisitStartTime-data.getPOI()[POI_ID - 2][4 + data.getNumberOfPersons()] ;
        return result;
    }
    
    static float getTripSpentTime(float TourSpentTime[]){
        float result=0;
        for(int i=0;i<TourSpentTime.length;i++){
            result+=TourSpentTime[i];
        }
        return result;
    }
static void updateTourMaxShift(clsData data,int TourIndex, int TourLastVisit[][], 
        clsVisit Itinerary[][], float MaxShift[][][]) {
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
//                
//                //Test
//                if(MaxShift[TourIndex][0][i]<0)
//                    System.out.println();
//                if(MaxShift[TourIndex][1][i]<0)
//                    System.out.println();
//                //Test
                
            }
        }
    static int [][] getMaxShiftImpactInTrip(int TourLastVisit[][],float MaxShift[][][] ){
        int result[][]=new int[2*MaxShift.length][3];
        for(int i=0;i<result.length;i++)
        {
            int RightLeftImpact=i%2;
            int currentMaxShiftNeighbourImpact[]=getMaxShiftNeighbourImpact(i,TourLastVisit[1][i/2],MaxShift,RightLeftImpact);
            int currentMaxShiftImpactInTour[]=getMaxShiftImpactInTour(currentMaxShiftNeighbourImpact,TourLastVisit[1][i/2],i);  
            result[i]=currentMaxShiftImpactInTour;
        }
        return result;
}
 static int [] getMaxShiftNeighbourImpact(int TourIndex,int TourLastVisit,
        float MaxShift[][][], int RightLeft){
        int result[]=new int[TourLastVisit+1];
        int ImpactIndex=TourLastVisit;    
        for(int i=TourLastVisit-1;i>=0;i--){
            float CurrentVisitMaxShift=MaxShift[TourIndex/2][RightLeft][i];           
            if(CurrentVisitMaxShift< MaxShift[TourIndex/2][RightLeft][i+1]){
                ImpactIndex=i;
            }
            else{
                    result [ImpactIndex]++;
            }
        }   
        return result;
  }
       static int [] findMaxShiftImpact(int ShiftingImpact[],int LastVisitInTour){
            int MAX []=new int [2];//0-Value, 1-Index
            MAX[0] = ShiftingImpact[0];
            MAX[1] =0;
            for(int i=1;i<=LastVisitInTour;i++)
               if(ShiftingImpact[i]>MAX[0]){
                   MAX[0]=ShiftingImpact[i];
                   MAX[1] =i;
               }
               return MAX;
       }
       static int [] getMaxShiftImpactInTour(int  MaxShiftNeighbourImpact[],
               int TourLastVisit, int TourIndex){
           int result[]=new int[3]; 
           int MaxShiftImpact[]=findMaxShiftImpact(MaxShiftNeighbourImpact,
                    TourLastVisit);      
            result[0]=MaxShiftImpact[0];
            result[1]=MaxShiftImpact[1];
            result[2]=TourIndex/2;
            return result;
       }
       static void updateRecencyMemory(int IdOfPOIToBeSwapt, int IdOfPOIThatSwaps, int iterationIndeks,int RecencyMemory[][])
    {
       boolean isSweptPOIIDSmaller=IdOfPOIToBeSwapt<IdOfPOIThatSwaps;
        if(isSweptPOIIDSmaller)
        {
            RecencyMemory[IdOfPOIToBeSwapt-1][IdOfPOIThatSwaps-2]=iterationIndeks;
        }
        else
        {
            RecencyMemory[IdOfPOIThatSwaps-1][IdOfPOIToBeSwapt-2]=iterationIndeks;
        }
    }
    static void updateFrequencyMemory(int IdOfPOIToBeSwapt, int IdOfPOIThatSwaps,int FrequencyMemory[][], int FREQUENCY_MEMORY_HORIZON)
    {
       boolean isSweptPOIIDSmaller=IdOfPOIToBeSwapt<IdOfPOIThatSwaps;
        if(isSweptPOIIDSmaller)
        {
            if(FrequencyMemory[IdOfPOIToBeSwapt-1][IdOfPOIThatSwaps-2]==FREQUENCY_MEMORY_HORIZON)
                FrequencyMemory[IdOfPOIToBeSwapt-1][IdOfPOIThatSwaps-2]=0;
            else
                FrequencyMemory[IdOfPOIToBeSwapt-1][IdOfPOIThatSwaps-2]++;
        }
        else
        {
            if(FrequencyMemory[IdOfPOIThatSwaps-1][IdOfPOIToBeSwapt-2]==FREQUENCY_MEMORY_HORIZON)
                FrequencyMemory[IdOfPOIThatSwaps-1][IdOfPOIToBeSwapt-2]=0;
            else
                FrequencyMemory[IdOfPOIThatSwaps-1][IdOfPOIToBeSwapt-2]++;
        }
    }
    
    static boolean EveryTourHasAtLeastTwoPOIs(clsSolution solution){
        boolean result=true;
        for(int i=0;i<solution.Itinerary.length;i++){
            if(solution.Itinerary[i][1]==null){
                result=false;
                break;
            }
        }
        return result;
    }
    
    static int getTabooListSize(int INITIAL_TABU_LIST_SIZE, int IterationsWithoutImprovement,int TLS_Step,int IWI_Step ){
        int result;
        if(IterationsWithoutImprovement<IWI_Step)
            result=INITIAL_TABU_LIST_SIZE;
        else if(IterationsWithoutImprovement>=IWI_Step && IterationsWithoutImprovement<2*IWI_Step)
            result=INITIAL_TABU_LIST_SIZE+TLS_Step;
        else if(IterationsWithoutImprovement>=2*IWI_Step && IterationsWithoutImprovement<3*IWI_Step)
            result=INITIAL_TABU_LIST_SIZE+2*TLS_Step; 
         else if(IterationsWithoutImprovement>=3*IWI_Step && IterationsWithoutImprovement<4*IWI_Step)
            result=INITIAL_TABU_LIST_SIZE+3*TLS_Step; 
         else if(IterationsWithoutImprovement>=4*IWI_Step && IterationsWithoutImprovement<5*IWI_Step)
            result=INITIAL_TABU_LIST_SIZE+4*TLS_Step;
         else if(IterationsWithoutImprovement>=5*IWI_Step && IterationsWithoutImprovement<6*IWI_Step)
            result=INITIAL_TABU_LIST_SIZE+5*TLS_Step;    
         else if(IterationsWithoutImprovement>=6*IWI_Step && IterationsWithoutImprovement<7*IWI_Step)
            result=INITIAL_TABU_LIST_SIZE+6*TLS_Step;           
         else if(IterationsWithoutImprovement>=7*IWI_Step && IterationsWithoutImprovement<8*IWI_Step)
            result=INITIAL_TABU_LIST_SIZE+7*TLS_Step;   
         else if(IterationsWithoutImprovement>=8*IWI_Step && IterationsWithoutImprovement<9*IWI_Step)
            result=INITIAL_TABU_LIST_SIZE+8*TLS_Step;
        else
            result=INITIAL_TABU_LIST_SIZE+9*TLS_Step; 
        return result;
    }
    static float getTourUnusedTimeAtTheStart(clsData data,int POI_ID){
        float result=0;
        float DistanceFromStartPointToCurrentPOI=getDistanceBetweenPoints(1,POI_ID,data.getDistance());
        float OpeningTimeOfPOI=data.getPOI()[POI_ID-2][data.getNumberOfPersons()+4];
        if(DistanceFromStartPointToCurrentPOI<OpeningTimeOfPOI)
            result=OpeningTimeOfPOI-DistanceFromStartPointToCurrentPOI-data.getStartPoint()[5];
        return result;
    }
    
    static int getTourIndexToStartInsertionInInitialSolution( int  TourLastVisit [][]){
        int result=-1;
        for(int i=0;i<TourLastVisit[0].length;i++){
            if(TourLastVisit[1][i]<2){
                result=i;
                break;
            }
        }
        return result;
    }
}
