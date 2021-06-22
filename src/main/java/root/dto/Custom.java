package root.dto;

import java.util.Date;
import java.util.Objects;

public class Custom {
    private Date time;
    private Long count;

    public Custom(Date time, Long count) {
        this.time = time;
        this.count = count;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Custom custom = (Custom) o;
        return time.equals(custom.time) &&
                count.equals(custom.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, count);
    }
}
