    package main;
    
    import javafx.application.Application;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.stage.Stage;
    
    import java.sql.Connection;
    import java.sql.DriverManager;
    
    public class Main extends Application {
    
        public static Connection conn;
    
        @Override
        public void start(Stage primaryStage) throws Exception {
            String url = "jdbc:postgresql://localhost:5432/tri_selectif";
            String user = "postgres";
            String password = "admin123";
    
            try {
                conn = DriverManager.getConnection(url, user, password);
                System.out.println("Connexion PostgreSQL réussie !");
            } catch (Exception e) {
                e.printStackTrace();
                return; // On arrête si la connexion échoue
            }
    
            // Charger l'interface FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/pages/MainConnection.fxml"));
            Parent root = loader.load();
    
            primaryStage.setTitle("Création d'un Compte Ménage");
            primaryStage.setScene(new Scene(root, 400, 300));
            primaryStage.show();
        }
    
        public static void main(String[] args) {
            launch(args);
        }
    }
