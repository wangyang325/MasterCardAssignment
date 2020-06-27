package com.mastercard.codetest.jerseystore.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mastercard.codetest.jerseystore.common.Utils;
import com.mastercard.codetest.jerseystore.file.GoodFileLoader;
import com.mastercard.codetest.jerseystore.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Service
public class JerseyStoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JerseyStoreService.class);

    @Autowired
    GoodFileLoader fileLoader;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public JerseyStoreService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    // ------------------------------------------------------------------------------------

    public String checkJersey(File file) {
        // get Jersey from file
        Map<String, Jersey> mapFile = fileLoader.loadFile(file, 1);
        if (mapFile == null || mapFile.size() == 0) {
            return "Error: read file failed";
        }

        // get Jersey from database
        List<Jersey> list = getAllJerseys();
        StringBuilder sb = new StringBuilder();
        sb.append("*****  How to read: Id exists -> existing in database || Amount exists -> existing in cvs  ********");
        sb.append("</br>");
        sb.append(System.lineSeparator());
        sb.append("</br>");
        sb.append(System.lineSeparator());
        // no data
        if (list != null && list.size() != 0) {
            // change list to Json
            list.stream().forEach((jersey) -> {
                GsonBuilder builder = new GsonBuilder();
                builder.excludeFieldsWithoutExposeAnnotation();
                Gson gson = builder.create();
                String key = Utils.getKey(jersey);
                if (mapFile.containsKey(key) == false) {
                    jersey.setAmount(mapFile.get(key).getAmount());
                    mapFile.remove(key);
                }
                String jerseyStr = gson.toJson(jersey);
                sb.append(jerseyStr);
                sb.append("</br>");
                sb.append(System.lineSeparator());
            });
        }
        //
        for (String key : mapFile.keySet()) {
            GsonBuilder builder = new GsonBuilder();
            builder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = builder.create();
            String jerseyStr = gson.toJson(mapFile.get(key));
            sb.append(jerseyStr);
            sb.append("</br>");
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }

    public String addJersey(File file) {
        // get Jersey from file
        Map<String, Jersey> mapFile = fileLoader.loadFile(file, 1);
        if (mapFile == null || mapFile.size() == 0) {
            return "Error: read file failed";
        }

        // get Jersey from database
        Map<String, Jersey> mapDb = getJerseys();

        List<Jersey> listNg = new ArrayList<>();
        List<Jersey> listOk = new ArrayList<>();

        for (String key : mapFile.keySet()) {
            Jersey js = mapFile.get(key);
            // Update
            if (mapDb.containsKey(key)) {
                js.setId(mapDb.get(key).getId());
                if (updateAmount(js) == false) {
                    listNg.add(js);
                } else {
                    listOk.add(js);
                }
            }
            // Insert
            else {
                if (insertJerseyAndStock(js) == false) {
                    listNg.add(js);
                } else {
                    listOk.add(js);
                }
            }
        }
        String rs = "";
        String okStr = changeListToJson(listOk);
        if (okStr.length() != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("*****  Update OK List  ********");
            sb.append("</br>");
            sb.append(System.lineSeparator());
            sb.append("</br>");
            sb.append(System.lineSeparator());
            rs = rs + sb.toString() + okStr;
        }

        String ngStr = changeListToJson(listNg);
        if (ngStr.length() != 0) {
            StringBuilder sb = new StringBuilder();
            sb = new StringBuilder();
            sb.append("*****  Update NG List  ********");
            sb.append("</br>");
            sb.append(System.lineSeparator());
            sb.append("</br>");
            sb.append(System.lineSeparator());
            rs = rs + sb.toString() + ngStr;
        }
        return rs;
    }

    private String changeListToJson(List<Jersey> list) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() != 0) {
            // change list to Json
            list.stream().forEach((jersey) -> {
                GsonBuilder builder = new GsonBuilder();
                builder.excludeFieldsWithoutExposeAnnotation();
                Gson gson = builder.create();
                String jerseyStr = gson.toJson(jersey);
                sb.append(jerseyStr);
                sb.append("</br>");
                sb.append(System.lineSeparator());
            });
        }
        return sb.toString();
    }

    @Transactional
    public boolean insertJerseyAndStock(Jersey pJersey) {
        boolean result = false;
        result = addJersey(pJersey);
        if (result) {
            result = updateStock(pJersey);
            if (result == false) {
                return result;
            }
        } else {
            return result;
        }
        return result;
    }

    @Transactional
    public boolean updateAmount(Jersey pJersey) {
        boolean result = false;
        result = updateStock(pJersey);
        if (result == false) {
            return result;
        }
        return result;
    }

    public Map<String, Jersey> getJerseys() {
        Map<String, Jersey> rs = new HashMap<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM jersey");
        for (Map row : rows) {
            Jersey jersey = new Jersey();
            jersey.setId((String) row.get("id"));
            jersey.setBrand((String) row.get("brand"));
            jersey.setSize(JerseySize.valueOf((Integer) row.get("size")));
            jersey.setClub((String) row.get("club"));

            Date dtBuffer = (Date) row.get("year");
            jersey.setYear("" + (dtBuffer.getYear() + 1900));

            jersey.setType(JerseyType.valueOf((Integer) row.get("type")));
            jersey.setCut(JerseyCut.valueOf((Integer) row.get("cut")));
            // add material column
            jersey.setMaterial(JerseyMaterial.valueOf((Integer) row.get("mat")));
            // add optimistic lock
            jersey.setCreateDate((Timestamp) row.get("createDate"));
            // add amount
            jersey.setAmount(getStock(jersey.getId()));
            String key = Utils.getKey(jersey);
            if (rs.containsKey(key) == false) {
                rs.put(key, jersey);
            }
        }
        return rs;
    }

    public boolean updateStock(Jersey jersey) {
        String value = stringRedisTemplate.opsForValue().get("STOCK:" + jersey.getId());
        if (value != null) {
            try {
                int nowV = Integer.valueOf(value);
                stringRedisTemplate.opsForValue().set("STOCK:" + jersey.getId(), jersey.getAmount() + nowV + "");
            } catch (Exception e) {
                return false;
            }
        } else {
            stringRedisTemplate.opsForValue().set("STOCK:" + jersey.getId(), jersey.getAmount() + "");
        }
        return true;
    }

    public int getStock(String tShirtId) {
        String value = stringRedisTemplate.opsForValue().get("STOCK:" + tShirtId);
        if (value != null) {
            try {
                return Integer.valueOf(value);
            } catch (Exception e) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    // ------------------------------------------------------------------------------------
    public Jersey getJersey(String tShirtId) {
        return jdbcTemplate.queryForObject("SELECT * FROM jersey WHERE id = ?",
                new Object[]{tShirtId},
                new RowMapper<Jersey>() {
                    @Nullable
                    @Override
                    public Jersey mapRow(ResultSet resultSet, int i) throws SQLException {
                        Jersey jersey = new Jersey();
                        jersey.setId(resultSet.getString("id"));
                        jersey.setBrand(resultSet.getString("brand"));
                        jersey.setSize(JerseySize.valueOf(resultSet.getInt("size")));
                        jersey.setClub(resultSet.getString("club"));
                        Date date = resultSet.getDate("year");
                        jersey.setYear((date.getYear() + 1900) + "");
                        jersey.setType(JerseyType.valueOf(resultSet.getInt("type")));
                        jersey.setCut(JerseyCut.valueOf(resultSet.getInt("cut")));
                        // add material column
                        jersey.setMaterial(JerseyMaterial.valueOf(resultSet.getInt("mat")));
                        // add optimistic lock
                        jersey.setCreateDate(resultSet.getTimestamp("createDate"));
                        // add amount
                        jersey.setAmount(getStock(jersey.getId()));
                        return jersey;
                    }
                });
    }

    /**
     * ID, SIZE, BRAND, CLUB, YEAR, TYPE, CUT
     *
     * @param jersey
     */
    public boolean addJersey(Jersey jersey) {
        jersey.setId(UUID.randomUUID().toString());
        return jdbcTemplate.update("INSERT INTO jersey(ID, SIZE, BRAND, CLUB, YEAR, TYPE, CUT, MAT) VALUES (?,?,?,?,?,?,?,?)",
                jersey.getId(), jersey.getSize().getId(), jersey.getBrand(), jersey.getClub(),
                jersey.getYear(), jersey.getType().getId(), jersey.getCut().getId(), jersey.getMaterial().getId()) > 0;
    }

    public List<Jersey> getAllJerseys() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM jersey");
        List<Jersey> jerseys = new ArrayList<>(rows.size());
        for (Map row : rows) {
            Jersey jersey = new Jersey();
            jersey.setId((String) row.get("id"));
            jersey.setBrand((String) row.get("brand"));
            jersey.setSize(JerseySize.valueOf((Integer) row.get("size")));
            jersey.setClub((String) row.get("club"));
            Date dtBuffer = (Date) row.get("year");
            jersey.setYear("" + (dtBuffer.getYear() + 1900));
            jersey.setType(JerseyType.valueOf((Integer) row.get("type")));
            jersey.setCut(JerseyCut.valueOf((Integer) row.get("cut")));
            // add material column
            jersey.setMaterial(JerseyMaterial.valueOf((Integer) row.get("mat")));
            // add optimistic lock
            jersey.setCreateDate((Timestamp) row.get("createDate"));
            // add amount
            jersey.setAmount(getStock(jersey.getId()));
            jerseys.add(jersey);
        }
        return jerseys;
    }
}
