package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

class ExtraHourList {
	String title;
	Map<Worker, Double> extraHours;
	
	ExtraHourList(){
		 this.extraHours = new HashMap<Worker, Double>();
	}
}

public class HourListPane extends ScrollPane {
	class ExtraListPane extends FlowPane {
		private TextArea titleTextArea;
		private Button addNameButton;
		private List<FlowPane> namePanes;
		private Button removeSelfButton;
		
		public String getTitle() {
			return this.titleTextArea.getText();
		}
		public void setTitle(String title) {
			this.titleTextArea.setText(title);;
		}
		
		@SuppressWarnings("unchecked")
		public ExtraHourList getExtraList() {
			ExtraHourList extraList = new ExtraHourList();
			extraList.title = this.getTitle();
			
			for(FlowPane namePane: this.namePanes) {
				String name = ((ChoiceBox<String>) namePane.getChildren().get(0)).getValue();
				double hours = ((Spinner<Double>) namePane.getChildren().get(1)).getValue();
				
				for(Worker worker: workerList) {
					if(worker.name.equals(name)) {
						extraList.extraHours.put(worker, hours);
						break;
					}
				}
			}
			return extraList;
		}
		
		ExtraListPane() {
			this.setPrefWidth(WIDTH);
			
			this.titleTextArea = new TextArea("请输入名称");
			this.addNameButton = new Button("添加");
			this.removeSelfButton = new Button("删除");
			this.namePanes = new ArrayList<FlowPane>();
			
			this.addNameButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					FlowPane namePane = new FlowPane();
					ChoiceBox<String> nameChoiceBox = new ChoiceBox<String>(FXCollections.observableArrayList(nameList));
					Spinner<Double> hoursSpinner = new Spinner<Double>(-10, 10, 1, 0.5);
					Button removeButton = new Button("删除");
					
					removeButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							namePanes.remove(namePane);
							getChildren().remove(namePane);
						}
					});
					
					namePane.getChildren().addAll(nameChoiceBox, hoursSpinner, removeButton);
					namePanes.add(namePane);
					getChildren().add(namePane);
				}
			});
			
			this.removeSelfButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					removeSelf();
				}
			});
			
			this.getChildren().addAll(this.addNameButton, this.removeSelfButton, this.titleTextArea);
		}
		
		private void removeSelf() {
			extraListPanes.remove(this);
			vbox.getChildren().remove(this);
		}
	}
	
	private VBox vbox;
	private List<Worker> workerList;
	private List<String> nameList;
	private List<ExtraListPane> extraListPanes;
	private Button addListButton;
	
	public static final int WIDTH = 300;
	
	public List<ExtraHourList> getExtraLists() {
		List<ExtraHourList> extraLists = new ArrayList<ExtraHourList>();
		for(ExtraListPane extraListPane: this.extraListPanes) {
			extraLists.add(extraListPane.getExtraList());
		}
		
		return extraLists;
	}
	
	public void setWorkerList(List<Worker> workerList) {
		this.workerList = workerList;
		for(Worker worker: this.workerList) {
			this.nameList.add(worker.name);
		}
	}
	
	public HourListPane() {
		this.setPrefWidth(WIDTH);
		
		this.workerList = new ArrayList<Worker>();
		this.nameList = new ArrayList<String>();
		
		this.vbox = new VBox();
		this.extraListPanes = new ArrayList<ExtraListPane>();
		
		ExtraListPane spotCheckListPane = new ExtraListPane();
		ExtraListPane meteringListPane = new ExtraListPane();
		ExtraListPane absentListPane = new ExtraListPane();
		ExtraListPane omissionListPane = new ExtraListPane();
		
		spotCheckListPane.setTitle("参与抽查");
		meteringListPane.setTitle("测光");
		absentListPane.setTitle("旷工");
		omissionListPane.setTitle("漏关漏检");
		
		extraListPanes.add(spotCheckListPane);
		extraListPanes.add(meteringListPane);
		extraListPanes.add(absentListPane);
		extraListPanes.add(omissionListPane);
		
		this.addListButton = new Button("添加");
		this.addListButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ExtraListPane extraListPane = new ExtraListPane();
				extraListPanes.add(extraListPane);
				vbox.getChildren().add(extraListPane);
			}
		});
		
		this.vbox.getChildren().add(this.addListButton);
		this.vbox.getChildren().addAll(spotCheckListPane, meteringListPane, absentListPane, omissionListPane);
		this.setContent(this.vbox);
	}
}