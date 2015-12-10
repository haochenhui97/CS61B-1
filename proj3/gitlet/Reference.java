/**
 * 
 */
package gitlet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author william
 *
 */
public class Reference implements Serializable {

    /**
     * The serial reference ID.
     */
    private static final long serialVersionUID = -1972946612858325631L;
    
    /**
     * The target of the reference.
     */
    private String target;
    
    /**
     * If the reference links forward.
     */
    private ReferenceType targetType;
    
    public Reference(ReferenceType targetType, String targetRef){
        this.targetType = targetType;
        this.target = targetRef;
    }
    
    /**
     * Constructs a reference with a target.
     * @param target The target.
     */
    public Reference(String target){
        this(ReferenceType.NONE, target);
    }
    
    public Reference(){
        this.target = "";
        this.targetType = ReferenceType.NONE;
    }
    
    /**
     * Gets the target of this reference.
     * @return The target.
     */
    public String target(){
        return target;
    }
    

    /**
     * Sets the target of a reference.
     * @param target The reference target.
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * If the target is another reference.
     */
    public boolean targetIsReference(){
        return targetType != ReferenceType.NONE;
    }
    
    /**
     * Gets the target type.
     */
    public ReferenceType targetType(){
        return this.targetType;
    }
}