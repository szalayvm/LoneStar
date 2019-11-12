import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

public class TextFieldListener implements ActionListener{
	private JTextField box; 
	public TextFieldListener(JTextField field ){
		this.box= field;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// Here we will pass the text to the algorithim to do starting city ect
		
		
	}

}
