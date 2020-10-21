
package com.music.SalesService.dao;

import com.music.SalesService.domain.Product;
import com.music.SalesService.domain.Track;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import static com.music.SalesService.dao.DBConstants.PRODUCT_TABLE;
import static com.music.SalesService.dao.DBConstants.TRACK_TABLE;

@Repository
public class ProductDAO {

//	public ProductDAO() {
//	}

    public Product findProductById(Connection connection, long productId) throws SQLException {
        Product prod = null;
        Statement stmt = connection.createStatement();
        try {
            String sqlString = " select * from " +
                    PRODUCT_TABLE + " p, " +
                    TRACK_TABLE + " t " +
                    " where p.product_id = " + productId +
                    " and p.product_id = t.product_id order by t.track_number";
            ResultSet set = stmt.executeQuery(sqlString);
            if (set.next()) {
                prod = new Product(set.getInt("product_id"), set.getString("product_code"),
                        set.getString("product_description"), set.getBigDecimal("product_price"), null);
                Set<Track> tracks = new HashSet<Track>();
                Track track = new Track();
                track.setId(set.getInt("track_id"));
                track.setProduct(prod);
                track.setSampleFilename(set.getString("sample_filename"));
                track.setTitle(set.getString("title"));
                track.setTrackNumber(set.getInt("track_number"));
                tracks.add(track);

                while (set.next()) {
                    track = new Track();
                    track.setId(set.getInt("track_id"));
                    track.setProduct(prod);
                    track.setSampleFilename(set.getString("sample_filename"));
                    track.setTitle(set.getString("title"));
                    track.setTrackNumber(set.getInt("track_number"));
                    tracks.add(track);
                }
                prod.setTracks(tracks);
            }
            set.close();
        } finally {
            stmt.close();
        }

        return prod;
    }

    public Product findProductByCode(Connection connection, String prodCode) throws SQLException {
        System.out.println("entered dao product by code method.......");
        Product prod = null;
        Statement stmt = connection.createStatement();
        try {
            String sqlString = " select * from " +
                    PRODUCT_TABLE + " p, " +
                    TRACK_TABLE + " t " +
                    " where p.product_code = '" + prodCode + "'" +
                    " and p.product_id = t.product_id  order by t.track_number";
            ResultSet set = stmt.executeQuery(sqlString);
            if (set.next()) {
                prod = new Product(set.getInt("product_id"), set.getString("product_code"),
                        set.getString("product_description"), set.getBigDecimal("product_price"), null);
                Set<Track> tracks = new HashSet<Track>();
                Track track = new Track();
                track.setId(set.getInt("track_id"));
                track.setProduct(prod);
                track.setSampleFilename(set.getString("sample_filename"));
                track.setTitle(set.getString("title"));
                track.setTrackNumber(set.getInt("track_number"));
                tracks.add(track);

                while (set.next()) {
                    track = new Track();
                    track.setId(set.getInt("track_id"));
                    track.setProduct(prod);
                    track.setSampleFilename(set.getString("sample_filename"));
                    track.setTitle(set.getString("title"));
                    track.setTrackNumber(set.getInt("track_number"));
                    tracks.add(track);
                }
                prod.setTracks(tracks);
            }
            set.close();
        } finally {
            stmt.close();
        }
        System.out.println("exited dao product by code method.......");
        return prod;
    }

    public Set<Product> findAllProducts(Connection connection) throws SQLException {
        Set<Product> prods = new HashSet<Product>();
        Statement stmt = connection.createStatement();
        try {
            ResultSet set = stmt.executeQuery(" select * from " + PRODUCT_TABLE);
            while (set.next()) {
                Product prod = new Product(set.getInt("product_id"), set.getString("product_code"),
                        set.getString("product_description"), set.getBigDecimal("product_price"), null);
                prods.add(prod);
            }
            set.close();
        } finally {
            stmt.close();
        }

        return prods;
    }

    public Track findTrackByTID(Connection connection, int trackId) throws SQLException {
        Product prod;
        Track track_found = null;
        Statement stmt = connection.createStatement();
        try {
            String sqlString = " select * from " +
                    PRODUCT_TABLE + " p, " +
                    TRACK_TABLE + " t " +
                    " where t.track_id = " + trackId +
                    " and p.product_id = t.product_id order by t.track_number";
            ResultSet set = stmt.executeQuery(sqlString);
            if (set.next()) {
                prod = this.findProductById(connection, set.getInt("product_id"));
                if (prod != null) {
                    track_found = prod.findTrackbyID(trackId);
                }
            }
            set.close();
        } finally {
            stmt.close();
        }

        return track_found;
    }

}
