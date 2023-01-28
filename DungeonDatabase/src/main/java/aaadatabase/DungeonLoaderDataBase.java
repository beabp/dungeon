package aaadatabase;

import connection.PoolConnection;
import game.DungeonLoader;
import game.demiurge.Demiurge;
import game.demiurge.DungeonConfiguration;
import game.dungeon.Site;
import jakarta.inject.Inject;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DungeonLoaderDataBase implements DungeonLoader {
    //Home
    static String DESCRIPTION_HOME;
    static int INITIAL_COMFORT;
    static int INITIAL_SINGA;
    static int INITIAL_SINGA_CAPACITY;
    static int INITIAL_CHEST_CAPACITY;
    //Wizard
    static int INITIAL_LIFE = 10;
    static int INITIAL_ENERGY = 10;
    static int INITIAL_LIFE_MAX = 10;
    static int INITIAL_ENERGY_MAX = 10;
    static int INITIAL_CRYSTAL_CARRIER_CAPACITY = 3;
    static int INITIAL_CRYSTAL_BAG_CAPACITY = 2;

    static int MAX_WEAPONS = 1;
    static int MAX_NECKLACES = 1;
    static int MAX_RINGS = 2;

    private final PoolConnection pool;


    @Inject
    public DungeonLoaderDataBase(PoolConnection pool) {
        this.pool = pool;
    }

    @Override
    public void load(Demiurge demiurge, DungeonConfiguration dungeonConfiguration) {


    }

    private Demiurge get(LocalDateTime savingTime) {
        Demiurge result;
        try (Connection con = pool.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement("SELECT id FROM dungeon where saving_time = ?")) {

            preparedStatement.setDate(1, Date.valueOf(savingTime.toString()));
            ResultSet rs = preparedStatement.executeQuery();

            Demiurge demiurge = new Demiurge();
            if (rs.next()) {
                int id = rs.getInt("id_dungeon");

            }
            result = demiurge;
        } catch (SQLException ex) {
            Logger.getLogger(DungeonLoaderDataBase.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        }

        return result;
    }

    private List<Site> readRS(ResultSet rs) {
        List<Site> sites = new ArrayList<>();
        try {
            while (rs.next()) {
                int id = rs.getInt(1);
                String description = rs.getString(2);
                int id_owner = rs.getInt(3);

                sites.add(new Site(id, description, null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sites;
    }

    public List<Site> getAll() {
        List<Site> result;
        try (Connection con = pool.getConnection();
             Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_READ_ONLY)) {

            ResultSet rs = statement.executeQuery("Select * from site where id_dungeon = ?");

            result = readRS(rs);
        } catch (SQLException ex) {
            Logger.getLogger(DungeonLoaderDataBase.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        }

        return result;
    }

    public int addDungeon(Date saving_date_dungeon) {
        int result;

        try (Connection con = pool.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO dungeon (saving_time) VALUES (?)")) {

            preparedStatement.setDate(1, saving_date_dungeon);
            preparedStatement.executeUpdate();
            result = 1;


        } catch (SQLException ex) {
            Logger.getLogger(DungeonLoaderDataBase.class.getName()).log(Level.SEVERE, null, ex);
            result = -1;
        }
        return result;
    }


}
