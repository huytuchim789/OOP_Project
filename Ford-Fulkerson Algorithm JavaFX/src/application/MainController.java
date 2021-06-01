package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Scanner;

//this class controls Main.fxml file
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;



public class MainController{
	
	public static int numOfPump;
	
	@FXML
	private Label lblStatus;
	
	@FXML
	private TextField txtNumOfPump;
	
	
	public void TakeNumOfPump(ActionEvent event) {
		if( txtNumOfPump.getText().equals("")) {
			lblStatus.setText("Please Enter the Number of Faucets\n!");
			System.out.println("BOS");
		}else {
			numOfPump=Integer.valueOf(txtNumOfPump.getText());
			System.out.println( "Number of taps\n: "+txtNumOfPump.getText());
			lblStatus.setText("Generating Graph..");
			
			Graph graph = new SingleGraph("You are viewing the nodes");

	        graph.setStrict(false);
	        graph.setAutoCreate(true);

	        
			for(int i=0;i<numOfPump;i++) {
				graph.addNode(i+"");
			}
			
			  for (Node node : graph) {
			        node.addAttribute("ui.label", node.getId());
			    }
			  
			graph.display();
			
			
			Main.set_Pane(1);
		}
	}
}
