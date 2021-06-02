package application;
	
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * 主界面类
 * @author Syderny
 *
 */
public class Main extends Application {
	private BorderPane root;
	private DatePane datePane;
	private HourListPane hourListPane;
	private TablePane tablePane;
	private ExportPane exportPane;
	private BorderPane titlePane;
	private FlowPane bottomPane;
	private Button previousButton;
	private Button nextButton;
	private Button closeButton;
	private Label titleLabel;
	private int paneState;
	private double xOffset, yOffset;
	private String[] titles = {
		"导入表格",
		"设置日期",
		"其他工时",
		"导出表格"
	};
	
	@Override
	public void start(Stage primaryStage) {
		try {
			this.root = new BorderPane();
			this.root.setId("main-border-pane");
			
			Scene scene = new Scene(this.root,800,500);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.setTitle("多媒体工时小助手");
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			
			this.root.setOnMousePressed(new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	                xOffset = event.getSceneX();
	                yOffset = event.getSceneY();
	            }
	        });
	        this.root.setOnMouseDragged(new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	                primaryStage.setX(event.getScreenX() - xOffset);
	                primaryStage.setY(event.getScreenY() - yOffset);
	            }
	        });
			
			this.datePane = new DatePane();
			this.hourListPane = new HourListPane();
			this.tablePane = new TablePane(primaryStage, this.hourListPane);
			this.exportPane = new ExportPane(primaryStage, this.tablePane, this.datePane, this.hourListPane);
			this.titlePane = new BorderPane();
			this.bottomPane = new FlowPane();
			
			this.paneState = 0;
			this.root.setCenter(this.tablePane);
			this.initBottomButtons();
			
			this.titleLabel = new Label(this.titles[0]);
			this.titleLabel.setId("title-label");
			this.titlePane.setLeft(this.titleLabel);
			this.root.setTop(this.titlePane);
			
			this.bottomPane.setAlignment(Pos.BOTTOM_RIGHT);
			this.bottomPane.setId("bottom-pane");
			this.bottomPane.getChildren().addAll(this.previousButton, this.nextButton);
			this.root.setBottom(this.bottomPane);
			this.previousButton.setVisible(false);
			
			this.closeButton = new Button("X");
			this.closeButton.setId("close-button");
			this.titlePane.setRight(this.closeButton);
			this.closeButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					Platform.exit();
				}
			});
			
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置当前面板的标题
	 * @param title
	 */
	private void setTitleLabel(String title) {
		((Label) this.titlePane.getChildren().get(0)).setText(title);
	}
	
	/**
	 * 初始化底下的两个上一步下一步按钮
	 */
	private void initBottomButtons() {
		this.previousButton = new Button("<");
		this.nextButton = new Button(">");
		this.previousButton.getStyleClass().add("bottom-button");
		this.nextButton.getStyleClass().add("bottom-button");
		
		this.previousButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				switch(paneState) {
				case 0:
					break;
				case 1:
					paneState--;
					previousButton.setVisible(false);
					root.setCenter(tablePane);
					break;
				case 2:
					paneState--;
					root.setCenter(datePane);
					break;
				case 3:
					paneState--;
					nextButton.setVisible(true);
					root.setCenter(hourListPane);
					break;
				}
				setTitleLabel(titles[paneState]);
			}
		});
		this.nextButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				switch(paneState) {
				case 0:
					paneState++;
					previousButton.setVisible(true);
					root.setCenter(datePane);
					break;
				case 1:
					paneState++;
					root.setCenter(hourListPane);
					break;
				case 2:
					paneState++;
					nextButton.setVisible(false);
					root.setCenter(exportPane);
				case 3:
					break;
				}
				setTitleLabel(titles[paneState]);
			}
		});
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * 将字符串转换成Date日期对象
	 * @param strDate 如"2021-5-30"
	 * @return 对应的Date对象
	 */
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
	
	/**
	 * 弹出异常信息的警告窗口
	 * @param e 对应的异常
	 */
	public static void alertError(Exception e) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("错误");
		alert.setHeaderText(e.getMessage());
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