/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassLibrary;

/**
 *
 * @author Kadri Sylejmani
 */
public class clsConstraint {
    
    int Reply;//(-1) OverBudget,(-2) - POI Type constraint Violation, (-3) TimeWindow Violation, (1) - No Violation
    float Cost;
    clsTypeConstraint POIType;
    clsTimeWindowConstraint TimeWindow;
    
}
