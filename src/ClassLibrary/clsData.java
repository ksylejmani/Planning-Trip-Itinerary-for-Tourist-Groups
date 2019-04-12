package ClassLibrary;
import java.io.*;
import java.util.*;
public class clsData {
    
    public  int NumberOfTours,
            NumberOfVertices,
            NumberOfPersons,// Change for MCTOPMTW
            MinimumNumberOfPersonsInSubgroup;// Change for MCTOPMTW
            float BudgetLimitation;
            int NumberOfVerticesOfTypeZ=10,
            MaxNumberOfPOIsPossiblePerTour;
    public  float MaximumNumberOfVerticesOfTypeZ[] =new float[NumberOfVerticesOfTypeZ];
    
    public  float StartPoint []=new float [7];// Seven details for the start point
    public  float POI[][];//details for each point of interest
    
     private float Distance[][]=new float[NumberOfVertices][NumberOfVertices];
     private float POIMiddleOfTimeWindow[]=new float[NumberOfVertices];

    clsData(int aNumberOfTours,int aNumberOfVertices,int aNumberOfPersons,int aMinimumNumberOfPersonsInSubgroup,
            float aBudgetLimitation,float aMaximumNumberOfVerticesOfTypeZ[],float aStartPoint [],float aPOI[][],
            float aDistance[][],int aMaxNumberOfPOIsPossiblePerTour){
        NumberOfTours=aNumberOfTours;
        NumberOfVertices=aNumberOfVertices;
        NumberOfPersons=1;
        MinimumNumberOfPersonsInSubgroup=1;
        BudgetLimitation=aBudgetLimitation;
        System.arraycopy(aMaximumNumberOfVerticesOfTypeZ,0,   MaximumNumberOfVerticesOfTypeZ, 0,10);
        System.arraycopy(aStartPoint,0,StartPoint, 0,StartPoint.length);
        POI=get2DArrayCopy(aPOI);
        Distance=get2DArrayCopy(aDistance);
        regulateTimeWindows();
        POIMiddleOfTimeWindow=this.calculatePOIMiddleOfTimeWindow();
        MaxNumberOfPOIsPossiblePerTour=aMaxNumberOfPOIsPossiblePerTour;
    }
    
    clsData(String filename)throws IOException{
       readFromFile(filename);
       Distance=calculateDistance();
       regulateTimeWindows();
       POIMiddleOfTimeWindow=this.calculatePOIMiddleOfTimeWindow();      
       MaxNumberOfPOIsPossiblePerTour =(int)Math.ceil(getStartPoint()[6]/
                (getMinimumDistance()+getAverageDuration()));
      }
    
    clsData(clsData AnotherData){
        this.NumberOfTours=AnotherData.NumberOfTours;
        this.NumberOfPersons=AnotherData.NumberOfPersons;
        this.NumberOfVertices=AnotherData.NumberOfVertices;
        this.MinimumNumberOfPersonsInSubgroup=AnotherData.MinimumNumberOfPersonsInSubgroup;
        this.BudgetLimitation=AnotherData.BudgetLimitation;
        this.NumberOfVerticesOfTypeZ=AnotherData.NumberOfVerticesOfTypeZ;
        this.MaxNumberOfPOIsPossiblePerTour=AnotherData.MaxNumberOfPOIsPossiblePerTour;
        System.arraycopy(AnotherData.MaximumNumberOfVerticesOfTypeZ, 0, 
                this.MaximumNumberOfVerticesOfTypeZ, 0, this.MaximumNumberOfVerticesOfTypeZ.length);
        System.arraycopy(AnotherData.StartPoint, 0, 
                this.StartPoint, 0, this.StartPoint.length);       
        System.arraycopy(AnotherData.POI, 0, 
                this.POI, 0, this.POI.length);
        System.arraycopy(AnotherData.Distance, 0, 
                this.Distance, 0, this.Distance.length);
        System.arraycopy(AnotherData.POIMiddleOfTimeWindow, 0, 
                this.POIMiddleOfTimeWindow, 0, this.POIMiddleOfTimeWindow.length);
    }
    final float  [][] get2DArrayCopy(float OriginalArray[][]){
        float result[][]=new float [OriginalArray.length][OriginalArray[0].length];
        for(int i=0;i<result.length;i++)
            System.arraycopy(OriginalArray[i], 0, result[i], 0, result[0].length);
        return result;
    }
    public int getNumberOfTours(){
        return NumberOfTours;
    }
    public int getNumberOfVertices(){
        return NumberOfVertices;
    }
    public  int getNumberOfPersons(){
        return NumberOfPersons;
    }
     public int getMinimumNumberOfPersonsInSubgroup(){
        return MinimumNumberOfPersonsInSubgroup;
    }
     public float getBudgetLimitation(){
        return BudgetLimitation;
    }
     public int getNumberOfVerticesOfTypeZ(){
        return NumberOfVerticesOfTypeZ;
    }
     public int getMaxNumberOfPOIsPossiblePerTour(){
        return MaxNumberOfPOIsPossiblePerTour;
    }
     public float [] getStartPoint(){
        return StartPoint;
    }
   public  float [][] getPOI(){
       return POI;
   }
   public  float [] getMaximumNumberOfVerticesOfTypeZ(){
       return MaximumNumberOfVerticesOfTypeZ;
   }
    public float [][] getDistance(){
       return Distance;
   }
   
    public float getPOIMiddleOfTimeWindow(int POI_ID){
       return POIMiddleOfTimeWindow[POI_ID-2];
   }

   
   private float [][] calculateDistance(){
        float[][] result=new float[NumberOfVertices][NumberOfVertices];
        
        for(int i=0;i<result.length;i++)
            for(int j=i;j<result[0].length;j++)
            {
                if(i==0)
                {
                    result[i][j]=(float)(
                                        Math.sqrt(
                                                    Math.pow((StartPoint[1]-POI[j][1]),2)+
                                                    Math.pow((StartPoint[2]-POI[j][2]),2)
                                                )
                                        );
                    //System.out.println("Distance:"+result[i][j]);
                }
                else
                {
                    result[i][j]=(float)(
                                        Math.sqrt(
                                                    Math.pow((POI[i-1][1]-POI[j][1]),2)+
                                                    Math.pow((POI[i-1][2]-POI[j][2]),2)
                                                )
                                        );
                }
            }
       
        return result;
    }
   private float [] calculatePOIMiddleOfTimeWindow(){
       float[] result=new float[NumberOfVertices];
       for(int i=0;i<result.length;i++)
       {
           result[i]=POI[i][4+NumberOfPersons]+(POI[i][8+NumberOfPersons]-POI[i][4+NumberOfPersons])/2;
       }
       return result;
   } 

   
   
   
  public  float getAverageDistance(){
      float result=0;
      for (int i=0;i<Distance.length;i++)
          for(int j=i;j<Distance[0].length;j++)
              result+=Distance[i][j];
      
      result=result/(Distance.length/2*(Distance.length+1));
      return result;
  }
  public float getAverageDuration(){
      float result=0;
      for(int i=0;i<POI.length;i++)
          result+=POI[i][3]; //Data about duration is in index 4;
      return result/POI.length;
  }
  
  public float getMinimumDistance(){
      float result=Distance[0][0];
      for (int i=0;i<Distance.length;i++)
          for(int j=i;j<Distance[0].length;j++)
              if(Distance[i][j]<result)
                result=Distance[i][j];
      return result;
  }
  public float getMaximumDistance(){
      float result=Distance[0][0];
      for (int i=0;i<Distance.length;i++)
          for(int j=i;j<Distance[0].length;j++)
              if(Distance[i][j]>result)
                result=Distance[i][j];
      return result;
  }
 
      
      
        
          
 public float [][] calculateNormalisedDistance(){
     float result[][]=new float[NumberOfVertices][NumberOfVertices];
     float MaximumDistance=this.getMaximumDistance(); 
     for (int i=0;i<result.length;i++)
          for(int j=i;j<result[0].length;j++)
              result[i][j]=Distance[i][j]/MaximumDistance;
     return result;
  }
  

  
  final int calculateTypeConstraintMaxDiff(){// Sum of each type constraint limit
      int result=0;
      for(int i=0;i<MaximumNumberOfVerticesOfTypeZ.length;i++){
          result+=MaximumNumberOfVerticesOfTypeZ[i];
      }
      return result;
  }
  
    int findPOIWithEarliestTimeWindow(){
      float MinOpenTime=(int)POI[0][4+NumberOfPersons];
      int MinPOIID=(int)POI[0][0];
      for(int i=1;i<POI.length;i++)
          if(POI[i][4+NumberOfPersons]<MinOpenTime){
              MinOpenTime=POI[i][4+NumberOfPersons];
              MinPOIID=(int)POI[i][0];
          }
      return MinPOIID;
  }
  
  void readFromFile(String filename) throws IOException {
       Reader r = new BufferedReader(new FileReader(filename));
        StreamTokenizer stok = new StreamTokenizer(r);
        stok.parseNumbers();
        stok.nextToken();
        while (stok.ttype != StreamTokenizer.TT_EOF) {

            if (stok.ttype == StreamTokenizer.TT_NUMBER)
            {      
                this.NumberOfTours=(int)stok.nval;
            }
            else
                System.out.println("Nonnumber: " + stok.sval);
            stok.nextToken();
            if (stok.ttype == StreamTokenizer.TT_NUMBER)
            {      
                this.NumberOfVertices=(int)stok.nval;
            }
            else
                System.out.println("Nonnumber: " + stok.sval);
            stok.nextToken();

            if (stok.ttype == StreamTokenizer.TT_NUMBER)
            {      
                this.NumberOfPersons=(int)stok.nval;
            }
            else
                System.out.println("Nonnumber: " + stok.sval);
            stok.nextToken();

            if (stok.ttype == StreamTokenizer.TT_NUMBER)
            {      
                this.MinimumNumberOfPersonsInSubgroup=(int)stok.nval;
            }
            else
                System.out.println("Nonnumber: " + stok.sval);
            stok.nextToken();
           
            for(int i=0;i<StartPoint.length;i++){
                    if (stok.ttype == StreamTokenizer.TT_NUMBER)
                    {      
                         StartPoint[i]=(float)stok.nval;
                    }
                    else
                        System.out.println("Nonnumber: " + stok.sval);
                    stok.nextToken();   
            }
            
            POI=new float [NumberOfVertices][21+NumberOfPersons];
            for(int i=0;i<POI.length;i++){
                for(int j=0;j<POI[0].length;j++){
                    if (stok.ttype == StreamTokenizer.TT_NUMBER)
                    {      
                         POI[i][j]=(float)stok.nval;
                    }
                    else
                        System.out.println("Nonnumber: " + stok.sval);
                    stok.nextToken();   
                }
            }
            System.out.println("Test");
            //Te mos harrohet edhe ID-ja e personit...

            if (stok.ttype == StreamTokenizer.TT_NUMBER)
            {      
                this.BudgetLimitation=(int)stok.nval;
            }
            else
                System.out.println("Nonnumber: " + stok.sval);
            stok.nextToken();
            this.NumberOfVerticesOfTypeZ=10;//Number of types of POIs
            for(int i=0;i<MaximumNumberOfVerticesOfTypeZ.length;i++){
                    if (stok.ttype == StreamTokenizer.TT_NUMBER)
                    {      
                        MaximumNumberOfVerticesOfTypeZ[i]=(int)stok.nval;
//                        MaximumNumberOfVerticesOfTypeZ[i]=20;
                    }
                    else
                        System.out.println("Nonnumber: " + stok.sval);
                    stok.nextToken();   
            }
            
            
        }
        //System.out.println("Test");
   }
  
   final void regulateTimeWindows(){
//     for(int i=0;i<POI.length;i++){
//         POI[i][NumberOfPersons+8]+=POI[i][3];
//     }
 }

}