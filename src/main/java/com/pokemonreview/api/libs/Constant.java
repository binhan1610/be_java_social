package com.pokemonreview.api.libs;

public class Constant {
    public static class RoomStatus {
        public static final String EMPTY = "EMPTY";           // Phòng trống
        public static final String OCCUPIED = "OCCUPIED";     // Đã có người ở
        public static final String DEPOSITED = "DEPOSITED";   // Đã cọc nhưng chưa vào
        public static final String REPAIRING = "REPAIRING";   // Đang sửa chữa
    }

    // 2. Trạng thái của Hợp đồng (Contract)
    public static class ContractStatus {
        public static final String ACTIVE = "ACTIVE";         // Đang hiệu lực
        public static final String PENDING = "PENDING";       // Đang chờ ký/duyệt
        public static final String EXPIRED = "EXPIRED";       // Đã hết hạn
        public static final String TERMINATED = "TERMINATED"; // Đã thanh lý/chấm dứt
    }

    // 3. Trạng thái của Hóa đơn (Invoice)
    public static class InvoiceStatus {
        public static final String UNPAID = "UNPAID";         // Chưa thanh toán
        public static final String PAID = "PAID";             // Đã thanh toán đủ
        public static final String PARTIAL = "PARTIAL";       // Thanh toán một phần (còn nợ)
        public static final String CANCELLED = "CANCELLED";   // Đã hủy hóa đơn
    }
}
