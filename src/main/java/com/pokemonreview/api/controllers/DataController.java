package com.pokemonreview.api.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemonreview.api.models.DataEntity;
import com.pokemonreview.api.service.DataService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataController {

    private final DataService dataService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/latest")
    public String getLatestByTopic(@RequestParam int type) {
        String topic = switch (type) {
            case 1 -> "/doan/air_quality/prediction_24h";
            case 2 -> "/doan/air_quality/system_info";
            case 3 -> "/doan/air_quality/debug_sub";
            case 4 -> "/doan/air_quality/log";
            default -> "/doan/air_quality/realtime_display";
        };
        DataEntity data = dataService.getLastByTopic(topic);
        return data.getData();
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam int type,
                                         @RequestParam int limit,
                                         @RequestParam int offset) throws Exception {

        String topic = switch (type) {
            case 1 -> "/doan/air_quality/prediction_24h";
            case 2 -> "/doan/air_quality/system_info";
            case 3 -> "/doan/air_quality/debug_sub";
            case 4 -> "/doan/air_quality/log";
            default -> "/doan/air_quality/realtime_display";
        };

        // Lấy dữ liệu
        List<DataEntity> rs = dataService.getPaged(limit, offset, topic);

        List<Map<String, Object>> list = new ArrayList<>();
        for (DataEntity entity : rs) {
            String jsonStr = entity.getData();  // chuỗi JSON
            Map<String, Object> map = objectMapper.readValue(
                    jsonStr,
                    new TypeReference<Map<String, Object>>() {}
            );
            list.add(map);
        }

        if (list.isEmpty()) {
            // Không có dữ liệu thì trả về 204
            return ResponseEntity.noContent().build();
        }

        // ===== TẠO FILE EXCEL =====
        byte[] excelBytes;
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Data");

            // Lấy danh sách cột từ bản ghi đầu tiên
            Map<String, Object> firstRow = list.get(0);
            List<String> columns = new ArrayList<>(firstRow.keySet()); // giữ nguyên thứ tự key JSON

            // Style header
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // ----- Ghi header -----
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns.get(i));
                cell.setCellStyle(headerStyle);
            }

            // ----- Ghi dữ liệu -----
            int rowIdx = 1;
            for (Map<String, Object> rowData : list) {
                Row row = sheet.createRow(rowIdx++);

                for (int colIdx = 0; colIdx < columns.size(); colIdx++) {
                    String key = columns.get(colIdx);
                    Object value = rowData.get(key);

                    Cell cell = row.createCell(colIdx);

                    if (value == null) {
                        cell.setBlank();
                    } else if (value instanceof Number number) {
                        cell.setCellValue(number.doubleValue());
                    } else if (value instanceof Boolean b) {
                        cell.setCellValue(b);
                    } else {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Auto-size cột
            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            excelBytes = out.toByteArray();
        }

        // ===== HEADER RESPONSE =====
        String topicName = switch (type) {
            case 1 -> "prediction_24h";
            case 2 -> "system_info";
            case 3 -> "debug_sub";
            case 4 -> "log";
            default -> "realtime_display";
        };

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = topicName + "_" + timestamp + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        // tránh lỗi ký tự Unicode trong header
        String contentDisposition = "attachment; filename=\"" +
                new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) +
                "\"";
        headers.set(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelBytes);
    }

    @GetMapping("/log")
    public List<DataEntity> getLog(
            @RequestParam int limit,
            @RequestParam int offset
    ) throws Exception {
        String topic = "/doan/air_quality/log";

        return dataService.getPaged(limit, offset, topic);
    }

    @GetMapping("/page")
    public List<Map<String, Object>> getPaged(
            @RequestParam int limit,
            @RequestParam int offset,
            @RequestParam int type
    ) throws Exception {
        String topic = switch (type) {
            case 1 -> "/doan/air_quality/prediction_24h";
            case 2 -> "/doan/air_quality/system_info";
            case 3 -> "/doan/air_quality/debug_sub";
            default -> "/doan/air_quality/realtime_display";
        };

        List<Map<String, Object>> list = new ArrayList<>();
        List<DataEntity> rs = dataService.getPaged(limit, offset, topic);
        for (DataEntity entity : rs) {
            String jsonStr = entity.getData(); // chuỗi như thầy gửi ở trên
            // parse JSON string → Map
            Map<String, Object> map = objectMapper.readValue(
                    jsonStr,
                    new TypeReference<Map<String, Object>>() {}
            );
            list.add(map);
        }
        return list;
    }

    @GetMapping("/range")
    public List<String> getByRange(
            @RequestParam long from,
            @RequestParam long to,
            @RequestParam int type
    ) {
        String topic = switch (type) {
            case 1 -> "/doan/air_quality/prediction_24h";
            case 2 -> "/doan/air_quality/system_info";
            case 3 -> "/doan/air_quality/debug_sub";
            case 4 -> "/doan/air_quality/log";
            default -> "/doan/air_quality/realtime_display";
        };

        List<String> list = new ArrayList<>();
        List<DataEntity> rs = dataService.getByTimeRange(from, to, topic);
        for (DataEntity entity : rs) {
            list.add(entity.getData());
        }
        return list;
    }
}
