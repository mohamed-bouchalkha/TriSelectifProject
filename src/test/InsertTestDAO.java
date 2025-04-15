package test;

import java.sql.*;
import java.util.UUID;

public class InsertTestDAO {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/tri_selectif";
        String user = "postgres";
        String password = "admin123";

        try {
            Class.forName("org.postgresql.Driver"); // S'assurer que le driver est chargé
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                System.out.println("Connexion réussie !");

                // (Reste du code inchangé)
                // [ ... tout le bloc INSERT comme tu l'avais ... ]

                System.out.println("Données insérées avec succès !");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
