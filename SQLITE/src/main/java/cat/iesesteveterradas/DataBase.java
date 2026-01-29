package cat.iesesteveterradas;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {
    private final String dbPath;

    public DataBase(String dbPath) {
        this.dbPath = dbPath;
        ensureDataDir();
    }

    private void ensureDataDir() {
        Path pathDataDir = Paths.get(dbPath).getParent();
        if (pathDataDir != null && !Files.exists(pathDataDir)) {
            try {
                Files.createDirectories(pathDataDir);
            } catch (Exception e) {
                System.err.println("Error creando directorio: " + e.getMessage());
            }
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    public void initializeDatabase() {
        try (Connection conn = connect(); Statement st = conn.createStatement()) {

            st.executeUpdate("PRAGMA foreign_keys = ON;");

            // tablas 
            st.executeUpdate("CREATE TABLE IF NOT EXISTS Faccion (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre VARCHAR(15), " +
                    "resum VARCHAR(500))");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS Personaje (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre VARCHAR(15), " +
                    "ataque REAL, " +
                    "defensa REAL, " +
                    "idFaccion INTEGER, " +
                    "FOREIGN KEY(idFaccion) REFERENCES Faccion(id))");

            // agg si no hay data zz
            if (isTableEmpty(conn, "Faccion")) {
                populateSampleData(conn);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isTableEmpty(Connection conn, String table) throws SQLException {
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT count(*) FROM " + table)) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }

    private void populateSampleData(Connection conn) throws SQLException {
        String insertF = "INSERT INTO Faccion(nombre, resum) VALUES(?, ?)";
        String insertP = "INSERT INTO Personaje(nombre, ataque, defensa, idFaccion) VALUES(?, ?, ?, ?)";

        try (PreparedStatement pf = conn.prepareStatement(insertF);
                PreparedStatement pp = conn.prepareStatement(insertP)) {

            // facs
            pf.setString(1, "Cavallers");
            pf.setString(2, "Though seen as a single group, the Knights are hardly unified. There are many Legions in Ashfeld, the most prominent being The Iron Legion.");
            pf.executeUpdate();

            pf.setString(1, "Vikings");
            pf.setString(2,"The Vikings are a loose coalition of hundreds of clans and tribes, the most powerful being The Warborn.");
            pf.executeUpdate();

            pf.setString(1, "Samurais");
            pf.setString(2, "The Samurai are the most unified of the three factions, though this does not say much as the Daimyos were often battling each other for dominance.");
            pf.executeUpdate();

            // pers
            pp.setString(1, "Warden");
            pp.setDouble(2, 1);
            pp.setDouble(3, 3);
            pp.setInt(4, 1);
            pp.executeUpdate();

            pp.setString(1, "Conqueror");
            pp.setDouble(2, 2);
            pp.setDouble(3, 2);
            pp.setInt(4, 1);
            pp.executeUpdate();

            pp.setString(1, "Peacekeep");
            pp.setDouble(2, 2);
            pp.setDouble(3, 3);
            pp.setInt(4, 1);
            pp.executeUpdate();

            pp.setString(1, "Raider");
            pp.setDouble(2, 3);
            pp.setDouble(3, 3);
            pp.setInt(4, 2);
            pp.executeUpdate();

            pp.setString(1, "Warlord");
            pp.setDouble(2, 2);
            pp.setDouble(3, 2);
            pp.setInt(4, 2);
            pp.executeUpdate();

            pp.setString(1, "Berserker");
            pp.setDouble(2, 1);
            pp.setDouble(3, 1);
            pp.setInt(4, 2);
            pp.executeUpdate();

            pp.setString(1, "Kensei");
            pp.setDouble(2, 3);
            pp.setDouble(3, 2);
            pp.setInt(4, 3);
            pp.executeUpdate();

            pp.setString(1, "Shugoki");
            pp.setDouble(2, 2);
            pp.setDouble(3, 1);
            pp.setInt(4, 3);
            pp.executeUpdate();

            pp.setString(1, "Orochi");
            pp.setDouble(2, 3);
            pp.setDouble(3, 2);
            pp.setInt(4, 3);
            pp.executeUpdate();
        }
    }

    public List<Faccion> getAllFaccionns() {
        List<Faccion> list = new ArrayList<>();
        try (Connection conn = connect();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT id, nombre, resum FROM Faccion")) {

            while (rs.next()) {
                list.add(new Faccion(rs.getInt("id"), rs.getString("nombre"), rs.getString("resum")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Personaje> getAllPersonajes() {
        List<Personaje> list = new ArrayList<>();
        try (Connection conn = connect();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT id, nombre, ataque, defensa, idFaccion FROM Personaje")) {
            while (rs.next()) {
                list.add(new Personaje(rs.getInt("id"), rs.getString("nombre"),rs.getDouble("ataque"), rs.getDouble("defensa"), rs.getInt("idFaccion")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Personaje> getPersonajesByFaccion(int idFaccion) {
        List<Personaje> list = new ArrayList<>();
        String query = "SELECT id, nombre, ataque, defensa, idFaccion FROM Personaje WHERE idFaccion = ?";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idFaccion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Personaje(rs.getInt("id"), rs.getString("nombre"), rs.getDouble("ataque"), rs.getDouble("defensa"), rs.getInt("idFaccion")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Personaje getBestAtacantByFaccion(int idFaccion) {
        String query = "SELECT id, nombre, ataque, defensa, idFaccion FROM Personaje WHERE idFaccion = ? ORDER BY ataque DESC LIMIT 1";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idFaccion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Personaje(rs.getInt("id"), rs.getString("nombre"),rs.getDouble("ataque"), rs.getDouble("defensa"), rs.getInt("idFaccion"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Personaje getBestDefensorByFaccion(int idFaccion) {
        String query = "SELECT id, nombre, ataque, defensa, idFaccion FROM Personaje WHERE idFaccion = ? ORDER BY defensa DESC LIMIT 1";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idFaccion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Personaje(rs.getInt("id"), rs.getString("nombre"), rs.getDouble("ataque"), rs.getDouble("defensa"), rs.getInt("idFaccion"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}