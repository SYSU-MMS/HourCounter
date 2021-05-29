package application;
	
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class Main extends Application {
	private BorderPane root;
	private DatePane datePane;
	private HourListPane hourListPane;
	private TablePane tablePane;
	private ExportPane exportPane;
	private FlowPane bottomPane;
	private Button previousButton;
	private Button nextButton;
	private int paneState;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			this.root = new BorderPane();
			Scene scene = new Scene(this.root,800,500);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			
			this.datePane = new DatePane();
			this.hourListPane = new HourListPane();
			this.tablePane = new TablePane(primaryStage, this.hourListPane);
			this.exportPane = new ExportPane(primaryStage, this.tablePane, this.datePane, this.hourListPane);
			this.bottomPane = new FlowPane();
			
			this.paneState = 0;
			this.root.setCenter(this.tablePane);
			this.initBottomButtons();
			
			this.bottomPane.getChildren().addAll(this.previousButton, this.nextButton);
			this.root.setBottom(this.bottomPane);
			
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initBottomButtons() {
		this.previousButton = new Button("上一步");
		this.nextButton = new Button("下一步");
		
		this.previousButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				switch(paneState) {
				case 0:
					break;
				case 1:
					paneState--;
					root.setCenter(tablePane);
					break;
				case 2:
					paneState--;
					root.setCenter(datePane);
					break;
				case 3:
					paneState--;
					root.setCenter(hourListPane);
					break;
				}
			}
		});
		this.nextButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				switch(paneState) {
				case 0:
					paneState++;
					root.setCenter(datePane);
					break;
				case 1:
					paneState++;
					root.setCenter(hourListPane);
					break;
				case 2:
					paneState++;
					root.setCenter(exportPane);
				case 3:
					break;
				}
			}
		});
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static Date string2Date(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
	}
	
	public static void alertError(Exception e) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("异常");
		alert.setHeaderText("异常");
		alert.setContentText(e.toString());
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("The exception stacktrace was:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		alert.getDialogPane().setExpandableContent(expContent);
		alert.showAndWait();
	}
}