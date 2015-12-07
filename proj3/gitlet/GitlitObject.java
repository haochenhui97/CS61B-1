/**
 * 
 */
package gitlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author william
 *
 */
public abstract class GitlitObject implements Serializable{

    /**
     * The serial version ID of the general gitlit object.
     */
    private static final long serialVersionUID = -2684175013405697783L;

    /**
     * Generates the SHA-1 for the Gitlit object.
     * @return The SHA-1.
     */
    public String sha1(){        
        
        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA1");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(this);

            byte[] result = mDigest.digest(bos.toByteArray());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < result.length; i++) {
                sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
            }
             
            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }

}