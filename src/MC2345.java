import com.sk89q.craftbook.CraftBookWorld;
import com.sk89q.craftbook.SignText;
import com.sk89q.craftbook.Vector;
import com.sk89q.craftbook.ic.BaseIC;
import com.sk89q.craftbook.ic.ChipState;
import com.sk89q.craftbook.ic.MC1110;

/**
 * Marquee Transmitter
 * 
 * Given a band prefix ("myband") and a low and high counter values, the
 * IC will sequentially power all bands, eg:
 * 
 * myband:0:3
 * 
 * myband0
 * myband1
 * myband2
 * myband3
 * 
 * Incrementing the band with each input 1 toggle. The marquee will restart at
 * low after reaching high, or on input 2 toggle.
 * 
 * @author gbuxton
 *
 */
public class MC2345 extends BaseIC {

	@Override
	public String getTitle() {

		return "MARQUEETRANSMIT";
	}

    @Override
    public String validateEnvironment(CraftBookWorld cbworld, Vector pos, SignText sign) {
        String lineThree = sign.getLine3();
        String lineFour = sign.getLine4();
        String parts[] = lineThree.split(":");
        int lowVal = 0;
        int highVal = 0;
        
        if (parts.length != 3) {
        	return "Specify a band name, low, and high values. Band:Low#:High#";
        } else if (parts[0].length() == 0) {
        	return "You must specify a band name.";
        }
        
        lowVal = Integer.parseInt(parts[1]);
        highVal = Integer.parseInt(parts[2]);
        
        if (lowVal < 0) {
        	return "Low value must be >= 0";
        } else if (lowVal > highVal) {
        	return "Low value cannot be greater than high value.";
        }
        
        if (lineFour.length() != 0) {
        	return "Line four must be left blank!";
        }
        
        return null;
    }
	
	@Override
	public void think(ChipState chip) {
        String lineThree = chip.getText().getLine3();
        String lineFour = chip.getText().getLine4();
        String parts[] = lineThree.split(":");
        int lowVal = Integer.parseInt(parts[1]);
        int highVal = Integer.parseInt(parts[2]);
        int current = 0;

        // Get the current number in the sequence if there is one
        if (lineFour.length() > 0) {
        	current = Integer.parseInt(lineFour);
        }
                
		if (chip.getIn(1).is() == true && chip.getIn(2).is() == false) {

	        // Turn that band off
	        MC1110.airwaves.put(parts[0] + current, false);
	        
	    	if (current < highVal) {
	    		current++;
	    	} else {
	    		current = lowVal;
	    	}
	    	chip.getText().setLine4(Integer.toString(current));
	    	
	        MC1110.airwaves.put(parts[0] + current, true);
		} else if (chip.getIn(2).is() == true) {

			MC1110.airwaves.put(parts[0] + current, false);
			chip.getText().setLine4(Integer.toString(lowVal));
			MC1110.airwaves.put(parts[0] + lowVal, true);
		}
	}
}
