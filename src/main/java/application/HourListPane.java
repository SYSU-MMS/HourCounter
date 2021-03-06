package application;

import java.text.Collator;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

/**
 * 额外工时信息类，用于记录常规工时外的工时项目，如测光、旷工等工时的加减
 * @author Syderny
 *
 */
class ExtraHourList {
	String title;
	Map<Worker, Double> extraHours;
	
	ExtraHourList(){
		 this.extraHours = new HashMap<Worker, Double>();
	}
}

/**
 * 用于设置额外工时信息的面板
 * @author Syderny
 *
 */
public class HourListPane extends ScrollPane {
	class ExtraListPane extends BorderPane {
		private TextField titleTextField;
		private Button addNameButton;
		private List<FlowPane> namePanes;
		private Button removeSelfButton;
		
		public String getTitle() {
			return this.titleTextField.getText();
		}
		public void setTitle(String title) {
			this.titleTextField.setText(title);;
		}
		
		/**
		 * 按照面板设置获取额外的工时信息
		 * @return
		 */
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
			this.titleTextField = new TextField("请输入名称");
			this.addNameButton = new Button("+");
			this.removeSelfButton = new Button("X");
			this.namePanes = new ArrayList<FlowPane>();
			
			this.getStyleClass().add("extra-list-pane");
			this.titleTextField.getStyleClass().add("extra-list-title");
			this.removeSelfButton.getStyleClass().add("extra-list-close-button");
			
			BorderPane titleBorderPane = new BorderPane();
			titleBorderPane.getStyleClass().add("extra-title-border-pane");
			titleBorderPane.setRight(this.removeSelfButton);
			titleBorderPane.setCenter(this.titleTextField);
			
			ScrollPane namePaneScrollPane = new ScrollPane();
			FlowPane namePaneFlowPane = new FlowPane();
			namePaneFlowPane.getStyleClass().add("names-flow-pane");
			namePaneScrollPane.setContent(namePaneFlowPane);
			namePaneScrollPane.getStyleClass().add("name-pane-scroll-pane");
			
			this.setTop(titleBorderPane);
			this.setCenter(namePaneScrollPane);
			
			namePaneFlowPane.getChildren().add(this.addNameButton);
			this.addNameButton.getStyleClass().add("add-button");
			
			/**
			 * 添加新的助理和对应的工时加减
			 */
			this.addNameButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					FlowPane namePane = new FlowPane();
					ChoiceBox<String> nameChoiceBox = new ChoiceBox<String>(FXCollections.observableArrayList(nameList));
					Spinner<Double> hoursSpinner = new Spinner<Double>(-10, 10, 1, 0.5);
					Button removeButton = new Button("X");
					removeButton.getStyleClass().add("small-close-button");
					nameChoiceBox.getStyleClass().add("name-choice-box");
					hoursSpinner.getStyleClass().add("spinner");
					hoursSpinner.getEditor().getStyleClass().add("spinner-editor");
					
					/**
					 * 移除此助理的工时加减信息
					 */
					removeButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							namePanes.remove(namePane);
							namePaneFlowPane.getChildren().remove(namePane);
						}
					});
					
					namePane.getStyleClass().add("name-pane");
					namePane.getChildren().addAll(nameChoiceBox, hoursSpinner, removeButton);
					namePanes.add(namePane);
					namePaneFlowPane.setHgap(0);
					namePaneFlowPane.setVgap(5);
					namePaneFlowPane.getChildren().add(namePaneFlowPane.getChildren().size()-1, namePane);
				}
			});
			
			this.removeSelfButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					removeSelf();
				}
			});
		}
		
		/**
		 * 移除自身，即此项目的工时列表
		 */
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
	
	/**
	 * 根据面板的设置获取额外项目工时列表
	 * @return
	 */
	public List<ExtraHourList> getExtraLists() {
		List<ExtraHourList> extraLists = new ArrayList<ExtraHourList>();
		for(ExtraListPane extraListPane: this.extraListPanes) {
			extraLists.add(extraListPane.getExtraList());
		}
		
		return extraLists;
	}
	
	public void setWorkerList(List<Worker> workerList) {
		this.workerList = workerList;

		Collections.sort(this.workerList, new Comparator<Worker>() {
			@Override
			public int compare(Worker worker1, Worker worker2) {
				return Collator.getInstance(Locale.CHINA).compare(worker1.name, worker2.name);
			}
		});

		for(Worker worker: this.workerList) {
			this.nameList.add(worker.name);
		}
	}
	
	public HourListPane() {
		this.workerList = new ArrayList<Worker>();
		this.nameList = new ArrayList<String>();
		
		this.vbox = new VBox(20);
		this.vbox.setAlignment(Pos.CENTER);
		this.vbox.getStyleClass().add("extra-pane-vbox");
		this.getStyleClass().add("extra-scroll-pane");
		this.extraListPanes = new ArrayList<ExtraListPane>();
		
		ExtraListPane spotCheckListPane = new ExtraListPane();
		ExtraListPane meteringListPane = new ExtraListPane();
		ExtraListPane absentListPane = new ExtraListPane();
		ExtraListPane omissionListPane = new ExtraListPane();
		
		// 一些常用的工时项目
		spotCheckListPane.setTitle("参与抽查");
		meteringListPane.setTitle("测光");
		absentListPane.setTitle("旷工");
		omissionListPane.setTitle("漏关漏检");
		
		extraListPanes.add(spotCheckListPane);
		extraListPanes.add(meteringListPane);
		extraListPanes.add(absentListPane);
		extraListPanes.add(omissionListPane);
		
		this.addListButton = new Button("+");
		this.addListButton.getStyleClass().add("add-button");
		this.addListButton.getStyleClass().add("large-add-button");
		this.addListButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ExtraListPane extraListPane = new ExtraListPane();
				extraListPanes.add(extraListPane);
				vbox.getChildren().add(vbox.getChildren().size()-1, extraListPane);
			}
		});
		
		this.vbox.getChildren().addAll(spotCheckListPane, meteringListPane, absentListPane, omissionListPane);
		this.vbox.getChildren().add(this.addListButton);
		this.setContent(this.vbox);
	}
}
