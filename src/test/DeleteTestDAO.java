package test;

import dao.*;
import java.sql.*;
import java.util.UUID;

public class DeleteTestDAO {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/tri_selectif";
        String user = "postgres";
        String password = "admin123";

        try {
            Class.forName("org.postgresql.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                System.out.println("Connexion réussie !");

                // (Reste du code inchangé)
                // [ ... tout le bloc DELETE comme tu l'avais ... ]
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
