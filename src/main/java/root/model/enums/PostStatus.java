package root.model.enums;

public enum PostStatus {
    INACTIVE, PENDING, DECLINED, PUBLISHED;

    public static boolean exists(String status) {
        boolean result = false;
        try {
            PostStatus.valueOf(status.toUpperCase());
            result = true;
        } catch (IllegalArgumentException e) {
            System.out.println("PostStatus.exists(String status):\n" +
                    "Запрошенный статус отсутствует в базе данных.");
        }
        return result;
    }
}
