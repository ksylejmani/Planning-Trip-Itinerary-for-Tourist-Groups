/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassLibrary;

/**
 *
 * @author user
 */
public class clsTest {
    
  
    static void PrintTrip(clsData data,clsSolution solution){
        System.out.println("Trip Itinerary for period from "+data.getStartPoint()[5]+" until "+data.getStartPoint()[6]+".");
        System.out.println("-----------");
        for(int i=0;i<solution.Itinerary.length;i++){
            System.out.println("Tour "+(i+1));
            for(int j=0;j<=solution.TourLastVisit[1][i];j++){
                System.out.print("Visit "+(j+1)+" to POI "+solution.Itinerary[i][j].POI_ID+
                        " - Start time:" +solution.Itinerary[i][j].constraints.TimeWindow.StartTime);
                System.out.print(", End time:"+solution.Itinerary[i][j].constraints.TimeWindow.EndTime+"\n");
            }
             System.out.println("---");
             System.out.println("chechkTimeSpent="+chechkTripTimeSpent(data,solution));
             System.out.println("Time spent of tour "+i+" "+solution.TourTimeSpent[i]);
             System.out.println("Left time:"+(data.getStartPoint()[6]-solution.TourTimeSpent[i]));
            PrintMemory(solution.MaxShift[i],"MaxShift");
        }
        System.out.println("Evaluation:"+solution.Evaluation);
         System.out.println("-----------");
        System.out.println("Cost:"+solution.BudgetCost);
         System.out.println("-----------");
         printArray(solution.TypeConstraintCounter);
          System.out.println("-----------");
    }
    
    static void PrintMemory(int [][] Memory, String MemoryName){
        System.out.println(MemoryName);
        System.out.println("-----------");
        for(int i=0;i<Memory.length;i++){
            for(int j=0;j<Memory[0].length;j++){
                System.out.print(Memory[i][j]+ "  ");
            }
            System.out.println();
        }
         System.out.println("-----------");
    }
    static void PrintMemory(float [][] Memory, String MemoryName){
        System.out.println(MemoryName);
        System.out.println("-----------");
        for(int i=0;i<Memory.length;i++){
            for(int j=0;j<Memory[0].length;j++){
                System.out.print(Memory[i][j]+ "  ");
            }
            System.out.println();
        }
         System.out.println("-----------");
    }    
    static void DistanceTest(clsSolution Solution)
    {
        for(int i=0;i<Solution.Itinerary.length;i++)
        {
            for(int j=0;j<Solution.TourLastVisit[1][i];j++)
            {
                //Test
                if((j>0)&& Solution.Itinerary[i][j].constraints.TimeWindow.DistanceFromPrevioiusPoint!=
                        Solution.Itinerary[i][j-1].constraints.TimeWindow.DistanceToNextPoint)
                    System.out.println("Test");

                //Test
            }
        }
    }
        static void StartTimeTest(clsSolution Solution)
    {
        for(int i=0;i<Solution.Itinerary.length;i++)
        {
            for(int j=0;j<Solution.TourLastVisit[1][i];j++)
            {
                //Test
                if((j>0)&& 
                        Solution.Itinerary[i][j].constraints.TimeWindow.StartTime<
                        Solution.Itinerary[i][j-1].constraints.TimeWindow.EndTime)
                    System.out.println("Test");

                //Test
            }
        }
    }
        
        static void StartTimeTest(clsSolution CurrentSolution,clsOperatorSolution SwapSolution,int IterationCounter){
                            //Test
                if((SwapSolution.VisitIndex>0)&&(IterationCounter==1)&& SwapSolution.InsertedVisitStartTime<
                        CurrentSolution.Itinerary[SwapSolution.TourIndex][SwapSolution.VisitIndex-1].constraints.TimeWindow.EndTime
                        )
                    System.out.println("Test");

                //Test
        }
        
        static void OnlyStartTimeTest(clsData data,clsSolution Solution){//

        float StartTime;
        for(int i=0;i<Solution.Itinerary.length;i++)
        {
            for(int j=0;j<Solution.TourLastVisit[1][i];j++)
            {
                //Test
                if(j==0){
                    StartTime=clsGeneral.getDistanceBetweenPoints(1, Solution.Itinerary[i][j].POI_ID, data.getDistance());
                }
                else{
                    StartTime=Solution.Itinerary[i][j-1].constraints.TimeWindow.EndTime +
                            clsGeneral.getDistanceBetweenPoints(Solution.Itinerary[i][j-1].POI_ID, 
                            Solution.Itinerary[i][j].POI_ID, data.getDistance());
                }
                if(clsGeneral.roundToDecimals(StartTime,4) != 
                   clsGeneral.roundToDecimals(Solution.Itinerary[i][j].constraints.TimeWindow.StartTime,4)){
                    System.out.println("True StartTime :  "+StartTime);
                    System.out.println("False StartTime:  "+
                            Solution.Itinerary[i][j].constraints.TimeWindow.StartTime);
                    System.out.println("StartTime Not OK");
                }
                        

                //Test
            }
        }
            
        }
        
        static void OnlyStartTimeTest(clsData data,clsSolution CurrentSolution,clsOperatorSolution SwapSolution){ 
            float StartTime;
            if(SwapSolution.VisitIndex==0){
                    StartTime=clsGeneral.getDistanceBetweenPoints(1, 
                           SwapSolution.InsertedPOI_ID, data.getDistance());
                }
                else{
                    StartTime=CurrentSolution.Itinerary[SwapSolution.TourIndex][SwapSolution.VisitIndex-1].constraints.TimeWindow.EndTime +
                            clsGeneral.getDistanceBetweenPoints(CurrentSolution.
                            Itinerary[SwapSolution.TourIndex][SwapSolution.VisitIndex-1].POI_ID, 
                            SwapSolution.InsertedPOI_ID, data.getDistance());
                }
                    if(StartTime != CurrentSolution.Itinerary[SwapSolution.TourIndex][SwapSolution.VisitIndex].
                            constraints.TimeWindow.StartTime){
                        System.out.println("True StartTime :  "+StartTime);
                        System.out.println("False StartTime:  "+
                                CurrentSolution.Itinerary[SwapSolution.TourIndex][SwapSolution.VisitIndex].constraints.TimeWindow.StartTime);
                        System.out.println("Test");
                    }
        }
        
        static boolean chechkCost(clsData data,clsSolution Solution){//Initial Solution, Swap OK
            boolean rez=true;
            float RealCost=0;
            for(int i=0;i<Solution.Itinerary.length;i++)
            {
                for(int j=0;j<=Solution.TourLastVisit[1][i];j++)
                {
                    RealCost=RealCost+ data.getPOI()[Solution.Itinerary[i][j].POI_ID-2][10+data.getNumberOfPersons()] ;
                }
            }
            if(RealCost!=Solution.BudgetCost){
                rez=false;
            }
            return rez;
        }
        
        static boolean chechkTimeWindows(clsData data,clsSolution Solution)
        {
            boolean rez=true;
            for(int i=0;i<Solution.Itinerary.length;i++)
            {
                for(int j=0;j<=Solution.TourLastVisit[1][i];j++)
                {
                    if((Solution.Itinerary[i][j].constraints.TimeWindow.StartTime <
                            data.getPOI()[Solution.Itinerary[i][j].POI_ID-2][4+data.getNumberOfPersons()]) ||
                            (Solution.Itinerary[i][j].constraints.TimeWindow.EndTime > 
                            data.getPOI()[Solution.Itinerary[i][j].POI_ID-2][8+data.getNumberOfPersons()])
                            ){
                        rez=false;//Time windows nuk jane ne rregulle...
                        System.out.println("Time Window of point "+Solution.Itinerary[i][j].POI_ID+" is surpassed!");
                    }
                }
            }
            return rez;
        }
        
        static boolean chechkTypeConstraintCounting(clsData data,clsSolution Solution){
            boolean result=true;
            int RealTypeCounter []=new int [10];
            for(int i=0;i<Solution.Itinerary.length;i++)
            {
                for(int j=0;j<=Solution.TourLastVisit[1][i];j++)
                {
                    for(int k=0;k<10;k++){
                        RealTypeCounter[k]+=data.getPOI()[Solution.Itinerary[i][j].POI_ID-2][11+data.getNumberOfPersons()+k];
                    }
                }
            }            
            
            for(int i=0;i<10;i++){
                if(RealTypeCounter[i]!=Solution.TypeConstraintCounter[i])
                {
                    result=false;
                    break;
                }
            }
            return result;
        }
        static boolean chechkTypeConstraintLimit(clsData data,clsSolution Solution){
            boolean result=true;
            float MaximumNumberOfVerticesOfTypeZ[]=data.getMaximumNumberOfVerticesOfTypeZ();
            for(int i=0;i<10;i++){
                if(MaximumNumberOfVerticesOfTypeZ[i]<Solution.TypeConstraintCounter[i]){
                    result=false;
                    break;
                }
            }
            return result;
        }
        
        static boolean chechkTripTimeSpent(clsData data,clsSolution Solution){
            boolean result=true;
            float RealTourSpentTime[]=new float[Solution.Itinerary.length];
            for(int i=0;i<Solution.Itinerary.length && Solution.Itinerary[i][0]!=null ;i++){
                RealTourSpentTime[i]=Solution.Itinerary[i][0].constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour+
                        clsGeneral.getDistanceBetweenPoints(1, Solution.Itinerary[i][0].POI_ID, data.getDistance());
            }
           
            for(int i=0;i<Solution.Itinerary.length && Solution.Itinerary[i][0]!=null;i++)
            {
                for(int j=0;j<=Solution.TourLastVisit[1][i];j++)
                {
                    if(j!=Solution.TourLastVisit[1][i]){
                        RealTourSpentTime[i]+=(data.getPOI()[Solution.Itinerary[i][j].POI_ID-2][3]+
                                clsGeneral.getDistanceBetweenPoints(Solution.Itinerary[i][j].POI_ID, 
                                Solution.Itinerary[i][j+1].POI_ID, data.getDistance()));
                    }
                    else{
                        RealTourSpentTime[i]+=(data.getPOI()[Solution.Itinerary[i][j].POI_ID-2][3]+
                                clsGeneral.getDistanceBetweenPoints(Solution.Itinerary[i][j].POI_ID, 
                                1, data.getDistance()));                       
                    }
                }
            }
            
            for(int i=0;i<Solution.Itinerary.length;i++){
                boolean TourTimeSpentTest=Math.abs(clsGeneral.roundToDecimals(RealTourSpentTime[i], 5)
                        -clsGeneral.roundToDecimals(Solution.TourTimeSpent[i], 5))>=0.5f;
                if( TourTimeSpentTest){
                    System.out.println("RealTourSpentTime[i]="+
                            RealTourSpentTime[i]);
                    System.out.println("clsGeneral.roundToDecimals(RealTourSpentTime[i], 3)="+
                            clsGeneral.roundToDecimals(RealTourSpentTime[i], 3));
                    
                    System.out.println("Solution.TourTimeSpent[i]="+
                            Solution.TourTimeSpent[i]);
                    System.out.println("clsGeneral.roundToDecimals(Solution.TourTimeSpent[i], 3)="+
                            clsGeneral.roundToDecimals(Solution.TourTimeSpent[i], 3));
                    
                    result=false;
                    break;
                }
            }
            return result;
        }
static boolean chechkTourTimeSpent(clsData data,clsVisit Itinerary[], int TourLastVisit, float TourTimeSpent){
            boolean result=true;
            float RealTourSpentTime;
                RealTourSpentTime=clsGeneral.getDistanceBetweenPoints(1, Itinerary[0].POI_ID, data.getDistance());
           
                for(int j=0;j<=TourLastVisit;j++)
                {
                    if(j!=TourLastVisit){
                        RealTourSpentTime+=(data.getPOI()[Itinerary[j].POI_ID-2][3]+
                                clsGeneral.getDistanceBetweenPoints(Itinerary[j].POI_ID, 
                                Itinerary[j+1].POI_ID, data.getDistance()));
                    }
                    else{
                        RealTourSpentTime+=(data.getPOI()[Itinerary[j].POI_ID-2][3]+
                                clsGeneral.getDistanceBetweenPoints(Itinerary[j].POI_ID, 
                                1, data.getDistance()));                       
                    }
                }
            
                boolean TourTimeSpentTest=Math.abs(clsGeneral.roundToDecimals(RealTourSpentTime, 5)
                        -clsGeneral.roundToDecimals(TourTimeSpent, 5))>=0.5f;
                if( TourTimeSpentTest){
                    System.out.println("RealTourSpentTime=" + RealTourSpentTime);
                    System.out.println("clsGeneral.roundToDecimals(RealTourSpentTime, 3)="+
                            clsGeneral.roundToDecimals(RealTourSpentTime, 3));
                    
                    System.out.println("Solution.TourTimeSpent[i]="+ TourTimeSpent);
                    System.out.println("clsGeneral.roundToDecimals(TourTimeSpent, 3)="+
                            clsGeneral.roundToDecimals(TourTimeSpent, 3));
                    result=false;
                }
            return result;
        }
        
        static boolean chechkEvaluation(clsData data,clsSolution Solution){
            boolean result=true;
            float RealEvaluation=0;
            for(int i=0;i<Solution.Itinerary.length;i++)
            {
                for(int j=0;j<=Solution.TourLastVisit[1][i];j++)
                {
                    for(int k=0;k<data.getNumberOfPersons();k++){
                        RealEvaluation+=data.getPOI()[Solution.Itinerary[i][j].POI_ID-2][4+k];
                    }
                }
            }   
            if(RealEvaluation!=Solution.Evaluation){
                    result=false;
                }
            return result;
        }
        
        static boolean chechkPOIsInItineraryAreDifferent(clsSolution Solution){
            boolean result=true;
            
            for(int i=0;i<Solution.Itinerary.length;i++)
            {
                for(int j=0;j<=Solution.TourLastVisit[1][i];j++)
                {
                //Te implementohet
                }
            }
            return result;
        }
        
        static void generateSwapInCombination(clsSolution solution){
         
//         int A[][]={{1,2,3},{4,5,6},{7,8,9}};   
//         int p=1;
//         for (int i = 0; i < A.length; i++) {
//             for (int j = 0; j<A[0].length; j++) {
//                if((i==A.length-1 ) && (j==A[0].length-1))
//                    break;
//                 int q=p;
//                 for (int k = i; k < A.length; k++) {
//                    for (int l = q; l < A[0].length; l++) {
//                        System.out.println(A[i][j]+" & "+A[k][l]);
//                    }
//                    q=0;
//                }
//                 p++;
//            }
//             p=1;
//          }
            
         // n*(n-1)/2 combinations
         for (int i = 0; i < solution.Itinerary.length; i++) {
             int p=1;
             for (int j = 0; j<=solution.TourLastVisit[1][i]; j++) {
                if((i==solution.Itinerary.length-1 ) && (j==solution.TourLastVisit[1][i]))
                    break;
                 int q=p;
                 for (int k = i; k <solution.Itinerary.length; k++) {
                    for (int l = q; l <= solution.TourLastVisit[1][k]; l++) {
                        System.out.println(solution.Itinerary[i][j].POI_ID+" & "+solution.Itinerary[k][l].POI_ID);
                    }
                    q=0;
                }
                 p++;
            }
            p=1;
          }
            
            
//            for (int i = 0; i < solution.Itinerary.length; i++) {
//            for (int j = 0; j <= solution.TourLastVisit[1][i]; j++) {
//                for (int k = i; k < solution.Itinerary.length; k++) {
//                    for (int l = j; j < solution.TourLastVisit[1][i]; l++) {
//                        System.out.println(solution.Itinerary[i][j].POI_ID+" & "+solution.Itinerary[k][l].POI_ID);
//                    }
//                }
//            }
//          }
        }
        
        static void printArray(int A[]){
            System.out.print("Initial Solution:");
            for(int i=0;i<A.length;i++)
               System.out.print(A[i] + " ");
        } 
        
        
       static void HeuristicFunctionTest(){
         int NumberOfIterationsForHeuristicFunction=30;
           for(int i=1;i<=200;i++){
                boolean IsHeuristicFunctionApplied=!((i/NumberOfIterationsForHeuristicFunction)%2==0);
                System.out.println("i="+i);
                System.out.println("IsHeuristicFunctionApplied="+IsHeuristicFunctionApplied);
           }
       }
       
       static void printSequenceofPOIs(int PersonID,clsVisit  Itinerary [][],int  TourLastVisit [][]){
           System.out.print("PersonID : "+PersonID+" ->  ");
           for(int i=0;i<Itinerary.length;i++){
               for(int j=0;j<=TourLastVisit[1][i];j++){
//                   System.out.print(Itinerary[i][j].POI_ID+"  ");
                   System.out.format("% 5d", Itinerary[i][j].POI_ID);
               }
           }
           System.out.println();
       }
}
