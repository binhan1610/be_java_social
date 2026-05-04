package com.pokemonreview.api.service;

import com.pokemonreview.api.models.*;
import com.pokemonreview.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class InvoiceJob {

    @Autowired private RoomRepository roomRepository;
    @Autowired private ContractRepository contractRepository;
    @Autowired private MeterReadingRepository meterReadingRepository;
    @Autowired private RoomServiceRepository roomServiceRepository;
    @Autowired private ServiceCatalogRepository serviceCatalogRepository;
    @Autowired private InvoiceRepository invoiceRepository;
    @Autowired private MailService mailService;

    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void generateMonthlyInvoices() {
        // 1. Xác định tháng và năm cần tính tiền (Tháng trước)
        LocalDate lastMonthDate = LocalDate.now().minusMonths(1);
        int targetMonth = lastMonthDate.getMonthValue();
        int targetYear = lastMonthDate.getYear();

        // 2. Lấy tất cả các phòng đang có người ở (OCCUPIED)
        List<Room> activeRooms = roomRepository.findAll(); // Có thể lọc thêm .stream().filter(...)

        for (Room room : activeRooms) {
            try {
                // 3. Tìm hợp đồng đang hiệu lực của phòng
                Contract activeContract = contractRepository.findByRoomIdAndStatus(room.getRoomId(), "ACTIVE")
                        .orElse(null);

                if (activeContract == null) continue; // Phòng không có hợp đồng thì bỏ qua

                // 4. Lấy chỉ số điện nước tháng vừa rồi
                MeterReading reading = meterReadingRepository
                        .findByRoomIdAndMonthAndYear(room.getRoomId(), targetMonth, targetYear)
                        .orElse(null);

                long totalAmount = room.getPrice(); // Bắt đầu bằng tiền thuê phòng cơ bản

                if (reading != null) {
                    long electricCost = (long) (reading.getElectricNew() - reading.getElectricOld()) * 3500;
                    long waterCost = (long) (reading.getWaterNew() - reading.getWaterOld()) * 15000;
                    totalAmount += (electricCost + waterCost);
                }

                List<RoomService> services = roomServiceRepository.findByRoomId(room.getRoomId());
                for (RoomService rs : services) {
                    totalAmount += serviceCatalogRepository.findById(rs.getServiceId())
                            .map(ServiceCatalog::getPrice)
                            .orElse(0L);
                }

                Invoice invoice = new Invoice();
                invoice.setInvoiceId(IdGeneratorService.generateNewId(IdGeneratorService.IdentityType.INVOICE));
                invoice.setWorkspaceId(room.getWorkspaceId());
                invoice.setRoomId(room.getRoomId());
                invoice.setMonth(targetMonth);
                invoice.setYear(targetYear);
                invoice.setTotalAmount(totalAmount);
                invoice.setPaidAmount(0L);
                invoice.setStatus("UNPAID");

                long now = System.currentTimeMillis();
                invoice.setCreateTime(now);
                invoice.setUpdateTime(now);

                invoiceRepository.save(invoice);

                // 7. Gửi email thông báo khi hóa đơn đã được tạo thành công
                String subject = String.format("Hóa đơn mới tháng %d/%d cho phòng %d", targetMonth, targetYear, room.getRoomId());
                String content = String.format(
                        "Hóa đơn mới đã được tạo thành công.%n" +
                        "Phòng: %d%n" +
                        "Tháng: %d/%d%n" +
                        "Tổng tiền: %d VND%n" +
                        "Trạng thái: %s%n",
                        room.getRoomId(), targetMonth, targetYear, totalAmount, invoice.getStatus()
                );
                mailService.sendInvoiceNotification(subject, content);

            } catch (Exception e) {
                // Log lỗi cho từng phòng để không làm chết cả Job
                System.err.println("Lỗi khi tạo hóa đơn cho phòng: " + room.getRoomId() + " - " + e.getMessage());
            }
        }
    }
}