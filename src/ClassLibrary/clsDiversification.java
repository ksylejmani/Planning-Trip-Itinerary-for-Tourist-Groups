/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassLibrary;

/**
 *
 * @author user
 */
public class clsDiversification {
    
    static boolean IsSearchDiversificationNeeded(int countIterationsWithoutImprovement,int DIVERSIFICATION_ITERATIONS){
        boolean result;
        result= (countIterationsWithoutImprovement!=0) && 
                ((countIterationsWithoutImprovement % DIVERSIFICATION_ITERATIONS)==0);
//        if(result)
//            System.out.println("Test");
        return result;
    }  
   static float getPenalty(int FrequencyBasedMemory[][],int IdOfPOIToBeSwapt, 
           int IdOfPOIThatSwaps, float PENALTY_COEFFICIENT){
       float result;
        int frequencyMemoryValue=getFrequencyMemoryValue(
                FrequencyBasedMemory,
                IdOfPOIToBeSwapt,
                IdOfPOIThatSwaps);
       result=PENALTY_COEFFICIENT*frequencyMemoryValue;
       return result;
   }     
    
   static int getFrequencyMemoryValue(int [][] memory,int IdOfPOIToBeSwapt, int IdOfPOIThatSwaps)
    {
        int result;
        boolean isSweptPOIIDSmaller=IdOfPOIToBeSwapt<IdOfPOIThatSwaps;
        if(isSweptPOIIDSmaller)
        {
            result=memory[IdOfPOIToBeSwapt-1][IdOfPOIThatSwaps-2];
        }
        else
        {
            result=memory[IdOfPOIThatSwaps-1][IdOfPOIToBeSwapt-2];
        }
        return result;
    }
    static void updateRecencyMemory(int [][] RecencyMemory,int [][] FrequencyMemory,
            int IterationNumber,int DIVERSIFICATION_APPLICATION_LIMIT,
            int DIVERSIFICATION_TABU_LIST_SIZE){
        for(int i=0;i<RecencyMemory.length;i++){
            for(int j=i;j<RecencyMemory.length;j++){
                if(FrequencyMemory[i][j]>=DIVERSIFICATION_APPLICATION_LIMIT){
                    RecencyMemory[i][j]=IterationNumber+FrequencyMemory[i][j];
                }
            }
        }
    }
}
