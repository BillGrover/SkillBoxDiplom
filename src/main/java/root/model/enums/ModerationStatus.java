package root.model.enums;

public enum ModerationStatus {
    NEW, ACCEPTED, DECLINED;

    public static boolean exists(String status) {
        boolean result = false;
        try {
            ModerationStatus.valueOf(status.toUpperCase());
            result = true;
        } catch (IllegalArgumentException e) {
            System.out.println("ModerationStatus.exists(String status):\n" +
                    "Запрошенный статус отсутствует в базе данных.");
        }
        return result;
    }
}
